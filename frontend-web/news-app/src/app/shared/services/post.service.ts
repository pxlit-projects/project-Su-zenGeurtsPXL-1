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

  getPost(id: number): Observable<Post> {
    return this.http.get<Post>(this.api + "/" + id);
  }

  getCategories(): Observable<string[]> {
    return this.http.get<string[]>(this.api + '/category');
  }

  getPublishedPosts(): Observable<Post[]> {
    return this.http.get<Post[]>(this.api + '/published');
  }

  getMyPosts(userId: string | null): Observable<Post[]> {
    return this.http.get<Post[]>(this.api + "/user/" + userId);
  }

  addPost(post: PostRequest): Observable<Post> {
    return this.http.post<Post>(this.api, post);
  }

  submitPost(id: number, userId: number | null): Observable<void> {
    if (userId == null) {
      throw new Error("Nobody is signed in.");
    } else {
      return this.http.post<void>(this.api + "/submit/" + id, userId);
    }
  }

  editPost(id: number, post: PostRequest): Observable<Post> {
    return this.http.put<Post>(this.api + "/" + id, post);
  }

  filterPublishedPosts(filter: Filter): Observable<Post[]> {
    return this.getPublishedPosts().pipe(
      map((posts: Post[]) => posts.filter(post => this.isPostMatchingFilter(post, filter)))
    );
  }

  filterMyPosts(filter: Filter, userId: string | null): Observable<Post[]> {
    return this.getMyPosts(userId).pipe(
      map((posts: Post[]) => posts.filter(post => this.isPostMatchingFilter(post, filter)))
    );
  }


  public transformDate(date: string): string {
    const dateDate = new Date(date);
    return dateDate.toLocaleDateString('en-US', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric', hour: 'numeric', minute: 'numeric' });
  }

  public toPascalCasing(word: string): string {
    return word.charAt(0).toUpperCase() + word.slice(1).toLowerCase();
  }


  public isPostMatchingFilter(post: Post, filter: Filter): boolean {
    const user = this.authenticationService.getUserById(post.userId);
    if (user == undefined) return false;

    const matchesContent = this.checkInclusion(post.content, filter.content);
    const matchesAuthor = this.checkInclusion(user.fullName, filter.author);
    const matchesDate = filter.date === ""? true : this.checkDate(new Date(post.createdAt), new Date(filter.date));
    return matchesContent && matchesAuthor && matchesDate;
  }

  public checkDate(postDate: Date, filterDate: Date): boolean {
    const matchesDay = postDate.getDate() === filterDate.getDate();
    const matchesMonth = postDate.getMonth() === filterDate.getMonth();
    const matchesYear = postDate.getFullYear() === filterDate.getFullYear();

    return matchesDay && matchesMonth && matchesYear;
  }

  public checkInclusion(item: string, filter: string): boolean {
    return item.toLowerCase().includes(filter.toLowerCase());
  }

  public orderToMostRecent(posts: Observable<Post[]>): Observable<Post[]> {
    return posts.pipe(map((posts: Post[]) =>
      posts.sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())));
  }
}
