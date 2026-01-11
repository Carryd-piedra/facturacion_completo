package com.sistemalp.facturacion.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardDTO {
    private long totalClientes;
    private long totalProductos;
    private long totalFacturas;
    private double totalVentas;
}
