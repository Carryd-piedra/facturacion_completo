package com.sistemalp.facturacion.Repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sistemalp.facturacion.Entidades.FacturaPago;

@Repository
public interface FacturaPagoRepositorio extends JpaRepository<FacturaPago, Long> {

}

