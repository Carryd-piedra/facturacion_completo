import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { FormaPagoService } from '../../servicio/forma-pago.service';
import { FormaPago } from '../../modelos/forma-pago';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-formapago',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './formapago.component.html',
  styleUrl: './formapago.component.css'
})
export class FormapagoComponent implements OnInit {
  formasPago: FormaPago[] = [];
  form: FormGroup;
  mostrarFormulario = false;
  editando = false;
  formaPagoIdSeleccionada?: number;

  constructor(
    private fb: FormBuilder,
    private formaPagoService: FormaPagoService
  ) {
    this.form = this.fb.group({
      nombre: ['', [Validators.required]],
      codigoSri: ['', [Validators.required]]
    });
  }

  ngOnInit(): void {
    this.listar();
  }

  listar() {
    this.formaPagoService.listar().subscribe({
      next: (data) => this.formasPago = data,
      error: (e) => console.error('Error al listar formas de pago', e)
    });
  }

  toggleForm() {
    this.mostrarFormulario = !this.mostrarFormulario;
    if (!this.mostrarFormulario) {
      this.resetForm();
    }
  }

  resetForm() {
    this.form.reset();
    this.editando = false;
    this.formaPagoIdSeleccionada = undefined;
  }

  guardar() {
    if (this.form.invalid) return;

    const formaPago = this.form.value;

    if (this.editando && this.formaPagoIdSeleccionada) {
      this.formaPagoService.actualizar(this.formaPagoIdSeleccionada, formaPago).subscribe({
        next: () => {
          Swal.fire('Éxito', 'Forma de pago actualizada', 'success');
          this.listar();
          this.toggleForm();
        },
        error: (e) => Swal.fire('Error', 'No se pudo actualizar', 'error')
      });
    } else {
      this.formaPagoService.crear(formaPago).subscribe({
        next: () => {
          Swal.fire('Éxito', 'Forma de pago guardada', 'success');
          this.listar();
          this.toggleForm();
        },
        error: (e) => Swal.fire('Error', 'No se pudo guardar', 'error')
      });
    }
  }

  editar(fp: FormaPago) {
    this.editando = true;
    this.formaPagoIdSeleccionada = fp.formaPagoId;
    this.form.patchValue({
      nombre: fp.nombre,
      codigoSri: fp.codigoSri
    });
    this.mostrarFormulario = true;
  }

  eliminar(id?: number) {
    if (!id) return;

    Swal.fire({
      title: '¿Estás seguro?',
      text: "No podrás revertir esto",
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: 'Sí, eliminar',
      cancelButtonText: 'Cancelar'
    }).then((result) => {
      if (result.isConfirmed) {
        this.formaPagoService.eliminar(id).subscribe({
          next: (res: any) => {
            Swal.fire('Eliminado', res.mensaje || 'La forma de pago ha sido eliminada', 'success');
            this.listar();
          },
          error: (e) => {
            console.error('Error al eliminar', e);
            const msg = e.error?.mensaje || (typeof e.error === 'string' ? e.error : 'No se pudo eliminar la forma de pago');
            Swal.fire('Error', msg, 'error');
          }
        });
      }
    });
  }
}
