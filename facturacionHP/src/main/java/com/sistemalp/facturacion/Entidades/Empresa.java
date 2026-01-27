package com.sistemalp.facturacion.Entidades;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
//Clase que representa la empresa
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long empresaId;
 //razon social es el nombre de la empresa anotada en el RUC
    private String razonSocial;
//nombre comercial es el nombre que se le da a la empresa
    private String nombreComercial;
    private String ruc;

    //hace referencia a la direccion de la matriz de la empresa
    private String dirMatriz;
    //hace referencia a la direccion del establecimiento de la empresa
    private String dirEstablecimiento;

    //hace referencia al establecimiento de la empresa 
    private String establecimiento;
    //hace referencia al punto de emision de la empresa
    private String puntoEmision;

    private Integer ambiente; // 1 pruebas siempre sera uno 
    private Integer tipoEmision; // 1 normal

    private String obligadoContabilidad; // SI / NO

    //valores opcionales dentro de la factura
    private String contribuyenteEspecial; // opcional
    private String resolucion; // opcional

    //valores para la firma digital
    private String rutaFirma;
    private String claveFirma;
}
