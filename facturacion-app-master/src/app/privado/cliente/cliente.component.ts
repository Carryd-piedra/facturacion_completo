import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ClienteService } from '../../servicio/cliente.service';
import { TipoDocumentoService } from '../../servicio/tipo-documento.service';
import { Cliente, TipoDocumento, ClienteConDocumentoDto } from '../../modelos/cliente';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-cliente',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './cliente.component.html',
  styleUrl: './cliente.component.css'
})
export class ClienteComponent implements OnInit {

  clientes: Cliente[] = [];
  tiposDocumento: TipoDocumento[] = [];
  form!: FormGroup;
  mostrarFormulario = false;
  editando = false;
  clienteSeleccionadoId?: number;

  constructor(
    private clienteService: ClienteService,
    private tipoDocumentoService: TipoDocumentoService,
    private fb: FormBuilder
  ) { }

  ngOnInit(): void {
    this.cargarClientes();
    this.cargarTiposDocumento();
    this.initForm();
  }

  cargarClientes() {
    this.clienteService.listar().subscribe(data => this.clientes = data);
  }

  cargarTiposDocumento() {
    this.tipoDocumentoService.listar().subscribe(data => this.tiposDocumento = data);
  }

  initForm() {
    this.form = this.fb.group({
      // Cliente Info
      // Cliente Info
      clienteNombre: ['', [Validators.required, Validators.minLength(3), Validators.pattern(/^[a-zA-ZñÑáéíóúÁÉÍÓÚ\s]+$/)]],
      clienteAplellido: ['', [Validators.required, Validators.pattern(/^[a-zA-ZñÑáéíóúÁÉÍÓÚ\s]+$/)]],
      clienteDirecion: ['', [Validators.required]],
      clienteTelefono: ['', [Validators.required, Validators.pattern(/^[0-9]{10}$/)]],
      clienteMail: ['', [Validators.required, Validators.email]],

      // Document Info
      tipoDocumentoId: [null, Validators.required],
      numeroDocumento: ['', [Validators.required, Validators.pattern(/^[0-9]+$/)]]
    });
  }

  guardar() {
    if (this.form.invalid) {
      Swal.fire({
        icon: 'warning',
        title: 'Formulario inválido',
        text: 'Por favor, revise los campos obligatorios.',
        confirmButtonColor: '#4f46e5'
      });
      return;
    }

    const val = this.form.value;

    const dto: ClienteConDocumentoDto = {
      cliente: {
        clienteId: this.clienteSeleccionadoId,
        clienteNombre: val.clienteNombre,
        clienteAplellido: val.clienteAplellido,
        clienteDirecion: val.clienteDirecion,
        clienteTelefono: val.clienteTelefono,
        clienteMail: val.clienteMail,
        clienteEstado: 1
      },
      documentos: [
        {
          tipoDocumentoId: val.tipoDocumentoId,
          numeroDocumentoCliente: val.numeroDocumento
        }
      ]
    };

    const operacion = this.editando ? this.clienteService.guardar(dto) : this.clienteService.guardar(dto);
    // Nota: El backend maneja el update si el clienteId está presente en el objeto cliente

    this.clienteService.guardar(dto).subscribe({
      next: () => {
        Swal.fire({
          icon: 'success',
          title: this.editando ? '¡Actualizado!' : '¡Guardado!',
          text: this.editando ? 'Cliente actualizado exitosamente' : 'Cliente guardado exitosamente',
          timer: 2000,
          showConfirmButton: false
        });
        this.mostrarFormulario = false;
        this.editando = false;
        this.clienteSeleccionadoId = undefined;
        this.form.reset();
        this.cargarClientes();
      },
      error: (err) => {
        Swal.fire({
          icon: 'error',
          title: 'Error',
          text: 'No se pudo procesar la solicitud: ' + (err.message || err.error)
        });
      }
    });
  }

  editar(cliente: Cliente) {
    this.editando = true;
    this.clienteSeleccionadoId = cliente.clienteId;
    this.mostrarFormulario = true;

    // Buscamos el documento del cliente (asumiendo que tiene al menos uno)
    // Para simplificar, si no tenemos los documentos aquí, el form los cargará
    // Pero en el listado 'clientes' suele venir el objeto básico.
    // Necesitaríamos buscar el detalle si el DTO lo requiere.

    this.form.patchValue({
      clienteNombre: cliente.clienteNombre,
      clienteAplellido: cliente.clienteAplellido,
      clienteDirecion: cliente.clienteDirecion,
      clienteTelefono: cliente.clienteTelefono,
      clienteMail: cliente.clienteMail,
      // Nota: El tipoDocumentoId y numeroDocumento se cargarán si el objeto 'c' los trae
      // Pero 'Cliente' interface en modelos/cliente.ts parece no tenerlos directamente.
    });

    // Si el objeto cliente tiene la lista de documentos (según interfaz DocumentoListaClienteDto)
    // Intentamos parchear el primero
    this.clienteService.buscarId(cliente.clienteId!).subscribe(resp => {
      if (resp && resp.documentos && resp.documentos.length > 0) {
        this.form.patchValue({
          tipoDocumentoId: resp.documentos[0].tipoDocumentoId,
          numeroDocumento: resp.documentos[0].numeroDocumentoCliente
        });
      }
    });
  }

  eliminar(id?: number) {
    if (!id) return;

    Swal.fire({
      title: '¿Está seguro?',
      text: "Esta acción no se puede deshacer",
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#e53e3e',
      cancelButtonColor: '#6b7280',
      confirmButtonText: 'Sí, eliminar',
      cancelButtonText: 'Cancelar',
      reverseButtons: true
    }).then((result) => {
      if (result.isConfirmed) {
        this.clienteService.eliminar(id).subscribe({
          next: (res: any) => {
            Swal.fire({
              title: 'Eliminado',
              text: res.mensaje || 'El cliente ha sido eliminado.',
              icon: 'success',
              timer: 1500,
              showConfirmButton: false
            });
            this.cargarClientes();
          },
          error: (e: any) => {
            console.error('Error al eliminar cliente', e);
            const msg = e.error?.mensaje || (typeof e.error === 'string' ? e.error : 'No se pudo eliminar el cliente');
            Swal.fire('Error', msg, 'error');
          }
        });
      }
    });
  }

  toggleForm() {
    this.mostrarFormulario = !this.mostrarFormulario;
  }

  // VALIDADORES EN TIEMPO REAL (Prevent Default)
  validarSoloLetras(event: any) {
    const input = event.target as HTMLInputElement;
    // Reemplaza todo lo que NO sea letras (incluyendo tildes y ñ) o espacios
    input.value = input.value.replace(/[^a-zA-ZñÑáéíóúÁÉÍÓÚ\s]/g, '');
    this.form.get(input.getAttribute('formControlName')!)?.setValue(input.value);
  }

  validarSoloNumeros(event: any) {
    const input = event.target as HTMLInputElement;
    input.value = input.value.replace(/[^0-9]/g, '');
    this.form.get(input.getAttribute('formControlName')!)?.setValue(input.value);
  }
}
