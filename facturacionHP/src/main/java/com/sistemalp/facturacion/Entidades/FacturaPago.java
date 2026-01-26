package com.sistemalp.facturacion.Entidades;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class FacturaPago { // entidad para guardar los pagos de la factura

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long facturaPagoId;

    @com.fasterxml.jackson.annotation.JsonIgnore
    @ManyToOne
    private Factura factura;

    @ManyToOne
    private FormaPago formaPago;

    private Double total;

    private Integer plazo; // opcional
    private String unidadTiempo; // “Días”, “Meses”
}
