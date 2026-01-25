package com.sistemalp.facturacion.Repositorios;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sistemalp.facturacion.Entidades.Cliente;
import com.sistemalp.facturacion.Entidades.TipoDocumentoCliente;
@Repository
public interface TipoDocumentoClienteRepositorio extends JpaRepository<TipoDocumentoCliente,String>{
    List<TipoDocumentoCliente> findByCliente(Cliente cliente);
}
