import {Component, inject, OnInit} from '@angular/core';
import {PostItemComponent} from "../post-item/post-item.component";
import {Post} from "../../../shared/models/post.model";
import {AddPostComponent} from "../add-post/add-post.component";
import {PostService} from "../../../shared/services/post.service";

@Component({
  selector: 'app-post-list',
  standalone: true,
  imports: [PostItemComponent, AddPostComponent],
  templateUrl: './post-list.component.html',
  styleUrl: './post-list.component.css'
})

export class PostListComponent implements OnInit {
  posts!: Post[];
  postService: PostService = inject(PostService);

  ngOnInit(): void {
    this.postService.getAllPosts();
    // this.fetchPosts();
  }

  fetchPosts(): void {
    this.postService.getPosts().subscribe((data: Post[]) => {
      console.log(data);
      this.posts = data;
    });
  }

  processAdd(newPost: Post){
  //   this.postService.addPost(newPost);
  }
}
