import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_URL } from '../../core/api';
import { ShoppingList } from './shopping.models';

@Injectable({ providedIn: 'root' })
export class ShoppingService {
  private readonly http = inject(HttpClient);

  /** This week's list, or null if there is no plan yet (backend answers 204 No Content). */
  current(): Observable<ShoppingList | null> {
    return this.http.get<ShoppingList | null>(`${API_URL}/shopping-list`);
  }

  setChecked(key: string, checked: boolean): Observable<void> {
    return this.http.put<void>(`${API_URL}/shopping-list/checked`, { key, checked });
  }
}
