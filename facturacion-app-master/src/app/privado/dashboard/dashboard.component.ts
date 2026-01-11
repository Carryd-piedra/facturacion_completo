import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DashboardService, DashboardStats } from '../../servicio/dashboard.service';
import { FacturaService } from '../../servicio/factura.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit {
  stats: DashboardStats = {
    totalClientes: 0,
    totalProductos: 0,
    totalFacturas: 0,
    totalVentas: 0
  };
  recentInvoices: any[] = [];

  constructor(
    private dashboardService: DashboardService,
    private facturaService: FacturaService
  ) { }

  ngOnInit(): void {
    this.loadStats();
    this.loadRecentInvoices();
  }

  loadStats() {
    this.dashboardService.getStats().subscribe({
      next: (data) => this.stats = data,
      error: (e) => console.error('Error fetching dashboard stats', e)
    });
  }

  loadRecentInvoices() {
    this.facturaService.listar().subscribe({
      next: (data) => {
        // Just show the last 5 invoices
        this.recentInvoices = data.slice(-5).reverse();
      },
      error: (e) => console.error('Error fetching recent invoices', e)
    });
  }
}
