package com.sistemalp.facturacion.Entidades;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class FormaPago { //entidad para guardar las formas de pago de la factura

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long formaPagoId;

    private String nombre;     // EJ: Tarjeta de crédito
    private String codigoSri;  // EJ: 19
}
