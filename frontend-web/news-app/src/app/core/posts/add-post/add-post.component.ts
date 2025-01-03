import {Component, inject, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {AsyncPipe, NgClass} from "@angular/common";
import {Router} from "@angular/router";
import {Observable} from "rxjs";

import {PostItemComponent} from "../post-item/post-item.component";
import {PostRequest} from "../../../shared/models/post-request.model";
import {PostService} from "../../../shared/services/post.service";


@Component({
  selector: 'app-add-post',
  standalone: true,
  imports: [ReactiveFormsModule, NgClass, AsyncPipe, PostItemComponent],
  templateUrl: './add-post.component.html',
  styleUrl: './add-post.component.css'
})
export class AddPostComponent implements OnInit{
  categories$!: Observable<string[]>;
  postService: PostService = inject(PostService);
  fb: FormBuilder = inject(FormBuilder);
  router: Router = inject(Router);

  postForm: FormGroup = this.fb.group({
    title: ['', [Validators.required, Validators.maxLength(130)]],
    content: ['', Validators.required],
    category: ['', Validators.required]
  });

  ngOnInit(): void {
    this.fetchCategories();
  }

  fetchCategories(): void {
    this.categories$ = this.postService.getCategories();
  }

  cancel() {
    this.router.navigate(['/myPost']);
  }

  onSubmit() {
    const newPost: PostRequest = {
      ...this.postForm.value
    };

    this.postService.addPost(newPost).subscribe(() => {
      this.postForm.reset();
      this.router.navigate(['/myPost']);
    });
  }
}
