package com.sistemalp.facturacion.Servicios;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sistemalp.facturacion.Entidades.Producto;
import com.sistemalp.facturacion.Repositorios.ProductoRepositorio;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductoServicio {

    private final ProductoRepositorio productoRepository;

    public List<Producto> listarProductos() {
        return productoRepository.findAll();
    }

    public Producto obtenerProductoPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
    }

    public Producto crearProducto(Producto producto) {
        validarProducto(producto);
        if (productoRepository.existsByProductoSerial(producto.getProductoSerial())) {
            throw new RuntimeException("El serial del producto ya existe");
        }
        return productoRepository.save(producto);
    }

    public Producto actualizarProducto(Long id, Producto productoActualizado) {
        validarProducto(productoActualizado);
        Producto existente = obtenerProductoPorId(id);

        existente.setProductoSerial(productoActualizado.getProductoSerial());
        existente.setProductoNombre(productoActualizado.getProductoNombre());
        existente.setProductoPrecio(productoActualizado.getProductoPrecio());
        existente.setProductoStock(productoActualizado.getProductoStock());
        existente.setProductoTasa(productoActualizado.getProductoTasa());
        existente.setProductoEstado(productoActualizado.getProductoEstado());
        existente.setProductoCategoria(productoActualizado.getProductoCategoria());

        return productoRepository.save(existente);
    }

    public void eliminarProducto(Long id) {
        Producto existente = obtenerProductoPorId(id);
        productoRepository.delete(existente);
    }

    private void validarProducto(Producto p) {
        // Nombre (Max 40)
        if (p.getProductoNombre() == null || p.getProductoNombre().trim().isEmpty()) {
            throw new RuntimeException("{\"campo\": \"productoNombre\", \"motivo\": \"El nombre es obligatorio\"}");
        }
        if (p.getProductoNombre().length() > 40) {
            throw new RuntimeException(
                    "{\"campo\": \"productoNombre\", \"motivo\": \"El nombre no puede exceder 40 caracteres\"}");
        }

        // Serial (Max 10)
        if (p.getProductoSerial() == null || p.getProductoSerial().trim().isEmpty()) {
            throw new RuntimeException("{\"campo\": \"productoSerial\", \"motivo\": \"El serial es obligatorio\"}");
        }
        if (p.getProductoSerial().length() > 10) {
            throw new RuntimeException(
                    "{\"campo\": \"productoSerial\", \"motivo\": \"El serial no puede exceder 10 caracteres\"}");
        }

        // Categoría (Max 30, Solo letras)
        if (p.getProductoCategoria() == null || p.getProductoCategoria().trim().isEmpty()) {
            throw new RuntimeException(
                    "{\"campo\": \"productoCategoria\", \"motivo\": \"La categoría es obligatoria\"}");
        }
        if (p.getProductoCategoria().length() > 30) {
            throw new RuntimeException(
                    "{\"campo\": \"productoCategoria\", \"motivo\": \"La categoría no puede exceder 30 caracteres\"}");
        }
        if (!p.getProductoCategoria().matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")) {
            throw new RuntimeException(
                    "{\"campo\": \"productoCategoria\", \"motivo\": \"La categoría solo puede contener letras\"}");
        }
    }
}
