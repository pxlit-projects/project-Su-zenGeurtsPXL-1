import {Component, inject, OnDestroy, OnInit} from '@angular/core';
import {AsyncPipe, NgClass, NgIf, NgOptimizedImage} from '@angular/common';
import {MatDividerModule} from "@angular/material/divider";
import { Post } from '../../../shared/models/posts/post.model';
import {interval, Observable, Subscription} from 'rxjs';
import {ActivatedRoute, Router, RouterLink, RouterLinkActive, UrlSegment} from '@angular/router';
import {PostService} from "../../../shared/services/post/post.service";
import {AuthenticationService} from "../../../shared/services/authentication/authentication.service";
import {ReviewRequest} from "../../../shared/models/reviews/reviewRequest.model";
import {ReviewListComponent} from "../../reviews/review-list/review-list.component";
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {CommentListComponent} from "../../comments/comment-list/comment-list.component";
import {CommentRequest} from "../../../shared/models/comments/comment-request.model";
import {ReviewService} from "../../../shared/services/review/review.service";
import {CommentService} from "../../../shared/services/comment/comment.service";
import {HelperService} from "../../../shared/services/helper/helper.service";

@Component({
  selector: 'app-post-detail',
  standalone: true,
  imports: [NgIf, NgClass, AsyncPipe, RouterLinkActive, RouterLink, NgOptimizedImage, MatDividerModule, ReviewListComponent, CommentListComponent, FormsModule, ReactiveFormsModule],
  templateUrl: './post-detail.component.html',
  styleUrl: './post-detail.component.css'
})
export class PostDetailComponent implements OnInit, OnDestroy {
  postService: PostService = inject(PostService);
  commentService: CommentService = inject(CommentService);
  reviewService: ReviewService = inject(ReviewService);
  authenticationService: AuthenticationService = inject(AuthenticationService);
  helperService: HelperService = inject(HelperService);

  router: Router = inject(Router);
  route: ActivatedRoute = inject(ActivatedRoute);
  url: UrlSegment[] = this.route.snapshot.url;
  id: number = this.route.snapshot.params['id'];

  post$!: Post | undefined;
  userId$: number | null = Number(localStorage.getItem('userId'));
  isMine: boolean = this.url[0].path === 'myPost';
  isToReview: boolean = this.url[0].path === 'review';

  fb: FormBuilder = inject(FormBuilder);
  commentForm: FormGroup = this.fb.group({
    content: [ '', [Validators.required, this.helperService.noWhitespaceValidator]]
  });

  fetchSubscription: Subscription | undefined;

  ngOnInit(): void {
   this.fetchPost();

    this.fetchSubscription = interval(1000).subscribe(() => this.fetchPost());
  }

  ngOnDestroy() {
    this.fetchSubscription?.unsubscribe();
  }

  fetchPost() {
    this.id = this.route.snapshot.params['id'];
    let data = this.postService.getPost(this.id);
    data.subscribe({
      next: (post) => {
        if (post.state !== 'DRAFTED' && post.state !== 'PUBLISHED') {
          data = this.postService.getPostWithReviews(this.id);
        }

        if (post.state === 'PUBLISHED') {
          data = this.postService.getPostWithComments(this.id);
        }

        this.handleFetch(data);
      },
      error: () => {
        this.router.navigate(['/pageNotFound']);
      }
    });
  }

  handleFetch(data: Observable<Post>): void {
    data.subscribe(post => { this.post$ = post; });
  }

  submitPost() {
      this.postService.submitPost(this.id).subscribe(() => {
        this.router.navigate(['/myPost']);
      });
  }

  publishPost() {
    this.postService.publishPost(this.id).subscribe(() => {
      this.router.navigate(['/myPost']);
    });
  }

  reviewPost(type: string) {
    const review: ReviewRequest = {
      postId: this.id,
      ...this.commentForm.value
    };
      this.reviewService.reviewPost(type, review).subscribe(() => {
        this.commentForm.reset();
        this.fetchPost();
      });
  }

  addComment() {
    const comment: CommentRequest = {
      postId: this.id,
      ...this.commentForm.value
    };

    this.commentService.addComment(comment).subscribe(() => {
      this.commentForm.reset();
      this.fetchPost();
    });
  }

  login(){
    this.router.navigate(['/login']);
  }

  handleDelete() {
    this.handleFetch(this.postService.getPostWithComments(this.id));
  }
}
