package com.sistemalp.facturacion.Servicios;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;   // ← NECESARIO
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;


@Service
public class SriRecepcionService {

    // ==========================
    // 🔗 URLs del SRI
    // ==========================
    private static final String SRI_RECEPCION_PRUEBAS =
            "https://celcer.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantesOffline?wsdl";
    private static final String SRI_AUTORIZACION_PRUEBAS =
            "https://celcer.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantesOffline?wsdl";

    // ==========================
    // 📤 ENVIAR XML FIRMADO
    // ==========================
    public String enviarFactura(String rutaXmlFirmado) throws Exception {

        log("📩 Recibido archivo: " + rutaXmlFirmado);

        File file = new File(rutaXmlFirmado);
        if (!file.exists()) {
            throw new Exception("❌ Archivo no encontrado: " + rutaXmlFirmado);
        }

        byte[] xmlBytes = Files.readAllBytes(file.toPath());
        String xmlBase64 = Base64.getEncoder().encodeToString(xmlBytes);

        log("📄 XML leído correctamente: " + xmlBytes.length + " bytes");

        String soapRequest = generarSoapRecepcion(xmlBase64);

        log("🧾 SOAP construido:\n" + soapRequest);

        String rawResponse = enviarSoap(soapRequest, SRI_RECEPCION_PRUEBAS);

        log("📨 Respuesta SRI Recepción:\n" + rawResponse);

        return procesarRespuestaRecepcion(rawResponse);
    }

    // ==========================
    // 🔎 CONSULTAR AUTORIZACIÓN
    // ==========================
    public String consultarAutorizacion(String claveAcceso) throws Exception {

        log("🔍 Consultando autorización SRI para clave: " + claveAcceso);

        String soapRequest = generarSoapAutorizacion(claveAcceso);

        String rawResponse = enviarSoap(soapRequest, SRI_AUTORIZACION_PRUEBAS);

        log("📨 Respuesta SRI Autorización:\n" + rawResponse);

        return procesarRespuestaAutorizacion(rawResponse);
    }

    // =====================================================================
    // 🧾 SOAP RECEPCIÓN
    // =====================================================================
    private String generarSoapRecepcion(String xmlBase64) {

        return """
        <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                          xmlns:ec="http://ec.gob.sri.ws.recepcion">
            <soapenv:Header/>
            <soapenv:Body>
                <ec:validarComprobante>
                    <xml>%s</xml>
                </ec:validarComprobante>
            </soapenv:Body>
        </soapenv:Envelope>
        """.formatted(xmlBase64);
    }

    // =====================================================================
    // 🧾 SOAP AUTORIZACIÓN
    // =====================================================================
    private String generarSoapAutorizacion(String claveAcceso) {

        return """
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
    }

    // =====================================================================
    // 🚀 ENVÍO DE SOAP
    // =====================================================================
    private String enviarSoap(String soapRequest, String urlString) throws Exception {

        log("🌐 Enviando SOAP a: " + urlString);

        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "text/xml; charset=UTF-8");
        conn.setRequestProperty("SOAPAction", "");
        conn.setRequestProperty("Accept", "text/xml");

        try (OutputStream os = conn.getOutputStream()) {
            os.write(soapRequest.getBytes(StandardCharsets.UTF_8));
        }

        int status = conn.getResponseCode();
        log("🔎 HTTP Status: " + status);

        InputStream is = (status >= 200 && status < 300)
                ? conn.getInputStream()
                : conn.getErrorStream();

        return new String(is.readAllBytes(), StandardCharsets.UTF_8);
    }

    // =====================================================================
    // 📌 RESPUESTA RECEPCIÓN
    // =====================================================================
    private String procesarRespuestaRecepcion(String xml) {

        if (xml.contains("<estado>RECIBIDA</estado>")) {
            return "✔ RECIBIDA — El SRI aceptó el comprobante";
        }

        if (xml.contains("<estado>DEVUELTA</estado>")) {

            StringBuilder sb = new StringBuilder("❌ DEVUELTA — Errores del SRI:\n");
            Matcher m = Pattern.compile("<mensaje>(.*?)</mensaje>").matcher(xml);

            while (m.find()) sb.append(" - ").append(m.group(1)).append("\n");

            return sb.toString();
        }

        if (xml.contains("<faultstring>"))
            return "❌ SOAP Fault: " + extraer(xml, "<faultstring>(.*?)</faultstring>");

        return "⚠ Respuesta desconocida:\n" + xml;
    }

    // =====================================================================
    // 📌 RESPUESTA AUTORIZACIÓN
    // =====================================================================
    private String procesarRespuestaAutorizacion(String xml) {

        if (xml.contains("<estado>AUTORIZADO</estado>")) {
            return "✔ AUTORIZADO POR EL SRI";
        }

        if (xml.contains("<estado>NO AUTORIZADO</estado>")) {
            return "❌ NO AUTORIZADO — Motivo: " +
                    extraer(xml, "<mensaje>(.*?)</mensaje>");
        }

        return "⚠ Respuesta desconocida:\n" + xml;
    }

    // =====================================================================
    // 🔧 EXTRAER TEXTO
    // =====================================================================
    private String extraer(String xml, String regex) {
        Matcher m = Pattern.compile(regex, Pattern.DOTALL).matcher(xml);
        return m.find() ? m.group(1).trim() : "NO DEFINIDO";
    }

    // =====================================================================
    // 🖨 LOG BONITO
    // =====================================================================
    private void log(String msg) {
        System.out.println("\n" + msg + "\n----------------------------------------");
    }
}
