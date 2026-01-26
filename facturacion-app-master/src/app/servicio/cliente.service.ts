import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Cliente, ClienteConDocumentoDto } from '../modelos/cliente';
import { environment } from '../../environments/environment';

@Injectable({
    providedIn: 'root'
})
export class ClienteService {

    private apiUrl = `${environment.apiUrl}/api/cliente`;

    constructor(private http: HttpClient) { }

    listar(): Observable<Cliente[]> {
        return this.http.get<Cliente[]>(this.apiUrl);
    }

    guardar(cliente: ClienteConDocumentoDto): Observable<Cliente> {
        return this.http.post<Cliente>(this.apiUrl, cliente);
    }

    eliminar(id: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/${id}`);
    }

    buscarId(id: number): Observable<ClienteConDocumentoDto> {
        return this.http.get<ClienteConDocumentoDto>(`${this.apiUrl}/${id}`);
    }
}
