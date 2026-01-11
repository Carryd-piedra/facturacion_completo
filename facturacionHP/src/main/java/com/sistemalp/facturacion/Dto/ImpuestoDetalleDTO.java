package com.sistemalp.facturacion.Dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImpuestoDetalleDTO {

    private String codigo;            // 2 = IVA
    private String codigoPorcentaje;  // 2=15%, 0=0%
    private Double tarifa;            // 15
    private Double baseImponible;
    private Double valor;
}

