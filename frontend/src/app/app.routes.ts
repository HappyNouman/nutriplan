import { Routes } from '@angular/router';
import { authGuard, guestGuard } from './core/auth/auth.guards';

export const routes: Routes = [
  {
    path: 'login',
    title: 'Log in · NutriPlan',
    canActivate: [guestGuard],
    loadComponent: () => import('./features/auth/login/login').then((m) => m.Login),
  },
  {
    path: 'register',
    title: 'Create account · NutriPlan',
    canActivate: [guestGuard],
    loadComponent: () => import('./features/auth/register/register').then((m) => m.Register),
  },
  {
    // Everything below needs login and shares the top bar (Shell)
    path: '',
    canActivate: [authGuard],
    loadComponent: () => import('./features/layout/shell').then((m) => m.Shell),
    children: [
      {
        path: '',
        title: 'Dashboard · NutriPlan',
        loadComponent: () => import('./features/home/home').then((m) => m.Home),
      },
      {
        path: 'profile',
        title: 'Health profile · NutriPlan',
        loadComponent: () => import('./features/profile/profile').then((m) => m.Profile),
      },
      {
        path: 'plan',
        title: 'Meal plan · NutriPlan',
        loadComponent: () => import('./features/plan/plan').then((m) => m.Plan),
      },
    ],
  },
  { path: '**', redirectTo: '' },
];
