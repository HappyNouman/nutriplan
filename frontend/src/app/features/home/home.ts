import { Component, inject, OnInit } from '@angular/core';
import { AuthService } from '../../core/auth/auth.service';

/**
 * First page after login. For now it proves the protected API works (GET /api/users/me);
 * later it becomes the dashboard with the weekly plan.
 */
@Component({
  selector: 'app-home',
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home implements OnInit {
  protected readonly auth = inject(AuthService);

  ngOnInit(): void {
    // Refresh the user from the backend; if the token is invalid the interceptor logs out.
    this.auth.loadMe().subscribe({ error: () => {} });
  }

  protected firstName(): string {
    return this.auth.user()?.fullName.split(' ')[0] ?? '';
  }
}
