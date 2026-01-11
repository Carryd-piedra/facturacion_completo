package com.sistemalp.facturacion.Entidades;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "cliente_documentos")
@Setter
@Getter
public class TipoDocumentoCliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "clienteId", nullable = false)
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "tipoDucumentoId", nullable = false)
    private TipoDocumento tipoDocumento;

    private String numeroDocumentoCliente;
    private LocalDateTime tipoDocumentoFecha;
}
