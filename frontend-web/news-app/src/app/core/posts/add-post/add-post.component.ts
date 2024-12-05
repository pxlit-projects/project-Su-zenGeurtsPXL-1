import {Component, inject} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {Post} from "../../../shared/models/post.model";
import {NgClass} from "@angular/common";
import {Router} from "@angular/router";
import {PostService} from "../../../shared/services/post.service";

@Component({
  selector: 'app-add-post',
  standalone: true,
  imports: [ReactiveFormsModule, NgClass],
  templateUrl: './add-post.component.html',
  styleUrl: './add-post.component.css'
})
export class AddPostComponent {
  postService: PostService = inject(PostService);
  fb: FormBuilder = inject(FormBuilder);
  router: Router = inject(Router);

  postForm: FormGroup = this.fb.group({
    title: ['', Validators.required],
    content: ['', Validators.required],
    userId: [123],
    category: ['', Validators.required],
    createdAt: [new Date(Date.now())],
    state: ['DRAFTED'],
    imageUrl: ["ACADEMIC.png" ],
  });

  onSubmit() {
    console.log('Adding post...');

    const newPost: Post = {
      ...this.postForm.value
    };

    // this.postService.addPost(newPost).subscribe(post => {
    //   this.postForm.reset();
    //   this.router.navigate(['/posts']);
    // });
  }
}
