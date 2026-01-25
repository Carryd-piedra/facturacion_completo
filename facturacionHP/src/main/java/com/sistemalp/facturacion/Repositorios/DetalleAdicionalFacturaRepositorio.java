package com.sistemalp.facturacion.Repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sistemalp.facturacion.Entidades.DetalleAdicionalFactura;

@Repository
public interface DetalleAdicionalFacturaRepositorio extends JpaRepository<DetalleAdicionalFactura, Long> {

}
