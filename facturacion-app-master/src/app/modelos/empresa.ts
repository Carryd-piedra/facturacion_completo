export interface Empresa {
    empresaId?: number;
    razonSocial: string;
    nombreComercial: string;
    ruc: string;
    dirMatriz: string;
    dirEstablecimiento: string;
    establecimiento: string;
    puntoEmision: string;
    ambiente: number;
    tipoEmision: number;
    obligadoContabilidad: string;
    rutaFirma: string;
    claveFirma: string;
    contribuyenteEspecial?: string;
    resolucion?: string;
}
