import {TestBed} from '@angular/core/testing';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import {ActivatedRoute, CanDeactivateFn, Router, UrlSegment} from '@angular/router';
import { confirmLeaveGuard } from './confirm-leave.guard';
import { AddPostComponent } from '../../../core/posts/add-post/add-post.component';
import { EditPostComponent } from '../../../core/posts/edit-post/edit-post.component';

class MockAddPostComponent {
  postForm = new FormBuilder().group({
    title: [''],
    content: ['']
  });
}

class MockEditPostComponent {
  postForm = new FormBuilder().group({
    title: [''],
    content: ['']
  });
}

describe('confirmLeaveGuard', () => {
  let guard: CanDeactivateFn<AddPostComponent | EditPostComponent>;
  let routeMock: jasmine.SpyObj<ActivatedRoute>;
  let currentRouterMock: jasmine.SpyObj<Router>;
  let nextRouterMock: jasmine.SpyObj<Router>;

  beforeEach(() => {
    routeMock = jasmine.createSpyObj('ActivatedRoute', [], {
      snapshot: { url: [new UrlSegment('addPost', {})]}
    });

    currentRouterMock = jasmine.createSpyObj('Router', [], {
      state: {url: [new UrlSegment('addPost', {})]}
    });

    nextRouterMock = jasmine.createSpyObj('Router', [], {
      state: {url: [new UrlSegment('addPost', {})]}
    });

    TestBed.configureTestingModule({
      imports: [ReactiveFormsModule],
      providers: [
        { provide: AddPostComponent, useClass: MockAddPostComponent },
        { provide: EditPostComponent, useClass: MockEditPostComponent },
        { provide: ActivatedRoute, useValue: routeMock }
      ]
    });

    guard = confirmLeaveGuard;
  });

  it('should allow navigation if the form is pristine', async () => {
    const mockComponent = new MockAddPostComponent();
    mockComponent.postForm.markAsPristine();

    // @ts-ignore
    const result = await guard(mockComponent, routeMock, currentRouterMock, nextRouterMock);
    expect(result).toBeTrue();
  });

  it('should show confirmation if the form is dirty', async () => {
    const mockComponent = new MockAddPostComponent();
    mockComponent.postForm.markAsDirty();

    spyOn(window, 'confirm').and.returnValue(false);

    // @ts-ignore
    const result = await guard(mockComponent, routeMock, currentRouterMock, nextRouterMock);
    expect(window.confirm).toHaveBeenCalledWith(
      'Are you sure you want to leave this page?'
    );
    expect(result).toBeFalse();
  });
});
