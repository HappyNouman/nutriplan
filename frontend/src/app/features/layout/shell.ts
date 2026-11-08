import { Component, inject, OnInit } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';

/**
 * Layout for every page that needs login: top bar (logo, navigation, user, logout)
 * and the current page below it in <router-outlet>.
 */
@Component({
  selector: 'app-shell',
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './shell.html',
  styleUrl: './shell.css',
})
export class Shell implements OnInit {
  protected readonly auth = inject(AuthService);

  ngOnInit(): void {
    // Refresh the user from the backend; if the token is invalid the interceptor logs out.
    this.auth.loadMe().subscribe({ error: () => {} });
  }

  protected initial(): string {
    return this.auth.user()?.fullName.charAt(0) ?? '';
  }
}
