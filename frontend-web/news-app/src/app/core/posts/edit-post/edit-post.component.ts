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
  protected readonly localStorage = localStorage;

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
    this.post$.subscribe(post => {
      this.postForm.patchValue({
        content: post.content,
      });
    });

    // document.addEventListener("DOMContentLoaded", () => {
    //   let textArea = document.querySelector("textarea");
    //   // @ts-ignore
    //   textArea.style.height = "";
    //   // @ts-ignore
    //   textArea.style.height = textArea.scrollHeight + "px";
    // });
  }
  cancel() {
    this.router.navigate(['/post/mine']);
  }

  onSubmit() {
    const editedPost: PostRequest = {
      ...this.postForm.value
    };

    this.postService.editPost(this.id, editedPost).subscribe(() => {
      this.postForm.reset();
      this.router.navigate(['/post/mine/' + this.id]);
    });
  }
}