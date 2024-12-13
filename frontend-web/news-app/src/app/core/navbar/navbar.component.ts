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
  hiddenUserMenu: boolean = true;

  userMenu() {
    this.hiddenUserMenu = !this.hiddenUserMenu;
  }

  login() {
    this.router.navigate(['/login']);
  }

  logout() {
    this.hiddenUserMenu = true;
    localStorage.removeItem('userId');
    localStorage.removeItem('userFullName');
    localStorage.removeItem('userRole');
    this.router.navigate(['/post']);
  }


}
