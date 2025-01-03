import {ComponentFixture, TestBed} from "@angular/core/testing";
import {CommentListComponent} from "./comment-list.component";

describe('ReviewListComponent', () => {
  let component: CommentListComponent;
  let fixture: ComponentFixture<CommentListComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [CommentListComponent],
    });

    fixture = TestBed.createComponent(CommentListComponent);
    component = fixture.componentInstance;
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should sort comments in ngOnInit()', () => {
    component.comments = [
      {id: 1, userId: 1, postId: 1, content: 'Comment', createdAt: '2024-12-1 15:30:07'},
      {id: 1, userId: 1, postId: 1, content: 'Comment', createdAt: '2024-12-10 15:30:07'},
      {id: 1, userId: 1, postId: 1, content: 'Comment', createdAt: '2024-12-4 15:30:07'},
    ];

    component.ngOnInit();

    expect(component.comments[0].createdAt).toBe('2024-12-10 15:30:07');
    expect(component.comments[1].createdAt).toBe('2024-12-4 15:30:07');
    expect(component.comments[2].createdAt).toBe('2024-12-1 15:30:07');
  });
});
