import {Component, inject, OnInit} from '@angular/core';
import {PostItemComponent} from "../post-item/post-item.component";
import {Post} from "../../../shared/models/post.model";
import {AddPostComponent} from "../add-post/add-post.component";
import {PostService} from "../../../shared/services/post.service";
import {Observable} from "rxjs";
import {AsyncPipe} from "@angular/common";

@Component({
  selector: 'app-post-list',
  standalone: true,
  imports: [PostItemComponent, AddPostComponent, AsyncPipe],
  templateUrl: './post-list.component.html',
  styleUrl: './post-list.component.css'
})

export class PostListComponent implements OnInit {
  posts$!: Observable<Post[]>;

  postService: PostService = inject(PostService);

  ngOnInit(): void {
    this.fetchPosts();
  }

  fetchPosts(): void {
    this.posts$ = this.postService.getPosts();
  }

  processAdd(newPost: Post){
  //   this.postService.addPost(newPost);
  }
}
