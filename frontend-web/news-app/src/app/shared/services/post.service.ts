import {inject, Injectable} from '@angular/core';
import {Post} from "../models/post.model";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class PostService {
  api: string = '/post/api/post';
  http: HttpClient = inject(HttpClient);

  getPosts(): Observable<Post[]> {
    console.log("Fetching posts...");
    return this.http.get<Post[]>(this.api);
  }
}

