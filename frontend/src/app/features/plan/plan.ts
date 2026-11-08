import { DatePipe, DecimalPipe } from '@angular/common';
import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';
import { errorMessage } from '../../core/http-errors';
import { MEAL_TYPE_LABELS, MealPlan, PlanDay, todayIso } from './plan.models';
import { PlanService } from './plan.service';

@Component({
  selector: 'app-plan',
  imports: [RouterLink, DatePipe, DecimalPipe],
  templateUrl: './plan.html',
  styleUrl: './plan.css',
})
export class Plan implements OnInit {
  private readonly planService = inject(PlanService);
  private readonly auth = inject(AuthService);

  protected readonly labels = MEAL_TYPE_LABELS;
  protected readonly today = todayIso();

  protected readonly plan = signal<MealPlan | null>(null);
  protected readonly loading = signal(true);
  protected readonly generating = signal(false);
  protected readonly error = signal<string | null>(null);

  /** The plan needs a calorie target, which comes from the health profile. */
  protected readonly hasProfile = computed(() => this.auth.user()?.dailyCalories != null);
  protected readonly dailyCalories = computed(() => this.auth.user()?.dailyCalories ?? 0);

  /** Average calories per day across the week. */
  protected readonly weekAverage = computed(() => {
    const days = this.plan()?.days ?? [];
    return days.length ? Math.round(days.reduce((sum, d) => sum + d.calories, 0) / days.length) : 0;
  });

  ngOnInit(): void {
    this.planService.current().subscribe({
      next: (plan) => {
        this.plan.set(plan);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(errorMessage(err));
        this.loading.set(false);
      },
    });
  }

  protected generate(): void {
    this.generating.set(true);
    this.error.set(null);
    this.planService.generate().subscribe({
      next: (plan) => {
        this.plan.set(plan);
        this.generating.set(false);
      },
      error: (err) => {
        this.error.set(errorMessage(err));
        this.generating.set(false);
      },
    });
  }

  /** How full the day's calorie bar is, relative to the target (capped at 100%). */
  protected fillPercent(day: PlanDay): number {
    const target = this.plan()?.targetCalories ?? 1;
    return Math.min(100, Math.round((day.calories / target) * 100));
  }

  protected servingsLabel(servings: number): string {
    return servings === 1 ? '1 serving' : `${servings} servings`;
  }
}
