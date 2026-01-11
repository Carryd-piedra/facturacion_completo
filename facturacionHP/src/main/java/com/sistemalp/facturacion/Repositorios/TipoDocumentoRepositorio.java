package com.sistemalp.facturacion.Repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import com.sistemalp.facturacion.Entidades.TipoDocumento;

@Repository
public interface TipoDocumentoRepositorio extends JpaRepository<TipoDocumento, Long> {
    Optional<TipoDocumento> findByTipoDocumentoCodigo(String codigo);
}
