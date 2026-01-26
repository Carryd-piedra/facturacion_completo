import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface DashboardStats {
    totalClientes: number;
    totalProductos: number;
    totalFacturas: number;
    totalVentas: number;
}

@Injectable({
    providedIn: 'root'
})
export class DashboardService {

    private apiUrl = `${environment.apiUrl}/api/dashboard`;

    constructor(private http: HttpClient) { }

    getStats(): Observable<DashboardStats> {
        return this.http.get<DashboardStats>(`${this.apiUrl}/stats`);
    }
}
