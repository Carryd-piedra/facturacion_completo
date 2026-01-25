package com.sistemalp.facturacion.Servicios;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.sistemalp.facturacion.Entidades.Cliente;
import com.sistemalp.facturacion.Entidades.Factura;
import com.sistemalp.facturacion.Entidades.Producto;

public class ReporteHtmlBuilder {

    public static String generarHtmlReporte(List<Cliente> clientes, List<Producto> productos, List<Factura> facturas)
            throws Exception {

        String template = new String(Files.readAllBytes(Paths.get("src/main/resources/templates/reporte_db.html")));

        // 1. Clientes
        StringBuilder sbClientes = new StringBuilder();
        if (clientes != null) {
            for (Cliente c : clientes) {
                sbClientes.append("<tr>")
                        .append("<td>").append(c.getClienteId()).append("</td>")
                        .append("<td>").append(c.getClienteNombre() != null ? c.getClienteNombre() : "").append("</td>")
                        .append("<td>").append(c.getClienteTelefono() != null ? c.getClienteTelefono() : "")
                        .append("</td>")
                        .append("<td>").append(c.getClienteMail() != null ? c.getClienteMail() : "").append("</td>")
                        .append("</tr>");
            }
        }

        // 2. Productos
        StringBuilder sbProductos = new StringBuilder();
        if (productos != null) {
            for (Producto p : productos) {
                sbProductos.append("<tr>")
                        .append("<td>").append(p.getProductoId()).append("</td>")
                        .append("<td>").append(p.getProductoNombre() != null ? p.getProductoNombre() : "")
                        .append("</td>")
                        .append("<td>$ ")
                        .append(p.getProductoPrecio() != null ? String.format("%.2f", p.getProductoPrecio()) : "0.00")
                        .append("</td>")
                        .append("<td>").append(p.getProductoStock() != null ? p.getProductoStock() : "0")
                        .append("</td>")
                        .append("<td>").append(p.getProductoCategoria() != null ? p.getProductoCategoria() : "")
                        .append("</td>")
                        .append("</tr>");
            }
        }

        // 3. Facturas
        StringBuilder sbFacturas = new StringBuilder();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        if (facturas != null) {
            for (Factura f : facturas) {
                String fecha = f.getFechaEmision() != null ? f.getFechaEmision().format(dtf) : "";
                String clienteNombre = f.getCliente() != null ? f.getCliente().getClienteNombre() : "N/A";

                sbFacturas.append("<tr>")
                        .append("<td>").append(f.getFacturaId()).append("</td>")
                        .append("<td>").append(f.getSecuencial() != null ? f.getSecuencial() : "").append("</td>")
                        .append("<td>").append(fecha).append("</td>")
                        .append("<td>").append(clienteNombre).append("</td>")
                        .append("<td>$ ")
                        .append(f.getTotalFactura() != null ? String.format("%.2f", f.getTotalFactura()) : "0.00")
                        .append("</td>")
                        .append("<td>").append(f.getEstado() != null ? f.getEstado() : "").append("</td>")
                        .append("</tr>");
            }
        }

        // Totales
        String totalClientes = "Total registros: " + (clientes != null ? clientes.size() : 0);
        String totalProductos = "Total registros: " + (productos != null ? productos.size() : 0);
        String totalFacturas = "Total registros: " + (facturas != null ? facturas.size() : 0);

        return template
                .replace("{{TABLA_CLIENTES}}", sbClientes.toString())
                .replace("{{TOTAL_CLIENTES}}", totalClientes)
                .replace("{{TABLA_PRODUCTOS}}", sbProductos.toString())
                .replace("{{TOTAL_PRODUCTOS}}", totalProductos)
                .replace("{{TABLA_FACTURAS}}", sbFacturas.toString())
                .replace("{{TOTAL_FACTURAS}}", totalFacturas);
    }
}
