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
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long facturaId;

    private String secuencial; // 9 dígitos
    private String claveAcceso; // generado por SRI

    // Info Tributaria (Snapshot)
    private String ambiente;
    private String tipoEmision;
    private String razonSocial;
    private String nombreComercial;
    private String ruc;
    private String codDoc; // 01 para factura
    private String estab;
    private String ptoEmi;
    private String dirMatriz;

    // Info Factura Adicional
    private String contribuyenteEspecial;
    private String obligadoContabilidad;

    @com.fasterxml.jackson.annotation.JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaEmision;
    private Double subtotal12;
    private Double subtotal0;
    private Double subtotalNoObjeto;
    private Double subtotalExento;
    private Double totalDescuento;
    private Double totalIva;
    private Double totalFactura;

    private Integer estado; // 1=Registrada,2=Enviada,3=Autorizada

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DetalleFactura> detalles;

    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FacturaPago> pagos;

    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CampoAdicionalFactura> infoAdicional;

}
