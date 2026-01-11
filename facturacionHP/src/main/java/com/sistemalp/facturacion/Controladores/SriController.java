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

    public SriController(SriRecepcionService recepcionService,
                         SriAutorizacionService autorizacionService) {
        this.recepcionService = recepcionService;
        this.autorizacionService = autorizacionService;
    }

    @PostMapping("/enviar")
    public ResponseEntity<?> enviar(@RequestParam String ruta) {
        try {
            String resultado = recepcionService.enviarFactura(ruta);
            return ResponseEntity.ok(Map.of("resultado", resultado));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/autorizar")
    public ResponseEntity<?> autorizar(@RequestParam String claveAcceso) {
        try {
            String resultado = autorizacionService.consultarAutorizacion(claveAcceso);
            return ResponseEntity.ok(Map.of("resultado", resultado));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
