package com.sistemalp.facturacion.Entidades;

import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class TipoDocumento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tipoDucumentoId")
    private Long tipoDocumentoId;
    private String tipoDocumentoNombre;
    private String tipoDocumentoCodigo;

}
