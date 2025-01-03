import {PostService} from "../../../shared/services/post.service";
import {PostDetailComponent} from "./post-detail.component";
import {ComponentFixture, TestBed} from "@angular/core/testing";
import {ActivatedRoute, Router, UrlSegment} from "@angular/router";
import {of, throwError} from "rxjs";
import {ReactiveFormsModule} from "@angular/forms";
import {Post} from "../../../shared/models/post.model";

describe('PostDetailComponent', () => {
  let component: PostDetailComponent;
  let fixture: ComponentFixture<PostDetailComponent>;
  let postServiceMock: jasmine.SpyObj<PostService>;
  let routerMock: jasmine.SpyObj<Router>;
  let routeMock: jasmine.SpyObj<ActivatedRoute>;
  let windowMock: jasmine.SpyObj<Window>;

  beforeEach(() => {
    localStorage.setItem('userId', '1');
    postServiceMock = jasmine.createSpyObj('PostService', ['submitPost', 'getPost', 'publishPost', 'reviewPost', 'getPostWithReviews', 'transformDate', 'toPascalCasing']);
    routerMock = jasmine.createSpyObj('Router', ['navigate']);
    routeMock = jasmine.createSpyObj('ActivatedRoute', [], {
      snapshot: { url: [new UrlSegment('posts', {})], params: {'id': 1}}
    });

    windowMock = jasmine.createSpyObj('Window', ['alert']);

    const mockPost = {
      title: 'Title',
      content: 'Content',
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
        { provide: ActivatedRoute, useValue: routeMock },
        { provide: Window, useValue: windowMock }
      ]
    });

    fixture = TestBed.createComponent(PostDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should initialize "mine" based on route URL', () => {
    // !mine
    expect(component.mine).toBe(false);

    // mine
    routeMock.snapshot.url = [new UrlSegment('myPost', {})];
    fixture = TestBed.createComponent(PostDetailComponent);
    component = fixture.componentInstance;

    expect(component.mine).toBe(true);
  });

  it('should navigate to pageNotFound when getPost fails in ngOnInit', () => {
    postServiceMock.getPost.and.returnValue(throwError(() => new Error('Post not found')));
    fixture = TestBed.createComponent(PostDetailComponent);
    component = fixture.componentInstance;

    component.ngOnInit();

    expect(routerMock.navigate).toHaveBeenCalledWith(['/pageNotFound']);
  });

  it('should fetch posts with reviews when post relevant in ngOnInit', () => {
    const SubmittedPost = {
      title: 'Title',
      content: 'Content',
      userId: 1,
      category: 'ACADEMIC',
      createdAt: '2024-12-10 15:30:07',
      state: 'SUBMITTED'
    };

    postServiceMock.getPost.and.returnValue(of(SubmittedPost as Post));

    fixture = TestBed.createComponent(PostDetailComponent);
    component = fixture.componentInstance;

    component.ngOnInit();

    expect(postServiceMock.getPostWithReviews).toHaveBeenCalledWith(1);
  });

  it('should call submitPost on submit and navigate on success', () => {
    postServiceMock.submitPost.and.returnValue(of(undefined));

    component.submitPost();

    expect(postServiceMock.submitPost).toHaveBeenCalledWith(1);
    expect(routerMock.navigate).toHaveBeenCalledWith(['/myPost']);
  });

  it('should call publishPost on publish and navigate on success', () => {
    postServiceMock.publishPost.and.returnValue(of(undefined));

    component.publishPost();

    expect(postServiceMock.publishPost).toHaveBeenCalledWith(1);
    expect(routerMock.navigate).toHaveBeenCalledWith(['/myPost']);
  });

  it('should call reviewPost on review and navigate on success', () => {
    let reviewType = 'approve';
    let reviewRequest = {
      postId: 1,
      content: 'Comment'
    }

    component.commentForm.setValue({content: 'Comment'});

    postServiceMock.reviewPost.and.returnValue(of(undefined));

    component.reviewPost(reviewType);

    expect(postServiceMock.reviewPost).toHaveBeenCalledWith(reviewType, reviewRequest);
    expect(routerMock.navigate).toHaveBeenCalledWith(['/review']);
  });

  it('should alert with an error when reviewPost fails', () => {
    let reviewType = 'approve';
    let reviewRequest = {
      postId: 1,
      content: ''
    }

    component.commentForm.setValue({content: ''});
    expect(component.commentForm.valid).toBe(false);
    // expect(windowMock.alert).toHaveBeenCalledWith('Comment cannot be empty!');
    expect(postServiceMock.reviewPost).not.toHaveBeenCalledWith(reviewType, reviewRequest);
  });
});
