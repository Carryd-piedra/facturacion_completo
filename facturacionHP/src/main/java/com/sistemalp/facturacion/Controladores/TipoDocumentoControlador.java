package com.sistemalp.facturacion.Controladores;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sistemalp.facturacion.Entidades.TipoDocumento;
import com.sistemalp.facturacion.Servicios.TipoDocumentoServicio;

@RestController
@RequestMapping("/api/tipodocumento")
@CrossOrigin("*")
public class TipoDocumentoControlador {
    @Autowired
    private TipoDocumentoServicio tipoDocumentoServicio;

    @PostMapping
    public TipoDocumento guardar(@RequestBody TipoDocumento tipoDocumento) {
        return tipoDocumentoServicio.guardar(tipoDocumento);
    }

    @GetMapping
    public List<TipoDocumento> listaAll() {
        return tipoDocumentoServicio.listarAll();
    }

    @DeleteMapping("/{id}")
    public org.springframework.http.ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            tipoDocumentoServicio.eliminar(id);
            return org.springframework.http.ResponseEntity
                    .ok(Map.of("mensaje", "Tipo de documento eliminado correctamente"));
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            return org.springframework.http.ResponseEntity.badRequest()
                    .body(Map.of("mensaje",
                            "No se puede eliminar el tipo de documento porque está asignado a clientes."));
        } catch (Exception e) {
            return org.springframework.http.ResponseEntity.internalServerError()
                    .body(Map.of("mensaje", "Error al eliminar: " + e.getMessage()));
        }
    }

}
