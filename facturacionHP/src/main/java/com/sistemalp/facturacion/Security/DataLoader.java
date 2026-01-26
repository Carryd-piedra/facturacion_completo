package com.sistemalp.facturacion.Security;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sistemalp.facturacion.Entidades.TipoUsuario;
import com.sistemalp.facturacion.Entidades.Usuario;
import com.sistemalp.facturacion.Repositorios.TipoUsuarioRepositorio;
import com.sistemalp.facturacion.Servicios.UsuarioServicio;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initData(
            TipoUsuarioRepositorio tipoRepo,
            UsuarioServicio usuarioService,
            com.sistemalp.facturacion.Servicios.ProductoServicio productoServicio,
            com.sistemalp.facturacion.Repositorios.TipoDocumentoRepositorio tipoDocRepo) {
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

            // Siempre resetear la contraseña
            admin.setPassword("admin");
            usuarioService.guardar(admin);

            System.out.println("==================================================");
            System.out.println(" CREDENCIALES POR DEFECTO PARA PRUEBAS: ");
            System.out.println(" Usuario: admin ");
            System.out.println(" Password: admin ");
            System.out.println("==================================================");

            // Crear producto dummy si no hay productos
            if (productoServicio.listarProductos().isEmpty()) {
                System.out.println("No hay productos. Creando producto de prueba...");
                com.sistemalp.facturacion.Entidades.Producto p = new com.sistemalp.facturacion.Entidades.Producto();
                p.setProductoNombre("Producto Prueba");
                p.setProductoSerial("PROD-001");
                p.setProductoPrecio(100.0);
                p.setProductoStock(50.0);
                p.setProductoEstado(1);
                p.setProductoCategoria("General");
                productoServicio.crearProducto(p);
                System.out.println("Producto de prueba creado.");
            }

            // Seed TipoDocumento
            if (tipoDocRepo.count() == 0) {
                System.out.println("Creando Tipos de Documento...");
                crearTipoDoc(tipoDocRepo, "Cédula", "05");
                crearTipoDoc(tipoDocRepo, "RUC", "04");
                crearTipoDoc(tipoDocRepo, "Pasaporte", "06");
                crearTipoDoc(tipoDocRepo, "Consumidor Final", "07");
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
}
