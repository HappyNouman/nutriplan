import { HttpClient } from '@angular/common/http';
import { computed, inject, Injectable, signal } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { API_URL } from '../api';
import { AuthResponse, LoginRequest, RegisterRequest, User } from './auth.models';

const TOKEN_KEY = 'nutriplan.token';
const USER_KEY = 'nutriplan.user';

/**
 * Keeps track of who is logged in.
 * The JWT and the user are saved in localStorage so the session survives a page refresh.
 */
@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);

  private readonly _token = signal<string | null>(localStorage.getItem(TOKEN_KEY));
  private readonly _user = signal<User | null>(readStoredUser());

  /** The logged-in user, or null. Components can read it like a value: auth.user() */
  readonly user = this._user.asReadonly();

  /** True when we have a token that has not expired yet. */
  readonly isLoggedIn = computed(() => {
    const token = this._token();
    return token !== null && !isExpired(token);
  });

  token(): string | null {
    return this._token();
  }

  register(request: RegisterRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${API_URL}/auth/register`, request)
      .pipe(tap((res) => this.saveSession(res)));
  }

  login(request: LoginRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${API_URL}/auth/login`, request)
      .pipe(tap((res) => this.saveSession(res)));
  }

  /** Reloads the current user from the backend (protected endpoint, needs the token). */
  loadMe(): Observable<User> {
    return this.http.get<User>(`${API_URL}/users/me`).pipe(tap((user) => this.saveUser(user)));
  }

  logout(): void {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    this._token.set(null);
    this._user.set(null);
    this.router.navigateByUrl('/login');
  }

  private saveSession(res: AuthResponse): void {
    localStorage.setItem(TOKEN_KEY, res.token);
    this._token.set(res.token);
    this.saveUser(res.user);
  }

  private saveUser(user: User): void {
    localStorage.setItem(USER_KEY, JSON.stringify(user));
    this._user.set(user);
  }
}

function readStoredUser(): User | null {
  try {
    const raw = localStorage.getItem(USER_KEY);
    return raw ? (JSON.parse(raw) as User) : null;
  } catch {
    return null;
  }
}

/** A JWT is "header.payload.signature"; the payload is Base64URL JSON containing "exp" (seconds). */
function isExpired(token: string): boolean {
  try {
    const payload = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/');
    const { exp } = JSON.parse(atob(payload)) as { exp?: number };
    return exp !== undefined && exp * 1000 <= Date.now();
  } catch {
    return true; // unreadable token -> treat as logged out
  }
}
