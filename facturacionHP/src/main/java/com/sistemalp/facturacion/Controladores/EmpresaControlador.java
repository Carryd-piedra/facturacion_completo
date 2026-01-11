package com.sistemalp.facturacion.Controladores;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sistemalp.facturacion.Dto.EmpresaDTO;
import com.sistemalp.facturacion.Entidades.Empresa;
import com.sistemalp.facturacion.Servicios.EmpresaServicio;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/empresas")
@RequiredArgsConstructor
@CrossOrigin("*")
public class EmpresaControlador {

    private final EmpresaServicio empresaService;

    @GetMapping
    public ResponseEntity<List<Empresa>> listar() {
        return ResponseEntity.ok(empresaService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Empresa> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(empresaService.obtener(id));
    }

    @PostMapping
    public ResponseEntity<Empresa> crear(@RequestBody EmpresaDTO dto) {
        return ResponseEntity.ok(empresaService.crear(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Empresa> actualizar(@PathVariable Long id, @RequestBody EmpresaDTO dto) {
        return ResponseEntity.ok(empresaService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        empresaService.eliminar(id);
        return ResponseEntity.ok("Empresa eliminada correctamente");
    }
}
