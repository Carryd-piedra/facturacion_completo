package com.sistemalp.facturacion.Controladores;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sistemalp.facturacion.Dto.ClienteConDocumentoDto;
import com.sistemalp.facturacion.Entidades.Cliente;
import com.sistemalp.facturacion.Servicios.ClienteServicio;

@RestController
@RequestMapping("/api/cliente")
@org.springframework.web.bind.annotation.CrossOrigin("*")
public class ClienteControlador {
    @Autowired
    private ClienteServicio clienteServicio;

    @PostMapping
    public Cliente guardar(@RequestBody ClienteConDocumentoDto clienteDto) {
        try {
            System.out.println("Recibiendo clienteDto: " + clienteDto.getCliente().getClienteNombre());
            return clienteServicio.guardar(clienteDto);
        } catch (Exception e) {
            System.err.println("Error al guardar cliente: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error backend: " + e.getMessage());
        }
    }

    @GetMapping
    public List<Cliente> listasAll() {
        return clienteServicio.listarAll();
    }

    @GetMapping("{id}")
    public ClienteConDocumentoDto buscarId(@PathVariable Long id) {
        return clienteServicio.buscarDtoPorId(id);
    }

    @DeleteMapping("{id}")
    public org.springframework.http.ResponseEntity<?> elimnar(@PathVariable Long id) {
        try {
            clienteServicio.eliminar(id);
            return org.springframework.http.ResponseEntity.ok(Map.of("mensaje", "Cliente eliminado correctamente"));
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            return org.springframework.http.ResponseEntity.badRequest().body(
                    Map.of("mensaje",
                            "No se puede eliminar el cliente porque tiene registros asociados (facturas, documentos, etc.)"));
        } catch (Exception e) {
            return org.springframework.http.ResponseEntity.internalServerError()
                    .body(Map.of("mensaje", "Error al eliminar: " + e.getMessage()));
        }
    }

}
