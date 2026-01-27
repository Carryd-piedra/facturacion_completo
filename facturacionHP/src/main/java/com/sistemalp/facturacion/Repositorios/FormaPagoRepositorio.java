package com.sistemalp.facturacion.Repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import com.sistemalp.facturacion.Entidades.FormaPago;

//interfaz  sirve generalmente para definir el repositorio
// (repositorio es una capa que permite hacer consultas a la base de datos) de una entidad


public interface FormaPagoRepositorio extends JpaRepository<FormaPago, Long> {

    boolean existsByCodigoSri(String codigoSri);
}
