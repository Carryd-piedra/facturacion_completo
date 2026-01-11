import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

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

    private apiUrl = 'http://localhost:9090/api/dashboard';

    constructor(private http: HttpClient) { }

    getStats(): Observable<DashboardStats> {
        return this.http.get<DashboardStats>(`${this.apiUrl}/stats`);
    }
}
