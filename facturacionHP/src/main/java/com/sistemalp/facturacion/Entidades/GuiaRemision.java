package com.sistemalp.facturacion.Entidades;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "guia_remision")
@Getter
@Setter
public class GuiaRemision {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Info Tributaria
    private String ambiente;
    private String tipoEmision;
    private String razonSocial;
    private String nombreComercial;
    private String ruc;
    private String claveAcceso;
    private String codDoc;
    private String estab;
    private String ptoEmi;
    private String secuencial;
    private String dirMatriz;

    // Info Guia Remision
    private String dirEstablecimiento;
    private String dirPartida;
    private String razonSocialTransportista;
    private String tipoIdentificacionTransportista;
    private String rucTransportista;
    private String rise;
    private String obligadoContabilidad;
    private String contribuyenteEspecial;

    private LocalDate fechaIniTransporte;
    private LocalDate fechaFinTransporte;
    private String placa;

    private Integer estado; // 1=Registrada, 2=Enviada, 3=Autorizada

    @OneToMany(mappedBy = "guiaRemision", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DestinatarioGuia> destinatarios;

    @OneToMany(mappedBy = "guiaRemision", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CampoAdicionalGuia> infoAdicional;
}
