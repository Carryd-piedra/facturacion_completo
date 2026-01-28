# Sistema de Facturación Electrónica

Este proyecto es un sistema completo de facturación electrónica que consta de un backend en Spring Boot y un frontend en Angular.

## 🚀 Cómo Ejecutar el Sistema

### 1. Backend (Java Spring Boot)
El backend se encuentra en la carpeta `facturacionHP`.

**Requisitos:**
- JDK 17 o superior.
- Maven (opcional si usas el wrapper incluido).

**Pasos:**
1. Navega a la carpeta del backend:
   ```bash
   cd facturacionHP
   ```
2. Ejecuta la aplicación:
   - **Windows:**
     ```powershell
     .\mvnw spring-boot:run
     ```
   - **Linux/Mac:**
     ```bash
     ./mvnw spring-boot:run
     ```
3. El servidor iniciará en el puerto **8080** (por defecto).

### 2. Frontend (Angular)
El frontend se encuentra en la carpeta `facturacion-app-master`.

**Requisitos:**
- Node.js (v18 o superior recomendado).
- Angular CLI.

**Pasos:**
1. Navega a la carpeta del frontend:
   ```bash
   cd facturacion-app-master
   ```
2. Instala las dependencias (solo la primera vez):
   ```bash
   npm install
   ```
3. Ejecuta el servidor de desarrollo:
   ```bash
   npm start
   ```
4. Abre tu navegador en [http://localhost:4200](http://localhost:4200).

---

## 👥 Usuarios y Roles Predefinidos

El sistema carga automáticamente los siguientes usuarios para pruebas (definidos en `DataLoader.java`):

| Usuario   | Contraseña | Rol      | Descripción                                      |
|-----------|------------|----------|--------------------------------------------------|
| **admin**     | `admin`    | Admin    | Acceso total al sistema.                         |
| **contador**  | `contador` | Contador | Acceso a Dashboard y Reportes.                   |
| **vendedor**  | `vendedor` | Vendedor | Acceso a Facturación y Gestión de Clientes.      |

---

## 📊 Reportes y Simulación SRI

### Generar Reportes
1. Inicia sesión con un usuario **Admin** o **Contador**.
2. Ve a la sección **Reportes** en el menú lateral.
3. Tendrás dos opciones:
   - **Ver Reporte:** Genera una vista HTML con el listado de clientes, productos y facturas.
   - **Descargar PDF:** Descarga el mismo reporte en formato PDF.

### Simulación de Envío al SRI
El sistema permite simular el ciclo de vida de una factura electrónica:

1. **Crear Factura:**
   - Ve a **Facturación** -> **Nueva Factura**.
   - Llena los datos del cliente y productos, y guarda la factura.

2. **Enviar al SRI (Recepción):**
   - En el listado de facturas, haz clic en el botón **"Enviar al SRI"**.
   - Esto simula el envío del XML. El estado cambiará a "RECIBIDA" o "PROCESAMIENTO".

3. **Consultar Autorización:**
   - Una vez enviada, haz clic en el botón **"Consultar Autorización"**.
   - El sistema consultará el estado. Si todo es correcto, la factura pasará a estado **"AUTORIZADO"**.
   - Si hubo errores, se mostrará el detalle del error devuelto por la simulación.
