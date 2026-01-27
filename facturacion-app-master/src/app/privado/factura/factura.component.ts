import { Component, OnInit } from '@angular/core';
import { CommonModule, CurrencyPipe, DatePipe } from '@angular/common';
import { FormArray, FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { FacturaService } from '../../servicio/factura.service';
import { Factura, FacturaRequestDTO } from '../../modelos/factura';
import { Producto } from '../../modelos/producto';
import { Cliente } from '../../modelos/cliente';
import { ClienteService } from '../../servicio/cliente.service';
import { ProductoService } from '../../servicio/producto.service';
import { FormaPagoService } from '../../servicio/forma-pago.service';
import { EmpresaService } from '../../servicio/empresa.service';
import { Empresa } from '../../modelos/empresa';
import Swal from 'sweetalert2';

@Component({
    selector: 'app-factura',
    standalone: true,
    imports: [CommonModule, ReactiveFormsModule, FormsModule, CurrencyPipe, DatePipe],
    templateUrl: './factura.component.html',
    styleUrl: './factura.component.css'
})
export class FacturaComponent implements OnInit {

    facturas: Factura[] = [];
    clientes: Cliente[] = [];
    productos: Producto[] = [];
    formasPago: any[] = [];

    mostrarFormulario = false;
    form!: FormGroup;

    clientesFiltrados: Cliente[] = [];
    productosFiltrados: Producto[] = [];
    busquedaCliente: string = '';
    busquedaProducto: string = '';
    mostrarModalCliente = false;
    mostrarModalProducto = false;
    indiceDetalleSeleccionado: number = -1;

    // Nombre del cliente seleccionado para mostrar en el input readonly
    clienteSeleccionadoNombre: string = '';

    empresas: Empresa[] = [];

    constructor(
        private facturaService: FacturaService,
        private clienteService: ClienteService,
        private productoService: ProductoService,
        private formaPagoService: FormaPagoService,
        private empresaService: EmpresaService,
        private fb: FormBuilder
    ) { }

    getFechaActual(): string {
        const today = new Date();
        const year = today.getFullYear();
        const month = ('0' + (today.getMonth() + 1)).slice(-2);
        const day = ('0' + today.getDate()).slice(-2);
        return `${year}-${month}-${day}`;
    }

    ngOnInit(): void {
        this.cargarFacturas();
        this.cargarCatalogos();
        this.initForm();
    }

    cargarFacturas() {
        this.facturaService.listar().subscribe(data => this.facturas = data);
    }

    cargarCatalogos() {
        this.clienteService.listar().subscribe(data => {
            this.clientes = data;
            this.clientesFiltrados = data;
        });
        this.productoService.listar().subscribe(data => {
            this.productos = data;
            this.productosFiltrados = data;
        });
        this.formaPagoService.listar().subscribe(data => this.formasPago = data);
        this.empresaService.listar().subscribe(data => {
            this.empresas = data;
            // Si hay empresas, seleccionar la primera por defecto
            if (this.empresas.length > 0) {
                this.form.patchValue({ empresaId: this.empresas[0].empresaId });
            }
        });
    }

    initForm() {
        this.form = this.fb.group({
            clienteId: [null, Validators.required],
            empresaId: [1, Validators.required],
            fechaEmision: [this.getFechaActual(), Validators.required],
            detalles: this.fb.array([]),
            pagos: this.fb.array([])
        });
        this.clienteSeleccionadoNombre = '';
        this.agregarDetalle();
        this.agregarPago();
    }

    // --- LOGICA MODAL CLIENTES ---
    abrirModalCliente() {
        this.busquedaCliente = '';
        this.clientesFiltrados = this.clientes;
        this.mostrarModalCliente = true;
    }

    cerrarModalCliente() {
        this.mostrarModalCliente = false;
    }

    buscarCliente() {
        const termino = this.busquedaCliente.toLowerCase();
        this.clientesFiltrados = this.clientes.filter(c =>
            c.clienteNombre.toLowerCase().includes(termino) ||
            c.clienteAplellido.toLowerCase().includes(termino) ||
            c.clienteTelefono.includes(termino)
        );
    }

    seleccionarCliente(cliente: Cliente) {
        this.form.patchValue({ clienteId: cliente.clienteId });
        this.clienteSeleccionadoNombre = `${cliente.clienteNombre} ${cliente.clienteAplellido}`;
        this.cerrarModalCliente();
    }

    // --- LOGICA MODAL PRODUCTOS ---
    abrirModalProducto(index: number) {
        this.indiceDetalleSeleccionado = index;
        this.busquedaProducto = '';
        this.productosFiltrados = this.productos;
        this.mostrarModalProducto = true;
    }

    cerrarModalProducto() {
        this.mostrarModalProducto = false;
        this.indiceDetalleSeleccionado = -1;
    }

    buscarProducto() {
        const termino = this.busquedaProducto.toLowerCase();
        this.productosFiltrados = this.productos.filter(p =>
            p.productoNombre.toLowerCase().includes(termino) ||
            p.productoSerial.toLowerCase().includes(termino)
        );
    }

    seleccionarProducto(producto: Producto) {
        if (this.indiceDetalleSeleccionado >= 0) {
            const detalle = this.detalles.at(this.indiceDetalleSeleccionado);
            // Asegurarnos de usar la tasa del producto, o 15 por defecto si no viene (null o undefined)
            const tasa = (producto.productoTasa != null) ? producto.productoTasa : 15;

            detalle.patchValue({
                productoId: producto.productoId,
                nombreProducto: producto.productoNombre,
                precioUnitario: producto.productoPrecio,
            });

            // Actualizar también el grupo de impuestos con la nueva tarifa
            detalle.get('impuesto')?.patchValue({
                tarifa: tasa
            });

            this.calcularLinea(detalle);
            this.cerrarModalProducto();
        }
    }

    get detalles() {
        return this.form.get('detalles') as FormArray;
    }

    get pagos() {
        return this.form.get('pagos') as FormArray;
    }

    agregarDetalle() {
        const detalle = this.fb.group({
            productoId: [null, Validators.required],
            nombreProducto: [''], // Para mostrar en el input readonly
            cantidad: [1, [Validators.required, Validators.min(1)]],
            precioUnitario: [0],
            descuento: [0],
            subtotal: [0],
            impuesto: this.fb.group({
                codigo: ['2'],
                codigoPorcentaje: ['2'],
                tarifa: [15],
                baseImponible: [0],
                valor: [0]
            })
        });

        detalle.get('cantidad')?.valueChanges.subscribe(() => this.calcularLinea(detalle));
        this.detalles.push(detalle);
    }

    // Totales Calculados
    totalSubtotal: number = 0;
    totalIva: number = 0;
    totalGeneral: number = 0;

    // ...

    eliminarDetalle(index: number) {
        this.detalles.removeAt(index);
        this.calcularTotales();
    }

    agregarPago() {
        const pago = this.fb.group({
            formaPagoId: [1, Validators.required],
            total: [0],
            plazo: [0],
            unidadTiempo: ['dias']
        });
        this.pagos.push(pago);
    }

    eliminarPago(index: number) {
        this.pagos.removeAt(index);
    }

    // ...

    calcularLinea(group: FormGroup | any) {
        const cant = group.get('cantidad').value || 0;
        const precio = group.get('precioUnitario').value || 0;
        const subtotal = cant * precio;
        group.patchValue({ subtotal: subtotal }, { emitEvent: false });

        const baseImponible = subtotal;

        // Obtener la tarifa del form (que se seteó al seleccionar producto)
        const tarifa = group.get('impuesto')?.get('tarifa')?.value || 0;
        const valorIva = baseImponible * (tarifa / 100);

        group.get('impuesto').patchValue({
            baseImponible: baseImponible,
            valor: valorIva
        }, { emitEvent: false });

        this.calcularTotales();
    }

    calcularTotales() {
        this.totalSubtotal = 0;
        this.totalIva = 0;
        this.totalGeneral = 0;

        this.detalles.controls.forEach((d: any) => {
            const sub = d.get('subtotal')?.value || 0;
            const iva = d.get('impuesto')?.get('valor')?.value || 0;
            this.totalSubtotal += sub;
            this.totalIva += iva;
        });

        this.totalGeneral = this.totalSubtotal + this.totalIva;

        // Actualizar el pago si existe (solo si hay un solo pago por defecto)
        if (this.pagos.length > 0) {
            this.pagos.at(0).patchValue({ total: this.totalGeneral }, { emitEvent: false });
        }
    }

    guardar() {
        if (this.form.invalid) {
            Swal.fire({
                icon: 'warning',
                title: 'Factura incompleta',
                text: 'Por favor complete todos los campos requeridos.',
                confirmButtonColor: '#4f46e5'
            });
            this.form.markAllAsTouched();
            return;
        }

        const formValue = this.form.value;

        // Recalcular para asegurar (aunque calcularTotales lo mantiene al día)
        this.calcularTotales();

        const factura: FacturaRequestDTO = {
            ...formValue,
            secuencial: '',
            subtotalConImpuestos: this.totalSubtotal,
            subtotal0: 0,
            subtotalNoObjeto: 0,
            subtotalExento: 0,
            totalDescuento: 0,
            totalIva: this.totalIva,
            totalFactura: this.totalGeneral
        };

        if (this.pagos.length > 0) {
            this.pagos.at(0).patchValue({ total: this.totalGeneral });
            factura.pagos[0].total = this.totalGeneral;
        }

        this.facturaService.crear(factura).subscribe({
            next: () => {
                Swal.fire('¡Éxito!', 'Factura guardada correctamente', 'success');
                this.mostrarFormulario = false;
                this.initForm();
                this.cargarFacturas();
            },
            error: (err) => {
                console.error(err);
                let msg = 'No se pudo guardar la factura';
                if (err.error) {
                    if (typeof err.error === 'string') {
                        msg = err.error;
                    } else if (err.error.message) {
                        msg = err.error.message;
                    } else {
                        msg = JSON.stringify(err.error);
                    }
                }
                Swal.fire('Error', msg, 'error');
            }
        });
    }

    toggleForm() {
        this.mostrarFormulario = !this.mostrarFormulario;
    }

    // PASO 1: ENVIAR (Recepción)
    enviarRecepcion(factura: Factura) {
        Swal.fire({
            title: 'Enviando al SRI...',
            text: 'Espere un momento...',
            allowOutsideClick: false,
            didOpen: () => Swal.showLoading()
        });

        this.facturaService.enviarSRI(factura.facturaId).subscribe({
            next: (resp) => {
                const msj = resp.mensaje || '';
                if (msj.includes('RECIBIDA') || msj.includes('PROCESAMIENTO')) {
                    Swal.fire('Comprobante Recibido', msj, 'success');
                    this.cargarFacturas(); // Actualizar estado para habilitar btn Autorizar
                } else {
                    Swal.fire('Error en Recepción', msj, 'error');
                }
            },
            error: (err) => {
                console.error(err);
                Swal.fire('Error', 'Falló el envío al SRI', 'error');
            }
        });
    }

    // PASO 2: AUTORIZAR
    consultarAutorizacion(factura: Factura) {
        Swal.fire({
            title: 'Consultando Autorización...',
            text: 'Espere un momento...',
            allowOutsideClick: false,
            didOpen: () => Swal.showLoading()
        });

        this.facturaService.autorizarSRI(factura.facturaId).subscribe({
            next: (resp) => {
                const msj = resp.mensaje || '';
                if (msj.includes('AUTORIZADO') && !msj.includes('NO AUTORIZADO')) {
                    Swal.fire('¡Autorizado!', msj, 'success');
                    this.cargarFacturas();
                } else {
                    // Mostrar errores detallados si existen
                    Swal.fire({
                        title: 'No Autorizado',
                        html: `<pre style="text-align: left">${msj}</pre>`,
                        icon: 'warning'
                    });
                }
            },
            error: (err) => {
                console.error(err);
                Swal.fire('Error', 'Falló la consulta de autorización', 'error');
            }
        });
    }

    verXML(factura: Factura) {
        this.facturaService.obtenerXml(factura.facturaId).subscribe({
            next: (xml) => {
                // Formatear XML para mostrar
                const formattedXml = xml.replace(/</g, '&lt;').replace(/>/g, '&gt;');
                Swal.fire({
                    title: 'XML Generado',
                    html: `<pre style="text-align: left; max-height: 400px; overflow-y: auto; background: #f4f4f4; padding: 10px; border-radius: 5px;"><code>${formattedXml}</code></pre>`,
                    width: '800px',
                    confirmButtonText: 'Cerrar'
                });
            },
            error: (err) => {
                Swal.fire('Error', 'No se pudo obtener el XML. Asegúrate de que la factura haya sido generada.', 'error');
            }
        });
    }

    eliminar(factura: Factura) {
        Swal.fire({
            title: '¿Estás seguro?',
            text: "No podrás revertir esto. Si la factura ya fue enviada al SRI, deberás anularla allá también.",
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#d33',
            cancelButtonColor: '#3085d6',
            confirmButtonText: 'Sí, eliminar',
            cancelButtonText: 'Cancelar'
        }).then((result) => {
            if (result.isConfirmed) {
                this.facturaService.eliminar(factura.facturaId).subscribe({
                    next: () => {
                        Swal.fire('Eliminado!', 'La factura ha sido eliminada.', 'success');
                        this.cargarFacturas();
                    },
                    error: (err) => {
                        Swal.fire('Error', 'No se pudo eliminar la factura.', 'error');
                    }
                });
            }
        });
    }

    // --- VER DETALLE ---
    mostrarModalDetalle = false;
    facturaSeleccionada: Factura | null = null;

    verDetalle(factura: Factura) {
        this.facturaSeleccionada = factura;
        this.mostrarModalDetalle = true;
    }

    cerrarModalDetalle() {
        this.mostrarModalDetalle = false;
        this.facturaSeleccionada = null;
    }
}
