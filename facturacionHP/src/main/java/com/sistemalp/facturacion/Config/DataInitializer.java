package com.sistemalp.facturacion.Config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import com.sistemalp.facturacion.Entidades.TipoDocumento;
import com.sistemalp.facturacion.Repositorios.TipoDocumentoRepositorio;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private TipoDocumentoRepositorio tipoDocumentoRepository;

    @Override
    public void run(String... args) throws Exception {
        crearSiNoExiste("RUC", "04");
        crearSiNoExiste("Cédula", "05");
        crearSiNoExiste("Pasaporte", "06");
        crearSiNoExiste("Consumidor Final", "07");
        crearSiNoExiste("Identificación del Exterior", "08");

    }

    private void crearSiNoExiste(String nombre, String codigo) {
        if (!tipoDocumentoRepository.findByTipoDocumentoCodigo(codigo).isPresent()) {
            System.out.println("Creando Tipo de Documento: " + nombre + " (" + codigo + ")");
            TipoDocumento tipo = new TipoDocumento();
            tipo.setTipoDocumentoNombre(nombre);
            tipo.setTipoDocumentoCodigo(codigo);
            tipoDocumentoRepository.save(tipo);
        } else {
            System.out.println("Tipo de Documento ya existe: " + nombre + " (" + codigo + ")");
        }
    }
}
