package com.sistemalp.facturacion.Security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.sistemalp.facturacion.Servicios.JwtService;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService; // Servicio para validar y extraer info del JWT (debes implementarlo)

    @Autowired
    private UserDetailsService userDetailsService; // Servicio para cargar usuario por username

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getServletPath();
        System.out.println("DEBUG JWT: Processing " + request.getMethod() + " request for path: " + path);

        if (path.startsWith("/auth/") || path.startsWith("/v3/") || path.startsWith("/swagger")
                || (path.startsWith("/api/productos") && "GET".equalsIgnoreCase(request.getMethod()))) {
            System.out.println("DEBUG JWT: Public path detected, skipping filter logic.");
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");
        String username = null;
        String jwtToken = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwtToken = authHeader.substring(7);
            try {
                username = jwtService.extractUsername(jwtToken);
                System.out.println("DEBUG JWT: Username extracted from token: " + username);
            } catch (Exception e) {
                System.out.println("DEBUG JWT: Error extracting username: " + e.getMessage());
                logger.error("Error al extraer username del JWT: " + e.getMessage());
            }
        } else {
            System.out.println("DEBUG JWT: No valid Auth header found. Header: " + authHeader);
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            System.out.println("DEBUG JWT: Loading UserDetails for: " + username);
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            if (jwtService.validarToken(jwtToken)) {
                System.out.println("DEBUG JWT: Token is valid.");
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);
                System.out.println("DEBUG JWT: Authentication set in SecurityContext.");
            } else {
                System.out.println("DEBUG JWT: Token validation failed.");
            }
        } else {
            if (username == null)
                System.out.println("DEBUG JWT: Username is null, skipping auth.");
            else
                System.out.println("DEBUG JWT: SecurityContext already authenticated.");
        }
        filterChain.doFilter(request, response);
    }

}
