import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Factura, FacturaRequestDTO } from '../modelos/factura';

@Injectable({
    providedIn: 'root'
})
export class FacturaService {

    private apiUrl = 'http://localhost:9090/api/facturas';

    constructor(private http: HttpClient) { }

    listar(): Observable<Factura[]> {
        return this.http.get<Factura[]>(this.apiUrl);
    }

    crear(factura: FacturaRequestDTO): Observable<any> {
        return this.http.post<any>(this.apiUrl, factura);
    }
}
