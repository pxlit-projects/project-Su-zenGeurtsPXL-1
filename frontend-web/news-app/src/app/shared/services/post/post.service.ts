import {inject, Injectable} from '@angular/core';
import {Post} from '../../models/posts/post.model';
import {Comment} from '../../models/comments/comment.model';
import {Notification} from '../../models/posts/notification.model';
import {HttpClient} from '@angular/common/http';
import {map, Observable} from 'rxjs';
import { environment } from '../../../../environments/environment';
import {PostRequest} from '../../models/posts/post-request.model';
import {Filter} from '../../models/filter.model';
import {AuthenticationService} from '../authentication/authentication.service';
import {ReviewRequest} from "../../models/reviews/reviewRequest.model";
import {CommentRequest} from "../../models/comments/comment-request.model";

@Injectable({
  providedIn: 'root'
})
export class PostService {
  postApi: string = environment.apiUrl + '/post/api/post';
  reviewApi: string = environment.apiUrl + '/review/api/review';
  commentApi: string = environment.apiUrl + '/comment/api/comment';
  http: HttpClient = inject(HttpClient);
  authenticationService: AuthenticationService = inject(AuthenticationService);

  getPost(id: number): Observable<Post> {
    return this.http.get<Post>(this.postApi + '/' + id);
  }

  getCategories(): Observable<string[]> {
    return this.http.get<string[]>(this.postApi + '/category');
  }

  getPublishedPosts(): Observable<Post[]> {
    return this.http.get<Post[]>(this.postApi + '/published');
  }

  getMyPosts(): Observable<Post[]> {
    return this.http.get<Post[]>(this.postApi + '/mine', { headers: this.getHeaders() });
  }

  getReviewablePosts(): Observable<Post[]> {
    return this.http.get<Post[]>(this.postApi + '/reviewable', { headers: this.getHeaders() });
  }

  getPostWithReviews(id: number): Observable<Post> {
    return this.http.get<Post>(this.postApi + '/' + id + '/with-reviews', { headers: this.getHeaders() });
  }

  getPostWithComments(id: number): Observable<Post> {
    return this.http.get<Post>(this.postApi + '/' + id + '/with-comments');
  }

  getNotifications(): Observable<Notification[]> {
    return this.http.get<Notification[]>(this.postApi + '/notification', { headers: this.getHeaders() });
  }

  readNotification(notificationId: number): Observable<void> {
    return this.http.post<void>(this.postApi + '/notification/' + notificationId + '/read', null, { headers:this.getHeaders() });
  }

  addPost(post: PostRequest): Observable<Post> {
    return this.http.post<Post>(this.postApi, post, { headers: this.getHeaders() });
  }

  submitPost(id: number): Observable<void> {
    return this.http.post<void>(this.postApi + '/' + id + '/submit', null, { headers:this.getHeaders() });
  }

  publishPost(id: number): Observable<void> {
    return this.http.post<void>(this.postApi + '/' + id + '/publish', null, { headers:this.getHeaders() });
  }

  reviewPost(type: string, review: ReviewRequest): Observable<void> {
    return this.http.post<void>(this.reviewApi + '/' + type, review, { headers:this.getHeaders() });
  }

  addComment(comment: CommentRequest): Observable<Comment> {
    return this.http.post<Comment>(this.commentApi, comment, { headers:this.getHeaders() });
  }

  deleteComment(id: number): Observable<void> {
    return this.http.delete<void>(this.commentApi + '/' + id, { headers:this.getHeaders() });
  }

  editPost(id: number, post: PostRequest): Observable<Post> {
    return this.http.put<Post>(this.postApi + '/' + id, post, { headers: this.getHeaders() });
  }

  editComment(id: number, comment: CommentRequest): Observable<Comment> {
    return this.http.put<Comment>(this.commentApi + '/' + id, comment, { headers: this.getHeaders() });
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

  filterReviewablePosts(filter: Filter): Observable<Post[]> {
    return this.getReviewablePosts().pipe(
      map((posts: Post[]) => posts.filter(post => this.isPostMatchingFilter(post, filter)))
    );
  }

  public isPostMatchingFilter(post: Post, filter: Filter): boolean {
    const user = this.authenticationService.getUserById(post.userId);
    // if (user == undefined) return false;

    const matchesContent = this.isIncluded(post.content, filter.content);
    const matchesAuthor = this.isIncluded(user!.fullName, filter.author);
    const matchesDate = filter.date === '' ? true : this.isDatesMatching(new Date(post.createdAt), new Date(filter.date));
    return matchesContent && matchesAuthor && matchesDate;
  }

  public isDatesMatching(postDate: Date, filterDate: Date): boolean {
    const matchesDay = postDate.getDate() === filterDate.getDate();
    const matchesMonth = postDate.getMonth() === filterDate.getMonth();
    const matchesYear = postDate.getFullYear() === filterDate.getFullYear();

    return matchesDay && matchesMonth && matchesYear;
  }

  public isIncluded(item: string, filter: string): boolean {
    return item.toLowerCase().includes(filter.toLowerCase());
  }

  public transformDate(date: string): string {
    const dateDate = new Date(date);
    return dateDate.toLocaleDateString('en-US', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric', hour: 'numeric', minute: 'numeric' });
  }

  public transformDateShort(date: string): string {
    const dateDate = new Date(date);
    const day = String(dateDate.getDate()).padStart(2, '0');
    const month = String(dateDate.getMonth() + 1).padStart(2, '0'); // Months are zero-based
    const year = dateDate.getFullYear();
    const hours = String(dateDate.getHours()).padStart(2, '0');
    const minutes = String(dateDate.getMinutes()).padStart(2, '0');

    return `${day}/${month}/${year} ${hours}:${minutes}`;
  }

  public toPascalCasing(word: string): string {
    return word.charAt(0).toUpperCase() + word.slice(1).toLowerCase();
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
