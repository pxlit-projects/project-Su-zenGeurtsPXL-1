import {Component, inject, OnInit} from '@angular/core';
import {AsyncPipe, NgClass, NgIf, NgOptimizedImage} from '@angular/common';
import {MatDividerModule} from "@angular/material/divider";
import { Post } from '../../../shared/models/posts/post.model';
import { Observable} from 'rxjs';
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
export class PostDetailComponent implements OnInit {
  postService: PostService = inject(PostService);
  commentService: CommentService = inject(CommentService);
  reviewService: ReviewService = inject(ReviewService);
  authenticationService: AuthenticationService = inject(AuthenticationService);
  helperService: HelperService = inject(HelperService);

  router: Router = inject(Router);
  route: ActivatedRoute = inject(ActivatedRoute);
  url: UrlSegment[] = this.route.snapshot.url;
  id: number = this.route.snapshot.params['id'];

  post$: Observable<Post> = this.postService.getPost(this.id);
  userId$: number | null = Number(localStorage.getItem('userId'));
  isMine: boolean = this.url[0].path === 'myPost';
  isToReview: boolean = this.url[0].path === 'review';

  fb: FormBuilder = inject(FormBuilder);
  commentForm: FormGroup = this.fb.group({
    content: [ '', Validators.required]
  });

  ngOnInit(): void {
   this.fetchPost();
  }

  fetchPost() {
    this.post$.subscribe({
      next: (post) => {
        if (post.state !== 'DRAFTED' && post.state !== 'PUBLISHED') {
          this.post$ = this.postService.getPostWithReviews(this.id);
        }

        if (post.state === 'PUBLISHED') {
          this.post$ = this.postService.getPostWithComments(this.id);
        }
      },
      error: () => {
        this.router.navigate(['/pageNotFound']);
      }
    });
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

    if (this.commentForm.valid) {
      this.reviewService.reviewPost(type, review).subscribe(() => {
        let element = document.getElementById('errorMessage');
        if (element) element.innerText = "";
        this.commentForm.reset();
        this.fetchPost();
      });
    }
    else {
      console.log("empty review")
      let element = document.getElementById('errorMessage');
      if (element) element.innerText = "Comment cannot be empty";
    }
  }

  addComment() {
    const comment: CommentRequest = {
      postId: this.id,
      ...this.commentForm.value
    };

    if (this.commentForm.valid) {
      this.commentService.addComment(comment).subscribe(() => {
        let element = document.getElementById('errorMessage');
        if (element) element.innerText = "";
        this.commentForm.reset();
        this.fetchPost();
      });
    } else {
      let element = document.getElementById('errorMessage');
      if (element) element.innerText = "Comment cannot be empty";
    }
  }

  login(){
    this.router.navigate(['/login']);
  }

  handleDelete() {
    this.post$ = this.postService.getPostWithComments(this.id);
  }
}
