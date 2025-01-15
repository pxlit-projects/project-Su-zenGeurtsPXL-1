import {User} from "../../shared/models/authentication/user.model";
import {LoginRequest} from "../../shared/models/authentication/login-request.model";
import {AuthenticationService} from "../../shared/services/authentication/authentication.service";
import {LoginComponent} from "./login.component";

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { ReactiveFormsModule } from '@angular/forms';
import {By} from "@angular/platform-browser";

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
    // EDITOR
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


    mockUser.role = 'user';
    component.loginForm.setValue(userRequest);
    authenticationServiceMock.login.and.returnValue(mockUser as User);
    fixture.detectChanges();

    component.onSubmit();

    expect(routerMock.navigate).toHaveBeenCalledWith(['/post']);
  });

  it('should display error message when login fails', () => {
    authenticationServiceMock.login.and.returnValue(null);

    component.onSubmit();

    const debugElement = fixture.debugElement.query(By.css('#errorMessage'));
    expect(debugElement.nativeElement.textContent).toContain('Username and/or password is incorrect');
  });
});
