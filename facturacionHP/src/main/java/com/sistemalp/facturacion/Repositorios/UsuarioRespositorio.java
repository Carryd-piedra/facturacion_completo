package com.sistemalp.facturacion.Repositorios;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sistemalp.facturacion.Entidades.Usuario;
@Repository
public interface UsuarioRespositorio extends JpaRepository<Usuario,Long>{
    Optional<Usuario> findByUsername(String username);
    boolean existsByUsername(String username);
}
