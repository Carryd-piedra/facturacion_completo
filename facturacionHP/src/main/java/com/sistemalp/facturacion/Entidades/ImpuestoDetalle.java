package com.sistemalp.facturacion.Entidades;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ImpuestoDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long impuestoId;

    @ManyToOne
    private DetalleFactura detalle;

    private String codigo;             // 2 = IVA
    private String codigoPorcentaje;   // 2=12%, 0=0%
    private Double tarifa;             // 12.00
    private Double baseImponible;      // subtotal del ítem
    private Double valor;              // IVA calculado
}
