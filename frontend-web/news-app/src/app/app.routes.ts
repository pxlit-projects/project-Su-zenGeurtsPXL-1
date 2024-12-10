import { Routes } from '@angular/router';
import {PostListComponent} from "./core/posts/post-list/post-list.component";
import {AddPostComponent} from "./core/posts/add-post/add-post.component";
import {LoginComponent} from "./core/login/login.component";
import {PageNotFoundComponent} from "./core/page-not-found/page-not-found.component";
import {confirmLeaveGuard} from "./confirm-leave.guard";
import {MyPostListComponent} from "./core/posts/my-post-list/my-post-list.component";
import {HomeComponent} from "./core/home/home.component";
import {PostDetailComponent} from "./core/posts/post-detail/post-detail.component";
import {AuthGuard} from "./auth.guard";
import {EditPostComponent} from "./core/posts/edit-post/edit-post.component";

export const routes: Routes = [
  {path: 'home', component: HomeComponent},
  {path: '', redirectTo: 'home', pathMatch: 'full'},
  {path: 'login', component: LoginComponent},
  {path: 'post', component: PostListComponent},
  {path: 'post/mine', component: MyPostListComponent, canActivate: [AuthGuard]},
  {path: 'addPost', component: AddPostComponent, canDeactivate: [confirmLeaveGuard]},
  {path: 'editPost/:id', component: EditPostComponent, canDeactivate: [confirmLeaveGuard]},
  {path: 'post/:id', component: PostDetailComponent},
  {path: 'post/mine/:id', component: PostDetailComponent, canActivate: [AuthGuard]},
  {path: '**', component: PageNotFoundComponent},
];
