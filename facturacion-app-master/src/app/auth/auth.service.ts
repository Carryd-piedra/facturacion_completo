import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) { }

  login(username: string, password: string): Observable<any> {
    return this.http.post<any>(
      `${this.apiUrl}/auth/login`,
      { username, password }
    ).pipe(
      tap(response => {
        localStorage.setItem('token', response.token);
        localStorage.setItem('username', response.username);

        // Decodificar token para guardar rol (opcional, o extraerlo bajo demanda)
        const payload = JSON.parse(atob(response.token.split('.')[1]));
        localStorage.setItem('role', payload.rol);
      })
    );
  }

  logout(): void {
    localStorage.clear();
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  getRole(): string | null {
    return localStorage.getItem('role');
  }

  // Verifica si el usuario tiene alguno de los roles permitidos
  hasAnyRole(allowedRoles: string[]): boolean {
    const userRole = this.getRole();
    if (!userRole) return false;
    if (userRole === 'Admin') return true; // El Admin tiene acceso a todo
    return allowedRoles.includes(userRole);
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }
  isTokenValid(): boolean {
    const token = this.getToken();
    if (!token) return false;

    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      const exp = payload.exp * 1000; // segundos → ms
      return Date.now() < exp;
    } catch {
      return false;
    }
  }
}
