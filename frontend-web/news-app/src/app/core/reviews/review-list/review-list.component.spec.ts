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
});
