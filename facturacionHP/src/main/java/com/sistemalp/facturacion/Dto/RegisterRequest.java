package com.sistemalp.facturacion.Dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String nombre;
    private String email;
    private String rol;
}
