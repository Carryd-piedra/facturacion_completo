package com.sistemalp.facturacion.Entidades;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
@Table(name = "destinatario_guia")
@Getter
@Setter
public class DestinatarioGuia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String identificacionDestinatario;
    private String razonSocialDestinatario;
    private String dirDestinatario;
    private String motivoTraslado;
    private String docAduaneroUnico;
    private String codEstabDestino;
    private String ruta;

    private String codDocSustento;
    private String numDocSustento;
    private String numAutDocSustento;
    private LocalDate fechaEmisionDocSustento;

    @ManyToOne
    @JoinColumn(name = "guia_remision_id", nullable = false)
    private GuiaRemision guiaRemision;

    @OneToMany(mappedBy = "destinatario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DetalleGuia> detalles;
}
