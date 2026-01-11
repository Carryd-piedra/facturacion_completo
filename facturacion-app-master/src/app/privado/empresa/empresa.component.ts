import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Empresa } from '../../modelos/empresa';
import { EmpresaService } from '../../servicio/empresa.service';
import Swal from 'sweetalert2';
import { CommonModule } from '@angular/common';

@Component({
    selector: 'app-empresa',
    standalone: true,
    imports: [CommonModule, ReactiveFormsModule],
    templateUrl: './empresa.component.html',
    styleUrl: './empresa.component.css'
})
export class EmpresaComponent implements OnInit {

    empresas: Empresa[] = [];
    form: FormGroup;
    mostrarFormulario = false;
    editando = false;
    empresaIdSeleccionada: number | null = null;

    constructor(
        private empresaService: EmpresaService,
        private fb: FormBuilder
    ) {
        this.form = this.fb.group({
            razonSocial: ['', [Validators.required]],
            nombreComercial: ['', [Validators.required]],
            ruc: ['', [Validators.required, Validators.pattern(/^[0-9]+$/), Validators.minLength(13), Validators.maxLength(13)]],
            dirMatriz: ['', Validators.required],
            dirEstablecimiento: ['', Validators.required],
            establecimiento: ['001', [Validators.required, Validators.pattern(/^[0-9]+$/)]],
            puntoEmision: ['001', [Validators.required, Validators.pattern(/^[0-9]+$/)]],
            ambiente: [1, Validators.required],
            tipoEmision: [1, Validators.required],
            obligadoContabilidad: ['NO', Validators.required],
            rutaFirma: ['', Validators.required],
            claveFirma: ['', Validators.required],
            contribuyenteEspecial: [''], // Opcional
            resolucion: [''] // Opcional
        });
    }

    ngOnInit(): void {
        this.cargarEmpresas();
    }

    cargarEmpresas() {
        this.empresaService.listar().subscribe({
            next: (data) => this.empresas = data,
            error: (err) => console.error('Error al cargar empresas', err)
        });
    }

    guardar() {
        if (this.form.invalid) {
            this.form.markAllAsTouched();
            return;
        }

        const empresa: Empresa = this.form.value;

        if (this.editando && this.empresaIdSeleccionada) {
            this.empresaService.actualizar(this.empresaIdSeleccionada, empresa).subscribe({
                next: () => {
                    Swal.fire('Actualizado', 'La empresa ha sido actualizada', 'success');
                    this.resetForm();
                    this.cargarEmpresas();
                },
                error: () => Swal.fire('Error', 'No se pudo actualizar', 'error')
            });
        } else {
            this.empresaService.crear(empresa).subscribe({
                next: () => {
                    Swal.fire('Creado', 'Empresa registrada correctamente', 'success');
                    this.resetForm();
                    this.cargarEmpresas();
                },
                error: () => Swal.fire('Error', 'No se pudo crear la empresa', 'error')
            });
        }
    }

    editar(empresa: Empresa) {
        this.editando = true;
        this.mostrarFormulario = true;
        this.empresaIdSeleccionada = empresa.empresaId!;
        this.form.patchValue(empresa);
    }

    eliminar(id: number) {
        Swal.fire({
            title: '¿Estás seguro?',
            text: "No podrás revertir esto",
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#3085d6',
            cancelButtonColor: '#d33',
            confirmButtonText: 'Sí, eliminar'
        }).then((result) => {
            if (result.isConfirmed) {
                this.empresaService.eliminar(id).subscribe({
                    next: () => {
                        Swal.fire('Eliminado', 'La empresa ha sido eliminada', 'success');
                        this.cargarEmpresas();
                    },
                    error: () => Swal.fire('Error', 'No se pudo eliminar', 'error')
                });
            }
        });
    }

    toggleForm() {
        this.mostrarFormulario = !this.mostrarFormulario;
        if (!this.mostrarFormulario) {
            this.resetForm();
        }
    }

    resetForm() {
        this.form.reset({
            establecimiento: '001',
            puntoEmision: '001',
            ambiente: 1,
            tipoEmision: 1,
            obligadoContabilidad: 'NO'
        });
        this.editando = false;
        this.empresaIdSeleccionada = null;
        this.mostrarFormulario = false;
    }

    // VALIDATORS REAL-TIME
    validarSoloNumeros(event: any) {
        const input = event.target as HTMLInputElement;
        input.value = input.value.replace(/[^0-9]/g, '');
        this.form.get(input.getAttribute('formControlName')!)?.setValue(input.value);
    }
}
