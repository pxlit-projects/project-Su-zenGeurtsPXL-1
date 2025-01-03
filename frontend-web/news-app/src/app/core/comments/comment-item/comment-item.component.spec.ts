import {ComponentFixture, TestBed} from "@angular/core/testing";
import {CommentItemComponent} from "./comment-item.component";
import {PostService} from "../../../shared/services/post.service";

describe('ReviewItemComponent', () => {
  let component: CommentItemComponent;
  let fixture: ComponentFixture<CommentItemComponent>;
  let postServiceMock: jasmine.SpyObj<PostService>;

  beforeEach(() => {
    postServiceMock = jasmine.createSpyObj('PostService', ['transformDateShort', 'toPascalCasing']);

    TestBed.configureTestingModule({
      imports: [CommentItemComponent],
      providers: [
        { provide: PostService, useValue: postServiceMock },
      ]
    });

    fixture = TestBed.createComponent(CommentItemComponent);
    component = fixture.componentInstance;
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });
});
