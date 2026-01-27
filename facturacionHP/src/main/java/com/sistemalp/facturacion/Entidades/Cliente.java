package com.sistemalp.facturacion.Entidades;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter

//Clase que representa el cliente
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long clienteId;

    @NotBlank(message = "El nombre no puede estar vacio")
    @Size(min = 3, max = 200, message = "El nombre debe tener entre 3 a 200 caracteres")
    @Column(length = 200, nullable = false)
    private String clienteNombre;
    private String clienteDirecion;
    private String clienteAplellido;
    @Pattern(regexp = "^[0-9]{10}$", message = "El numero de telefono debe tener 10 digitos")
    private String clienteTelefono;
    @Email(message = "El correo no es valido")
    private String clienteMail;
    private Integer clienteEstado;
}