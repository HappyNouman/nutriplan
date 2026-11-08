import { ActivityLevel, Gender, Goal } from '../../core/auth/auth.models';

/** Mirrors the backend's HealthProfileRequest. */
export interface HealthProfileRequest {
  gender: Gender;
  age: number;
  heightCm: number;
  weightKg: number;
  activityLevel: ActivityLevel;
  goal: Goal;
}

/** Mirrors the backend's HealthProfileResponse. Numbers are null until the profile is complete. */
export interface HealthProfile {
  gender: Gender | null;
  age: number | null;
  heightCm: number | null;
  weightKg: number | null;
  activityLevel: ActivityLevel | null;
  goal: Goal | null;
  complete: boolean;
  bmr: number | null;
  tdee: number | null;
  dailyCalories: number | null;
  proteinG: number | null;
  carbsG: number | null;
  fatG: number | null;
  bmi: number | null;
}

/** Labels shown in the form, in the order they appear. */
export const ACTIVITY_OPTIONS: { value: ActivityLevel; label: string; hint: string }[] = [
  { value: 'SEDENTARY', label: 'Sedentary', hint: 'Little or no exercise' },
  { value: 'LIGHT', label: 'Lightly active', hint: 'Exercise 1–3 days a week' },
  { value: 'MODERATE', label: 'Moderately active', hint: 'Exercise 3–5 days a week' },
  { value: 'ACTIVE', label: 'Very active', hint: 'Exercise 6–7 days a week' },
  { value: 'VERY_ACTIVE', label: 'Extra active', hint: 'Hard training or a physical job' },
];

export const GOAL_OPTIONS: { value: Goal; label: string; hint: string; icon: string }[] = [
  { value: 'LOSE_WEIGHT', label: 'Lose weight', hint: '−500 kcal a day', icon: '🔥' },
  { value: 'MAINTAIN', label: 'Stay healthy', hint: 'Keep my weight', icon: '⚖️' },
  { value: 'GAIN_MUSCLE', label: 'Build muscle', hint: '+300 kcal a day', icon: '💪' },
];
