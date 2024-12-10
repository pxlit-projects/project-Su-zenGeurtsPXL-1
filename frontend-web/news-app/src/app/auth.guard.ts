import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';

@Injectable({
  providedIn: 'root',
})
export class AuthGuard implements CanActivate {
  constructor(private router: Router) {}

  canActivate(): boolean {
    const userId = localStorage.getItem('userId');
    if (userId) {
      return true; // Allow access if userId exists
    } else {
      this.router.navigate(['/login']); // Redirect to login if userId is null
      return false;
    }
  }
}
