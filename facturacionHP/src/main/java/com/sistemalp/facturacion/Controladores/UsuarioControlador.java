package com.sistemalp.facturacion.Controladores;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sistemalp.facturacion.Dto.RegisterRequest;
import com.sistemalp.facturacion.Entidades.Usuario;
import com.sistemalp.facturacion.Servicios.UsuarioServicio;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioControlador {

    @Autowired
    private UsuarioServicio usuarioServicio;

    @Autowired
    private com.sistemalp.facturacion.Repositorios.TipoUsuarioRepositorio tipoUsuarioRepositorio;

    @GetMapping
    public List<Usuario> listar() {
        return usuarioServicio.listar();
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody RegisterRequest request) {
        if (usuarioServicio.findByUsername(request.getUsername()) != null) {
            return ResponseEntity.badRequest().body("El nombre de usuario ya existe");
        }

        try {
            Usuario usuario = new Usuario();
            usuario.setUsername(request.getUsername());
            usuario.setPassword(request.getPassword()); // UsuarioServicio se encarga de hashear
            usuario.setNombre(request.getNombre());

            // Asignar email si existe en la entidad, si no, ignorar o concatenar?
            // La entidad Usuario tiene campo 'correo'.
            usuario.setCorreo(request.getEmail());

            usuario.setEstaActivo("1"); // Por defecto activo

            // Asignar Rol
            String rolNombre = request.getRol() != null ? request.getRol() : "Vendedor"; // Default
            com.sistemalp.facturacion.Entidades.TipoUsuario tipoUsuario = tipoUsuarioRepositorio.findByRol(rolNombre)
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + rolNombre));

            usuario.setTipoUsuario(tipoUsuario);

            Usuario nuevoUsuario = usuarioServicio.guardar(usuario);
            return ResponseEntity.ok(nuevoUsuario);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al crear usuario: " + e.getMessage());
        }
    }

    @org.springframework.web.bind.annotation.PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody RegisterRequest request) {
        Usuario usuario = usuarioServicio.listaUsuario(id);
        if (usuario == null)
            return ResponseEntity.notFound().build();

        // Validar username único si cambió
        Usuario uExistente = usuarioServicio.findByUsername(request.getUsername());
        if (uExistente != null && !uExistente.getId().equals(id)) {
            return ResponseEntity.badRequest().body("El nombre de usuario ya está en uso");
        }

        usuario.setNombre(request.getNombre());
        usuario.setCorreo(request.getEmail());
        usuario.setUsername(request.getUsername());

        // Actualizar Rol
        String rolNombre = request.getRol() != null ? request.getRol() : "Vendedor";
        com.sistemalp.facturacion.Entidades.TipoUsuario tipoUsuario = tipoUsuarioRepositorio.findByRol(rolNombre)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
        usuario.setTipoUsuario(tipoUsuario);

        // Actualizar Password solo si viene en el request
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            usuario.setPassword(request.getPassword());
            usuarioServicio.guardar(usuario); // Encripta y guarda
        } else {
            usuarioServicio.actualizar(usuario); // Guarda sin encriptar (mantiene el hash anterior)
        }

        return ResponseEntity.ok(usuario);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            usuarioServicio.eliminar(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al eliminar usuario: " + e.getMessage());
        }
    }
}
