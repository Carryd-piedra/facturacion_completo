package com.sistemalp.facturacion.Controladores;

import java.util.List;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sistemalp.facturacion.Dto.FormaPagoDTO;
import com.sistemalp.facturacion.Entidades.FormaPago;
import com.sistemalp.facturacion.Servicios.FormaPagoServicio;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/formapagos")
@RequiredArgsConstructor
@CrossOrigin("*")
public class FormaPagoControlador {

    private final FormaPagoServicio formaPagoService;

    @GetMapping
    public ResponseEntity<List<FormaPago>> listar() {
        return ResponseEntity.ok(formaPagoService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FormaPago> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(formaPagoService.obtener(id));
    }

    @PostMapping
    public ResponseEntity<FormaPago> crear(@RequestBody FormaPagoDTO dto) {
        return ResponseEntity.ok(formaPagoService.crear(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FormaPago> actualizar(@PathVariable Long id, @RequestBody FormaPagoDTO dto) {
        return ResponseEntity.ok(formaPagoService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            formaPagoService.eliminar(id);
            return ResponseEntity.ok(Map.of("mensaje", "Forma de pago eliminada correctamente"));
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("mensaje",
                            "No se puede eliminar la forma de pago porque está siendo utilizada en facturas."));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("mensaje", "Error al eliminar: " + e.getMessage()));
        }
    }
}
