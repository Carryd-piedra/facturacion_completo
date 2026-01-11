import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators, FormsModule } from '@angular/forms';
import { TipoDocumentoService } from '../../servicio/tipo-documento.service';
import { TipoDocumento } from '../../modelos/cliente';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-documento',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  templateUrl: './documento.component.html',
  styleUrl: './documento.component.css'
})
export class DocumentoComponent implements OnInit {

  tipos: TipoDocumento[] = [];
  tiposFiltrados: TipoDocumento[] = [];
  form!: FormGroup;
  mostrarFormulario = false;
  editando = false;
  filtro = '';

  codigosSri = [
    { codigo: '04', nombre: 'RUC' },
    { codigo: '05', nombre: 'Cédula' },
    { codigo: '06', nombre: 'Pasaporte' },
    { codigo: '07', nombre: 'Consumidor Final' },
    { codigo: '08', nombre: 'Identificación del exterior' }
  ];

  constructor(
    private tipoDocumentoService: TipoDocumentoService,
    private fb: FormBuilder
  ) { }

  ngOnInit(): void {
    this.cargarTipos();
    this.initForm();
  }

  cargarTipos() {
    this.tipoDocumentoService.listar().subscribe(data => {
      this.tipos = data;
      this.aplicarFiltro();
    });
  }

  initForm() {
    this.form = this.fb.group({
      tipoDocumentoId: [null],
      tipoDocumentoNombre: ['', Validators.required],
      tipoDocumentoCodigo: ['', Validators.required]
    });
  }

  aplicarFiltro() {
    if (!this.filtro) {
      this.tiposFiltrados = [...this.tipos];
    } else {
      const f = this.filtro.toLowerCase();
      this.tiposFiltrados = this.tipos.filter(t =>
        (t.tipoDocumentoNombre?.toLowerCase().includes(f)) ||
        (t.tipoDocumentoCodigo?.toLowerCase().includes(f))
      );
    }
  }

  guardar() {
    if (this.form.invalid) {
      Swal.fire({
        icon: 'warning',
        title: 'Campos incompletos',
        text: 'Por favor complete todos los campos requeridos.',
        confirmButtonColor: '#4f46e5'
      });
      return;
    }

    const datos = this.form.value;
    this.tipoDocumentoService.guardar(datos).subscribe({
      next: () => {
        Swal.fire({
          icon: 'success',
          title: this.editando ? '¡Actualizado!' : '¡Guardado!',
          text: this.editando ? 'Documento actualizado exitosamente' : 'Documento guardado con éxito',
          timer: 2000,
          showConfirmButton: false
        });
        this.cargarTipos();
        this.cancelar();
      },
      error: (err) => {
        Swal.fire({
          icon: 'error',
          title: 'Error',
          text: 'No se pudo guardar: ' + (err.message || err.error)
        });
      }
    });
  }

  editar(t: TipoDocumento): void {
    this.mostrarFormulario = true;
    this.editando = true;
    this.form.patchValue({
      tipoDocumentoId: t.tipoDocumentoId,
      tipoDocumentoNombre: t.tipoDocumentoNombre,
      tipoDocumentoCodigo: t.tipoDocumentoCodigo
    });
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  eliminar(id: number): void {
    Swal.fire({
      title: '¿Eliminar documento?',
      text: "Esta acción no se puede revertir",
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#e53e3e',
      cancelButtonColor: '#6b7280',
      confirmButtonText: 'Sí, eliminar',
      cancelButtonText: 'Cancelar',
      reverseButtons: true
    }).then((result) => {
      if (result.isConfirmed) {
        this.tipoDocumentoService.eliminar(id).subscribe({
          next: (res: any) => {
            Swal.fire({
              title: 'Eliminado',
              text: res.mensaje || 'El registro ha sido borrado.',
              icon: 'success',
              timer: 1500,
              showConfirmButton: false
            });
            this.cargarTipos();
          },
          error: (e: any) => {
            console.error('Error al eliminar documento', e);
            const msg = e.error?.mensaje || (typeof e.error === 'string' ? e.error : 'No se pudo eliminar el documento');
            Swal.fire('Error', msg, 'error');
          }
        });
      }
    });
  }

  cancelar() {
    this.mostrarFormulario = false;
    this.editando = false;
    this.form.reset({ tipoDocumentoCodigo: '' });
  }

  toggleForm() {
    this.mostrarFormulario = !this.mostrarFormulario;
    if (!this.mostrarFormulario) {
      this.cancelar();
    }
  }
}
