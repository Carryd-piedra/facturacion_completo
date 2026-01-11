import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Empresa } from '../modelos/empresa';

@Injectable({
    providedIn: 'root'
})
export class EmpresaService {

    private apiUrl = `${environment.apiUrl}/api/empresas`;

    constructor(private http: HttpClient) { }

    listar(): Observable<Empresa[]> {
        return this.http.get<Empresa[]>(this.apiUrl);
    }

    obtener(id: number): Observable<Empresa> {
        return this.http.get<Empresa>(`${this.apiUrl}/${id}`);
    }

    crear(empresa: Empresa): Observable<Empresa> {
        return this.http.post<Empresa>(this.apiUrl, empresa);
    }

    actualizar(id: number, empresa: Empresa): Observable<Empresa> {
        return this.http.put<Empresa>(`${this.apiUrl}/${id}`, empresa);
    }

    eliminar(id: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/${id}`);
    }
}
