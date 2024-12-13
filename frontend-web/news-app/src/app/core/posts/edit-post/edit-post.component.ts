import {Component, inject, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {AsyncPipe, NgClass} from "@angular/common";
import {ActivatedRoute, Router} from "@angular/router";
import {Observable} from "rxjs";

import {PostItemComponent} from "../post-item/post-item.component";
import {PostRequest} from "../../../shared/models/post-request.model";
import {PostService} from "../../../shared/services/post.service";
import {Post} from "../../../shared/models/post.model";


@Component({
  selector: 'app-edit-post',
  standalone: true,
  imports: [ReactiveFormsModule, NgClass, AsyncPipe, PostItemComponent],
  templateUrl: './edit-post.component.html',
  styleUrl: './edit-post.component.css'
})
export class EditPostComponent implements OnInit{
  route: ActivatedRoute = inject(ActivatedRoute);
  id: number = this.route.snapshot.params['id'];
  postService: PostService = inject(PostService);
  post$: Observable<Post> = this.postService.getPost(this.id);

  fb: FormBuilder = inject(FormBuilder);
  router: Router = inject(Router);

  postForm: FormGroup = this.fb.group({
    content: [ '', Validators.required],
    userId: [localStorage.getItem("userId")]
  });

  ngOnInit(): void {
    this.post$.subscribe({
      next: (post) => {
        this.postForm.patchValue({
          content: post.content,
        });
      },
      error: () => {
        this.router.navigate(['/pageNotFound']);
      }
    });
  }
  cancel() {
    this.router.navigate(['/myPost']);
  }

  onSubmit() {
    const editedPost: PostRequest = {
      ...this.postForm.value
    };

    this.postService.editPost(this.id, editedPost).subscribe({
      next: () => {
        this.postForm.reset();
        this.router.navigate(['/myPost/' + this.id]);
      },
      error: (err) => {
        let element = document.getElementById('errorMessage');
        if (element) element.innerText = err.error.message;
      }
    });
  }
}
