import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from './auth.service';

export const authGuard: CanActivateFn = (route, state) => {

  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isTokenValid()) {

    // Verificar roles si la ruta los requiere
    const expectedRoles = route.data['roles'] as string[];
    if (expectedRoles && expectedRoles.length > 0) {
      if (authService.hasAnyRole(expectedRoles) || authService.getRole() === 'Admin') {
        return true;
      } else {
        // Redirigir si no tiene permisos (ej. al dashboard o login)
        router.navigate(['/dashboard']);
        return false;
      }
    }
    return true;
  }

  authService.logout();
  router.navigate(['/login']);
  return false;
};
