import {FilterComponent} from "./filter.component";
import {ComponentFixture, TestBed} from "@angular/core/testing";
import {Router} from "@angular/router";
import {ReactiveFormsModule} from "@angular/forms";

describe('FilterComponent', () => {
  let component: FilterComponent;
  let fixture: ComponentFixture<FilterComponent>;
  let routerMock: jasmine.SpyObj<Router>;

  beforeEach(() => {
    routerMock = jasmine.createSpyObj('Router', ['navigate']);

    TestBed.configureTestingModule({
      imports: [FilterComponent, ReactiveFormsModule],
      providers: [
        { provide: Router, useValue: routerMock },
      ]
    });

    fixture = TestBed.createComponent(FilterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should navigate to addPostback on addPost', () => {
    component.addPost();
    expect(routerMock.navigate).toHaveBeenCalledWith(['/addPost']);
  });

  it('should emit filterChanged when form is valid', () => {
    const mockForm = { valid: true };
    spyOn(component.filterChanged, 'emit');

    component.filter = { content: 'Test Content', author: 'Test Author', category: 'Test Category' };

    component.onSubmit(mockForm);

    expect(component.filterChanged.emit).toHaveBeenCalledWith({
      content: 'Test Content',
      author: 'Test Author',
      category: 'Test Category',
    });
  });

  it('should not emit filterChanged when form is invalid', () => {
    const mockForm = { valid: false };
    spyOn(component.filterChanged, 'emit');

    component.onSubmit(mockForm);

    expect(component.filterChanged.emit).not.toHaveBeenCalled();
  });

});
