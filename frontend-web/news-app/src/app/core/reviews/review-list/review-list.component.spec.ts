import {ComponentFixture, TestBed} from "@angular/core/testing";
import {ReviewListComponent} from "./review-list.component";

describe('ReviewListComponent', () => {
  let component: ReviewListComponent;
  let fixture: ComponentFixture<ReviewListComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ReviewListComponent],
    });

    fixture = TestBed.createComponent(ReviewListComponent);
    component = fixture.componentInstance;
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should sort reviews in ngOnInit()', () => {
    component.reviews = [
      {id: 1, userId: 1, postId: 1, content: 'Comment', createdAt: '2024-12-1 15:30:07', type: 'REJECTION'},
      {id: 1, userId: 1, postId: 1, content: 'Comment', createdAt: '2024-12-10 15:30:07', type: 'REJECTION'},
      {id: 1, userId: 1, postId: 1, content: 'Comment', createdAt: '2024-12-4 15:30:07', type: 'APPROVAL'},
    ];
    fixture.detectChanges();

    component.ngOnInit();

    expect(component.reviews[0].createdAt).toBe('2024-12-1 15:30:07');
    expect(component.reviews[1].createdAt).toBe('2024-12-4 15:30:07');
    expect(component.reviews[2].createdAt).toBe('2024-12-10 15:30:07');
  });
});
