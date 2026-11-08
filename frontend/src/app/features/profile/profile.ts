import { Component, inject, OnInit, signal } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { ActivityLevel, Gender, Goal } from '../../core/auth/auth.models';
import { AuthService } from '../../core/auth/auth.service';
import { errorMessage, fieldErrors } from '../../core/http-errors';
import { CalorieSummary } from './calorie-summary/calorie-summary';
import { ACTIVITY_OPTIONS, GOAL_OPTIONS, HealthProfile, HealthProfileRequest } from './profile.models';
import { ProfileService } from './profile.service';

type NumberField = 'age' | 'heightCm' | 'weightKg';

@Component({
  selector: 'app-profile',
  imports: [ReactiveFormsModule, RouterLink, CalorieSummary],
  templateUrl: './profile.html',
  styleUrl: './profile.css',
})
export class Profile implements OnInit {
  private readonly fb = inject(NonNullableFormBuilder);
  private readonly profileService = inject(ProfileService);
  private readonly auth = inject(AuthService);

  protected readonly activityOptions = ACTIVITY_OPTIONS;
  protected readonly goalOptions = GOAL_OPTIONS;

  protected readonly loading = signal(true);
  protected readonly saving = signal(false);
  protected readonly saved = signal(false);
  protected readonly error = signal<string | null>(null);
  protected readonly result = signal<HealthProfile | null>(null);

  // Same ranges as the backend's HealthProfileRequest
  protected readonly form = this.fb.group({
    gender: this.fb.control<Gender | null>(null, Validators.required),
    age: this.fb.control<number | null>(null, [Validators.required, Validators.min(15), Validators.max(100)]),
    heightCm: this.fb.control<number | null>(null, [Validators.required, Validators.min(100), Validators.max(250)]),
    weightKg: this.fb.control<number | null>(null, [Validators.required, Validators.min(30), Validators.max(300)]),
    activityLevel: this.fb.control<ActivityLevel | null>(null, Validators.required),
    goal: this.fb.control<Goal | null>(null, Validators.required),
  });

  ngOnInit(): void {
    // Fill the form with what the user saved before (if anything)
    this.profileService.get().subscribe({
      next: (profile) => {
        this.form.patchValue(profile);
        if (profile.complete) {
          this.result.set(profile);
        }
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(errorMessage(err));
        this.loading.set(false);
      },
    });
  }

  protected invalid(name: keyof typeof this.form.controls): boolean {
    const control = this.form.controls[name];
    return control.invalid && control.touched;
  }

  protected rangeError(name: NumberField): string {
    const c = this.form.controls[name];
    if (c.hasError('server')) return c.getError('server');
    if (c.hasError('required')) return 'Required.';
    const limits = { age: [15, 100, 'years'], heightCm: [100, 250, 'cm'], weightKg: [30, 300, 'kg'] }[name];
    return `Must be between ${limits[0]} and ${limits[1]} ${limits[2]}.`;
  }

  protected submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.saving.set(true);
    this.saved.set(false);
    this.error.set(null);

    // Safe cast: the form is valid, so no field is null
    const request = this.form.getRawValue() as HealthProfileRequest;
    this.profileService.save(request).subscribe({
      next: (profile) => {
        this.result.set(profile);
        this.saved.set(true);
        this.saving.set(false);
        this.auth.loadMe().subscribe({ error: () => {} }); // keep the stored user's dailyCalories in sync
      },
      error: (err) => {
        for (const [field, message] of Object.entries(fieldErrors(err))) {
          this.form.get(field)?.setErrors({ server: message });
        }
        this.error.set(errorMessage(err));
        this.saving.set(false);
      },
    });
  }
}
