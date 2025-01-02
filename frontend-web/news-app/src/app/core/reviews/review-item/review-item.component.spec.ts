import {ComponentFixture, TestBed} from "@angular/core/testing";
import {ReviewItemComponent} from "./review-item.component";

describe('ReviewItemComponent', () => {
  let component: ReviewItemComponent;
  let fixture: ComponentFixture<ReviewItemComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ReviewItemComponent],
    });

    fixture = TestBed.createComponent(ReviewItemComponent);
    component = fixture.componentInstance;
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });
});
