package com.sistemalp.facturacion.Controladores;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping
    public ResponseEntity<List<Factura>> listar() {
        return ResponseEntity.ok(facturaService.listar());
    }

    @PostMapping
    public ResponseEntity<?> crearFactura(@RequestBody FacturaRequestDTO request) {
        try {

            Factura factura = facturaService.crearFacturaCompleta(request);

            String claveAcceso = facturaService.generarClaveAcceso(factura);
            factura.setClaveAcceso(claveAcceso);

            String xmlSinFirma = facturaService.generarXMLFactura(factura);

            String xmlFirmado = firmaService.firmarXML(
                    xmlSinFirma,
                    factura.getEmpresa().getRutaFirma(),
                    factura.getEmpresa().getClaveFirma());

            return ResponseEntity.ok(new RespuestaFactura(
                    "Factura creada correctamente",
                    factura.getFacturaId(),
                    claveAcceso,
                    xmlSinFirma,
                    xmlFirmado));

        } catch (Exception e) {
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
