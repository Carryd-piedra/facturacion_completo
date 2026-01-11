import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Producto } from '../../modelos/producto';
import { ProductoService } from '../../servicio/producto.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-producto',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  templateUrl: './producto.component.html',
  styleUrl: './producto.component.css'
})
export class ProductoComponent implements OnInit {

  productos: Producto[] = [];
  productosFiltrados: Producto[] = [];
  productosPaginados: Producto[] = [];

  form!: FormGroup;
  editando = false;
  productoId?: number;


  filtro = '';


  paginaActual = 1;
  itemsPorPagina = 3;
  totalPaginas = 0;
  paginas: number[] = [];

  constructor(
    private productoService: ProductoService,
    private fb: FormBuilder
  ) { }

  ngOnInit(): void {
    this.cargarProductos();

    this.form = this.fb.group({
      productoSerial: ['', Validators.required],
      productoNombre: ['', Validators.required],
      productoPrecio: [0, Validators.required],
      productoStock: [0, Validators.required],
      productoTasa: [12],
      productoCategoria: ['', Validators.required],
      productoEstado: [1]
    });
  }

  cargarProductos() {
    this.productoService.listar().subscribe(data => {
      this.productos = data;
      this.productosFiltrados = [...data];
      this.configurarPaginacion();
    });
  }


  aplicarFiltro() {
    const texto = this.filtro.toLowerCase();

    this.productosFiltrados = this.productos.filter(p =>
      p.productoNombre.toLowerCase().includes(texto) ||
      p.productoCategoria.toLowerCase().includes(texto)
    );

    this.paginaActual = 1;
    this.configurarPaginacion();
  }


  configurarPaginacion() {
    this.totalPaginas = Math.ceil(this.productosFiltrados.length / this.itemsPorPagina);
    this.paginas = Array.from({ length: this.totalPaginas }, (_, i) => i + 1);
    this.actualizarPagina();
  }

  cambiarPagina(p: number) {
    if (p < 1 || p > this.totalPaginas) return;
    this.paginaActual = p;
    this.actualizarPagina();
  }

  actualizarPagina() {
    const inicio = (this.paginaActual - 1) * this.itemsPorPagina;
    const fin = inicio + this.itemsPorPagina;
    this.productosPaginados = this.productosFiltrados.slice(inicio, fin);
  }


  guardar() {
    if (this.form.invalid) return;

    if (this.editando && this.productoId) {
      this.productoService.actualizar(this.productoId, this.form.value)
        .subscribe({
          next: () => {
            Swal.fire({
              icon: 'success',
              title: 'Actualizado',
              text: 'Producto actualizado con éxito',
              timer: 1500,
              showConfirmButton: false
            });
            this.cancelar();
            this.cargarProductos();
          },
          error: () => Swal.fire('Error', 'No se pudo actualizar el producto', 'error')
        });
    } else {
      this.productoService.crear(this.form.value)
        .subscribe({
          next: () => {
            Swal.fire({
              icon: 'success',
              title: 'Guardado',
              text: 'Producto creado exitosamente',
              timer: 1500,
              showConfirmButton: false
            });
            this.form.reset({ productoEstado: 1, productoTasa: 12 });
            this.cargarProductos();
          },
          error: () => Swal.fire('Error', 'No se pudo crear el producto', 'error')
        });
    }
  }

  editar(p: Producto) {
    this.editando = true;
    this.productoId = p.productoId;
    this.form.patchValue(p);
  }

  eliminar(id?: number) {
    if (!id) return;

    Swal.fire({
      title: '¿Eliminar producto?',
      text: "Esta acción quitará el producto del catálogo",
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#e53e3e',
      cancelButtonColor: '#6b7280',
      confirmButtonText: 'Sí, eliminar',
      cancelButtonText: 'Cancelar',
      reverseButtons: true
    }).then((result) => {
      if (result.isConfirmed) {
        this.productoService.eliminar(id).subscribe({
          next: (res: any) => {
            Swal.fire({
              title: 'Eliminado',
              text: res.mensaje || 'El producto ha sido borrado.',
              icon: 'success',
              timer: 1500,
              showConfirmButton: false
            });
            this.cargarProductos();
          },
          error: (e: any) => {
            console.error('Error al eliminar producto', e);
            const msg = e.error?.mensaje || (typeof e.error === 'string' ? e.error : 'No se pudo eliminar el producto');
            Swal.fire('Error', msg, 'error');
          }
        });
      }
    });
  }

  cancelar() {
    this.editando = false;
    this.productoId = undefined;
    this.form.reset({ productoEstado: 1, productoTasa: 12 });
  }

  // VALIDATORS REAL-TIME
  validarSoloNumeros(event: any) {
    const input = event.target as HTMLInputElement;
    input.value = input.value.replace(/[^0-9]/g, '');
    this.form.get(input.getAttribute('formControlName')!)?.setValue(input.value);
  }

  validarSoloDecimales(event: any) {
    const input = event.target as HTMLInputElement;
    // Permite números y un solo punto decimal
    input.value = input.value.replace(/[^0-9.]/g, '').replace(/(\..*?)\..*/g, '$1');
    this.form.get(input.getAttribute('formControlName')!)?.setValue(input.value);
  }

  validarAlfanumerico(event: any) {
    const input = event.target as HTMLInputElement;
    // Permite letras, números, espacios y guiones
    input.value = input.value.replace(/[^a-zA-Z0-9\s-]/g, '');
    this.form.get(input.getAttribute('formControlName')!)?.setValue(input.value);
  }
}
