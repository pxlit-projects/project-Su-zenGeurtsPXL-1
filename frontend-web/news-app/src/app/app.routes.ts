import { Routes } from '@angular/router';
import {PostListComponent} from "./core/posts/post-list/post-list.component";
import {AddPostComponent} from "./core/posts/add-post/add-post.component";
import {LoginComponent} from "./core/login/login.component";
import {PageNotFoundComponent} from "./core/page-not-found/page-not-found.component";
import {confirmLeaveGuard} from "./shared/routeguards/confirm-leave/confirm-leave.guard";
import {HomeComponent} from "./core/home/home.component";
import {PostDetailComponent} from "./core/posts/post-detail/post-detail.component";
import {AuthGuard} from "./shared/routeguards/auth/auth.guard";
import {EditPostComponent} from "./core/posts/edit-post/edit-post.component";

export const routes: Routes = [
  {path: 'home', component: HomeComponent},
  {path: '', redirectTo: 'home', pathMatch: 'full'},
  {path: 'login', component: LoginComponent},
  {path: 'post', component: PostListComponent},
  {path: 'myPost', component: PostListComponent, canActivate: [AuthGuard]},
  {path: 'review', component: PostListComponent, canActivate: [AuthGuard]},
  {path: 'addPost', component: AddPostComponent, canDeactivate: [confirmLeaveGuard], canActivate: [AuthGuard]},
  {path: 'editPost/:id', component: EditPostComponent, canDeactivate: [confirmLeaveGuard], canActivate: [AuthGuard]},
  {path: 'post/:id', component: PostDetailComponent},
  {path: 'myPost/:id', component: PostDetailComponent, canActivate: [AuthGuard]},
  {path: 'review/:id', component: PostDetailComponent, canActivate: [AuthGuard]},
  {path: 'pageNotFound', component: PageNotFoundComponent},
  {path: '**', component: PageNotFoundComponent},
];
