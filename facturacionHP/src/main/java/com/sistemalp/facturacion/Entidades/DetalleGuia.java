package com.sistemalp.facturacion.Entidades;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "detalle_guia")
@Getter
@Setter
public class DetalleGuia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String codigoInterno;
    private String codigoAdicional;
    private String descripcion;
    private BigDecimal cantidad;

    @ManyToOne
    @JoinColumn(name = "destinatario_id", nullable = false)
    private DestinatarioGuia destinatario;

    @OneToMany(mappedBy = "detalleGuia", cascade = jakarta.persistence.CascadeType.ALL, fetch = jakarta.persistence.FetchType.LAZY)
    private java.util.List<DetalleAdicionalGuia> detallesAdicionales;
}
