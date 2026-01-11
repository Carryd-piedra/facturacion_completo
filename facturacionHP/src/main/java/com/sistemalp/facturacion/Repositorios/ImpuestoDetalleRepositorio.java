package com.sistemalp.facturacion.Repositorios;


import org.springframework.data.jpa.repository.JpaRepository;

import com.sistemalp.facturacion.Entidades.ImpuestoDetalle;

public interface ImpuestoDetalleRepositorio extends JpaRepository<ImpuestoDetalle, Long> {
    
}
