import {inject, Injectable} from '@angular/core';
import {Post} from "../models/post.model";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {PostRequest} from "../models/post-request.model";

@Injectable({
  providedIn: 'root'
})
export class PostService {
  api: string = 'http://localhost:8085/post/api/post';
  http: HttpClient = inject(HttpClient);

  getPosts(): Observable<Post[]> {
    return this.http.get<Post[]>(this.api);
  }

  getCategories(): Observable<string[]> {
    return this.http.get<string[]>(this.api + '/category');
  }

  addPost(post: PostRequest): Observable<Post> {
    return this.http.post<Post>(`${this.api}`, post);
  }
}
