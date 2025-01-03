import {User} from "../../shared/models/user.model";
import {LoginRequest} from "../../shared/models/login-request.model";
import {AuthenticationService} from "../../shared/services/authentication.service";
import {LoginComponent} from "./login.component";

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { ReactiveFormsModule } from '@angular/forms';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authenticationServiceMock: jasmine.SpyObj<AuthenticationService>;
  let routerMock: jasmine.SpyObj<Router>;

  beforeEach(() => {
    localStorage.setItem('userId', '1');
    authenticationServiceMock = jasmine.createSpyObj('AuthenticationService', ['login', 'getUserById']);
    routerMock = jasmine.createSpyObj('Router', ['navigate', 'then']);

    TestBed.configureTestingModule({
      imports: [LoginComponent, ReactiveFormsModule],
      providers: [
        { provide: AuthenticationService, useValue: authenticationServiceMock },
        { provide: Router, useValue: routerMock }
      ]
    });

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should call login on form submit for editor and navigate on success', () => {
    const userRequest = {
      username: 'john',
      password: '123'
    };

    const mockUser = {
      id: 1,
      username: 'john',
      fullName: 'John Doe',
      password: '123',
      role: 'editor'
    };

    component.loginForm.setValue(userRequest);

    authenticationServiceMock.login.and.returnValue(mockUser as User);

    component.onSubmit();

    expect(authenticationServiceMock.login).toHaveBeenCalledWith(userRequest as LoginRequest);

    expect(component.loginForm.pristine).toBeTrue();
    expect(routerMock.navigate).toHaveBeenCalledWith(['/myPost']);
  });

  it('should call login on form submit for user and navigate on success 2', () => {
    const userRequest = {
      username: 'john',
      password: '123'
    };

    const mockUser = {
      id: 1,
      username: 'john',
      fullName: 'John Doe',
      password: '123',
      role: 'user'
    };

    component.loginForm.setValue(userRequest);

    authenticationServiceMock.login.and.returnValue(mockUser as User);

    component.onSubmit();

    expect(authenticationServiceMock.login).toHaveBeenCalledWith(userRequest as LoginRequest);

    expect(component.loginForm.pristine).toBeTrue();
    expect(routerMock.navigate).toHaveBeenCalledWith(['/post']);
  });

  it('should set invalidLoginForm to true when form is invalid', () => {
     component.onSubmit();
    expect(component.formIsInvalid).toBeTrue();
  });

  it('should set invalidLoginForm to true when form is invalid', () => {
    const userRequest = {
      username: 'john',
      password: 'invalidPassword'
    };

    const mockUser = null;

    component.loginForm.setValue(userRequest);

    authenticationServiceMock.login.and.returnValue(mockUser);

    component.onSubmit();

    expect(authenticationServiceMock.login).toHaveBeenCalledWith(userRequest as LoginRequest);

    expect(component.loginForm.pristine).toBeTrue();
    expect(component.loginIsInvalid ).toBeTrue();
  });

});
