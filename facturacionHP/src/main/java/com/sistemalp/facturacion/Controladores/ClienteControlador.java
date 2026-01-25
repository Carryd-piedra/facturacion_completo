package com.sistemalp.facturacion.Controladores;

import java.util.List;

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
public class ClienteControlador {
    @Autowired
    private ClienteServicio clienteServicio;

    @PostMapping
    public Cliente guardar(@RequestBody ClienteConDocumentoDto cliente){
        return clienteServicio.guardar(cliente);
    }
    @GetMapping
    public List<Cliente> listasAll(){
        return clienteServicio.listarAll();
    }
    @GetMapping("{id}")
    public Cliente buscarId(@PathVariable Long id){
        return clienteServicio.buscarId(id);
    }
    @DeleteMapping("{id}")
    public void elimnar(@PathVariable Long id){
        clienteServicio.eliminar(id);
    }

}
