import { Routes } from '@angular/router';
import {PostListComponent} from "./core/posts/post-list/post-list.component";
import {AddPostComponent} from "./core/posts/add-post/add-post.component";
import {LoginComponent} from "./core/login/login.component";
import {PageNotFoundComponent} from "./core/page-not-found/page-not-found.component";
import {confirmLeaveGuard} from "./confirm-leave.guard";

export const routes: Routes = [
  {path: 'posts', component: PostListComponent},
  {path: 'addPost', component: AddPostComponent, canDeactivate: [confirmLeaveGuard]},
  {path: 'login', component: LoginComponent},
  {path: '', redirectTo: 'posts', pathMatch: 'full'},
  {path: '**', component: PageNotFoundComponent}
];
