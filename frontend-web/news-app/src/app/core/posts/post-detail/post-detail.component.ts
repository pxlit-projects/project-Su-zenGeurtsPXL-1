import {Component, OnDestroy, inject, OnInit} from '@angular/core';
import {AsyncPipe, NgClass, NgIf, NgOptimizedImage} from '@angular/common';
import { Post } from '../../../shared/models/post.model';
import { Observable, Subscription } from 'rxjs';
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
export class PostDetailComponent implements OnDestroy, OnInit {
  protected readonly localStorage = localStorage;

  route: ActivatedRoute = inject(ActivatedRoute);
  id: number = this.route.snapshot.params['id'];
  postService: PostService = inject(PostService);
  authenticationService: AuthenticationService = inject(AuthenticationService);
  post$: Observable<Post> = this.postService.getPost(this.id);
  sub!: Subscription;
  router: Router = inject(Router);

  ngOnInit(): void {
    this.post$.subscribe({
      error: () => {
        this.router.navigate(['/pageNotFound']);
      }
    });
  }

  submit() {
    this.postService.submitPost(this.id, Number(localStorage.getItem('userId'))).subscribe(() => {
      this.router.navigate(['/myPost']);
    });
  }

  ngOnDestroy(): void {
    if(this.sub){
      this.sub.unsubscribe();
    }
  }
}
