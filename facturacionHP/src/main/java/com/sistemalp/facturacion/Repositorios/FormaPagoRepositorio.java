package com.sistemalp.facturacion.Repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import com.sistemalp.facturacion.Entidades.FormaPago;

public interface FormaPagoRepositorio extends JpaRepository<FormaPago, Long> {

    boolean existsByCodigoSri(String codigoSri);
}
