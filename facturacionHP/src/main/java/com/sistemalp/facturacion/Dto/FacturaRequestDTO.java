package com.sistemalp.facturacion.Dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FacturaRequestDTO {

    // Información de la factura
    private String secuencial;
    private LocalDate fechaEmision;

    private Double subtotalConImpuestos;
    private Double subtotal0;
    private Double subtotalExento;
    private Double subtotalNoObjeto;
    private Double totalDescuento;
    private Double totalIva;
    private Double totalFactura;

    private Long clienteId; // cliente existente / o se puede agregar un nested DTO
    private Long empresaId; // datos del emisor

    // Lista de items
    private List<DetalleFacturaDTO> detalles;

    private List<PagoDTO> pagos;

    private List<CampoAdicionalDTO> infoAdicional;
}
