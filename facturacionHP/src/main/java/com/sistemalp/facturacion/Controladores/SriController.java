package com.sistemalp.facturacion.Controladores;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sistemalp.facturacion.Servicios.SriAutorizacionService;
import com.sistemalp.facturacion.Servicios.SriRecepcionService;

@RestController
@RequestMapping("/api/sri")
public class SriController {

    private final SriRecepcionService recepcionService;
    private final SriAutorizacionService autorizacionService;
    private final com.sistemalp.facturacion.Repositorios.FacturaRepositorio facturaRepository;

    // Imports manuales para evitar errores si no están arriba
    // com.sistemalp.facturacion.Servicios.PdfGenerator
    // com.sistemalp.facturacion.Servicios.FacturaHtmlBuilder

    public SriController(SriRecepcionService recepcionService,
            SriAutorizacionService autorizacionService,
            com.sistemalp.facturacion.Repositorios.FacturaRepositorio facturaRepository) {
        this.recepcionService = recepcionService;
        this.autorizacionService = autorizacionService;
        this.facturaRepository = facturaRepository;
    }

    @PostMapping("/enviar")
    public ResponseEntity<?> enviar(@RequestParam String ruta) {
        try {
            // 1. Enviar a Recepción
            String resultadoRecepcion = recepcionService.enviarFactura(ruta);

            // 2. Si es RECIBIDA, intentar Autorizar automáticamente
            if (resultadoRecepcion.contains("RECIBIDA")) {

                // Extraer clave de acceso del nombre del archivo (factura_CLAVE_firmado.xml)
                // Ex:
                // "C:\facturaSRI\factura_0123456789012345678901234567890123456789012345678_firmado.xml"
                String nombreArchivo = new java.io.File(ruta).getName();
                String claveAcceso = nombreArchivo.replace("factura_", "").replace("_firmado.xml", "").replace(".xml",
                        "");

                // Buscar factura
                com.sistemalp.facturacion.Entidades.Factura factura = facturaRepository.findByClaveAcceso(claveAcceso)
                        .orElse(null);

                if (factura != null) {

                    // --- NUEVO: Generar PDF inmediatamente (Solicitud Usuario) ---
                    try {
                        String numAuth = factura.getClaveAcceso(); // Provisional: Usar Clave como NumAuth
                        String fechaAuth = java.time.LocalDateTime.now().toString();

                        String html = com.sistemalp.facturacion.Servicios.FacturaHtmlBuilder.generarHtmlFactura(factura,
                                numAuth, fechaAuth);

                        java.io.File dir = new java.io.File("C:\\facturas_pdf");
                        if (!dir.exists())
                            dir.mkdirs();

                        String rutaPdf = "C:\\facturas_pdf\\factura_" + factura.getClaveAcceso() + ".pdf";
                        com.sistemalp.facturacion.Servicios.PdfGenerator.generarPDF(html, rutaPdf);
                        System.out.println("PDF Generado en Recepción: " + rutaPdf);

                    } catch (Exception e) {
                        e.printStackTrace();
                        System.err.println("Error generando PDF en Recepción: " + e.getMessage());
                    }
                    // -------------------------------------------------------------

                    // Llamar a Autorización (esto generará el PDF si se autoriza)
                    String resultadoAutorizacion = "Pendiente / No Respondio";
                    try {
                        resultadoAutorizacion = autorizacionService.consultarAutorizacion(claveAcceso, factura);
                    } catch (Exception e) {
                        resultadoAutorizacion = "Sin respuesta SRI Autorización: " + e.getMessage();
                    }

                    return ResponseEntity.ok(Map.of(
                            "recepcion", resultadoRecepcion,
                            "autorizacion", resultadoAutorizacion,
                            "mensaje", "PDF generado tras Recepción. Autorización: " + resultadoAutorizacion));
                }
            }

            return ResponseEntity.ok(Map.of("resultado", resultadoRecepcion));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/autorizar")
    public ResponseEntity<?> autorizar(@RequestParam String claveAcceso) {
        try {
            com.sistemalp.facturacion.Entidades.Factura factura = facturaRepository.findByClaveAcceso(claveAcceso)
                    .orElseThrow(() -> new RuntimeException("Factura no encontrada para clave: " + claveAcceso));

            String resultado = autorizacionService.consultarAutorizacion(claveAcceso, factura);
            return ResponseEntity.ok(Map.of("resultado", resultado));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
