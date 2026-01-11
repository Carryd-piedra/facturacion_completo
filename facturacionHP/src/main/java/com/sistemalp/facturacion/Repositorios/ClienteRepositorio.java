package com.sistemalp.facturacion.Repositorios;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sistemalp.facturacion.Entidades.Cliente;
@Repository
public interface ClienteRepositorio extends JpaRepository<Cliente,Long>{
 
}
