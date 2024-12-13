import {Component, inject, OnInit} from '@angular/core';
import {AsyncPipe, NgClass, NgIf, NgOptimizedImage} from '@angular/common';
import { Post } from '../../../shared/models/post.model';
import { Observable} from 'rxjs';
import {ActivatedRoute, Router, RouterLink, RouterLinkActive} from '@angular/router';
import {PostService} from "../../../shared/services/post.service";
import {AuthenticationService} from "../../../shared/services/authentication.service";

@Component({
  selector: 'app-post-detail',
  standalone: true,
  imports: [NgIf, NgClass, AsyncPipe, RouterLinkActive, RouterLink, NgOptimizedImage],
  templateUrl: './post-detail.component.html',
  styleUrl: './post-detail.component.css'
})
export class PostDetailComponent implements OnInit {
  protected readonly localStorage = localStorage;

  route: ActivatedRoute = inject(ActivatedRoute);
  id: number = this.route.snapshot.params['id'];
  postService: PostService = inject(PostService);
  authenticationService: AuthenticationService = inject(AuthenticationService);
  post$: Observable<Post> = this.postService.getPost(this.id);
  router: Router = inject(Router);

  ngOnInit(): void {
    this.post$.subscribe({
      error: () => {
        this.router.navigate(['/pageNotFound']);
      }
    });
  }

  submitPost() {
    this.postService.submitPost(this.id, Number(localStorage.getItem('userId'))).subscribe({
      next: () => {
        this.router.navigate(['/myPost']);
      },
      error: (err) => {
        let element = document.getElementById('errorMessage');
        if (element) element.innerText = err.error.message;
      }
    });
  }
}
