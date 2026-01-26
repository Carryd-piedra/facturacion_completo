import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Factura, FacturaRequestDTO } from '../modelos/factura';
import { environment } from '../../environments/environment';

@Injectable({
    providedIn: 'root'
})
export class FacturaService {

    private apiUrl = `${environment.apiUrl}/api/facturas`;

    constructor(private http: HttpClient) { }

    listar(): Observable<Factura[]> {
        return this.http.get<Factura[]>(this.apiUrl);
    }

    enviarSRI(id: number): Observable<any> {
        return this.http.post<any>(`${this.apiUrl}/${id}/enviar`, {});
    }

    autorizarSRI(id: number): Observable<any> {
        return this.http.post<any>(`${this.apiUrl}/${id}/autorizar`, {});
    }

    crear(factura: FacturaRequestDTO): Observable<any> {
        return this.http.post<any>(this.apiUrl, factura);
    }

    obtenerXml(id: number): Observable<string> {
        return this.http.get(`${this.apiUrl}/${id}/xml`, { responseType: 'text' });
    }

    eliminar(id: number): Observable<any> {
        return this.http.delete(`${this.apiUrl}/${id}`);
    }
}
