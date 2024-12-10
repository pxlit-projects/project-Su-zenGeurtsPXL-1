import { Component, OnDestroy, inject } from '@angular/core';
import {AsyncPipe, NgClass, NgIf, NgOptimizedImage} from '@angular/common';
import { Post } from '../../../shared/models/post.model';
import { Observable, Subscription } from 'rxjs';
import {ActivatedRoute, Router, RouterLink, RouterLinkActive} from '@angular/router';
import {PostService} from "../../../shared/services/post.service";
import {AuthenticationService} from "../../../shared/services/authentication.service";

@Component({
  selector: 'app-my-post-detail',
  standalone: true,
  imports: [NgIf, NgClass, AsyncPipe, RouterLinkActive, RouterLink, NgOptimizedImage],
  templateUrl: './my-post-detail.component.html',
  styleUrl: './my-post-detail.component.css'
})
export class MyPostDetailComponent implements OnDestroy {
  protected readonly localStorage = localStorage;

  route: ActivatedRoute = inject(ActivatedRoute);
  id: number = this.route.snapshot.params['id'];
  postService: PostService = inject(PostService);
  authenticationService: AuthenticationService = inject(AuthenticationService);
  post$: Observable<Post> = this.postService.getPost(this.id);
  sub!: Subscription;
  router: Router = inject(Router);

  submit() {
    this.postService.submitPost(this.id, Number(localStorage.getItem('userId'))).subscribe(() => {
      this.router.navigate(['/post/mine']);
    });
  }

  ngOnDestroy(): void {
    if(this.sub){
      this.sub.unsubscribe();
    }
  }
}
