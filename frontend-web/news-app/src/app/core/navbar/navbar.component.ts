import {Component, inject} from '@angular/core';
import {Router, RouterLink, RouterLinkActive} from "@angular/router";
import {NgOptimizedImage} from "@angular/common";
import {AuthenticationService} from "../../shared/services/authentication.service";

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive, NgOptimizedImage],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css'
})
export class NavbarComponent {
  protected readonly localStorage = localStorage;
  router: Router = inject(Router);
  authenticationService: AuthenticationService = inject(AuthenticationService);
  userMenuIsHidden: boolean = true;

  userMenu() {
    this.userMenuIsHidden = !this.userMenuIsHidden;
  }

  login() {
    this.router.navigate(['/login']);
  }

  logout() {
    this.userMenuIsHidden = true;
    this.localStorage.removeItem('userId');
    this.localStorage.removeItem('userFullName');
    this.localStorage.removeItem('userRole');
    this.router.navigate(['/post']);
  }
}
