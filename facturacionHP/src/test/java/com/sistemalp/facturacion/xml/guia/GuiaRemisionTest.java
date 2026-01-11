package com.sistemalp.facturacion.xml.guia;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;

public class GuiaRemisionTest {

    @Test
    public void testXmlGeneration() throws Exception {
        GuiaRemision guia = new GuiaRemision();
        guia.setId("comprobante");
        guia.setVersion("1.0.0");

        // InfoTributaria
        InfoTributaria infoTrib = new InfoTributaria();
        infoTrib.setAmbiente("1");
        infoTrib.setTipoEmision("1");
        infoTrib.setRazonSocial("Distribuidora de Suministros Nacional S.A.");
        infoTrib.setNombreComercial("Empresa Importadora y Exportadora de Piezas y Partes de Equipos de Oficina");
        infoTrib.setRuc("1792146739001");
        infoTrib.setClaveAcceso("2110201106179214673900100110020010000000011234567815");
        infoTrib.setCodDoc("06");
        infoTrib.setEstab("002");
        infoTrib.setPtoEmi("001");
        infoTrib.setSecuencial("000000001");
        infoTrib.setDirMatriz("Enrique Guerrero Portilla OE1-34 AV. Galo Plaza Lasso");
        guia.setInfoTributaria(infoTrib);

        // InfoGuiaRemision
        InfoGuiaRemision infoGuia = new InfoGuiaRemision();
        infoGuia.setDirEstablecimiento("Sebastián Moreno S/N Francisco García");
        infoGuia.setDirPartida("Av. Eloy Alfaro 34 y Av. Libertad Esq.");
        infoGuia.setRazonSocialTransportista("Transportes S.A.");
        infoGuia.setTipoIdentificacionTransportista("04");
        infoGuia.setRucTransportista("1796875790001");
        infoGuia.setRise("Contribuyente Regimen Simplificado RISE");
        infoGuia.setObligadoContabilidad("SI");
        infoGuia.setContribuyenteEspecial("5368");
        infoGuia.setFechaIniTransporte("21/10/2011");
        infoGuia.setFechaFinTransporte("22/10/2011");
        infoGuia.setPlaca("MCL0827");
        guia.setInfoGuiaRemision(infoGuia);

        // Destinatarios
        List<Destinatario> destinatarios = new ArrayList<>();
        Destinatario dest = new Destinatario();
        dest.setIdentificacionDestinatario("1716849140001");
        dest.setRazonSocialDestinatario("Alvarez Mina John Henry");
        dest.setDirDestinatario("Av. Simón Bolívar S/N Intercambiador");
        dest.setMotivoTraslado("Venta de Maquinaria de Impresión");
        dest.setDocAduaneroUnico("0041324846887");
        dest.setCodEstabDestino("001");
        dest.setRuta("Quito – Cayambe - Otavalo");
        dest.setCodDocSustento("01");
        dest.setNumDocSustento("002-001-000000001");
        dest.setNumAutDocSustento("2110201116302517921467390011234567891");
        dest.setFechaEmisionDocSustento("21/10/2011");

        // Detalles
        List<Detalle> detalles = new ArrayList<>();
        Detalle detalle = new Detalle();
        detalle.setCodigoInterno("125BJC-01");
        detalle.setCodigoAdicional("1234D56789-A");
        detalle.setDescripcion("CAMIONETA 4X4 DIESEL 3.7");
        detalle.setCantidad(new BigDecimal("10.00"));

        // Detalles Adicionales
        List<DetAdicional> detAdicionales = new ArrayList<>();
        detAdicionales.add(new DetAdicional("Marca", "Chevrolet"));
        detAdicionales.add(new DetAdicional("Modelo", "2012"));
        detAdicionales.add(new DetAdicional("Chasis", "8LDETA03V20003289"));
        detalle.setDetallesAdicionales(detAdicionales);

        detalles.add(detalle);
        dest.setDetalles(detalles);
        destinatarios.add(dest);
        guia.setDestinatarios(destinatarios);

        // InfoAdicional
        List<CampoAdicional> infoAdicional = new ArrayList<>();
        infoAdicional.add(new CampoAdicional("TELEFONO", "098568541"));
        infoAdicional.add(new CampoAdicional("E-MAIL", "info@organizacion.com"));
        infoAdicional.add(new CampoAdicional("SUCURSAL 03", "Guayaquil–12 de Octubre y Universo"));
        guia.setInfoAdicional(infoAdicional);

        // Marshalling
        JAXBContext context = JAXBContext.newInstance(GuiaRemision.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

        StringWriter sw = new StringWriter();
        marshaller.marshal(guia, sw);

        System.out.println("XML GENERADO:");
        System.out.println(sw.toString());
    }
}
