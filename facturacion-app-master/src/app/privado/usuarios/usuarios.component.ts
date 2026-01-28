import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { UsuarioService } from '../../servicio/usuario.service';
import { firstValueFrom } from 'rxjs';

@Component({
    selector: 'app-usuarios',
    standalone: true,
    imports: [CommonModule, ReactiveFormsModule],
    templateUrl: './usuarios.component.html',
    styleUrl: './usuarios.component.css'
})
export class UsuariosComponent implements OnInit {
    usuarios: any[] = [];
    usuarioForm: FormGroup;
    mostrarFormulario = false;
    procesando = false;
    error = '';

    roles = ['Admin', 'Contador', 'Vendedor'];

    usuarioEditarId: number | null = null; // ID si se esta editando

    constructor(
        private usuarioService: UsuarioService,
        private fb: FormBuilder
    ) {
        this.usuarioForm = this.fb.group({
            nombre: ['', Validators.required],
            username: ['', Validators.required],
            email: ['', [Validators.required, Validators.email]],
            password: ['', [Validators.required, Validators.minLength(4)]],
            rol: ['Vendedor', Validators.required]
        });
    }

    ngOnInit(): void {
        this.listarUsuarios();
    }

    listarUsuarios() {
        this.usuarioService.listar().subscribe({
            next: (data) => this.usuarios = data,
            error: (e) => console.error(e)
        });
    }

    nuevoUsuario() {
        this.mostrarFormulario = true;
        this.usuarioEditarId = null;
        this.usuarioForm.reset({ rol: 'Vendedor' });

        // Contraseña obligatoria al crear
        this.usuarioForm.get('password')?.setValidators([Validators.required, Validators.minLength(4)]);
        this.usuarioForm.get('password')?.updateValueAndValidity();

        this.error = '';
    }

    editar(usuario: any) {
        this.mostrarFormulario = true;
        this.usuarioEditarId = usuario.id;
        this.usuarioForm.patchValue({
            nombre: usuario.nombre,
            username: usuario.username,
            email: usuario.correo,
            rol: usuario.tipoUsuario?.rol,
            password: '' // Limpiar password
        });

        // Contraseña opcional al editar
        this.usuarioForm.get('password')?.clearValidators();
        this.usuarioForm.get('password')?.updateValueAndValidity();

        this.error = '';
    }

    cancelar() {
        this.mostrarFormulario = false;
        this.usuarioEditarId = null;
        this.error = '';
    }

    async guardar() {
        if (this.usuarioForm.invalid) return;

        this.procesando = true;
        this.error = '';

        try {
            if (this.usuarioEditarId) {
                await firstValueFrom(this.usuarioService.actualizar(this.usuarioEditarId, this.usuarioForm.value));
            } else {
                await firstValueFrom(this.usuarioService.crear(this.usuarioForm.value));
            }
            this.mostrarFormulario = false;
            this.listarUsuarios();
        } catch (e: any) {
            if (e.error && e.error.message) {
                this.error = e.error.message;
            } else if (typeof e.error === 'string') {
                this.error = e.error;
            } else {
                this.error = 'Error al guardar usuario';
            }
        } finally {
            this.procesando = false;
        }
    }

    eliminar(id: number) {
        if (!confirm('¿Estás seguro de eliminar este usuario?')) return;

        this.usuarioService.eliminar(id).subscribe({
            next: () => this.listarUsuarios(),
            error: (e) => alert('No se pudo eliminar el usuario')
        });
    }
}
