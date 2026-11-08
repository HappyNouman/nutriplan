import { DecimalPipe } from '@angular/common';
import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';
import { MEAL_TYPE_LABELS, PlanDay, todayIso } from '../plan/plan.models';
import { PlanService } from '../plan/plan.service';
import { CalorieSummary } from '../profile/calorie-summary/calorie-summary';
import { GOAL_OPTIONS, HealthProfile } from '../profile/profile.models';
import { ProfileService } from '../profile/profile.service';

/** Dashboard: daily target and today's meals, or prompts for the next setup step. */
@Component({
  selector: 'app-home',
  imports: [RouterLink, CalorieSummary, DecimalPipe],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home implements OnInit {
  private readonly auth = inject(AuthService);
  private readonly profileService = inject(ProfileService);
  private readonly planService = inject(PlanService);

  protected readonly labels = MEAL_TYPE_LABELS;

  protected readonly profile = signal<HealthProfile | null>(null);
  protected readonly today = signal<PlanDay | null>(null);
  protected readonly hasPlan = signal(false);
  protected readonly loading = signal(true);

  ngOnInit(): void {
    this.profileService.get().subscribe({
      next: (p) => {
        this.profile.set(p);
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });

    this.planService.current().subscribe({
      next: (plan) => {
        this.hasPlan.set(plan !== null);
        this.today.set(plan?.days.find((d) => d.date === todayIso()) ?? null);
      },
      error: () => {},
    });
  }

  protected firstName(): string {
    return this.auth.user()?.fullName.split(' ')[0] ?? '';
  }

  protected goalLabel(): string {
    return GOAL_OPTIONS.find((g) => g.value === this.profile()?.goal)?.label ?? '';
  }
}
