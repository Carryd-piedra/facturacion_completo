import { Cliente } from './cliente';
import { FormaPago } from './forma-pago';

export interface Factura {
    facturaId: number;
    secuencial: string;
    claveAcceso: string;
    fechaEmision: string; // ISO date
    subtotal12: number;
    subtotal0: number;
    subtotalNoObjeto: number;
    subtotalExento: number;
    totalDescuento: number;
    totalIva: number;
    totalFactura: number;
    estado: number;
    cliente: Cliente;
    empresa: Empresa;
    detalles: DetalleFactura[];
    pagos: FacturaPago[];
    estadoSri?: string; // Optional for UI
}

export interface DetalleFactura {
    detalleId?: number;
    producto: Producto;
    cantidad: number;
    precioUnitario: number;
    descuento: number;
    subtotal: number;
}

export interface FacturaPago {
    pagoId?: number; // Backend uses facturaPagoId, mapping might need adjustment if using this
    facturaPagoId?: number;
    formaPagoId?: number; // On response this might be null if using object
    formaPago?: FormaPago;
    total: number;
    plazo: number;
    unidadTiempo: string;
}

// Interfaces needed for creation
export interface FacturaRequestDTO {
    clienteId: number;
    empresaId: number;
    secuencial: string;
    fechaEmision: string;
    subtotal12: number;
    subtotal0: number;
    subtotalNoObjeto: number;
    subtotalExento: number;
    totalDescuento: number;
    totalIva: number;
    totalFactura: number;
    detalles: DetalleFacturaDTO[];
    pagos: PagoDTO[];
}

export interface DetalleFacturaDTO {
    productoId: number;
    cantidad: number;
    precioUnitario: number;
    descuento: number;
    subtotal: number;
    impuesto: ImpuestoDTO;
}

export interface ImpuestoDTO {
    codigo: string;
    codigoPorcentaje: string;
    tarifa: number;
    baseImponible: number;
    valor: number;
}

export interface PagoDTO {
    formaPagoId: number;
    total: number;
    plazo: number;
    unidadTiempo: string;
}

// Dependent interfaces (simplified if not available elsewhere)
// Dependent interfaces (simplified if not available elsewhere)
/* Cliente imported from ./cliente */

export interface Empresa {
    empresaId: number;
    razonSocial: string;
    ruc: string;
}

export interface Producto {
    productoId: number;
    productoNombre: string;
    productoPrecio: number;
    productoTasa: number; // IVA %
}
