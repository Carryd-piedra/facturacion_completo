package com.sistemalp.facturacion.xml.guia;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Getter;
import lombok.Setter;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
        "ambiente",
        "tipoEmision",
        "razonSocial",
        "nombreComercial",
        "ruc",
        "claveAcceso",
        "codDoc",
        "estab",
        "ptoEmi",
        "secuencial",
        "dirMatriz"
})
@Getter
@Setter
public class InfoTributaria {

    @XmlElement(required = true)
    private String ambiente;

    @XmlElement(required = true)
    private String tipoEmision;

    @XmlElement(required = true)
    private String razonSocial;

    @XmlElement
    private String nombreComercial;

    @XmlElement(required = true)
    private String ruc;

    @XmlElement(required = true)
    private String claveAcceso;

    @XmlElement(required = true)
    private String codDoc;

    @XmlElement(required = true)
    private String estab;

    @XmlElement(required = true)
    private String ptoEmi;

    @XmlElement(required = true)
    private String secuencial;

    @XmlElement(required = true)
    private String dirMatriz;
}
