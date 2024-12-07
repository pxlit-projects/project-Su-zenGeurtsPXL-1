import {Component, inject, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {NgClass} from "@angular/common";
import {Router} from "@angular/router";

import {AuthenticationService} from "../../shared/services/authentication.service";
import {User} from "../../shared/models/user.model";
import {PostService} from "../../shared/services/post.service";

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, NgClass],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent implements OnInit{
  fb: FormBuilder = inject(FormBuilder);
  router: Router = inject(Router);

  users!: User[];
  authenticationService: AuthenticationService = inject(AuthenticationService);

  userForm: FormGroup = this.fb.group({
    username: ['', Validators.required],
    password: ['', Validators.required],
  });

  ngOnInit(): void {
    this.fetchUsers();
  }

  fetchUsers(): void {
    this.users = this.authenticationService.getUsers();
    console.log(this.users);
  }

  onSubmit() {
    const loginForm: User = {
      ...this.userForm.value
    };
  }
}
