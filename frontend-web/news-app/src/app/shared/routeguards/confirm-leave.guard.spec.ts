import { confirmLeaveGuard } from './confirm-leave.guard';
import { AddPostComponent } from '../../core/posts/add-post/add-post.component';
import { EditPostComponent } from '../../core/posts/edit-post/edit-post.component';

import { TestBed } from '@angular/core/testing';
import { CanDeactivateFn } from '@angular/router';

describe('ConfirmLeaveGuard', () => {
  let guard: CanDeactivateFn<AddPostComponent | EditPostComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    guard = confirmLeaveGuard;
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  xit('should allow navigation if the form is not dirty', () => {
  });

  xit('should deny navigation if the form is dirty', () => {
  });
});
