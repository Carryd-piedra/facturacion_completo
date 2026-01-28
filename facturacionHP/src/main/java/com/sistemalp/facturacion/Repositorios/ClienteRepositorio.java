package com.sistemalp.facturacion.Repositorios;

//interfaz que sirve para definir el repositorio del cliente
//lo que hace un repositorio es que permite hacer consultas a la base de datos
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sistemalp.facturacion.Entidades.Cliente;
import com.sistemalp.facturacion.Entidades.TipoDocumento;

@Repository
public interface ClienteRepositorio extends JpaRepository<Cliente,Long>{
  

}
