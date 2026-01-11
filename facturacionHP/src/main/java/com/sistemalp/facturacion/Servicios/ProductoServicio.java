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
        if (productoRepository.existsByProductoSerial(producto.getProductoSerial())) {
            throw new RuntimeException("El serial del producto ya existe");
        }
        return productoRepository.save(producto);
    }

 
    public Producto actualizarProducto(Long id, Producto productoActualizado) {
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
}
