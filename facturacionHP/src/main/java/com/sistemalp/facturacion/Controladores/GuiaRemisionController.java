package com.sistemalp.facturacion.Controladores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sistemalp.facturacion.Servicios.GuiaRemisionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/guia-remision")
@Tag(name = "Guía de Remisión", description = "Endpoints para gestión de Guías de Remisión")
public class GuiaRemisionController {

    @Autowired
    private GuiaRemisionService guiaRemisionService;

    @GetMapping(value = "/xml/ejemplo", produces = MediaType.APPLICATION_XML_VALUE)
    @Operation(summary = "Generar XML de ejemplo", description = "Genera un XML de Guía de Remisión con datos de prueba")
    public ResponseEntity<String> generarXmlEjemplo() {
        try {
            String xml = guiaRemisionService.generarXmlEjemplo();
            return ResponseEntity.ok(xml);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al generar XML: " + e.getMessage());
        }
    }
}
