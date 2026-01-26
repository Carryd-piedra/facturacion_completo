package com.sistemalp.facturacion.Controladores;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sistemalp.facturacion.Dto.FacturaRequestDTO;
import com.sistemalp.facturacion.Entidades.Factura;
import com.sistemalp.facturacion.Servicios.FacturaServicio;
import com.sistemalp.facturacion.Servicios.FirmaElectronicaServicio;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/facturas")
@RequiredArgsConstructor
@CrossOrigin("*")
public class FacturaControlador {

    private final FacturaServicio facturaService;
    private final FirmaElectronicaServicio firmaService;

    @org.springframework.beans.factory.annotation.Value("${sri.firma.ruta}")
    private String firmaRuta;

    @org.springframework.beans.factory.annotation.Value("${sri.firma.clave}")
    private String firmaClave;

    @GetMapping
    public ResponseEntity<java.util.List<Factura>> listar() {
        return ResponseEntity.ok(facturaService.listar());
    }

    @PostMapping
    public ResponseEntity<?> crearFactura(@RequestBody FacturaRequestDTO request) {
        try {

            Factura factura = facturaService.crearFacturaCompleta(request);

            String claveAcceso = facturaService.generarClaveAcceso(factura);
            factura.setClaveAcceso(claveAcceso);
            facturaService.actualizarFactura(factura); // Guardar clave en BD

            String xmlSinFirma = facturaService.generarXMLFactura(factura);

            String xmlFirmado = firmaService.firmarXML(
                    xmlSinFirma,
                    firmaRuta,
                    firmaClave);

            return ResponseEntity.ok(new RespuestaFactura(
                    "Factura creada correctamente",
                    factura.getFacturaId(),
                    claveAcceso,
                    xmlSinFirma,
                    xmlFirmado));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(
                    "Error al crear factura: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/enviar")
    public ResponseEntity<?> enviarSRI(@PathVariable Long id) {
        try {
            String respuesta = facturaService.enviarFacturaSri(id);
            return ResponseEntity.ok(java.util.Collections.singletonMap("mensaje", respuesta));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Collections.singletonMap("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/autorizar")
    public ResponseEntity<?> autorizarSRI(@PathVariable Long id) {
        try {
            String respuesta = facturaService.verificarAutorizacionSRI(id);
            return ResponseEntity.ok(java.util.Collections.singletonMap("mensaje", respuesta));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Collections.singletonMap("error", e.getMessage()));
        }
    }

    @GetMapping(value = "/{id}/xml", produces = org.springframework.http.MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<?> obtenerXml(@PathVariable Long id) {
        try {
            String xmlContent = facturaService.obtenerXmlFactura(id);
            return ResponseEntity.ok(xmlContent);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Collections.singletonMap("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarFactura(@PathVariable Long id) {
        try {
            facturaService.eliminarFactura(id);
            return ResponseEntity.ok(java.util.Collections.singletonMap("mensaje", "Factura eliminada correctamente"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Collections.singletonMap("error", e.getMessage()));
        }
    }

    record RespuestaFactura(
            String mensaje,
            Long facturaId,
            String claveAcceso,
            String xmlSinFirma,
            String xmlFirmado) {
    }
}
