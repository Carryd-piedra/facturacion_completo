package com.sistemalp.facturacion.Dto;



import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PagoDTO {

    private Long formaPagoId;  // id interno BD
    private Double total;
    private Integer plazo;        // opcional
    private String unidadTiempo;  // opcional
}

