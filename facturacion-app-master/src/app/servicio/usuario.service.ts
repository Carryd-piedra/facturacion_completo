import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
    providedIn: 'root'
})
export class UsuarioService {

    private apiUrl = `${environment.apiUrl}/api/usuarios`;

    constructor(private http: HttpClient) { }

    listar(): Observable<any[]> {
        return this.http.get<any[]>(this.apiUrl);
    }

    crear(usuario: any): Observable<any> {
        return this.http.post<any>(this.apiUrl, usuario);
    }

    actualizar(id: number, usuario: any): Observable<any> {
        return this.http.put<any>(`${this.apiUrl}/${id}`, usuario);
    }

    eliminar(id: number): Observable<any> {
        return this.http.delete<any>(`${this.apiUrl}/${id}`);
    }
}
