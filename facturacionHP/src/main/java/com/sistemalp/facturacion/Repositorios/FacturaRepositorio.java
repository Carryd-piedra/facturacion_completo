package com.sistemalp.facturacion.Repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sistemalp.facturacion.Entidades.Factura;

@Repository
public interface FacturaRepositorio extends JpaRepository<Factura, Long> {

    java.util.Optional<Factura> findByClaveAcceso(String claveAcceso);

    boolean existsByCliente(com.sistemalp.facturacion.Entidades.Cliente cliente);

    @org.springframework.data.jpa.repository.Query("SELECT MAX(f.secuencial) FROM Factura f WHERE f.estab = :estab AND f.ptoEmi = :ptoEmi")
    String findMaxSecuencial(@org.springframework.data.repository.query.Param("estab") String estab,
            @org.springframework.data.repository.query.Param("ptoEmi") String ptoEmi);

}
