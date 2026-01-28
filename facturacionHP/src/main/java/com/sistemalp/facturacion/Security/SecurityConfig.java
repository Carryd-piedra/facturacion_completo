package com.sistemalp.facturacion.Security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**", "/api/auth/**", "/v3/**", "/swagger-ui/**").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/productos/**").permitAll()

                        // Permisos VENDEDOR y ADMIN
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/facturas/enviadas")
                        .hasAnyAuthority("Admin", "Contador")
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/facturas/{id}/enviar")
                        .hasAnyAuthority("Admin", "Vendedor")
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/facturas/{id}/anular")
                        .hasAnyAuthority("Admin", "Vendedor")
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/facturas/**")
                        .hasAnyAuthority("Admin", "Vendedor")
                        .requestMatchers("/api/facturas/**").hasAnyAuthority("Admin", "Vendedor")
                        .requestMatchers("/api/cliente/**").hasAnyAuthority("Admin", "Vendedor")
                        .requestMatchers("/api/tipodocumento/**").hasAnyAuthority("Admin", "Vendedor")
                        .requestMatchers("/api/empresas/**").hasAnyAuthority("Admin", "Vendedor")
                        .requestMatchers("/api/formapagos/**").hasAnyAuthority("Admin", "Vendedor")

                        // Permisos CONTADOR y ADMIN
                        .requestMatchers("/api/reporte/**").hasAnyAuthority("Admin", "Contador")
                        .requestMatchers("/api/dashboard/**").hasAnyAuthority("Admin", "Contador", "Vendedor") // Si
                                                                                                               // existe
                                                                                                               // endpoint
                        // dashboard
                        .requestMatchers("/api/usuarios/**").hasAuthority("Admin")

                        // Admin tiene acceso a todo lo demas por defecto si se configuran mas rutas
                        // Pero para seguridad explicita, permitimos todo al Admin
                        .requestMatchers("/api/**").hasAuthority("Admin")

                        .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
                        .anyRequest().authenticated())
                .cors(org.springframework.security.config.Customizer.withDefaults());

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // metodo que configura el password encoder(se encarga de encriptar y
    // desencriptar contraseñas)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    // metodo que configura la cors, es decir que permite peticiones de cualquier
    // origen del navegador
    // pero solo para peticiones GET, POST, PUT, DELETE y OPTIONS
    public CorsConfigurationSource corsConfigurationSource() {
        org.springframework.web.cors.CorsConfiguration configuration = new org.springframework.web.cors.CorsConfiguration();
        configuration.setAllowedOriginPatterns(java.util.Collections.singletonList("*"));
        configuration.setAllowedMethods(java.util.Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(java.util.Collections.singletonList("*"));
        configuration.setAllowCredentials(true);

        org.springframework.web.cors.UrlBasedCorsConfigurationSource source = new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
