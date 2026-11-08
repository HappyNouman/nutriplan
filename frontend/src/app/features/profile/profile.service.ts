import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_URL } from '../../core/api';
import { HealthProfile, HealthProfileRequest } from './profile.models';

@Injectable({ providedIn: 'root' })
export class ProfileService {
  private readonly http = inject(HttpClient);

  get(): Observable<HealthProfile> {
    return this.http.get<HealthProfile>(`${API_URL}/profile`);
  }

  save(request: HealthProfileRequest): Observable<HealthProfile> {
    return this.http.put<HealthProfile>(`${API_URL}/profile`, request);
  }
}
