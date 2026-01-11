package com.sistemalp.facturacion.xml.guia;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.XmlValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CampoAdicional {

    @XmlAttribute(required = true)
    private String nombre;

    @XmlValue
    private String valor;
}
