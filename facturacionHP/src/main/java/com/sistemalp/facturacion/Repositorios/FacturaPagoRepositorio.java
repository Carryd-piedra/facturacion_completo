package com.sistemalp.facturacion.Repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sistemalp.facturacion.Entidades.FacturaPago;

//interfaz que sirve para definir el repositorio de la factura de pago
//y permite hacer consultas a la base de datos
@Repository
public interface FacturaPagoRepositorio extends JpaRepository<FacturaPago, Long> {

}

