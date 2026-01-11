package com.sistemalp.facturacion.Servicios;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public Cliente guardar(ClienteConDocumentoDto clienteDto) {
        try {
            List<DocumentoListaClienteDto> documentos = clienteDto.getDocumentos();

            if (documentos == null || documentos.isEmpty()) {
                throw new RuntimeException("Error no tiene documentos");
            }

            Cliente cliente = clienteDto.getCliente();
            System.out.println("Intentando guardar cliente: " + cliente.getClienteNombre());
            cliente = clienteRepositorio.save(cliente);

            for (DocumentoListaClienteDto doc : documentos) {
                TipoDocumentoCliente tipoDocumentoCliente = new TipoDocumentoCliente();
                tipoDocumentoCliente.setCliente(cliente);
                tipoDocumentoCliente.setNumeroDocumentoCliente(doc.getNumeroDocumentoCliente());

                TipoDocumento tipoDocumento = tipoDocumentoRepositorio.findById(doc.getTipoDocumentoId())
                        .orElseThrow(() -> new RuntimeException(
                                "No se encontró Tipo de Documento ID: " + doc.getTipoDocumentoId()));

                tipoDocumentoCliente.setTipoDocumento(tipoDocumento);
                tipoDocumentoCliente.setTipoDocumentoFecha(LocalDateTime.now());
                tipoDocumentoClienteRepositorio.save(tipoDocumentoCliente);
            }
            return cliente;
        } catch (Exception e) {
            System.err.println("Error en ClienteServicio: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("No se pudo guardar el cliente: " + e.getMessage());
        }
    }

    public ClienteConDocumentoDto buscarDtoPorId(Long id) {
        Cliente cliente = clienteRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        List<TipoDocumentoCliente> docsEntity = tipoDocumentoClienteRepositorio.findByCliente(cliente);

        List<DocumentoListaClienteDto> docsDto = docsEntity.stream().map(d -> {
            DocumentoListaClienteDto dto = new DocumentoListaClienteDto();
            dto.setTipoDocumentoId(d.getTipoDocumento().getTipoDocumentoId());
            dto.setNumeroDocumentoCliente(d.getNumeroDocumentoCliente());
            return dto;
        }).toList();

        ClienteConDocumentoDto result = new ClienteConDocumentoDto();
        result.setCliente(cliente);
        result.setDocumentos(docsDto);
        return result;
    }

    public List<Cliente> listarAll() {
        return clienteRepositorio.findAll();
    }

    public Cliente buscarId(Long id) {
        return clienteRepositorio.findById(id).orElse(null);
    }

    @Transactional
    public void eliminar(Long id) {
        Cliente cliente = clienteRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        // Eliminamos los documentos asociados primero
        tipoDocumentoClienteRepositorio.deleteByCliente(cliente);

        // Ahora sí eliminamos el cliente
        clienteRepositorio.delete(cliente);
    }
}
