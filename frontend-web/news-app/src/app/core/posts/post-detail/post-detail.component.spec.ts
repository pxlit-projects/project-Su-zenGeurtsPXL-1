import {PostService} from "../../../shared/services/post.service";
import {PostDetailComponent} from "./post-detail.component";
import {ComponentFixture, TestBed} from "@angular/core/testing";
import {ActivatedRoute, Router} from "@angular/router";
import {of, throwError} from "rxjs";
import {ReactiveFormsModule} from "@angular/forms";
import {Post} from "../../../shared/models/post.model";

describe('PostDetailComponent', () => {
  let component: PostDetailComponent;
  let fixture: ComponentFixture<PostDetailComponent>;
  let postServiceMock: jasmine.SpyObj<PostService>;
  let routerMock: jasmine.SpyObj<Router>;
  let routeMock: jasmine.SpyObj<ActivatedRoute>;

  beforeEach(() => {
    localStorage.setItem('userId', '1');
    postServiceMock = jasmine.createSpyObj('PostService', ['submitPost', 'getPost', 'transformDate', 'toPascalCasing']);
    routerMock = jasmine.createSpyObj('Router', ['navigate']);
    routeMock = jasmine.createSpyObj('ActivatedRoute', [], {
      snapshot: { params: {'id': 1}}
    });

    const mockPost = {
      title: 'Title',
      content: 'Updated content',
      userId: 1,
      category: 'ACADEMIC',
      createdAt: '2024-12-10 15:30:07',
      state: 'DRAFTED'
    };

    postServiceMock.getPost.and.returnValue(of(mockPost as Post));

    TestBed.configureTestingModule({
      imports: [PostDetailComponent, ReactiveFormsModule],
      providers: [
        { provide: PostService, useValue: postServiceMock },
        { provide: Router, useValue: routerMock },
        { provide: ActivatedRoute, useValue: routeMock }
      ]
    });

    fixture = TestBed.createComponent(PostDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should navigate to pageNotFound when getPost fails in ngOnInit', () => {
    postServiceMock.getPost.and.returnValue(throwError(() => new Error('Post not found')));
    fixture = TestBed.createComponent(PostDetailComponent);
    component = fixture.componentInstance;

    component.ngOnInit();

    expect(routerMock.navigate).toHaveBeenCalledWith(['/pageNotFound']);
  });

  it('should call submitPost on form submit and navigate on success', () => {
    postServiceMock.submitPost.and.returnValue(of(undefined));

    component.submitPost();

    expect(postServiceMock.submitPost).toHaveBeenCalledWith(1);
    expect(routerMock.navigate).toHaveBeenCalledWith(['/myPost']);
  });
});
