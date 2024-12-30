import {Post} from "../../../shared/models/post.model";
import {PostRequest} from "../../../shared/models/post-request.model";
import {PostService} from "../../../shared/services/post.service";
import {EditPostComponent} from "./edit-post.component";

import {ComponentFixture, TestBed} from '@angular/core/testing';
import {Router, ActivatedRoute} from '@angular/router';
import {ReactiveFormsModule} from '@angular/forms';
import {of, throwError} from 'rxjs';
import {By} from "@angular/platform-browser";


describe('EditPostComponent', () => {
  let component: EditPostComponent;
  let fixture: ComponentFixture<EditPostComponent>;
  let postServiceMock: jasmine.SpyObj<PostService>;
  let routerMock: jasmine.SpyObj<Router>;
  let routeMock: jasmine.SpyObj<ActivatedRoute>;

  const mockPost = {
    title: 'Title',
    content: 'Updated content',
    userId: 1,
    category: 'ACADEMIC',
    createdAt: '2024-12-10 15:30:07',
    state: 'DRAFTED'
  };

  beforeEach(() => {
    localStorage.setItem('userId', '1');
    postServiceMock = jasmine.createSpyObj('PostService', ['editPost', 'getPost', 'toPascalCasing']);
    routerMock = jasmine.createSpyObj('Router', ['navigate']);
    routeMock = jasmine.createSpyObj('ActivatedRoute', [], {
      snapshot: { params: {'id': 1}}
    });

    postServiceMock.getPost.and.returnValue(of(mockPost as Post));

    TestBed.configureTestingModule({
      imports: [EditPostComponent, ReactiveFormsModule],
      providers: [
        { provide: PostService, useValue: postServiceMock },
        { provide: Router, useValue: routerMock },
        { provide: ActivatedRoute, useValue: routeMock }
      ]
    });

    fixture = TestBed.createComponent(EditPostComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the form with required controls and validators', () => {
    expect(component.postForm).toBeTruthy();
    expect(component.postForm.controls['content'].valid).toBeTrue();
  });

  it('should navigate to pageNotFound when getPost fails in ngOnInit', () => {
    postServiceMock.getPost.and.returnValue(throwError(() => new Error('Post not found')));
    fixture = TestBed.createComponent(EditPostComponent);
    component = fixture.componentInstance;

    component.ngOnInit();

    expect(routerMock.navigate).toHaveBeenCalledWith(['/pageNotFound']);
  });

  it('should call editPost on form submit and navigate on success', () => {
    const postRequest = {
      content: 'Updated content'
    };

    component.postForm.setValue(postRequest);
    postServiceMock.editPost.and.returnValue(of(mockPost as Post));

    component.onSubmit();

    expect(postServiceMock.editPost).toHaveBeenCalledWith(1, postRequest as PostRequest);

    expect(component.postForm.pristine).toBeTrue();
    expect(routerMock.navigate).toHaveBeenCalledWith(['/myPost/1']);
  });

  it('should display error message when editPost fails', () => {
    const errorResponse = {
      error: {message: 'Failed to update the post'
      },
    };
    postServiceMock.editPost.and.returnValue(throwError(() => errorResponse));

    component.onSubmit();
    fixture.detectChanges();

    const debugElement = fixture.debugElement.query(By.css('#errorMessage'));
    expect(debugElement.nativeElement.textContent).toContain('Failed to update the post');
  });

  it('should navigate back on cancel', () => {
    component.cancel();
    expect(routerMock.navigate).toHaveBeenCalledWith(['/myPost']);
  });
});
