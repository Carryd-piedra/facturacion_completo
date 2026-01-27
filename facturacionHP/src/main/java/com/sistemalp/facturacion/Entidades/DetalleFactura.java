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

    @com.fasterxml.jackson.annotation.JsonIgnore
    //relacion muchos a 1 con Factura
    @ManyToOne
    @JoinColumn(name = "facturaId", nullable = false)
    private Factura factura;

    @ManyToOne  //relacion muchos a 1 con Producto
    private Producto producto;

    private Double cantidad;
    private Double precioUnitario;
    private Double descuento;
    private Double subtotal;

    //relacion 1 a 1 con ImpuestoDetalle
    @OneToOne(mappedBy = "detalle", cascade = CascadeType.ALL)
    private ImpuestoDetalle impuesto;

    //relacion 1 a muchos con DetalleAdicionalFactura
    @jakarta.persistence.OneToMany(mappedBy = "detalleFactura", cascade = CascadeType.ALL, fetch = jakarta.persistence.FetchType.LAZY)
    private java.util.List<DetalleAdicionalFactura> detallesAdicionales;
}
