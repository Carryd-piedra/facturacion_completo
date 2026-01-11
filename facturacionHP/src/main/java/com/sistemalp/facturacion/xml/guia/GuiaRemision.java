package com.sistemalp.facturacion.xml.guia;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Getter;
import lombok.Setter;

@XmlRootElement(name = "guiaRemision")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
        "infoTributaria",
        "infoGuiaRemision",
        "destinatarios",
        "infoAdicional"
})
@Getter
@Setter
public class GuiaRemision {

    @XmlAttribute(required = true)
    private String id;

    @XmlAttribute(required = true)
    private String version;

    @XmlElement(required = true)
    private InfoTributaria infoTributaria;

    @XmlElement(required = true)
    private InfoGuiaRemision infoGuiaRemision;

    @XmlElementWrapper(name = "destinatarios")
    @XmlElement(name = "destinatario")
    private List<Destinatario> destinatarios;

    @XmlElementWrapper(name = "infoAdicional")
    @XmlElement(name = "campoAdicional")
    private List<CampoAdicional> infoAdicional;
}
