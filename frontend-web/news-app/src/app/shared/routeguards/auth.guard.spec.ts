import { TestBed } from '@angular/core/testing';
import { AuthGuard } from './auth.guard';
import { Router } from '@angular/router';

describe('AuthGuard', () => {
  let authGuard: AuthGuard;
  let routerMock: jasmine.SpyObj<Router>;

  beforeEach(() => {
    routerMock = jasmine.createSpyObj('Router', ['navigate']);

    TestBed.configureTestingModule({
      imports: [],
      providers: [
        AuthGuard,
        { provide: Router, useValue: routerMock },
      ],
    });

    authGuard = TestBed.inject(AuthGuard);
  });

  it('should allow activation if userId exists in localStorage', () => {
    spyOn(localStorage, 'getItem').and.returnValue('123');
    expect(authGuard.canActivate()).toBeTrue();
    // expect(routerSpy.navigate).not.toHaveBeenCalled();
    expect(routerMock.navigate).not.toHaveBeenCalled();
  });

  it('should block activation and navigate to login if userId is missing', () => {
    spyOn(localStorage, 'getItem').and.returnValue(null);
    expect(authGuard.canActivate()).toBeFalse();
    expect(routerMock.navigate).toHaveBeenCalledWith(['/login']);
  });
});
