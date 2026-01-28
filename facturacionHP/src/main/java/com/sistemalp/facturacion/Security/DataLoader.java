package com.sistemalp.facturacion.Security;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sistemalp.facturacion.Entidades.FormaPago;
import com.sistemalp.facturacion.Entidades.TipoUsuario;
import com.sistemalp.facturacion.Entidades.Usuario;
import com.sistemalp.facturacion.Repositorios.TipoUsuarioRepositorio;
import com.sistemalp.facturacion.Servicios.UsuarioServicio;
import com.sistemalp.facturacion.Servicios.ProductoServicio;
import com.sistemalp.facturacion.Repositorios.FormaPagoRepositorio;
import com.sistemalp.facturacion.Repositorios.TipoDocumentoRepositorio;

//Clase que se ejecuta al iniciar la aplicacion para cargar datos de prueba
@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initData(
            TipoUsuarioRepositorio tipoRepo,
            UsuarioServicio usuarioService,
            ProductoServicio productoServicio,
            TipoDocumentoRepositorio tipoDocRepo,
            FormaPagoRepositorio formapagoRepo,
            com.sistemalp.facturacion.Repositorios.ClienteRepositorio clienteRepositorio,
            com.sistemalp.facturacion.Repositorios.TipoDocumentoClienteRepositorio tipoClienteRepo) {
        return args -> {
            TipoUsuario adminTipo = tipoRepo.findByRol("Admin")
                    .orElseGet(() -> {
                        TipoUsuario nuevo = new TipoUsuario();
                        nuevo.setRol("Admin");
                        nuevo.setDescripcion("Administrador del sistema");
                        return tipoRepo.save(nuevo);
                    });

            Usuario admin = usuarioService.findByUsername("admin");
            if (admin == null) {
                admin = new Usuario();
                admin.setNombre("Administrador");
                admin.setUsername("admin");
                admin.setTipoUsuario(adminTipo);
                System.out.println("Creando nuevo usuario 'admin'...");
            } else {
                System.out.println("Usuario 'admin' encontrado. Actualizando credenciales...");
            }
            admin.setPassword("admin");
            usuarioService.guardar(admin);

            // === CONTADOR ===
            TipoUsuario contadorTipo = tipoRepo.findByRol("Contador")
                    .orElseGet(() -> {
                        TipoUsuario nuevo = new TipoUsuario();
                        nuevo.setRol("Contador");
                        nuevo.setDescripcion("Contador del sistema (Reportes y Dashboard)");
                        return tipoRepo.save(nuevo);
                    });

            Usuario contador = usuarioService.findByUsername("contador");
            if (contador == null) {
                contador = new Usuario();
                contador.setNombre("Contador Principal");
                contador.setUsername("contador");
                contador.setTipoUsuario(contadorTipo);
                contador.setPassword("contador");
                usuarioService.guardar(contador);
                System.out.println("Creando nuevo usuario 'contador'...");
            }

            // === VENDEDOR ===
            TipoUsuario vendedorTipo = tipoRepo.findByRol("Vendedor")
                    .orElseGet(() -> {
                        TipoUsuario nuevo = new TipoUsuario();
                        nuevo.setRol("Vendedor");
                        nuevo.setDescripcion("Vendedor (Facturación y Clientes)");
                        return tipoRepo.save(nuevo);
                    });

            Usuario vendedor = usuarioService.findByUsername("vendedor");
            if (vendedor == null) {
                vendedor = new Usuario();
                vendedor.setNombre("Vendedor Estrella");
                vendedor.setUsername("vendedor");
                vendedor.setTipoUsuario(vendedorTipo);
                vendedor.setPassword("vendedor");
                usuarioService.guardar(vendedor);
                System.out.println("Creando nuevo usuario 'vendedor'...");
            }

            System.out.println("==================================================");
            System.out.println(" CREDENCIALES POR DEFECTO PARA PRUEBAS: ");
            System.out.println(" Usuario: admin     | Password: admin ");
            System.out.println(" Usuario: contador  | Password: contador ");
            System.out.println(" Usuario: vendedor  | Password: vendedor ");
            System.out.println("==================================================");

            // Seed TipoDocumento
            if (tipoDocRepo.count() == 0) {
                System.out.println("Creando Tipos de Documento...");
                crearTipoDoc(tipoDocRepo, "Cédula", "05");
                crearTipoDoc(tipoDocRepo, "RUC", "04");
                crearTipoDoc(tipoDocRepo, "Pasaporte", "06");
                crearTipoDoc(tipoDocRepo, "Consumidor Final", "07");
            }

            // seed formapago
            if (formapagoRepo.count() == 0) {
                System.out.println("Creando Formas de Pago...");
                crearFormaPago(formapagoRepo, "SIN UTILIZACION DEL SISTEMA FINANCIERO", "01");
                crearFormaPago(formapagoRepo, "COMPENSACIÓN DE DEUDAS", "15");
                crearFormaPago(formapagoRepo, "TARJETA DE DÉBITO", "16");
                crearFormaPago(formapagoRepo, "DINERO ELECTRÓNICO", "17");
                crearFormaPago(formapagoRepo, "TARJETA PREPAGO", "18");
                crearFormaPago(formapagoRepo, "TARJETA DE CRÉDITO", "19");
                crearFormaPago(formapagoRepo, "OTROS CON UTILIZACION DEL SISTEMA FINANCIERO", "20");
                crearFormaPago(formapagoRepo, "ENDOSO DE TÍTULOS", "21");
            }

            // === CONSUMIDOR FINAL ===
            if (clienteRepositorio.count() == 0) {
                System.out.println("Creando Cliente: Consumidor Final...");
                com.sistemalp.facturacion.Entidades.Cliente c = new com.sistemalp.facturacion.Entidades.Cliente();
                c.setClienteNombre("CONSUMIDOR FINAL");
                c.setClienteAplellido("");
                c.setClienteDirecion("S/N");
                c.setClienteTelefono("9999999999");
                c.setClienteMail("consumidor@final.com");
                c.setClienteEstado(1);
                clienteRepositorio.save(c);

                // Asignar documento 9999999999999 por defecto
                // Necesitamos el Tipo de Documento 'Consumidor Final'
                com.sistemalp.facturacion.Entidades.TipoDocumento td = tipoDocRepo.findByTipoDocumentoCodigo("07")
                        .orElse(null);
                if (td != null) {
                    com.sistemalp.facturacion.Entidades.TipoDocumentoCliente tdc = new com.sistemalp.facturacion.Entidades.TipoDocumentoCliente();
                    tdc.setCliente(c);
                    tdc.setTipoDocumento(td);
                    tdc.setNumeroDocumentoCliente("9999999999999");
                    tdc.setTipoDocumentoFecha(java.time.LocalDateTime.now());
                    tipoClienteRepo.save(tdc);
                }
            }
        };
    }

    private void crearTipoDoc(com.sistemalp.facturacion.Repositorios.TipoDocumentoRepositorio repo, String nombre,
            String codigo) {
        com.sistemalp.facturacion.Entidades.TipoDocumento td = new com.sistemalp.facturacion.Entidades.TipoDocumento();
        td.setTipoDocumentoNombre(nombre);
        td.setTipoDocumentoCodigo(codigo);
        repo.save(td);
    }

    private void crearFormaPago(FormaPagoRepositorio repo, String nombre, String codigo) {
        FormaPago fp = new FormaPago();
        fp.setNombre(nombre);
        fp.setCodigoSri(codigo);
        repo.save(fp);
    }
}
