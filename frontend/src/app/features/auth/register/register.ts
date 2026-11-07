import { Component, inject, signal } from '@angular/core';
import {
  AbstractControl,
  NonNullableFormBuilder,
  ReactiveFormsModule,
  ValidationErrors,
  Validators,
} from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/auth/auth.service';
import { errorMessage, fieldErrors } from '../../../core/http-errors';

type FieldName = 'fullName' | 'email' | 'password' | 'confirmPassword';

/** Form-level validator: "confirm password" must equal "password". */
function passwordsMatch(group: AbstractControl): ValidationErrors | null {
  const password = group.get('password')?.value;
  const confirm = group.get('confirmPassword')?.value;
  return password && confirm && password !== confirm ? { passwordMismatch: true } : null;
}

@Component({
  selector: 'app-register',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './register.html',
  styleUrl: '../auth-page.css',
})
export class Register {
  private readonly fb = inject(NonNullableFormBuilder);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  protected readonly loading = signal(false);
  protected readonly error = signal<string | null>(null);

  // Same rules as the backend's RegisterRequest (@NotBlank, @Email, @Size(min = 8, max = 72))
  protected readonly form = this.fb.group(
    {
      fullName: ['', [Validators.required, Validators.maxLength(100)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(72)]],
      confirmPassword: ['', [Validators.required]],
    },
    { validators: passwordsMatch },
  );

  protected invalid(name: FieldName): boolean {
    const control = this.form.controls[name];
    const mismatch =
      name === 'confirmPassword' && this.form.hasError('passwordMismatch') && control.touched;
    return (control.invalid && control.touched) || mismatch;
  }

  protected submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.loading.set(true);
    this.error.set(null);

    const { fullName, email, password } = this.form.getRawValue();
    this.auth.register({ fullName, email, password }).subscribe({
      next: () => this.router.navigateByUrl('/'),
      error: (err) => {
        // Show the backend's per-field messages under the matching inputs
        for (const [field, message] of Object.entries(fieldErrors(err))) {
          this.form.get(field)?.setErrors({ server: message });
        }
        this.error.set(errorMessage(err));
        this.loading.set(false);
      },
    });
  }
}
