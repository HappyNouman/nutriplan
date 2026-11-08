import { DecimalPipe } from '@angular/common';
import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';
import { CalorieSummary } from '../profile/calorie-summary/calorie-summary';
import { GOAL_OPTIONS, HealthProfile } from '../profile/profile.models';
import { ProfileService } from '../profile/profile.service';

/** Dashboard: daily calorie target if the profile is filled, otherwise a prompt to fill it. */
@Component({
  selector: 'app-home',
  imports: [RouterLink, CalorieSummary, DecimalPipe],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home implements OnInit {
  private readonly auth = inject(AuthService);
  private readonly profileService = inject(ProfileService);

  protected readonly profile = signal<HealthProfile | null>(null);
  protected readonly loading = signal(true);

  ngOnInit(): void {
    this.profileService.get().subscribe({
      next: (p) => {
        this.profile.set(p);
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });
  }

  protected firstName(): string {
    return this.auth.user()?.fullName.split(' ')[0] ?? '';
  }

  protected goalLabel(): string {
    return GOAL_OPTIONS.find((g) => g.value === this.profile()?.goal)?.label ?? '';
  }
}
