package com.sistemalp.facturacion.Servicios;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sistemalp.facturacion.Dto.EmpresaDTO;
import com.sistemalp.facturacion.Entidades.Empresa;
import com.sistemalp.facturacion.Repositorios.EmpresaRepositorio;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmpresaServicio {

    private final EmpresaRepositorio empresaRepository;

    // LISTAR
    public List<Empresa> listar() {
        return empresaRepository.findAll();
    }

    // OBTENER POR ID
    public Empresa obtener(Long id) {
        return empresaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada"));
    }

    // CREAR
    public Empresa crear(EmpresaDTO dto) {
        validarEmpresa(dto);

        if (empresaRepository.existsByRuc(dto.getRuc())) {
            throw new RuntimeException("Ya existe una empresa con ese RUC.");
        }

        Empresa e = new Empresa();
        copiarDatos(dto, e);

        return empresaRepository.save(e);
    }

    // ACTUALIZAR
    public Empresa actualizar(Long id, EmpresaDTO dto) {
        validarEmpresa(dto);

        Empresa existente = obtener(id);
        copiarDatos(dto, existente);
        return empresaRepository.save(existente);
    }

    // ELIMINAR
    public void eliminar(Long id) {
        Empresa existente = obtener(id);
        empresaRepository.delete(existente);
    }

    // MAPEO DTO → ENTIDAD
    private void copiarDatos(EmpresaDTO dto, Empresa e) {
        e.setRazonSocial(dto.getRazonSocial());
        e.setNombreComercial(dto.getNombreComercial());
        e.setRuc(dto.getRuc());

        e.setDirMatriz(dto.getDirMatriz());
        e.setDirEstablecimiento(dto.getDirEstablecimiento());

        e.setEstablecimiento(dto.getEstablecimiento());
        e.setPuntoEmision(dto.getPuntoEmision());

        e.setAmbiente(dto.getAmbiente());
        e.setTipoEmision(dto.getTipoEmision());

        e.setObligadoContabilidad(dto.getObligadoContabilidad());

        e.setContribuyenteEspecial(dto.getContribuyenteEspecial());
        e.setResolucion(dto.getResolucion());

        e.setRutaFirma(dto.getRutaFirma());
        e.setClaveFirma(dto.getClaveFirma());
    }

    private void validarEmpresa(EmpresaDTO dto) {
        // 1. Campos Requeridos
        validarRequerido(dto.getRazonSocial(), "razonSocial", "La Razón Social es obligatoria");
        validarRequerido(dto.getNombreComercial(), "nombreComercial", "El Nombre Comercial es obligatorio");
        validarRequerido(dto.getRuc(), "ruc", "El RUC es obligatorio");
        validarRequerido(dto.getDirMatriz(), "dirMatriz", "La Dirección Matriz es obligatoria");
        validarRequerido(dto.getDirEstablecimiento(), "dirEstablecimiento",
                "La Dirección Establecimiento es obligatoria");
        validarRequerido(dto.getEstablecimiento(), "establecimiento", "El Establecimiento es obligatorio");
        validarRequerido(dto.getPuntoEmision(), "puntoEmision", "El Punto de Emisión es obligatorio");
        validarRequerido(dto.getRutaFirma(), "rutaFirma", "La Ruta de la Firma es obligatoria");
        validarRequerido(dto.getClaveFirma(), "claveFirma", "La Clave de la Firma es obligatoria");
        validarRequerido(dto.getObligadoContabilidad(), "obligadoContabilidad", "Obligado Contabilidad es obligatorio");

        // 2. Validación RUC (13 dígitos numéricos)
        if (!dto.getRuc().matches("\\d{13}")) {
            lanzarError("ruc", "El RUC debe tener exactamente 13 caracteres numéricos");
        }

        // 3. Establecimiento y Punto Emisión (3 dígitos)
        if (!dto.getEstablecimiento().matches("\\d{3}")) {
            lanzarError("establecimiento", "El Establecimiento debe tener exactamente 3 dígitos numéricos");
        }
        if (!dto.getPuntoEmision().matches("\\d{3}")) {
            lanzarError("puntoEmision", "El Punto de Emisión debe tener exactamente 3 dígitos numéricos");
        }

        // 4. Ruta Firma (.p12)
        if (!dto.getRutaFirma().toLowerCase().endsWith(".p12")) {
            lanzarError("rutaFirma", "La ruta de firma debe terminar en .p12");
        }

        // 5. Obligado Contabilidad (SI/NO)
        if (!"SI".equals(dto.getObligadoContabilidad()) && !"NO".equals(dto.getObligadoContabilidad())) {
            lanzarError("obligadoContabilidad", "El campo Obligado Contabilidad solo acepta 'SI' o 'NO'");
        }

        // 6. Validaciones de Longitud (Max 60)
        validarLongitud(dto.getRazonSocial(), 60, "razonSocial");
        validarLongitud(dto.getNombreComercial(), 60, "nombreComercial");
        validarLongitud(dto.getDirMatriz(), 80, "dirMatriz");
        validarLongitud(dto.getDirEstablecimiento(), 80, "dirEstablecimiento");
    }

    private void validarRequerido(String valor, String campo, String mensaje) {
        if (valor == null || valor.trim().isEmpty()) {
            lanzarError(campo, mensaje);
        }
    }

    private void validarLongitud(String valor, int max, String campo) {
        if (valor != null && valor.length() > max) {
            lanzarError(campo, "El campo no puede exceder los " + max + " caracteres");
        }
    }

    private void lanzarError(String campo, String motivo) {
        // Formato JSON simple
        String json = String.format("{\"campo\": \"%s\", \"motivo\": \"%s\"}", campo, motivo);
        throw new RuntimeException(json);
    }
}
