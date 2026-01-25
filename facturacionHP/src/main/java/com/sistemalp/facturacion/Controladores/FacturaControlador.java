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

    record RespuestaFactura(
            String mensaje,
            Long facturaId,
            String claveAcceso,
            String xmlSinFirma,
            String xmlFirmado) {
    }
}
