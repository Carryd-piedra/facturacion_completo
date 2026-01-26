import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { environment } from '../../../environments/environment';

@Component({
    selector: 'app-reporte',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './reporte.component.html'
})
export class ReporteComponent {

    private apiUrl = `${environment.apiUrl}/api/reporte`;

    verReporte() {
        window.open(`${this.apiUrl}/ver`, '_blank');
    }

    descargarPdf() {
        window.open(`${this.apiUrl}/pdf`, '_blank');
    }
}
