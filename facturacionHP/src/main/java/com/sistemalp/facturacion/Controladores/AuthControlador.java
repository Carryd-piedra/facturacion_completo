package com.sistemalp.facturacion.Controladores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import com.sistemalp.facturacion.Dto.JwtResponse;
import com.sistemalp.facturacion.Dto.LoginRequest;
import com.sistemalp.facturacion.Entidades.Usuario;
import com.sistemalp.facturacion.Servicios.JwtService;
import com.sistemalp.facturacion.Servicios.UsuarioServicio;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/auth")
public class AuthControlador {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UsuarioServicio usuarioService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            User user = (User) authentication.getPrincipal();
            Usuario usuario = usuarioService.findByUsername(user.getUsername());
            String token = jwtService.generateToken(usuario.getUsername());

            return ResponseEntity.ok(new JwtResponse(token, usuario.getUsername()));

        } catch (BadCredentialsException e) {
            // Usuario o contraseña incorrectos
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Usuario o contraseña incorrectos");
        } catch (Exception e) {
            // Otro error (por ejemplo, error interno del servidor)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al iniciar sesión: " + e.getMessage());
        }
    }
}
