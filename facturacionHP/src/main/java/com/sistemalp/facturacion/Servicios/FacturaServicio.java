package com.sistemalp.facturacion.Servicios;

import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sistemalp.facturacion.Dto.DetalleFacturaDTO;
import com.sistemalp.facturacion.Dto.FacturaRequestDTO;
import com.sistemalp.facturacion.Dto.PagoDTO;
import com.sistemalp.facturacion.Entidades.*;

import com.sistemalp.facturacion.Repositorios.*;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FacturaServicio {

    private final FacturaRepositorio facturaRepository;
    private final ClienteRepositorio clienteRepository;
    private final EmpresaRepositorio empresaRepository;
    private final ProductoRepositorio productoRepository;
    private final DetalleFacturaRepositorio detalleRepository;
    private final ImpuestoDetalleRepositorio impuestoRepository;
    private final FormaPagoRepositorio formaPagoRepository;
    private final FacturaPagoRepositorio facturaPagoRepository;
    private final CampoAdicionalFacturaRepositorio campoAdicionalRepository;
    private final DetalleAdicionalFacturaRepositorio detalleAdicionalRepository;

    @Transactional
    public Factura crearFacturaCompleta(FacturaRequestDTO request) {
        if (request.getDetalles() == null || request.getDetalles().isEmpty())
            throw new RuntimeException("Debe ingresar al menos un detalle.");
        if (request.getClienteId() == null)
            throw new RuntimeException("Debe seleccionar un cliente.");
        if (request.getEmpresaId() == null)
            throw new RuntimeException("Debe seleccionar una empresa emisora.");

        // Cargar Entidades Principales
        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado."));
        Empresa empresa = empresaRepository.findById(request.getEmpresaId())
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada."));

        Factura factura = new Factura();
        String rawSeq = request.getSecuencial();
        if (rawSeq != null && rawSeq.contains("-")) {
            rawSeq = rawSeq.substring(rawSeq.lastIndexOf("-") + 1);
        }
        factura.setSecuencial(rawSeq);
        factura.setFechaEmision(
                request.getFechaEmision() != null ? request.getFechaEmision() : java.time.LocalDateTime.now());

        // Se establecen inicialmente en 0 si vienen nulos, se recalcularán
        factura.setEstado(1);
        factura.setCliente(cliente);
        factura.setEmpresa(empresa);
        factura = facturaRepository.save(factura);

        // Variables para acumuladores si el request no trae totales
        double calcSubtotal12 = 0.0;
        double calcSubtotal0 = 0.0;
        double calcTotalDescuento = 0.0;
        double calcTotalIva = 0.0;

        // Guardar Info Adicional
        if (request.getInfoAdicional() != null) {
            List<CampoAdicionalFactura> infoAdicionalVars = new ArrayList<>();
            for (com.sistemalp.facturacion.Dto.CampoAdicionalDTO cmpDTO : request.getInfoAdicional()) {
                CampoAdicionalFactura cmp = new CampoAdicionalFactura();
                cmp.setFactura(factura);
                cmp.setNombre(cmpDTO.getNombre());
                cmp.setValor(cmpDTO.getValor());
                campoAdicionalRepository.save(cmp);
                infoAdicionalVars.add(cmp);
            }
            factura.setInfoAdicional(infoAdicionalVars);
        }

        List<DetalleFactura> detalles = new ArrayList<>();

        for (DetalleFacturaDTO detDTO : request.getDetalles()) {
            Producto producto = productoRepository.findById(detDTO.getProductoId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado ID: " + detDTO.getProductoId()));

            DetalleFactura det = new DetalleFactura();
            det.setFactura(factura);
            det.setProducto(producto);
            det.setCantidad(detDTO.getCantidad());

            // Lógica de Autocalculo de Precios
            Double precio = detDTO.getPrecioUnitario();
            if (precio == null) {
                precio = producto.getProductoPrecio(); // Usar precio base del producto
            }
            det.setPrecioUnitario(precio);

            Double descuento = detDTO.getDescuento() != null ? detDTO.getDescuento() : 0.0;
            det.setDescuento(descuento);

            Double subtotal = detDTO.getSubtotal();
            if (subtotal == null) {
                subtotal = (det.getCantidad() * precio) - descuento;
            }
            det.setSubtotal(subtotal);

            DetalleFactura detalleGuardado = detalleRepository.save(det);

            // Lógica de Impuestos
            ImpuestoDetalle imp = new ImpuestoDetalle();
            imp.setDetalle(detalleGuardado);

            if (detDTO.getImpuesto() != null) {
                // Si el usuario envía el impuesto explícitamente
                imp.setCodigo(detDTO.getImpuesto().getCodigo());
                imp.setCodigoPorcentaje(detDTO.getImpuesto().getCodigoPorcentaje());
                imp.setTarifa(detDTO.getImpuesto().getTarifa());
                imp.setBaseImponible(detDTO.getImpuesto().getBaseImponible());
                imp.setValor(detDTO.getImpuesto().getValor());
            } else {
                // Autocalcular Impuesto basado en producto.productoTasa (12 vs 0)
                // Asumimos IVA código 2
                imp.setCodigo("2");
                double tasa = producto.getProductoTasa() != null ? producto.getProductoTasa() : 0.0; // Ej: 12.0 o 15.0
                imp.setTarifa(tasa);

                // Mapeo básico de Código Porcentaje SRI
                if (tasa == 0.0) {
                    imp.setCodigoPorcentaje("0"); // 0%
                } else if (tasa == 12.0) {
                    imp.setCodigoPorcentaje("2"); // 12%
                } else if (tasa == 15.0) {
                    imp.setCodigoPorcentaje("4"); // 15% (Actualizado 2024/2025)
                } else {
                    imp.setCodigoPorcentaje("2"); // Default fallback
                }

                imp.setBaseImponible(subtotal);
                double valorIva = subtotal * (tasa / 100.0);
                // Redondear a 2 decimales
                valorIva = Math.round(valorIva * 100.0) / 100.0;
                imp.setValor(valorIva);
            }

            impuestoRepository.save(imp);
            det.setImpuesto(imp); // Fix: Ensure XML generator sees the tax

            // Acumular Totales
            calcTotalDescuento += descuento;
            if (imp.getTarifa() > 0) {
                calcSubtotal12 += imp.getBaseImponible();
                calcTotalIva += imp.getValor();
            } else {
                calcSubtotal0 += imp.getBaseImponible();
            }

            // Guardar Detalles Adicionales
            if (detDTO.getDetallesAdicionales() != null) {
                List<DetalleAdicionalFactura> detsAdicionales = new ArrayList<>();
                for (com.sistemalp.facturacion.Dto.DetalleAdicionalDTO detAdDTO : detDTO.getDetallesAdicionales()) {
                    DetalleAdicionalFactura da = new DetalleAdicionalFactura();
                    da.setDetalleFactura(det);
                    da.setNombre(detAdDTO.getNombre());
                    da.setValor(detAdDTO.getValor());
                    detalleAdicionalRepository.save(da);
                    detsAdicionales.add(da);
                }
                det.setDetallesAdicionales(detsAdicionales);
            }
            detalles.add(det);
        }
        factura.setDetalles(detalles);

        // Asignar Totales finales (Calculados Vs Enviados)
        factura.setSubtotal12(request.getSubtotal12() != null ? request.getSubtotal12() : calcSubtotal12);
        factura.setSubtotal0(request.getSubtotal0() != null ? request.getSubtotal0() : calcSubtotal0);
        factura.setSubtotalExento(request.getSubtotalExento() != null ? request.getSubtotalExento() : 0.0);
        factura.setSubtotalNoObjeto(request.getSubtotalNoObjeto() != null ? request.getSubtotalNoObjeto() : 0.0);
        factura.setTotalDescuento(
                request.getTotalDescuento() != null ? request.getTotalDescuento() : calcTotalDescuento);
        factura.setTotalIva(request.getTotalIva() != null ? request.getTotalIva() : calcTotalIva);

        Double totalFinal = factura.getSubtotal12() + factura.getSubtotal0() + factura.getSubtotalExento()
                + factura.getTotalIva() - factura.getTotalDescuento();
        factura.setTotalFactura(request.getTotalFactura() != null ? request.getTotalFactura() : totalFinal);

        // Guardar Factura con totales actualizados
        facturaRepository.save(factura);

        // Pagos
        if (request.getPagos() != null && !request.getPagos().isEmpty()) {
            List<FacturaPago> pagosList = new ArrayList<>();
            for (PagoDTO pdto : request.getPagos()) {
                FacturaPago fp = new FacturaPago();
                fp.setFactura(factura);

                // Soporte para ID vs Código (Si en el futuro usamos código, aquí iría la
                // lógica)
                // Usamos ID por ahora
                if (pdto.getFormaPagoId() != null) {
                    FormaPago fpago = formaPagoRepository.findById(pdto.getFormaPagoId())
                            .orElseThrow(() -> new RuntimeException("Forma de pago no encontrada."));
                    fp.setFormaPago(fpago);
                }

                // Auto-fill total si falta
                if (pdto.getTotal() == null) {
                    fp.setTotal(factura.getTotalFactura());
                } else {
                    fp.setTotal(pdto.getTotal());
                }

                fp.setPlazo(pdto.getPlazo());
                fp.setUnidadTiempo(pdto.getUnidadTiempo() != null ? pdto.getUnidadTiempo() : "Dias");
                facturaPagoRepository.save(fp);
                pagosList.add(fp);
            }
            factura.setPagos(pagosList);
        }

        return factura;
    }

    public Factura actualizarFactura(Factura factura) {
        return facturaRepository.save(factura);
    }

    public List<Factura> listar() {
        return facturaRepository.findAll();
    }

    public String generarClaveAcceso(Factura factura) {
        String fecha = factura.getFechaEmision().toLocalDate()
                .format(DateTimeFormatter.ofPattern("ddMMyyyy"));
        String tipoComprobante = "01";
        String ruc = factura.getEmpresa().getRuc();
        String ambiente = "1"; // Siempre 1

        // Padding Estricto
        String estab = String.format("%03d", Integer.parseInt(factura.getEmpresa().getEstablecimiento()));
        String ptoEmi = String.format("%03d", Integer.parseInt(factura.getEmpresa().getPuntoEmision()));
        String secuencial = String.format("%09d", Integer.parseInt(factura.getSecuencial()));

        String codigoNumerico = generarCodigoNumerico();
        String tipoEmision = "1";

        String claveSinDV = fecha + tipoComprobante + ruc + ambiente +
                estab + ptoEmi + secuencial + codigoNumerico + tipoEmision;

        String dv = calcularDigitoVerificador(claveSinDV);
        return claveSinDV + dv;
    }

    private String generarCodigoNumerico() {
        int numero = (int) (Math.random() * 99999999);
        return String.format("%08d", numero);
    }

    private String calcularDigitoVerificador(String cadena) {
        int factor = 2;
        int suma = 0;
        for (int i = cadena.length() - 1; i >= 0; i--) {
            suma += Character.getNumericValue(cadena.charAt(i)) * factor;
            factor++;
            if (factor > 7)
                factor = 2;
        }
        int dv = 11 - (suma % 11);
        if (dv == 11)
            return "0";
        if (dv == 10)
            return "1";
        return String.valueOf(dv);
    }

    public String generarXMLFactura(Factura factura) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();
            doc.setXmlStandalone(true); // Estándar SRI: standalone="yes"

            Element facturaEl = doc.createElement("factura");
            facturaEl.setAttribute("id", "comprobante");
            facturaEl.setAttribute("version", "1.1.0"); // Versión estándar actual
            doc.appendChild(facturaEl);

            // ================= INFO TRIBUTARIA =================
            Element infoTrib = doc.createElement("infoTributaria");
            facturaEl.appendChild(infoTrib);
            infoTrib.appendChild(add(doc, "ambiente", "1"));
            infoTrib.appendChild(add(doc, "tipoEmision", "1"));
            infoTrib.appendChild(add(doc, "razonSocial", factura.getEmpresa().getRazonSocial()));
            infoTrib.appendChild(add(doc, "nombreComercial", factura.getEmpresa().getNombreComercial()));
            infoTrib.appendChild(add(doc, "ruc", factura.getEmpresa().getRuc()));
            infoTrib.appendChild(add(doc, "claveAcceso", factura.getClaveAcceso()));
            infoTrib.appendChild(add(doc, "codDoc", "01"));
            infoTrib.appendChild(add(doc, "estab", factura.getEmpresa().getEstablecimiento()));
            infoTrib.appendChild(add(doc, "ptoEmi", factura.getEmpresa().getPuntoEmision()));
            infoTrib.appendChild(add(doc, "secuencial", factura.getSecuencial()));
            infoTrib.appendChild(add(doc, "dirMatriz", factura.getEmpresa().getDirEstablecimiento()));

            // ================= INFO FACTURA =================
            Element infoFac = doc.createElement("infoFactura");
            facturaEl.appendChild(infoFac);

            infoFac.appendChild(add(doc, "fechaEmision",
                    factura.getFechaEmision().toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
            infoFac.appendChild(add(doc, "dirEstablecimiento", factura.getEmpresa().getDirEstablecimiento()));

            // Obligado Contabilidad
            infoFac.appendChild(add(doc, "obligadoContabilidad", "NO"));

            // Determinar Tipo Identificación Comprador (04=RUC, 05=Cedula, 07=Consumidor
            // Final)
            String identComprador = factura.getCliente().getClienteTelefono();
            String tipoIdentificacion = "07";
            if (identComprador != null) {
                if (identComprador.length() == 13)
                    tipoIdentificacion = "04";
                else if (identComprador.length() == 10)
                    tipoIdentificacion = "05";
            }
            if ("9999999999999".equals(identComprador))
                tipoIdentificacion = "07";

            infoFac.appendChild(add(doc, "tipoIdentificacionComprador", tipoIdentificacion));
            infoFac.appendChild(add(doc, "razonSocialComprador", factura.getCliente().getClienteNombre()));
            infoFac.appendChild(add(doc, "identificacionComprador", identComprador));
            infoFac.appendChild(add(doc, "totalSinImpuestos",
                    String.format("%.2f", factura.getSubtotal12() + factura.getSubtotal0()).replace(",", ".")));
            infoFac.appendChild(
                    add(doc, "totalDescuento", String.format("%.2f", factura.getTotalDescuento()).replace(",", ".")));

            // Total Con Impuestos (Resumen)
            Element totalConImpuestos = doc.createElement("totalConImpuestos");
            infoFac.appendChild(totalConImpuestos);

            // Agrupar impuestos por código (Clave: CodigoPorcentaje, Valor: [Base,
            // ValorImpuesto])
            java.util.Map<String, double[]> impuestosAgrupados = new java.util.HashMap<>();

            for (DetalleFactura det : factura.getDetalles()) {
                if (det.getImpuesto() != null) {
                    String codPorc = det.getImpuesto().getCodigoPorcentaje();
                    double base = det.getImpuesto().getBaseImponible();
                    double val = det.getImpuesto().getValor();

                    impuestosAgrupados.putIfAbsent(codPorc, new double[] { 0.0, 0.0 });
                    double[] acumulado = impuestosAgrupados.get(codPorc);
                    acumulado[0] += base;
                    acumulado[1] += val;
                }
            }

            // Generar Bloques XML por cada código encontrado
            for (String codPorc : impuestosAgrupados.keySet()) {
                double[] valores = impuestosAgrupados.get(codPorc);
                if (valores[0] > 0 || valores[1] > 0) {
                    Element totalImpuesto = doc.createElement("totalImpuesto");
                    totalImpuesto.appendChild(add(doc, "codigo", "2")); // Código Impuesto 2 = IVA
                    totalImpuesto.appendChild(add(doc, "codigoPorcentaje", codPorc)); // 0, 2, 4...
                    totalImpuesto.appendChild(
                            add(doc, "baseImponible", String.format("%.2f", valores[0]).replace(",", ".")));
                    totalImpuesto.appendChild(add(doc, "valor", String.format("%.2f", valores[1]).replace(",", ".")));
                    totalConImpuestos.appendChild(totalImpuesto);
                }
            }

            infoFac.appendChild(add(doc, "propina", "0.00"));
            infoFac.appendChild(
                    add(doc, "importeTotal", String.format("%.2f", factura.getTotalFactura()).replace(",", ".")));
            infoFac.appendChild(add(doc, "moneda", "DOLAR"));

            // PAGOS
            Element pagos = doc.createElement("pagos");
            infoFac.appendChild(pagos);
            if (factura.getPagos() != null) {
                for (FacturaPago p : factura.getPagos()) {
                    Element pago = doc.createElement("pago");
                    pago.appendChild(add(doc, "formaPago", p.getFormaPago().getCodigoSri()));
                    pago.appendChild(add(doc, "total", String.format("%.2f", p.getTotal()).replace(",", ".")));
                    if (p.getPlazo() != null && p.getPlazo() > 0) {
                        pago.appendChild(add(doc, "plazo", String.valueOf(p.getPlazo())));
                        pago.appendChild(
                                add(doc, "unidadTiempo", p.getUnidadTiempo() != null ? p.getUnidadTiempo() : "Dias"));
                    }
                    pagos.appendChild(pago);
                }
            }

            // ================= DETALLES =================
            Element detallesEl = doc.createElement("detalles");
            facturaEl.appendChild(detallesEl);

            for (DetalleFactura det : factura.getDetalles()) {
                Element d = doc.createElement("detalle");
                d.appendChild(add(doc, "codigoPrincipal", det.getProducto().getProductoSerial()));
                d.appendChild(add(doc, "descripcion", det.getProducto().getProductoNombre()));
                d.appendChild(add(doc, "cantidad", String.format("%.2f", det.getCantidad()).replace(",", ".")));
                d.appendChild(
                        add(doc, "precioUnitario", String.format("%.2f", det.getPrecioUnitario()).replace(",", ".")));
                d.appendChild(add(doc, "descuento", String.format("%.2f", det.getDescuento()).replace(",", ".")));
                d.appendChild(
                        add(doc, "precioTotalSinImpuesto", String.format("%.2f", det.getSubtotal()).replace(",", ".")));

                // Detalles Adicionales (MUST BE BEFORE IMPUESTOS)
                if (det.getDetallesAdicionales() != null && !det.getDetallesAdicionales().isEmpty()) {
                    Element detallesAdicionalesEl = doc.createElement("detallesAdicionales");
                    for (DetalleAdicionalFactura da : det.getDetallesAdicionales()) {
                        Element detAd = doc.createElement("detAdicional");
                        detAd.setAttribute("nombre", da.getNombre());
                        detAd.setAttribute("valor", da.getValor());
                        detallesAdicionalesEl.appendChild(detAd);
                    }
                    d.appendChild(detallesAdicionalesEl);
                }

                Element impuestosDet = doc.createElement("impuestos");
                d.appendChild(impuestosDet);

                if (det.getImpuesto() != null) {
                    Element imp = doc.createElement("impuesto");
                    imp.appendChild(add(doc, "codigo", det.getImpuesto().getCodigo()));
                    imp.appendChild(add(doc, "codigoPorcentaje", det.getImpuesto().getCodigoPorcentaje()));
                    imp.appendChild(
                            add(doc, "tarifa", String.format("%.2f", det.getImpuesto().getTarifa()).replace(",", ".")));
                    imp.appendChild(add(doc, "baseImponible",
                            String.format("%.2f", det.getImpuesto().getBaseImponible()).replace(",", ".")));
                    imp.appendChild(
                            add(doc, "valor", String.format("%.2f", det.getImpuesto().getValor()).replace(",", ".")));
                    impuestosDet.appendChild(imp);
                }

                detallesEl.appendChild(d);
            }

            // ================= INFO ADICIONAL =================
            if (factura.getInfoAdicional() != null && !factura.getInfoAdicional().isEmpty()) {
                Element infoAdicionalEl = doc.createElement("infoAdicional");
                facturaEl.appendChild(infoAdicionalEl);
                for (CampoAdicionalFactura ca : factura.getInfoAdicional()) {
                    Element campoAdicional = doc.createElement("campoAdicional");
                    campoAdicional.setAttribute("nombre", ca.getNombre());
                    campoAdicional.appendChild(doc.createTextNode(ca.getValor()));
                    infoAdicionalEl.appendChild(campoAdicional);
                }
            }

            File carpeta = new File("C:\\facturaSRI");
            if (!carpeta.exists())
                carpeta.mkdirs();
            String ruta = "C:\\facturaSRI\\factura_" + factura.getClaveAcceso() + ".xml";
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.transform(new DOMSource(doc), new StreamResult(new File(ruta)));
            return ruta;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al generar XML: " + e.getMessage());
        }
    }

    private Element add(Document doc, String tag, Object value) {
        Element e = doc.createElement(tag);
        e.appendChild(doc.createTextNode(String.valueOf(value)));
        return e;
    }
}
