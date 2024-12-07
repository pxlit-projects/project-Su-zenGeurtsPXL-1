import { Component, Input } from '@angular/core';
import {NgClass} from "@angular/common";

import {Post} from "../../../shared/models/post.model";

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

  transformDate(date: string): string {
    const dateDate = new Date(date);
    return dateDate.toLocaleDateString('en-US', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric', hour: 'numeric', minute: 'numeric' });
  }
}
