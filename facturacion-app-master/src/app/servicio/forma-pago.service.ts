import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { FormaPago } from '../modelos/forma-pago';

@Injectable({
    providedIn: 'root'
})
export class FormaPagoService {

    private apiUrl = 'http://localhost:9090/api/formapagos';

    constructor(private http: HttpClient) { }

    listar(): Observable<FormaPago[]> {
        return this.http.get<FormaPago[]>(this.apiUrl);
    }

    obtener(id: number): Observable<FormaPago> {
        return this.http.get<FormaPago>(`${this.apiUrl}/${id}`);
    }

    crear(formaPago: FormaPago): Observable<FormaPago> {
        return this.http.post<FormaPago>(this.apiUrl, formaPago);
    }

    actualizar(id: number, formaPago: FormaPago): Observable<FormaPago> {
        return this.http.put<FormaPago>(`${this.apiUrl}/${id}`, formaPago);
    }

    eliminar(id: number): Observable<any> {
        return this.http.delete(`${this.apiUrl}/${id}`);
    }
}
