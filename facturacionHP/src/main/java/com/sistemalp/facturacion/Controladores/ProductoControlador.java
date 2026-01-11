package com.sistemalp.facturacion.Controladores;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sistemalp.facturacion.Entidades.Producto;
import com.sistemalp.facturacion.Servicios.ProductoServicio;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ProductoControlador {

    private final ProductoServicio productoService;

    @GetMapping
    public ResponseEntity<List<Producto>> listar() {
        return ResponseEntity.ok(productoService.listarProductos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.obtenerProductoPorId(id));
    }

    @PostMapping
    public ResponseEntity<Producto> crear(@RequestBody Producto producto) {
        return ResponseEntity.ok(productoService.crearProducto(producto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizar(
            @PathVariable Long id,
            @RequestBody Producto producto) {
        return ResponseEntity.ok(productoService.actualizarProducto(id, producto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            productoService.eliminarProducto(id);
            return ResponseEntity.ok(Map.of("mensaje", "Producto eliminado correctamente"));
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("mensaje",
                            "No se puede eliminar el producto porque está siendo utilizado en facturas."));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("mensaje", "Error al eliminar: " + e.getMessage()));
        }
    }
}
