import { Component, Input } from '@angular/core';
import {Post} from "../../../shared/models/post.model";
import {NgClass} from "@angular/common";

@Component({
  selector: 'app-post-item',
  standalone: true,
  imports: [
    NgClass
  ],
  templateUrl: './post-item.component.html',
  styleUrl: './post-item.component.css'
})
export class PostItemComponent {
  @Input() post!: Post;

  openPost(): void {
    console.log(this.post);
  }
}
