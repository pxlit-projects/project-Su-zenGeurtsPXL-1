import {inject, Injectable} from '@angular/core';
import {Post} from "../models/post.model";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import { environment } from '../../../environments/environment';
import {PostRequest} from "../models/post-request.model";

@Injectable({
  providedIn: 'root'
})
export class PostService {
  api: string = environment.apiUrl + '/post/api/post';
  http: HttpClient = inject(HttpClient);

  getPosts(): Observable<Post[]> {
    return this.http.get<Post[]>(this.api);
  }

  getPost(id: number): Observable<Post> {
    return this.http.get<Post>(this.api + "/" + id);
  }

  getCategories(): Observable<string[]> {
    return this.http.get<string[]>(this.api + '/category');
  }

  getPublishedPosts(): Observable<Post[]> {
    return this.http.get<Post[]>(this.api + '/published');
  }

  getPostsByUserId(userId: string | null): Observable<Post[]> {
    return this.http.get<Post[]>(this.api + "/user/" + userId);
  }

  addPost(post: PostRequest): Observable<Post> {
    return this.http.post<Post>(this.api, post);
  }

  submitPost(id: number, userId: number): Observable<void> {
    return this.http.post<void>(this.api + "/submit/" + id, userId);
  }

  editPost(id: number, post: PostRequest): Observable<Post> {
    return this.http.put<Post>(this.api + "/" + id, post);
  }

  public transformDate(date: string): string {
    const dateDate = new Date(date);
    return dateDate.toLocaleDateString('en-US', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric', hour: 'numeric', minute: 'numeric' });
  }

  public toPascalCasing(category: string): string {
    return category.charAt(0).toUpperCase() + category.slice(1).toLowerCase();
  }
}
