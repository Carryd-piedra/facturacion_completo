package com.sistemalp.facturacion.Entidades;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class DetalleFactura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long detalleId;

    @ManyToOne
    @JoinColumn(name = "facturaId", nullable = false)
    private Factura factura;

    @ManyToOne
    private Producto producto;

    private Double cantidad;
    private Double precioUnitario;
    private Double descuento;
    private Double subtotal;

    @OneToOne(mappedBy = "detalle", cascade = CascadeType.ALL)
    private ImpuestoDetalle impuesto;
}

