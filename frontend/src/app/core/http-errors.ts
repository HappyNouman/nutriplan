import { HttpErrorResponse } from '@angular/common/http';

/**
 * The backend returns errors as "problem details", e.g.
 *   { "status": 409, "detail": "Email is already registered" }
 *   { "status": 400, "detail": "Validation failed", "errors": { "email": "must be ..." } }
 */
interface ProblemDetail {
  detail?: string;
  errors?: Record<string, string>;
}

/** A message that can be shown to the user. */
export function errorMessage(err: unknown): string {
  if (err instanceof HttpErrorResponse) {
    if (err.status === 0) {
      return 'Cannot reach the server. Is the backend running?';
    }
    const body = err.error as ProblemDetail | null;
    if (body?.detail) {
      return body.detail;
    }
  }
  return 'Something went wrong. Please try again.';
}

/** Per-field validation errors from a 400 response, e.g. { email: "must be a well-formed email address" }. */
export function fieldErrors(err: unknown): Record<string, string> {
  if (err instanceof HttpErrorResponse && err.status === 400) {
    return (err.error as ProblemDetail | null)?.errors ?? {};
  }
  return {};
}
