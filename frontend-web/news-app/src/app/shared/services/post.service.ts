import {inject, Injectable} from '@angular/core';
import {Post} from "../models/post.model";
import {HttpClient} from "@angular/common/http";
import {map, Observable} from "rxjs";
import { environment } from '../../../environments/environment';
import {PostRequest} from "../models/post-request.model";
import {Filter} from "../models/filter.model";
import {AuthenticationService} from "./authentication.service";

@Injectable({
  providedIn: 'root'
})
export class PostService {
  api: string = environment.apiUrl + '/post/api/post';
  http: HttpClient = inject(HttpClient);
  authenticationService: AuthenticationService = inject(AuthenticationService);

  filterPosts(filter: Filter): Observable<Post[]> {
    return this.http.get<Post[]>(this.api + '/published').pipe(
      map((posts: Post[]) => posts.filter(post => this.isPostMatchingFilter(post, filter)))
    );
  }

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


  public isPostMatchingFilter(post: Post, filter: Filter): boolean {

    const user = this.authenticationService.getUserById(post.userId);
    const matchesContent = post.content.toLowerCase().includes(filter.content.toLowerCase());
    // @ts-ignore
    console.log(user.fullName);
    console.log(filter.author.toLowerCase());
    // @ts-ignore
    const matchesAuthor = user.fullName.toLowerCase().includes(filter.author.toLowerCase());
    const matchesCategory = filter.category ? post.category.toLowerCase() === filter.category.toLowerCase() : true;

    return matchesContent && matchesAuthor && matchesCategory;
  }
}
