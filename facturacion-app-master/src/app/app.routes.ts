import { Routes } from '@angular/router';

import { InicioComponent } from './publico/paginas/inicio/inicio.component';
import { ProductosComponent } from './publico/paginas/productos/productos.component';
import { ContactanosComponent } from './publico/paginas/contactanos/contactanos.component';
import { PublicoLayoutComponent } from './publico/layout/publico-layout/publico-layout.component';
import { authGuard } from './auth/auth.guard';
import { PrivadoLayoutComponent } from './privado/privado-layout/privado-layout.component';
import { LoginComponent } from './auth/login/login.component';
import { DashboardComponent } from './privado/dashboard/dashboard.component';
import { DocumentoComponent } from './privado/documento/documento.component';
import { ClienteComponent } from './privado/cliente/cliente.component';
import { FormapagoComponent } from './privado/formapago/formapago.component';
import { ProductoComponent } from './privado/producto/producto.component';

export const routes: Routes = [


  {
    path: '',
    component: PublicoLayoutComponent,
    children: [
      { path: '', component: InicioComponent },
      { path: 'productos', component: ProductosComponent },
      { path: 'contacto', component: ContactanosComponent }
    ]
  },
  {
    path: 'login',
    component: LoginComponent
  },

  {
    path: '',
    component: PrivadoLayoutComponent,
    canActivate: [authGuard],
    children: [
      { path: 'dashboard', component: DashboardComponent },
      { path: 'documentos', component: DocumentoComponent },
      { path: 'productos-admin', component: ProductoComponent },
      { path: 'clientes', component: ClienteComponent },
      { path: 'formas-pago', component: FormapagoComponent },
      { path: 'empresas', loadComponent: () => import('./privado/empresa/empresa.component').then(m => m.EmpresaComponent) },
      { path: 'facturas', loadComponent: () => import('./privado/factura/factura.component').then(m => m.FacturaComponent) }
    ]
  },
  { path: '**', redirectTo: '' }

];
