// Mirrors the backend's PlanResponse.

export type MealType = 'BREAKFAST' | 'LUNCH' | 'DINNER' | 'SNACK';
export type DayOfWeek =
  | 'MONDAY'
  | 'TUESDAY'
  | 'WEDNESDAY'
  | 'THURSDAY'
  | 'FRIDAY'
  | 'SATURDAY'
  | 'SUNDAY';

export interface PlannedMeal {
  mealType: MealType;
  mealId: number;
  name: string;
  description: string;
  servings: number;
  calories: number;
  proteinG: number;
  carbsG: number;
  fatG: number;
}

export interface PlanDay {
  day: DayOfWeek;
  date: string; // "2026-11-02"
  calories: number;
  proteinG: number;
  carbsG: number;
  fatG: number;
  meals: PlannedMeal[];
}

export interface MealPlan {
  id: number;
  weekStart: string;
  targetCalories: number;
  generatedAt: string;
  days: PlanDay[];
}

export const MEAL_TYPE_LABELS: Record<MealType, string> = {
  BREAKFAST: 'Breakfast',
  LUNCH: 'Lunch',
  DINNER: 'Dinner',
  SNACK: 'Snack',
};

/** Today's date as "yyyy-MM-dd" in the user's own time zone (to highlight today's card). */
export function todayIso(): string {
  const d = new Date();
  const pad = (n: number) => String(n).padStart(2, '0');
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`;
}
