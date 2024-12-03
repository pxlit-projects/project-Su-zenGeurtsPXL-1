import {Component, EventEmitter, inject, Output} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {Post} from "../../../shared/models/post.model";
import {NgClass} from "@angular/common";
@Component({
  selector: 'app-add-post',
  standalone: true,
  imports: [ReactiveFormsModule, NgClass],
  templateUrl: './add-post.component.html',
  styleUrl: './add-post.component.css'
})
export class AddPostComponent {
  fb: FormBuilder = inject(FormBuilder);
  @Output() addPost = new EventEmitter<Post>();
  postForm: FormGroup = this.fb.group({
    id: [1],
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
    this.postForm.value.imageUrl = this.postForm.value.category + ".png";
    const newPost: Post = {
      ...this.postForm.value
    };
    this.addPost.emit(newPost);
  }
}
