package com.sistemalp.facturacion.Entidades;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productoId;
    private String productoSerial;
    private String productoNombre;
    private Double productoPrecio;
    private Double productoStock;
    private Double productoTasa;
    private Integer productoEstado;
    private String productoCategoria;
}
