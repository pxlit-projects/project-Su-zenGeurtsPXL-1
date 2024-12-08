import {Component, inject, OnInit} from '@angular/core';
import {Observable} from "rxjs";
import {AsyncPipe} from "@angular/common";

import {PostItemComponent} from "../post-item/post-item.component";
import {Post} from "../../../shared/models/post.model";
import {PostService} from "../../../shared/services/post.service";

@Component({
  selector: 'app-my-post-list',
  standalone: true,
  imports: [PostItemComponent, AsyncPipe],
  templateUrl: './my-post-list.component.html',
  styleUrl: './my-post-list.component.css'
})

export class MyPostListComponent implements OnInit {
  posts$!: Observable<Post[]>;
  postService: PostService = inject(PostService);

  ngOnInit(): void {
    this.fetchPosts();
  }

  fetchPosts(): void {
    this.posts$ = this.postService.getPosts();
  }
}
