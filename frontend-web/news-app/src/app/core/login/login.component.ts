import {Component, inject} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {NgClass, NgOptimizedImage} from "@angular/common";
import {Router} from "@angular/router";

import {AuthenticationService} from "../../shared/services/authentication.service";
import {UserRequest} from "../../shared/models/user-request.model";

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, NgClass, NgOptimizedImage],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent{
  fb: FormBuilder = inject(FormBuilder);
  router: Router = inject(Router);

  authenticationService: AuthenticationService = inject(AuthenticationService);
  validLogin: boolean = false;
  validLoginForm: boolean = false;
  loginForm: FormGroup = this.fb.group({
    username: ['', Validators.required],
    password: ['', Validators.required],
  });

  onSubmit() {
    if (this.loginForm.invalid) {
      this.validLoginForm = true;
    } else {
      const login: UserRequest = {
        ...this.loginForm.value
      };

      const user = this.authenticationService.login(login);

      if (user != null) {
        localStorage.setItem('userId', user.id.toString());
        localStorage.setItem('userFullName', user.fullName);
        localStorage.setItem('userRole', user.role);
        this.loginForm.reset();
        this.router.navigate(['/posts']);
      } else {
        this.validLogin = true;
      }
    }
  }
}
