import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { TipoDocumento } from '../modelos/cliente';

@Injectable({
    providedIn: 'root'
})
export class TipoDocumentoService {

    private apiUrl = 'http://localhost:9090/api/tipodocumento';

    constructor(private http: HttpClient) { }

    listar(): Observable<TipoDocumento[]> {
        return this.http.get<TipoDocumento[]>(this.apiUrl);
    }

    guardar(tipo: TipoDocumento): Observable<TipoDocumento> {
        return this.http.post<TipoDocumento>(this.apiUrl, tipo);
    }

    eliminar(id: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/${id}`);
    }
}
