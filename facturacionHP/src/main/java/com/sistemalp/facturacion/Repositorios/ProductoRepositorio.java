package com.sistemalp.facturacion.Repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sistemalp.facturacion.Entidades.Producto;
@Repository
public interface ProductoRepositorio extends JpaRepository<Producto,Long>{
    boolean existsByProductoSerial(String productoSerial);
}
