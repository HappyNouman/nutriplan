import { Component, computed, input } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { HealthProfile } from '../profile.models';

/**
 * Shows the calculated numbers of a complete profile: daily calories, macros, BMR / TDEE / BMI.
 * Used on both the profile page and the dashboard.
 */
@Component({
  selector: 'app-calorie-summary',
  imports: [DecimalPipe],
  templateUrl: './calorie-summary.html',
  styleUrl: './calorie-summary.css',
})
export class CalorieSummary {
  readonly profile = input.required<HealthProfile>();

  protected readonly macros = computed(() => {
    const p = this.profile();
    const daily = p.dailyCalories ?? 1;
    const macro = (name: string, grams: number | null, kcalPerGram: number, color: string) => ({
      name,
      grams: grams ?? 0,
      percent: Math.round((((grams ?? 0) * kcalPerGram) / daily) * 100),
      color,
    });
    return [
      macro('Protein', p.proteinG, 4, '#16a34a'),
      macro('Carbs', p.carbsG, 4, '#f59e0b'),
      macro('Fat', p.fatG, 9, '#3b82f6'),
    ];
  });

  protected readonly bmiCategory = computed(() => {
    const bmi = this.profile().bmi ?? 0;
    if (bmi < 18.5) return 'Underweight';
    if (bmi < 25) return 'Healthy';
    if (bmi < 30) return 'Overweight';
    return 'Obese';
  });
}
