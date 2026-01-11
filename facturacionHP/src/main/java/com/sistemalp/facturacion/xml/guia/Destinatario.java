package com.sistemalp.facturacion.xml.guia;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Getter;
import lombok.Setter;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
        "identificacionDestinatario",
        "razonSocialDestinatario",
        "dirDestinatario",
        "motivoTraslado",
        "docAduaneroUnico",
        "codEstabDestino",
        "ruta",
        "codDocSustento",
        "numDocSustento",
        "numAutDocSustento",
        "fechaEmisionDocSustento",
        "detalles"
})
@Getter
@Setter
public class Destinatario {

    @XmlElement(required = true)
    private String identificacionDestinatario;

    @XmlElement(required = true)
    private String razonSocialDestinatario;

    @XmlElement(required = true)
    private String dirDestinatario;

    @XmlElement(required = true)
    private String motivoTraslado;

    @XmlElement
    private String docAduaneroUnico;

    @XmlElement
    private String codEstabDestino;

    @XmlElement
    private String ruta;

    @XmlElement
    private String codDocSustento;

    @XmlElement
    private String numDocSustento;

    @XmlElement
    private String numAutDocSustento;

    @XmlElement
    private String fechaEmisionDocSustento;

    @XmlElementWrapper(name = "detalles")
    @XmlElement(name = "detalle")
    private List<Detalle> detalles;
}
