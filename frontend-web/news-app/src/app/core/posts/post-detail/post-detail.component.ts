import {Component, inject, OnInit} from '@angular/core';
import {AsyncPipe, NgClass, NgIf, NgOptimizedImage} from '@angular/common';
import {MatDividerModule} from "@angular/material/divider";
import { Post } from '../../../shared/models/post.model';
import { Observable} from 'rxjs';
import {ActivatedRoute, Router, RouterLink, RouterLinkActive, UrlSegment} from '@angular/router';
import {PostService} from "../../../shared/services/post.service";
import {AuthenticationService} from "../../../shared/services/authentication.service";
import {ReviewRequest} from "../../../shared/models/reviewRequest.model";
import {ReviewListComponent} from "../../reviews/review-list/review-list.component";
import {PostListComponent} from "../post-list/post-list.component";
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";

@Component({
  selector: 'app-post-detail',
  standalone: true,
  imports: [NgIf, NgClass, AsyncPipe, RouterLinkActive, RouterLink, NgOptimizedImage, MatDividerModule, ReviewListComponent, PostListComponent, FormsModule, ReactiveFormsModule],
  templateUrl: './post-detail.component.html',
  styleUrl: './post-detail.component.css'
})
export class PostDetailComponent implements OnInit {
  router: Router = inject(Router);
  route: ActivatedRoute = inject(ActivatedRoute);
  url: UrlSegment[] = this.route.snapshot.url;
  isMine: boolean = this.url[0].path === 'myPost';
  isToReview: boolean = this.url[0].path === 'review';
  id: number = this.route.snapshot.params['id'];
  postService: PostService = inject(PostService);
  authenticationService: AuthenticationService = inject(AuthenticationService);
  post$: Observable<Post> = this.postService.getPost(this.id);
  userId$: number | null = Number(localStorage.getItem('userId'));
  fb: FormBuilder = inject(FormBuilder);
  commentForm: FormGroup = this.fb.group({
    content: [ '', Validators.required]
  });

  ngOnInit(): void {
    this.post$.subscribe({
      next: (post) => {
        if (post.state !== 'DRAFTED' && post.state !== 'PUBLISHED') {
          this.post$ = this.postService.getPostWithReviews(this.id);
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
      this.postService.reviewPost(type, review).subscribe(() => {
        this.router.navigate(['/review']);
      });
    }
    else {
      window.alert("Comment cannot be empty")
    }
  }
}
