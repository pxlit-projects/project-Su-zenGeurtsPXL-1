import {Component, inject, OnInit} from '@angular/core';
import {Router, RouterLink, RouterLinkActive} from "@angular/router";
import {NgOptimizedImage} from "@angular/common";
import {User} from "../../shared/models/user.model";
import {AuthenticationService} from "../../shared/services/authentication.service";

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive, NgOptimizedImage],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css'
})
export class NavbarComponent implements OnInit{
  protected readonly localStorage = localStorage;
  router: Router = inject(Router);
  authenticationService: AuthenticationService = inject(AuthenticationService);
  user: User | undefined;
  hiddenUserMenu: boolean = true;

  ngOnInit(): void {
    const userId = localStorage.getItem("userId");
    this.user = this.authenticationService.getUserById(Number(userId));
  }

  userMenu() {
    this.hiddenUserMenu = !this.hiddenUserMenu;
  }

  login() {
    this.router.navigate(['/login']);
  }

  addPost() {
    this.router.navigate(['/addPost']);
  }

  logout() {
    this.hiddenUserMenu = true;
    localStorage.removeItem('userId');
    localStorage.removeItem('userFullName');
    localStorage.removeItem('userRole');
  }


}
