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
        validarCliente(cliente);
        clienteRepositorio.save(cliente);
        try {

            for (DocumentoListaClienteDto doc : documentos) {
                TipoDocumentoCliente tipoDocumentoCliente = new TipoDocumentoCliente();
                tipoDocumentoCliente.setCliente(cliente);
                System.err.println(doc.getNumeroDocumentoCliente());
                System.err.println(doc.getTipoDocumentoId());
                tipoDocumentoCliente.setNumeroDocumentoCliente(doc.getNumeroDocumentoCliente());
                TipoDocumento tipoDocumento = tipoDocumentoRepositorio.getById(doc.getTipoDocumentoId());

                // VALIDACIONES DE TIPO DE DOCUMENTO
                String codigo = tipoDocumento.getTipoDocumentoNombre().toUpperCase();
                String numero = doc.getNumeroDocumentoCliente();

                if (codigo.contains("RUC")) {
                    if (!numero.matches("\\d{13}")) {
                        throw new RuntimeException(
                                "{\"campo\": \"numeroDocumento\", \"motivo\": \"El RUC debe tener 13 dígitos numéricos\"}");
                    }
                } else if (codigo.contains("CEDULA") || codigo.contains("CÉDULA")) {
                    if (!numero.matches("\\d{10}")) {
                        throw new RuntimeException(
                                "{\"campo\": \"numeroDocumento\", \"motivo\": \"La Cédula debe tener 10 dígitos numéricos\"}");
                    }
                }

                tipoDocumentoCliente.setNumeroDocumentoCliente(numero);
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

    private void validarCliente(Cliente c) {
        // Nombre
        if (c.getClienteNombre() == null || c.getClienteNombre().trim().isEmpty()) {
            lanzarError("clienteNombre", "El nombre es obligatorio");
        }
        if (c.getClienteNombre().length() > 30) {
            lanzarError("clienteNombre", "El nombre no puede exceder 30 caracteres");
        }
        if (!c.getClienteNombre().matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")) {
            lanzarError("clienteNombre", "El nombre solo puede contener letras");
        }

        // Apellido
        if (c.getClienteAplellido() == null || c.getClienteAplellido().trim().isEmpty()) {
            lanzarError("clienteAplellido", "El apellido es obligatorio");
        }
        if (c.getClienteAplellido().length() > 30) {
            lanzarError("clienteAplellido", "El apellido no puede exceder 30 caracteres");
        }
        if (!c.getClienteAplellido().matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")) {
            lanzarError("clienteAplellido", "El apellido solo puede contener letras");
        }

        // Dirección
        if (c.getClienteDirecion() == null || c.getClienteDirecion().trim().isEmpty()) {
            lanzarError("clienteDirecion", "La dirección es obligatoria");
        }
        if (c.getClienteDirecion().length() > 60) {
            lanzarError("clienteDirecion", "La dirección no puede exceder 60 caracteres");
        }

        // Teléfono (10 dígitos)
        if (c.getClienteTelefono() == null || !c.getClienteTelefono().matches("\\d{10}")) {
            lanzarError("clienteTelefono", "El teléfono debe tener 10 dígitos numéricos");
        }

        // Correo
        if (c.getClienteMail() == null || c.getClienteMail().length() > 50
                || !c.getClienteMail().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            lanzarError("clienteMail", "El correo electrónico no es válido o excede 50 caracteres");
        }
    }

    private void lanzarError(String campo, String motivo) {
        String json = String.format("{\"campo\": \"%s\", \"motivo\": \"%s\"}", campo, motivo);
        throw new RuntimeException(json);
    }
}
