package com.sistemalp.facturacion.Servicios;

import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.Init;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.X509Certificate;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
@Service
public class FirmaElectronicaServicio {

    static {
        Security.addProvider(new BouncyCastleProvider());
        Init.init();
    }

    public String firmarXML(String xmlPath, String p12Path, String password) {

        try {
            KeyStore ks = KeyStore.getInstance("PKCS12");
            ks.load(new FileInputStream(p12Path), password.toCharArray());

            String alias = ks.aliases().nextElement();
            PrivateKey privateKey = (PrivateKey) ks.getKey(alias, password.toCharArray());
            X509Certificate cert = (X509Certificate) ks.getCertificate(alias);

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            Document doc = dbf.newDocumentBuilder().parse(new File(xmlPath));

            XMLSignature firma = new XMLSignature(doc, "", XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA256);

            Element root = doc.getDocumentElement();
            root.appendChild(firma.getElement());

            Transforms transforms = new Transforms(doc);
            transforms.addTransform(Transforms.TRANSFORM_ENVELOPED_SIGNATURE);
            transforms.addTransform(Transforms.TRANSFORM_C14N_OMIT_COMMENTS);

            firma.addDocument("", transforms);

            firma.addKeyInfo(cert);
            firma.addKeyInfo(cert.getPublicKey());

            firma.sign(privateKey);

            String xmlFirmado = xmlPath.replace(".xml", "_firmado.xml");

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.transform(new DOMSource(doc), new StreamResult(new File(xmlFirmado)));

            return xmlFirmado;

        } catch (Exception e) {
            throw new RuntimeException("Error al firmar XML: " + e.getMessage(), e);
        }
    }
}

