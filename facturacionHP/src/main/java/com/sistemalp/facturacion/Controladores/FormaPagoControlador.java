package com.sistemalp.facturacion.Controladores;

import java.util.List;

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
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        formaPagoService.eliminar(id);
        return ResponseEntity.ok("Forma de pago eliminada correctamente");
    }
}

