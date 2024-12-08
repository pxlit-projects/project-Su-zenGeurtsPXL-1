import { CanDeactivateFn } from '@angular/router';
import {AddPostComponent} from "./core/posts/add-post/add-post.component";

export const confirmLeaveGuard: CanDeactivateFn<AddPostComponent> = (component) => {
  if(component.postForm.dirty){
    // TODO: Fix styling (https://tailwindui.com/components/application-ui/overlays/modal-dialogs)
    return window.confirm("Are you sure you want to leave this page?");
  }else{
    return true;
  }
};
