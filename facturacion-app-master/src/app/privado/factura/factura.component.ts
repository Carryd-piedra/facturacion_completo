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
            fechaEmision: [new Date().toISOString().substring(0, 10), Validators.required],
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
            detalle.patchValue({
                productoId: producto.productoId,
                nombreProducto: producto.productoNombre, // Campo auxiliar para mostrar nombre
                precioUnitario: producto.productoPrecio
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
                tarifa: [12],
                baseImponible: [0],
                valor: [0]
            })
        });

        detalle.get('cantidad')?.valueChanges.subscribe(() => this.calcularLinea(detalle));
        this.detalles.push(detalle);
    }

    eliminarDetalle(index: number) {
        this.detalles.removeAt(index);
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

    calcularLinea(group: FormGroup | any) {
        const cant = group.get('cantidad').value || 0;
        const precio = group.get('precioUnitario').value || 0;
        const subtotal = cant * precio;
        group.patchValue({ subtotal: subtotal }, { emitEvent: false });

        const baseImponible = subtotal;
        const valorIva = baseImponible * 0.12;

        group.get('impuesto').patchValue({
            baseImponible: baseImponible,
            valor: valorIva
        }, { emitEvent: false });
    }

    guardar() {
        if (this.form.invalid) {
            Swal.fire({
                icon: 'warning',
                title: 'Factura incompleta',
                text: 'Por favor complete todos los campos requeridos.',
                confirmButtonColor: '#4f46e5'
            });
            return;
        }

        const formValue = this.form.value;

        let subtotal12 = 0;
        let totalIva = 0;

        formValue.detalles.forEach((d: any) => {
            subtotal12 += d.subtotal;
            totalIva += d.impuesto.valor;
        });

        const totalFactura = subtotal12 + totalIva;

        const factura: FacturaRequestDTO = {
            ...formValue,
            secuencial: '', // Backend lo genera
            subtotal12: subtotal12,
            subtotal0: 0,
            subtotalNoObjeto: 0,
            subtotalExento: 0,
            totalDescuento: 0,
            totalIva: totalIva,
            totalFactura: totalFactura
        };

        if (this.pagos.length > 0) {
            this.pagos.at(0).patchValue({ total: totalFactura });
            factura.pagos[0].total = totalFactura;
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
}
