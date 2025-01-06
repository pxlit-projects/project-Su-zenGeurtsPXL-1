import {ComponentFixture, fakeAsync, TestBed, tick} from "@angular/core/testing";
import {ActivatedRoute, Router, UrlSegment} from "@angular/router";
import {NavbarComponent} from "./navbar.component";
import {PostService} from "../../shared/services/post/post.service";
import {AuthenticationService} from "../../shared/services/authentication/authentication.service";

describe('NavbarComponent', () => {
  let component: NavbarComponent;
  let fixture: ComponentFixture<NavbarComponent>;
  let postServiceMock: jasmine.SpyObj<PostService>;
  let authenticationServiceMock: jasmine.SpyObj<AuthenticationService>;
  let routerMock: jasmine.SpyObj<Router>;
  let routeMock: jasmine.SpyObj<ActivatedRoute>;

  beforeEach(() => {
    postServiceMock = jasmine.createSpyObj('PostService', ['getMyNotifications', 'markNotificationAsRead']);
    authenticationServiceMock = jasmine.createSpyObj('AuthenticationService', ['getUserById']);
    routerMock = jasmine.createSpyObj('Router', ['navigate']);
    routeMock = jasmine.createSpyObj('ActivatedRoute', [], {
      snapshot: { url: [new UrlSegment('post', {})] }
    });

    TestBed.configureTestingModule({
      imports: [NavbarComponent],
      providers: [
        { provide: PostService, useValue: postServiceMock },
        { provide: AuthenticationService, useValue: authenticationServiceMock },
        { provide: Router, useValue: routerMock },
        { provide: ActivatedRoute, useValue: routeMock }
      ]
    });

    fixture = TestBed.createComponent(NavbarComponent);
    component = fixture.componentInstance;
  });

  // it('should create the component', () => {
  //   expect(component).toBeTruthy();
  // });

  xit('should navigate to /login on login()', () => {
    component.login();
    expect(routerMock.navigate).toHaveBeenCalledWith(['/login']);
  });

  xit('should fetch notifications immediately on init', () => {
    spyOn(component, 'fetchNotifications'); // Spy on the fetchNotifications method

    component.ngOnInit(); // Trigger ngOnInit

    expect(component.fetchNotifications).toHaveBeenCalled(); // Verify initial fetch call
  });

  xit('should fetch notifications every 5 seconds', fakeAsync(() => {
    spyOn(component, 'fetchNotifications'); // Spy on the method

    component.ngOnInit(); // Initialize component
    expect(component.fetchNotifications).toHaveBeenCalledTimes(1); // Initial call

    tick(5000); // Move time forward by 5 seconds
    expect(component.fetchNotifications).toHaveBeenCalledTimes(2); // Called again after 5 seconds

    tick(5000); // Move forward by another 5 seconds
    expect(component.fetchNotifications).toHaveBeenCalledTimes(3); // Called again after 10 seconds
  }));
});
