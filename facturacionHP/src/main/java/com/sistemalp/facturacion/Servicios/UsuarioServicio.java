package com.sistemalp.facturacion.Servicios;

import java.util.List;

import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sistemalp.facturacion.Entidades.Usuario;
import com.sistemalp.facturacion.Repositorios.UsuarioRespositorio;

@Service
public class UsuarioServicio {
    @Autowired
    private UsuarioRespositorio usuarioRespositorio;

    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;

    public Usuario guardar(Usuario usuario) {
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        return usuarioRespositorio.save(usuario);
    }

    public List<Usuario> listar() {
        return usuarioRespositorio.findAll();
    }

    public Usuario listaUsuario(Long id) {
        return usuarioRespositorio.findById(id).orElse(null);
    }

    public Usuario findByUsername(String username) {
        return usuarioRespositorio.findByUsername(username).orElse(null);
    }

    public void eliminar(Long id) {
        usuarioRespositorio.deleteById(id);
    }
}
