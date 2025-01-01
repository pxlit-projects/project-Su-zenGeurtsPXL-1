import {inject, Injectable} from '@angular/core';
import {Post} from '../models/post.model';
import {HttpClient} from '@angular/common/http';
import {map, Observable} from 'rxjs';
import { environment } from '../../../environments/environment';
import {PostRequest} from '../models/post-request.model';
import {Filter} from '../models/filter.model';
import {AuthenticationService} from './authentication.service';

@Injectable({
  providedIn: 'root'
})
export class PostService {
  api: string = environment.apiUrl + '/post/api/post';
  http: HttpClient = inject(HttpClient);
  authenticationService: AuthenticationService = inject(AuthenticationService);

  getPost(id: number): Observable<Post> {
    return this.http.get<Post>(this.api + '/' + id);
  }

  getCategories(): Observable<string[]> {
    return this.http.get<string[]>(this.api + '/category');
  }

  getPublishedPosts(): Observable<Post[]> {
    return this.http.get<Post[]>(this.api + '/published');
  }

  getMyPosts(): Observable<Post[]> {
    return this.http.get<Post[]>(this.api + '/mine',  { headers: this.getHeaders() });
  }

  getSubmittedPosts(): Observable<Post[]> {
    return this.http.get<Post[]>(this.api + '/submitted',  { headers: this.getHeaders() });
  }

  addPost(post: PostRequest): Observable<Post> {
    return this.http.post<Post>(this.api, post, { headers: this.getHeaders() });
  }

  submitPost(id: number): Observable<void> {
    return this.http.post<void>(this.api + '/' + id + '/submit', null, { headers:this.getHeaders() });
  }

  editPost(id: number, post: PostRequest): Observable<Post> {
    return this.http.put<Post>(this.api + '/' + id, post, { headers: this.getHeaders() });
  }

  filterPublishedPosts(filter: Filter): Observable<Post[]> {
    return this.getPublishedPosts().pipe(
      map((posts: Post[]) => posts.filter(post => this.isPostMatchingFilter(post, filter)))
    );
  }

  filterMyPosts(filter: Filter): Observable<Post[]> {
    return this.getMyPosts().pipe(
      map((posts: Post[]) => posts.filter(post => this.isPostMatchingFilter(post, filter)))
    );
  }

  filterSubmittedPosts(filter: Filter): Observable<Post[]> {
    return this.getSubmittedPosts().pipe(
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
    const matchesDate = filter.date === '' ? true : this.checkDate(new Date(post.createdAt), new Date(filter.date));
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

  public getHeaders(): any {
    let userId = localStorage.getItem('userId') == null ? '' : localStorage.getItem('userId');
    let userRole = localStorage.getItem('userId') == null ? '' : localStorage.getItem('userRole');
    return { userId: userId, userRole: userRole, 'Content-Type': 'application/json' };
  }
}
