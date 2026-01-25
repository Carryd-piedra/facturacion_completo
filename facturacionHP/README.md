# Facturación Electrónica SRI

Proyecto de facturación electrónica con conexión al SRI (Ecuador).

## 🚀 Cómo ejecutar la aplicación

### Prerrequisitos
*   Java 17
*   MySQL 8

### Configuración de Base de Datos
1.  Asegúrese de tener MySQL corriendo en el puerto `3306`.
2.  Cree una base de datos llamada `Facturacion`.
3.  Las credenciales por defecto son usuario `root` y contraseña `admin`. Si necesita cambiarlas, edite el archivo `src/main/resources/application.properties`.

### Ejecución
Para iniciar la aplicación, ejecute el siguiente comando en la raíz del proyecto:

```bash
./mvnw spring-boot:run
```

La aplicación iniciará en `http://localhost:8080`.

## 👥 Roles y Usuarios

Al iniciar la aplicación, se crea automáticamente un usuario administrador predeterminado (ver `DataLoader.java`):

*   **Usuario:** `admin`
*   **Contraseña:** `admin`
*   **Rol:** `Admin`

## 📄 Generación y Revisión de Reportes

El sistema genera automáticamente archivos PDF de las facturas procesadas.

*   **Ubicación:** Los reportes en PDF se guardan automáticamente en la carpeta:
    `C:\facturas_pdf`
*   **Generación:** El PDF se crea automáticamente cuando una factura es enviada y recibida por el SRI, o cuando es autorizada.

## 📡 Simulación de Envío al SRI

Puede simular el envío de documentos XML firmados al SRI utilizando el API REST expuesto.

### Endpoint
*   **URL:** `/api/sri/enviar`
*   **Método:** `POST`

### Parámetros
*   `ruta`: La ruta absoluta del archivo XML firmado que se desea enviar.

### Ejemplo de uso (cURL)
```bash
curl -X POST "http://localhost:8080/api/sri/enviar?ruta=C:/rutas/a/mi/factura_firmada.xml"
```
