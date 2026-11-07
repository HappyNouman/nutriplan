import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { API_URL } from '../api';
import { AuthService } from './auth.service';

/**
 * Runs on every HTTP request:
 * 1. Adds "Authorization: Bearer <token>" to calls to our API.
 * 2. If the backend answers 401 (token expired/invalid), logs the user out.
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const token = auth.token();
  const isOurApi = req.url.startsWith(API_URL);

  const request =
    token && isOurApi ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } }) : req;

  return next(request).pipe(
    catchError((err: HttpErrorResponse) => {
      // A 401 from /auth/login just means "wrong password", not "session expired"
      if (err.status === 401 && token && !req.url.includes('/auth/')) {
        auth.logout();
      }
      return throwError(() => err);
    }),
  );
};
