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
            // 1. Cargar Keystore y Certificado
            KeyStore ks = KeyStore.getInstance("PKCS12");
            ks.load(new FileInputStream(p12Path), password.toCharArray());
            String alias = ks.aliases().nextElement();
            PrivateKey privateKey = (PrivateKey) ks.getKey(alias, password.toCharArray());
            X509Certificate cert = (X509Certificate) ks.getCertificate(alias);

            // 2. Preparar Documento XML
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            Document doc = dbf.newDocumentBuilder().parse(new File(xmlPath));

            // 3. Crear XMLSignature (Base)
            // SRI usa firma en el elemento raíz normalmente, o envuelta.
            // Creamos la firma apuntando al documento entero ""
            XMLSignature firma = new XMLSignature(doc, "", XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA1);
            // Asignar ID a la firma para que QualifyingProperties pueda referenciarla
            firma.setId("Signature-" + java.util.UUID.randomUUID().toString());
            // NOTA: SRI a veces acepta SHA1 o SHA256. XAdES suele usar SHA1 para el digest
            // del certificado.
            // Cambiado a RSA_SHA1 por compatibilidad común con XAdES-BES SRI, aunque SHA256
            // es mejor.

            Element root = doc.getDocumentElement();
            root.appendChild(firma.getElement());

            // 4. Transformaciones (Enveloped + C14N)
            Transforms transforms = new Transforms(doc);
            transforms.addTransform(Transforms.TRANSFORM_ENVELOPED_SIGNATURE);
            transforms.addTransform(Transforms.TRANSFORM_C14N_OMIT_COMMENTS);
            firma.addDocument("", transforms, org.apache.xml.security.utils.Constants.ALGO_ID_DIGEST_SHA1);

            // 5. KeyInfo
            firma.addKeyInfo(cert);
            firma.addKeyInfo(cert.getPublicKey());

            // ==================================================================================
            // 6. XAdES-BES implementation (Manual construction of Object)
            // ==================================================================================
            String xadesNs = "http://uri.etsi.org/01903/v1.3.2#";
            Element object = doc.createElementNS("http://www.w3.org/2000/09/xmldsig#", "ds:Object");

            Element qualifyingProperties = doc.createElementNS(xadesNs, "etsi:QualifyingProperties");
            qualifyingProperties.setAttribute("Target", "#" + firma.getId());

            Element signedProperties = doc.createElementNS(xadesNs, "etsi:SignedProperties");
            String signedPropsId = "SignedProperties_" + java.util.UUID.randomUUID().toString();
            signedProperties.setAttributeNS(null, "Id", signedPropsId);
            signedProperties.setIdAttributeNS(null, "Id", true); // Ensure ID is recognized in namespace-aware parsing

            Element signedSignatureProperties = doc.createElementNS(xadesNs, "etsi:SignedSignatureProperties");

            // 6.1 SigningTime
            Element signingTime = doc.createElementNS(xadesNs, "etsi:SigningTime");
            signingTime.setTextContent(
                    java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(java.time.ZonedDateTime.now()));
            signedSignatureProperties.appendChild(signingTime);

            // 6.2 SigningCertificate
            Element signingCertificate = doc.createElementNS(xadesNs, "etsi:SigningCertificate");
            Element certEl = doc.createElementNS(xadesNs, "etsi:Cert");

            // CertDigest
            Element certDigest = doc.createElementNS(xadesNs, "etsi:CertDigest");
            Element digestMethod = doc.createElementNS("http://www.w3.org/2000/09/xmldsig#", "ds:DigestMethod");
            digestMethod.setAttribute("Algorithm", org.apache.xml.security.utils.Constants.ALGO_ID_DIGEST_SHA1);
            certDigest.appendChild(digestMethod);

            Element digestValue = doc.createElementNS("http://www.w3.org/2000/09/xmldsig#", "ds:DigestValue");
            // Calcular SHA-1 hash del certificado
            java.security.MessageDigest sha1 = java.security.MessageDigest.getInstance("SHA-1");
            byte[] certHash = sha1.digest(cert.getEncoded());
            digestValue.setTextContent(java.util.Base64.getEncoder().encodeToString(certHash));
            certDigest.appendChild(digestValue);

            certEl.appendChild(certDigest);

            // IssuerSerial
            Element issuerSerial = doc.createElementNS(xadesNs, "etsi:IssuerSerial");
            Element x509IssuerName = doc.createElementNS("http://www.w3.org/2000/09/xmldsig#", "ds:X509IssuerName");
            x509IssuerName.setTextContent(cert.getIssuerX500Principal().getName()); // RFC2253 format usually required
            Element x509SerialNumber = doc.createElementNS("http://www.w3.org/2000/09/xmldsig#", "ds:X509SerialNumber");
            x509SerialNumber.setTextContent(cert.getSerialNumber().toString());
            issuerSerial.appendChild(x509IssuerName);
            issuerSerial.appendChild(x509SerialNumber);

            certEl.appendChild(issuerSerial);
            signingCertificate.appendChild(certEl);
            signedSignatureProperties.appendChild(signingCertificate);

            signedProperties.appendChild(signedSignatureProperties);

            // 6.3 SignedDataObjectProperties (Optional but recommended for strict XAdES)
            Element signedDataObjectProperties = doc.createElementNS(xadesNs, "etsi:SignedDataObjectProperties");
            Element dataObjectFormat = doc.createElementNS(xadesNs, "etsi:DataObjectFormat");
            dataObjectFormat.setAttribute("ObjectReference", "#" + firma.getId() + "-ObjectReference"); // Hacky
                                                                                                        // reference?
                                                                                                        // No, usually
                                                                                                        // refers to the
                                                                                                        // reference ID.
            // Accessing internal reference ID is hard. Let's skip DataObjectFormat for
            // basic BES if not strictly enforced,
            // OR simply point to the reference we added earlier.
            // Standard XMLSignature adds a reference. We need its ID.
            // Let's rely on SignedProperties reference which is mandatory.

            qualifyingProperties.appendChild(signedProperties);
            object.appendChild(qualifyingProperties);
            firma.getElement().appendChild(object);

            // 7. Add Reference to SignedProperties (Crucial for XAdES)
            Transforms transformsProps = new Transforms(doc);
            transformsProps.addTransform(Transforms.TRANSFORM_C14N_OMIT_COMMENTS);

            // Agregamos la referencia con el Tipo específico requerido por XAdES
            firma.addDocument(
                    "#" + signedProperties.getAttribute("Id"),
                    transformsProps,
                    org.apache.xml.security.utils.Constants.ALGO_ID_DIGEST_SHA1,
                    null,
                    "http://uri.etsi.org/01903#SignedProperties");

            // 8. Firmar
            firma.sign(privateKey);

            // 9. Guardar
            String xmlFirmado = xmlPath.replace(".xml", "_firmado.xml");
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.transform(new DOMSource(doc), new StreamResult(new File(xmlFirmado)));

            return xmlFirmado;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al firmar XML (XAdES-BES): " + e.getMessage(), e);
        }
    }
}
