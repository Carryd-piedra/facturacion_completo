package com.sistemalp.facturacion.Servicios;

import java.io.FileOutputStream;
import java.io.OutputStream;
import org.xhtmlrenderer.pdf.ITextRenderer;

public class PdfGenerator {
    public static void generarPDF(String html, String rutaDestino) throws Exception {
        OutputStream os = new FileOutputStream(rutaDestino);
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(html);
        renderer.layout();
        renderer.createPDF(os);
        os.close();
    }
}
