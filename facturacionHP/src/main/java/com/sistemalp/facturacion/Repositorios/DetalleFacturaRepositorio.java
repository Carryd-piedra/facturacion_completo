package com.sistemalp.facturacion.Repositorios;
import org.springframework.data.jpa.repository.JpaRepository;

import com.sistemalp.facturacion.Entidades.DetalleFactura;

public interface DetalleFacturaRepositorio extends JpaRepository<DetalleFactura, Long> {
    
}