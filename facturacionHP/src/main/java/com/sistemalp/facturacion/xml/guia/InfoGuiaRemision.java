package com.sistemalp.facturacion.xml.guia;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Getter;
import lombok.Setter;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
        "dirEstablecimiento",
        "dirPartida",
        "razonSocialTransportista",
        "tipoIdentificacionTransportista",
        "rucTransportista",
        "rise",
        "obligadoContabilidad",
        "contribuyenteEspecial",
        "fechaIniTransporte",
        "fechaFinTransporte",
        "placa"
})
@Getter
@Setter
public class InfoGuiaRemision {

    @XmlElement
    private String dirEstablecimiento;

    @XmlElement(required = true)
    private String dirPartida;

    @XmlElement(required = true)
    private String razonSocialTransportista;

    @XmlElement(required = true)
    private String tipoIdentificacionTransportista;

    @XmlElement(required = true)
    private String rucTransportista;

    @XmlElement
    private String rise;

    @XmlElement
    private String obligadoContabilidad;

    @XmlElement
    private String contribuyenteEspecial;

    @XmlElement(required = true)
    private String fechaIniTransporte;

    @XmlElement(required = true)
    private String fechaFinTransporte;

    @XmlElement(required = true)
    private String placa;
}
