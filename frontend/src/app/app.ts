import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

/** Root component: just hosts the current page (see app.routes.ts). */
@Component({
  imports: [RouterOutlet],
  selector: 'app-root',
  template: '<router-outlet />',
})
export class App {}
