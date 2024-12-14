import { CanDeactivateFn } from '@angular/router';
import { AddPostComponent } from '../../core/posts/add-post/add-post.component';
import {EditPostComponent} from "../../core/posts/edit-post/edit-post.component";

export const confirmLeaveGuard: CanDeactivateFn<AddPostComponent | EditPostComponent> = async (component) => {
  if (component.postForm.dirty) {
    return window.confirm("Are you sure you want to leave this page?");
  } else {
    return true;
  }
};
