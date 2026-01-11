package com.sistemalp.facturacion.Dto;

import java.util.List;

import com.sistemalp.facturacion.Entidades.Cliente;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClienteConDocumentoDto {
    private Cliente cliente;
    private List<DocumentoListaClienteDto> documentos;
}
