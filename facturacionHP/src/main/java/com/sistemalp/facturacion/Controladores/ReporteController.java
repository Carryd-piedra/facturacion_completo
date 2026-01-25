package com.sistemalp.facturacion.Controladores;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sistemalp.facturacion.Entidades.Cliente;
import com.sistemalp.facturacion.Entidades.Factura;
import com.sistemalp.facturacion.Entidades.Producto;
import com.sistemalp.facturacion.Repositorios.ClienteRepositorio;
import com.sistemalp.facturacion.Repositorios.FacturaRepositorio;
import com.sistemalp.facturacion.Repositorios.ProductoRepositorio;
import com.sistemalp.facturacion.Servicios.PdfGenerator;
import com.sistemalp.facturacion.Servicios.ReporteHtmlBuilder;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/reporte")
@Tag(name = "Reporte", description = "Reportes generales de la base de datos")
public class ReporteController {

    @Autowired
    private ClienteRepositorio clienteRepositorio;

    @Autowired
    private ProductoRepositorio productoRepositorio;

    @Autowired
    private FacturaRepositorio facturaRepositorio;

    @GetMapping(value = "/ver", produces = MediaType.TEXT_HTML_VALUE)
    public String verReporte() {
        try {
            List<Cliente> clientes = clienteRepositorio.findAll();
            List<Producto> productos = productoRepositorio.findAll();
            List<Factura> facturas = facturaRepositorio.findAll();

            return ReporteHtmlBuilder.generarHtmlReporte(clientes, productos, facturas);
        } catch (Exception e) {
            e.printStackTrace();
            return "<html><body><h1>Error generando reporte</h1><p>" + e.getMessage() + "</p></body></html>";
        }
    }

    @GetMapping("/pdf")
    public ResponseEntity<Resource> descargarPdf() {
        try {
            List<Cliente> clientes = clienteRepositorio.findAll();
            List<Producto> productos = productoRepositorio.findAll();
            List<Factura> facturas = facturaRepositorio.findAll();

            String html = ReporteHtmlBuilder.generarHtmlReporte(clientes, productos, facturas);

            File tempFile = File.createTempFile("reporte_db_" + System.currentTimeMillis(), ".pdf");
            PdfGenerator.generarPDF(html, tempFile.getAbsolutePath());

            InputStreamResource resource = new InputStreamResource(new FileInputStream(tempFile));

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte_db.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(tempFile.length())
                    .body(resource);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
