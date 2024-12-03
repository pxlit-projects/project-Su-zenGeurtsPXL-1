import {inject, Injectable} from '@angular/core';
import {Post} from "../models/post.model";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class PostService {
  api: string = 'http://localhost:8081/api/post/';
  http: HttpClient = inject(HttpClient);

  getPosts(): Observable<Post[]>  {
    return this.http.get<Post[]>(this.api);
  }

  getAllPosts(): void {
    console.log("Getting all posts...");
    this.http.get(this.api, {responseType: "text"})
      .subscribe({
        next: (data: string) => {
          console.log(data);
        },
        error: (error) => {
          console.log(error);
        }
      })
  }

  // addPost(newPost: Post) {
  //   this.posts.push(newPost);
  // }
}
