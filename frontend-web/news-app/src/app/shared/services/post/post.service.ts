import {inject, Injectable} from '@angular/core';
import {Post} from '../../models/posts/post.model';
import {Notification} from '../../models/posts/notification.model';
import {HttpClient} from '@angular/common/http';
import {map, Observable} from 'rxjs';
import { environment } from '../../../../environments/environment';
import {PostRequest} from '../../models/posts/post-request.model';
import {Filter} from '../../models/filter.model';
import {AuthenticationService} from '../authentication/authentication.service';

@Injectable({
  providedIn: 'root'
})

export class PostService {
  api: string = environment.apiUrl + '/post/api/post';
  http: HttpClient = inject(HttpClient);
  authenticationService: AuthenticationService = inject(AuthenticationService);

  getPosts(isMine: boolean, isToReview: boolean): Observable<Post[]> {
    if (isMine) {
      return this.http.get<Post[]>(this.api + '/mine', { headers: this.authenticationService.getHeaders() });
    }
    else if (isToReview) {
      return this.http.get<Post[]>(this.api + '/reviewable', { headers: this.authenticationService.getHeaders() });
    } else {
      return this.http.get<Post[]>(this.api + '/published');
    }
  }

  getPost(id: number): Observable<Post> {
    return this.http.get<Post>(this.api + '/' + id);
  }

  getPostWithReviews(id: number): Observable<Post> {
    return this.http.get<Post>(this.api + '/' + id + '/with-reviews', { headers: this.authenticationService.getHeaders() });
  }

  getPostWithComments(id: number): Observable<Post> {
    return this.http.get<Post>(this.api + '/' + id + '/with-comments');
  }

  getCategories(): Observable<string[]> {
    return this.http.get<string[]>(this.api + '/category');
  }

  getMyNotifications(): Observable<Notification[]> {
    return this.http.get<Notification[]>(this.api + '/notification/mine', { headers: this.authenticationService.getHeaders() });
  }

  addPost(post: PostRequest): Observable<void> {
    return this.http.post<void>(this.api, post, { headers: this.authenticationService.getHeaders() });
  }

  editPost(id: number, content: string): Observable<void> {
    return this.http.put<void>(this.api + '/' + id, content, { headers: this.authenticationService.getHeaders() });
  }

  submitPost(id: number): Observable<void> {
    return this.http.put<void>(this.api + '/' + id + '/submit', null, { headers:this.authenticationService.getHeaders() });
  }

  publishPost(id: number): Observable<void> {
    return this.http.put<void>(this.api + '/' + id + '/publish', null, { headers:this.authenticationService.getHeaders() });
  }

  markNotificationAsRead(notificationId: number): Observable<void> {
    return this.http.put<void>(this.api + '/notification/' + notificationId + '/read', null, { headers:this.authenticationService.getHeaders() });
  }

  filterPosts(filter: Filter, isMine: boolean, isToReview: boolean): Observable<Post[]> {
    return this.getPosts(isMine, isToReview).pipe(
      map((posts: Post[]) => posts.filter(post => this.isPostMatchingFilter(post, filter)))
    );
  }

  isPostMatchingFilter(post: Post, filter: Filter): boolean {
    const user = this.authenticationService.getUserById(post.userId);

    const matchesContent = this.isIncluded(post.content, filter.content);
    const matchesAuthor = this.isIncluded(user!.fullName, filter.author);
    const matchesDate = filter.date === '' ? true : this.isDatesMatching(new Date(post.createdAt), new Date(filter.date));

    return matchesContent && matchesAuthor && matchesDate;
  }

  isDatesMatching(postDate: Date, filterDate: Date): boolean {
    const matchesDay = postDate.getDate() === filterDate.getDate();
    const matchesMonth = postDate.getMonth() === filterDate.getMonth();
    const matchesYear = postDate.getFullYear() === filterDate.getFullYear();

    return matchesDay && matchesMonth && matchesYear;
  }

  isIncluded(item: string, filter: string): boolean {
    return item.toLowerCase().includes(filter.toLowerCase());
  }
}
