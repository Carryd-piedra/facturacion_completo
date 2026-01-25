package com.sistemalp.facturacion.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CambioRolDTO {
    private Long usuarioId;
    private String rol;
    private String descripcion;
}
