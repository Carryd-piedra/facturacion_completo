import { Component, OnInit } from '@angular/core';
import { CommonModule, CurrencyPipe, DatePipe } from '@angular/common';
import { FormArray, FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { FacturaService } from '../../servicio/factura.service';
import { Factura, FacturaRequestDTO, Producto } from '../../modelos/factura';
import { Cliente } from '../../modelos/cliente';
import { ClienteService } from '../../servicio/cliente.service';
import { ProductoService } from '../../servicio/producto.service';
import { FormaPagoService } from '../../servicio/forma-pago.service';
import Swal from 'sweetalert2';

@Component({
    selector: 'app-factura',
    standalone: true,
    imports: [CommonModule, ReactiveFormsModule, CurrencyPipe, DatePipe],
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

    constructor(
        private facturaService: FacturaService,
        private clienteService: ClienteService,
        private productoService: ProductoService,
        private formaPagoService: FormaPagoService,
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
        this.clienteService.listar().subscribe(data => this.clientes = data);
        this.productoService.listar().subscribe(data => this.productos = data);
        this.formaPagoService.listar().subscribe(data => this.formasPago = data);
    }

    initForm() {
        this.form = this.fb.group({
            clienteId: [null, Validators.required],
            empresaId: [1, Validators.required], // Default to 1 for now
            secuencial: ['000000001', Validators.required], // Should be auto-generated or managed
            fechaEmision: [new Date().toISOString(), Validators.required],
            detalles: this.fb.array([]),
            pagos: this.fb.array([])
        });
        this.agregarDetalle();
        this.agregarPago();
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
            cantidad: [1, [Validators.required, Validators.min(1)]],
            precioUnitario: [0], // Read-only, populated from product
            descuento: [0],
            subtotal: [0],
            impuesto: this.fb.group({
                codigo: ['2'], // IVA
                codigoPorcentaje: ['2'], // 12%
                tarifa: [12],
                baseImponible: [0],
                valor: [0]
            })
        });

        // Listen for changes to update totals
        detalle.get('productoId')?.valueChanges.subscribe(id => {
            const prod = this.productos.find(p => p.productoId == id);
            if (prod) {
                detalle.patchValue({ precioUnitario: prod.productoPrecio });
                this.calcularLinea(detalle);
            }
        });

        detalle.get('cantidad')?.valueChanges.subscribe(() => this.calcularLinea(detalle));

        this.detalles.push(detalle);
    }

    eliminarDetalle(index: number) {
        this.detalles.removeAt(index);
    }

    agregarPago() {
        const pago = this.fb.group({
            formaPagoId: [1, Validators.required], // Default "Sin utilizacion sistema financiero" usually 1 or 01
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

        // Calculate taxes (Mocked for 12% IVA default)
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
                text: 'Por favor revise los datos del cliente y los detalles de la factura.',
                confirmButtonColor: '#4f46e5'
            });
            return;
        }

        const formValue = this.form.value;

        // Calculate totals for the main header
        let subtotal12 = 0;
        let totalIva = 0;

        formValue.detalles.forEach((d: any) => {
            subtotal12 += d.subtotal;
            totalIva += d.impuesto.valor;
        });

        const totalFactura = subtotal12 + totalIva;

        const factura: FacturaRequestDTO = {
            ...formValue,
            subtotal12: subtotal12,
            subtotal0: 0,
            subtotalNoObjeto: 0,
            subtotalExento: 0,
            totalDescuento: 0,
            totalIva: totalIva,
            totalFactura: totalFactura
        };

        // Ensure payments match total
        if (this.pagos.length > 0) {
            this.pagos.at(0).patchValue({ total: totalFactura });
            factura.pagos[0].total = totalFactura;
        }

        this.facturaService.crear(factura).subscribe({
            next: () => {
                Swal.fire({
                    icon: 'success',
                    title: '¡Factura Generada!',
                    text: 'El documento se ha creado y registrado correctamente.',
                    timer: 2500,
                    showConfirmButton: false
                });
                this.mostrarFormulario = false;
                this.initForm();
                this.cargarFacturas();
            },
            error: (err) => {
                Swal.fire({
                    icon: 'error',
                    title: 'Error de Facturación',
                    text: 'No se pudo generar la factura: ' + (err.message || err.error)
                });
            }
        });
    }

    toggleForm() {
        this.mostrarFormulario = !this.mostrarFormulario;
    }
}
