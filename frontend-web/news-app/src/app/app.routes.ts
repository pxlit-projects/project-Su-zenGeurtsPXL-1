import { Routes } from '@angular/router';
import {PostListComponent} from "./core/posts/post-list/post-list.component";
import {AddPostComponent} from "./core/posts/add-post/add-post.component";

export const routes: Routes = [
  {path: 'posts', component: PostListComponent},
  {path: 'addPost', component: AddPostComponent},
  {path: '', redirectTo: 'posts', pathMatch: 'full'}
];
