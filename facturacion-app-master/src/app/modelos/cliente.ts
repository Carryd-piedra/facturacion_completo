export interface Cliente {
    clienteId?: number;
    clienteNombre: string;
    clienteDirecion: string;
    clienteAplellido: string;
    clienteTelefono: string;
    clienteMail: string;
    clienteEstado?: number;
}

export interface DocumentoListaClienteDto {
    tipoDocumentoId: number;
    numeroDocumentoCliente: string;
}

export interface ClienteConDocumentoDto {
    cliente: Cliente;
    documentos: DocumentoListaClienteDto[];
}

export interface TipoDocumento {
    tipoDocumentoId: number;
    tipoDocumentoNombre: string;
    tipoDocumentoCodigo?: string;
    tipoDocumentoEstado: number;
}
