import {ComponentFixture, TestBed} from '@angular/core/testing';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import {ActivatedRoute, CanDeactivateFn, Router, UrlSegment} from '@angular/router';
import { confirmLeaveGuard } from './confirm-leave.guard';
import { AddPostComponent } from '../../../core/posts/add-post/add-post.component';
import { EditPostComponent } from '../../../core/posts/edit-post/edit-post.component';

// Mock Components
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
  let fixture: ComponentFixture<AddPostComponent | EditPostComponent>;

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

    // currentRoute: Route(url:'addPost', path:'addPost')
    // confirm-leave.guard.ts:7 currentState: Route(url:'', path:'') { Route(url:'addPost', path:'addPost') }
    // confirm-leave.guard.ts:8 nextState: Route(url:'', path:'') { Route(url:'post', path:'post') }

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
    fixture.detectChanges();

    // @ts-ignore
    const result = await guard(mockComponent, routeMock, currentRouterMock, nextRouterMock);
    expect(window.confirm).toHaveBeenCalledWith(
      'Are you sure you want to leave this page?'
    );
    expect(result).toBeFalse();
  });
  //
  // xit('should allow navigation if user confirms leaving', async () => {
  //   const mockComponent = new MockEditPostComponent();
  //   mockComponent.postForm.markAsDirty();
  //
  //   spyOn(window, 'confirm').and.returnValue(true);
  //
  //   const result = await guard(mockComponent, null, null, null);
  //   expect(result).toBeTrue();
  // });
  //
  // xit('should block navigation if user cancels leaving', async () => {
  //   const mockComponent = new MockEditPostComponent();
  //   mockComponent.postForm.markAsDirty();
  //
  //   spyOn(window, 'confirm').and.returnValue(false);
  //
  //   const result = await guard(mockComponent, null, null, null);
  //   expect(result).toBeFalse();
  // });
});
