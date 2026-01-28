import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { environment } from '../../../environments/environment';
import { HttpClient } from '@angular/common/http';

@Component({
    selector: 'app-reporte',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './reporte.component.html'
})
export class ReporteComponent {

    private apiUrl = `${environment.apiUrl}/api/reporte`;

    constructor(private http: HttpClient) { }

    verReporte() {
        this.http.get(`${this.apiUrl}/ver`, { responseType: 'text' }).subscribe({
            next: (html) => {
                const win = window.open('', '_blank');
                if (win) {
                    win.document.write(html);
                    win.document.close();
                } else {
                    alert('Por favor habilita las ventanas emergentes para ver el reporte.');
                }
            },
            error: (err) => console.error('Error al obtener reporte HTML', err)
        });
    }

    descargarPdf() {
        this.http.get(`${this.apiUrl}/pdf`, { responseType: 'blob' }).subscribe({
            next: (blob) => {
                const url = window.URL.createObjectURL(blob);
                const a = document.createElement('a');
                a.href = url;
                a.download = 'reporte_db.pdf';
                a.click();
                window.URL.revokeObjectURL(url);
            },
            error: (err) => console.error('Error al descargar PDF', err)
        });
    }
}
