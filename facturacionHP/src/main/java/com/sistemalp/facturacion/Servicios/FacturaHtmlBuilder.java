package com.sistemalp.facturacion.Servicios;

import java.nio.file.Files;
import java.nio.file.Paths;

import com.sistemalp.facturacion.Entidades.Factura;

//Clase que genera el HTML de la factura
public class FacturaHtmlBuilder {

    public static String generarHtmlFactura(Factura factura, String autorizacion, String fechaAutorizacion)
            throws Exception {

                //Se lee el template(html) de la factura 
        String template = new String(Files.readAllBytes(Paths.get("src/main/resources/templates/factura.html")));

        //Se crea un StringBuilder para los detalles de la factura
        StringBuilder detalles = new StringBuilder();
        factura.getDetalles().forEach(d -> {
            detalles.append("<tr>")
                    .append("<td>").append(d.getProducto().getProductoNombre()).append("</td>")
                    .append("<td>").append(d.getCantidad()).append("</td>")
                    .append("<td>").append(d.getPrecioUnitario()).append("</td>")
                    .append("<td>").append(d.getSubtotal()).append("</td>")
                    .append("</tr>");
        });

        return template
                .replace("{{AUTORIZACION}}", autorizacion)
                .replace("{{FECHA_AUTORIZACION}}", fechaAutorizacion)

                // Datos Empresa
                .replace("{{EMPRESA_NOMBRE}}", factura.getEmpresa().getRazonSocial())
                .replace("{{EMPRESA_DIRECCION}}", factura.getEmpresa().getDirMatriz())
                .replace("{{EMPRESA_RUC}}", factura.getEmpresa().getRuc())
                .replace("{{EMPRESA_TELEFONO}}", "") // No hay campo telefono en Empresa

                .replace("{{CLIENTE}}", factura.getCliente().getClienteNombre())
                .replace("{{RUC}}", factura.getCliente().getClienteTelefono()) // Provisional: Usando Telefono como RUC
                                                                               // Cliente
                .replace("{{DETALLES}}", detalles.toString())
                .replace("{{TOTAL}}", factura.getTotalFactura().toString());
    }
}
