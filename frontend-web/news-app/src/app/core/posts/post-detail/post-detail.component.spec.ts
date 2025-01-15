import {PostService} from "../../../shared/services/post/post.service";
import {PostDetailComponent} from "./post-detail.component";
import {ComponentFixture, fakeAsync, TestBed, tick} from "@angular/core/testing";
import {ActivatedRoute, Router, UrlSegment} from "@angular/router";
import {of, throwError} from "rxjs";
import {ReactiveFormsModule} from "@angular/forms";
import {Post} from "../../../shared/models/posts/post.model";
import {ReviewService} from "../../../shared/services/review/review.service";
import {CommentService} from "../../../shared/services/comment/comment.service";
import {HelperService} from "../../../shared/services/helper/helper.service";

describe('PostDetailComponent', () => {
  let component: PostDetailComponent;
  let fixture: ComponentFixture<PostDetailComponent>;

  let postServiceMock: jasmine.SpyObj<PostService>;
  let reviewServiceMock: jasmine.SpyObj<ReviewService>;
  let commentServiceMock: jasmine.SpyObj<CommentService>;
  let helperServiceMock: jasmine.SpyObj<HelperService>;

  let routerMock: jasmine.SpyObj<Router>;
  let routeMock: jasmine.SpyObj<ActivatedRoute>;
  let locationMock: jasmine.SpyObj<Location>;

  localStorage.setItem('userId', '1');

  beforeEach(() => {
    postServiceMock = jasmine.createSpyObj('PostService', ['getPost', 'getPostWithReviews', 'getPostWithComments', 'publishPost', 'submitPost']);
    reviewServiceMock = jasmine.createSpyObj('ReviewService', ['reviewPost']);
    commentServiceMock = jasmine.createSpyObj('CommentService', ['addComment']);
    helperServiceMock = jasmine.createSpyObj('HelperService', ['transformDate', 'toPascalCasing', 'noWhitespaceValidator']);

    routerMock = jasmine.createSpyObj('Router', ['navigate']);
    routeMock = jasmine.createSpyObj('ActivatedRoute', [], {
      snapshot: { url: [new UrlSegment('posts', {})], params: {'id': 1}}
    });
    locationMock = jasmine.createSpyObj('Location', ['reload', 'alert']);

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
        { provide: ReviewService, useValue: reviewServiceMock },
        { provide: CommentService, useValue: commentServiceMock },
        { provide: HelperService, useValue: helperServiceMock },
        { provide: Router, useValue: routerMock },
        { provide: ActivatedRoute, useValue: routeMock },
        { provide: Location, useValue: locationMock }
      ]
    });

    fixture = TestBed.createComponent(PostDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should initialize "isMine" based on route URL', () => {
    // !MINE
    expect(component.isMine).toBe(false);

    // MINE
    routeMock.snapshot.url = [new UrlSegment('myPost', {})];
    fixture = TestBed.createComponent(PostDetailComponent);
    component = fixture.componentInstance;

    expect(component.isMine).toBe(true);
  });

  it('should call fetchPost periodically', fakeAsync(() => {
    fixture = TestBed.createComponent(PostDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();

    spyOn(component, 'fetchPost');

    tick(3000);

    expect(component.fetchPost).toHaveBeenCalledTimes(3);

    component.fetchSubscription?.unsubscribe();
  }));

  it('should navigate to pageNotFound when getPost fails in ngOnInit', () => {
    postServiceMock.getPost.and.returnValue(throwError(() => new Error('Post not found')));
    fixture = TestBed.createComponent(PostDetailComponent);
    component = fixture.componentInstance;

    component.ngOnInit();

    expect(routerMock.navigate).toHaveBeenCalledWith(['/pageNotFound']);
  });

  it('should fetch post with reviews when relevant in ngOnInit', () => {
    spyOn(component, 'handleFetch').and.returnValue(undefined)
    fixture.detectChanges();

    const post = {
      title: 'Title',
      content: 'Content',
      userId: 1,
      category: 'ACADEMIC',
      createdAt: '2024-12-10 15:30:07',
      state: 'SUBMITTED'
    };

    postServiceMock.getPost.and.returnValue(of(post as Post));
    postServiceMock.getPostWithReviews.and.returnValue(of(post as Post));

    fixture = TestBed.createComponent(PostDetailComponent);
    component = fixture.componentInstance;

    component.ngOnInit();

    expect(postServiceMock.getPostWithReviews).toHaveBeenCalledWith(1);
  });

  it('should fetch post with comments when relevant in ngOnInit', () => {
    const post = {
      title: 'Title',
      content: 'Content',
      userId: 1,
      category: 'ACADEMIC',
      createdAt: '2024-12-10 15:30:07',
      state: 'PUBLISHED'
    };
    postServiceMock.getPost.and.returnValue(of(post as Post));
    postServiceMock.getPostWithComments.and.returnValue(of(post as Post));
    // spyOn(component, 'handleFetch');

    fixture = TestBed.createComponent(PostDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();

    component.ngOnInit();

    expect(postServiceMock.getPostWithComments).toHaveBeenCalledWith(1);
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

  it('should add review on reviewPost', () => {
    let reviewType = 'approve';
    let reviewRequest = {
      postId: 1,
      content: 'Comment'
    }

    component.commentForm.setValue({content: 'Comment'});

    reviewServiceMock.reviewPost.and.returnValue(of(undefined));

    component.reviewPost(reviewType);

    expect(reviewServiceMock.reviewPost).toHaveBeenCalledWith(reviewType, reviewRequest);
  });

  it('should add comment on addComment', () => {
    const content = 'Comment'
    let commentRequest = {
      postId: 1,
      content: content
    }

    const commentForm = {
      content: content
    }

    component.commentForm.setValue(commentForm);

    commentServiceMock.addComment.and.returnValue(of(undefined));

    component.addComment();

    expect(commentServiceMock.addComment).toHaveBeenCalledWith(commentRequest);
  });

  it('should navigate to login on login', () => {

    component.login();

    expect(routerMock.navigate).toHaveBeenCalledWith(['/login']);
  });

  it('should refetch data on handleDelete', () => {
    spyOn(component, 'handleFetch');
    component.handleDelete();

    expect(component.handleFetch).toHaveBeenCalled();
    expect(postServiceMock.getPostWithComments).toHaveBeenCalled();
  });
});
