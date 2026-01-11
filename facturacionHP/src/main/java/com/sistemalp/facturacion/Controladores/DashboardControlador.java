package com.sistemalp.facturacion.Controladores;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sistemalp.facturacion.Dto.DashboardDTO;
import com.sistemalp.facturacion.Servicios.ClienteServicio;
import com.sistemalp.facturacion.Servicios.FacturaServicio;
import com.sistemalp.facturacion.Servicios.ProductoServicio;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@CrossOrigin("*")
public class DashboardControlador {

    private final ClienteServicio clienteServicio;
    private final ProductoServicio productoServicio;
    private final FacturaServicio facturaServicio;

    @GetMapping("/stats")
    public ResponseEntity<DashboardDTO> getStats() {
        long totalClientes = clienteServicio.listarAll().size();
        long totalProductos = productoServicio.listarProductos().size();
        var facturas = facturaServicio.listar();
        long totalFacturas = facturas.size();
        double totalVentas = facturas.stream()
                .mapToDouble(f -> f.getTotalFactura())
                .sum();

        return ResponseEntity.ok(new DashboardDTO(totalClientes, totalProductos, totalFacturas, totalVentas));
    }
}
