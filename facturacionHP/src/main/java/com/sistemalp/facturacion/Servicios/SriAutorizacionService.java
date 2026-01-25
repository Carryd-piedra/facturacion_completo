package com.sistemalp.facturacion.Servicios;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

@Service
public class SriAutorizacionService {

    private static final String SRI_AUTORIZACION = "https://celcer.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantes";

    public String consultarAutorizacion(String claveAcceso, com.sistemalp.facturacion.Entidades.Factura factura)
            throws Exception {

        String soap = """
                    <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                                      xmlns:ec="http://ec.gob.sri.ws.autorizacion">
                        <soapenv:Header/>
                        <soapenv:Body>
                            <ec:autorizacionComprobante>
                                <claveAccesoComprobante>%s</claveAccesoComprobante>
                            </ec:autorizacionComprobante>
                        </soapenv:Body>
                    </soapenv:Envelope>
                """.formatted(claveAcceso);

        HttpURLConnection conn = (HttpURLConnection) new URL(SRI_AUTORIZACION).openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");

        conn.getOutputStream().write(soap.getBytes());

        String response = new String(conn.getInputStream().readAllBytes());

        return procesarRespuestaAutorizacion(response, factura);
    }

    // Procesar estado AUTORIZADO / NO AUTORIZADO
    private String procesarRespuestaAutorizacion(String xml, com.sistemalp.facturacion.Entidades.Factura factura) {

        if (xml.contains("<estado>AUTORIZADO</estado>")) {
            try {
                // Extraer fecha y numero de autorizacion (usualmente numero = clave acceso si
                // no cambia)
                String fechaAutorizacion = extraeTag(xml, "fechaAutorizacion");
                String numeroAutorizacion = extraeTag(xml, "numeroAutorizacion");

                if (numeroAutorizacion == null)
                    numeroAutorizacion = factura.getClaveAcceso();
                if (fechaAutorizacion == null)
                    fechaAutorizacion = java.time.LocalDateTime.now().toString();

                String html = FacturaHtmlBuilder.generarHtmlFactura(factura, numeroAutorizacion, fechaAutorizacion);

                // Crear carpeta si no existe
                java.io.File dir = new java.io.File("C:\\facturas_pdf");
                if (!dir.exists())
                    dir.mkdirs();

                String rutaPdf = "C:\\facturas_pdf\\factura_" + factura.getClaveAcceso() + ".pdf";
                PdfGenerator.generarPDF(html, rutaPdf);

                System.out.println("PDF generado correctamente: " + rutaPdf);
            } catch (Exception e) {
                System.err.println("Error generando PDF: " + e.getMessage());
                e.printStackTrace();
            }
            return "AUTORIZADO";
        }

        if (xml.contains("<estado>NO AUTORIZADO</estado>")) {

            StringBuilder errores = new StringBuilder("NO AUTORIZADO:\n");
            Pattern p = Pattern.compile("<mensaje>(.*?)</mensaje>");
            Matcher m = p.matcher(xml);

            while (m.find()) {
                errores.append("- ").append(m.group(1)).append("\n");
            }

            return errores.toString();
        }

        return "Respuesta desconocida:\n" + xml;
    }

    private String extraeTag(String xml, String tag) {
        Pattern p = Pattern.compile("<" + tag + ">(.*?)</" + tag + ">");
        Matcher m = p.matcher(xml);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }
}
