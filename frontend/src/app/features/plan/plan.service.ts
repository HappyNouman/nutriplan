import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_URL } from '../../core/api';
import { MealPlan } from './plan.models';

@Injectable({ providedIn: 'root' })
export class PlanService {
  private readonly http = inject(HttpClient);

  /** This week's plan, or null if none was generated yet (backend answers 204 No Content). */
  current(): Observable<MealPlan | null> {
    return this.http.get<MealPlan | null>(`${API_URL}/plans/current`);
  }

  /** Creates this week's plan, or replaces it with a new one. */
  generate(): Observable<MealPlan> {
    return this.http.post<MealPlan>(`${API_URL}/plans/generate`, null);
  }
}
