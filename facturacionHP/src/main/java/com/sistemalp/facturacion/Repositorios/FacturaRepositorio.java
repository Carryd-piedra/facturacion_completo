package com.sistemalp.facturacion.Repositorios;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sistemalp.facturacion.Entidades.Factura;
@Repository
public interface FacturaRepositorio extends JpaRepository<Factura,Long>{

    

}
