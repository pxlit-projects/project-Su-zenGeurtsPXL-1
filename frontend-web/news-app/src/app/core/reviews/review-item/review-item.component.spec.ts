import {ComponentFixture, TestBed} from "@angular/core/testing";
import {ReviewItemComponent} from "./review-item.component";
import {HelperService} from "../../../shared/services/helper/helper.service";

describe('ReviewItemComponent', () => {
  let component: ReviewItemComponent;
  let fixture: ComponentFixture<ReviewItemComponent>;
  let helperServiceMock: jasmine.SpyObj<HelperService>;

  beforeEach(() => {
    helperServiceMock = jasmine.createSpyObj('HelperService', ['transformDateShort', 'toPascalCasing']);

    TestBed.configureTestingModule({
      imports: [ReviewItemComponent],
      providers: [
        { provide: HelperService, useValue: helperServiceMock },
      ]
    });

    fixture = TestBed.createComponent(ReviewItemComponent);
    component = fixture.componentInstance;
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });
});
