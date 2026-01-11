package com.sistemalp.facturacion.xml.guia;

import java.math.BigDecimal;
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
        "codigoInterno",
        "codigoAdicional",
        "descripcion",
        "cantidad",
        "detallesAdicionales"
})
@Getter
@Setter
public class Detalle {

    @XmlElement
    private String codigoInterno;

    @XmlElement
    private String codigoAdicional;

    @XmlElement(required = true)
    private String descripcion;

    @XmlElement(required = true)
    private BigDecimal cantidad;

    @XmlElementWrapper(name = "detallesAdicionales")
    @XmlElement(name = "detAdicional")
    private List<DetAdicional> detallesAdicionales;
}
