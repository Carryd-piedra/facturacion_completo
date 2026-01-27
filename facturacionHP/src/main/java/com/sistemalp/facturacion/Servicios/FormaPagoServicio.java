package com.sistemalp.facturacion.Servicios;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sistemalp.facturacion.Dto.FormaPagoDTO;
import com.sistemalp.facturacion.Entidades.FormaPago;
import com.sistemalp.facturacion.Repositorios.FormaPagoRepositorio;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FormaPagoServicio {

    private final FormaPagoRepositorio formaPagoRepository;

    public List<FormaPago> listar() {
        return formaPagoRepository.findAll();
    }

    public FormaPago obtener(Long id) {
        return formaPagoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Forma de pago no encontrada"));
    }

    public FormaPago crear(FormaPagoDTO dto) {
        validarFormaPago(dto);

        if (formaPagoRepository.existsByCodigoSri(dto.getCodigoSri())) {
            throw new RuntimeException("El código SRI ya está registrado.");
        }

        FormaPago fp = new FormaPago();
        copiarDatos(dto, fp);

        return formaPagoRepository.save(fp);
    }

    public FormaPago actualizar(Long id, FormaPagoDTO dto) {
        validarFormaPago(dto);
        FormaPago existente = obtener(id);
        copiarDatos(dto, existente);
        return formaPagoRepository.save(existente);
    }

    public void eliminar(Long id) {
        FormaPago fp = obtener(id);
        formaPagoRepository.delete(fp);
    }

    private void copiarDatos(FormaPagoDTO dto, FormaPago fp) {
        fp.setNombre(dto.getNombre());
        fp.setCodigoSri(dto.getCodigoSri());
    }

    private void validarFormaPago(FormaPagoDTO dto) {
        // 1. Campos Requeridos
        if (dto.getNombre() == null || dto.getNombre().trim().isEmpty()) {
            lanzarError("nombre", "El nombre es obligatorio");
        }
        if (dto.getCodigoSri() == null || dto.getCodigoSri().trim().isEmpty()) {
            lanzarError("codigoSri", "El código SRI es obligatorio");
        }

        // 2. Longitud Nombre (Max 50) y Solo Letras
        if (dto.getNombre().length() > 60) {
            lanzarError("nombre", "El nombre no puede exceder los 50 caracteres");
        }
        if (!dto.getNombre().matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")) {
            lanzarError("nombre", "El nombre solo puede contener letras y espacios");
        }

        // 3. Validación Código SRI (Numérico 2-3 dígitos)
        if (!dto.getCodigoSri().matches("\\d{2,3}")) {
            lanzarError("codigoSri", "El código SRI debe tener entre 2 y 3 dígitos numéricos");
        }
    }

    private void lanzarError(String campo, String motivo) {
        String json = String.format("{\"campo\": \"%s\", \"motivo\": \"%s\"}", campo, motivo);
        throw new RuntimeException(json);
    }
}
