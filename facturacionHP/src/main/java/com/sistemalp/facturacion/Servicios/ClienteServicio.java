package com.sistemalp.facturacion.Servicios;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sistemalp.facturacion.Dto.ClienteConDocumentoDto;
import com.sistemalp.facturacion.Dto.DocumentoListaClienteDto;
import com.sistemalp.facturacion.Entidades.Cliente;
import com.sistemalp.facturacion.Entidades.TipoDocumento;
import com.sistemalp.facturacion.Entidades.TipoDocumentoCliente;
import com.sistemalp.facturacion.Repositorios.ClienteRepositorio;
import com.sistemalp.facturacion.Repositorios.TipoDocumentoClienteRepositorio;
import com.sistemalp.facturacion.Repositorios.TipoDocumentoRepositorio;

@Service
public class ClienteServicio {
    @Autowired
    private ClienteRepositorio clienteRepositorio;
    @Autowired
    private TipoDocumentoRepositorio tipoDocumentoRepositorio;
    @Autowired
    private TipoDocumentoClienteRepositorio tipoDocumentoClienteRepositorio;
    @Autowired
    private com.sistemalp.facturacion.Repositorios.FacturaRepositorio facturaRepositorio;

    public Cliente guardar(ClienteConDocumentoDto clienteDto) {
        List<DocumentoListaClienteDto> documentos = clienteDto.getDocumentos();

        if (documentos == null || documentos.isEmpty()) {
            throw new RuntimeException("Error no tiene documentos");
        }

        Cliente cliente = clienteDto.getCliente();
        clienteRepositorio.save(cliente);
        try {

            for (DocumentoListaClienteDto doc : documentos) {
                TipoDocumentoCliente tipoDocumentoCliente = new TipoDocumentoCliente();
                tipoDocumentoCliente.setCliente(cliente);
                System.err.println(doc.getNumeroDocumentoCliente());
                System.err.println(doc.getTipoDocumentoId());
                tipoDocumentoCliente.setNumeroDocumentoCliente(doc.getNumeroDocumentoCliente());
                TipoDocumento tipoDocumento = tipoDocumentoRepositorio.getById(doc.getTipoDocumentoId());
                tipoDocumentoCliente.setTipoDocumento(tipoDocumento);
                tipoDocumentoCliente.setTipoDocumentoFecha(LocalDateTime.now());
                tipoDocumentoClienteRepositorio.save(tipoDocumentoCliente);
            }
        } catch (Exception e) {
            clienteRepositorio.delete(cliente);
            throw new RuntimeException("Error no pudo crear los documentos del cliente");
        }

        return cliente;
    }

    public List<Cliente> listarAll() {
        return clienteRepositorio.findAll();
    }

    public Cliente buscarId(Long id) {
        return clienteRepositorio.findById(id).orElse(null);
    }

    public void eliminar(Long id) {
        Cliente cliente = buscarId(id);
        if (cliente == null) {
            throw new RuntimeException("Cliente no encontrado");
        }

        if (facturaRepositorio.existsByCliente(cliente)) {
            throw new RuntimeException("No se puede eliminar el cliente porque tiene facturas asociadas.");
        }

        // Eliminar documentos asociados (TipoDocumentoCliente)
        List<TipoDocumentoCliente> documentos = tipoDocumentoClienteRepositorio.findByCliente(cliente);
        if (documentos != null && !documentos.isEmpty()) {
            tipoDocumentoClienteRepositorio.deleteAll(documentos);
        }

        clienteRepositorio.deleteById(id);
    }
}
