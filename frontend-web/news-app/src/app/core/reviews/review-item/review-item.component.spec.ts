import {ComponentFixture, TestBed} from "@angular/core/testing";
import {ReviewItemComponent} from "./review-item.component";
import {PostService} from "../../../shared/services/post/post.service";

describe('ReviewItemComponent', () => {
  let component: ReviewItemComponent;
  let fixture: ComponentFixture<ReviewItemComponent>;
  let postServiceMock: jasmine.SpyObj<PostService>;

  beforeEach(() => {
    postServiceMock = jasmine.createSpyObj('PostService', ['transformDateShort', 'toPascalCasing']);

    TestBed.configureTestingModule({
      imports: [ReviewItemComponent],
      providers: [
        { provide: PostService, useValue: postServiceMock },
      ]
    });

    fixture = TestBed.createComponent(ReviewItemComponent);
    component = fixture.componentInstance;
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });
});
