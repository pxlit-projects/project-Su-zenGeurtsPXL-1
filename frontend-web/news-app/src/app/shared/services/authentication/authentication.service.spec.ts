import {AuthenticationService} from "./authentication.service";

import {TestBed} from "@angular/core/testing";

describe('AuthenticationService', () => {
  let service: AuthenticationService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        AuthenticationService
      ],
    });
    service = TestBed.inject(AuthenticationService);
  });

  it('should return a user with a correct username and password ', () => {
    const userRequest = {
      username: 'emma',
      password: 'ed123'
    };

    const expectedUser = { id: 4, username: 'emma', fullName: 'Emma Janssen', password: 'ed123', role: 'editor' };

    expect(service.login(userRequest)).toEqual(expectedUser);
  });

  it('should return null with a incorrect username or password ', () => {
    const userRequest = {
      username: 'emma',
      password: 'invalid'
    };

    expect(service.login(userRequest)).toEqual(null);

    const userRequest2 = {
      username: 'nobody',
      password: 'invalid'
    };

    expect(service.login(userRequest2)).toEqual(null);
  });

  it('should format from string to number when ID is a string ', () => {
    const expectedUser = { id: 4, username: 'emma', fullName: 'Emma Janssen', password: 'ed123', role: 'editor' };
    expect(service.getUserById('4')).toEqual(expectedUser);
  });
});
