import {Component, inject, Injectable} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {NgClass, NgOptimizedImage} from "@angular/common";
import {Router} from "@angular/router";

import {AuthenticationService} from "../../shared/services/authentication/authentication.service";
import {LoginRequest} from "../../shared/models/authentication/login-request.model";

@Injectable({
  providedIn: 'root',
})

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, NgClass, NgOptimizedImage],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  fb: FormBuilder = inject(FormBuilder);
  router: Router = inject(Router);
  authenticationService: AuthenticationService = inject(AuthenticationService);
  loginIsInvalid: boolean = false;
  formIsInvalid: boolean = false;
  loginForm: FormGroup = this.fb.group({
    username: ['', Validators.required],
    password: ['', Validators.required],
  });

  onSubmit() {
    if (this.loginForm.invalid) {
      this.formIsInvalid = true;
    } else {
      const login: LoginRequest = {
        ...this.loginForm.value
      };

      const user = this.authenticationService.login(login);

      if (user != null) {
        const imageUrl = 'user-profiles/' + user.role + user.id + '.png';

        localStorage.setItem('userId', user.id.toString());
        localStorage.setItem('userFullName', user.fullName);
        localStorage.setItem('userRole', user.role);
        localStorage.setItem('userImage', imageUrl);
        this.loginForm.reset();
        if (user.role === 'editor') {
          this.router.navigate(['/myPost'])
        } else {
          this.router.navigate(['/post'])
        }
      } else {
        this.loginIsInvalid = true;
      }
    }
  }
}
