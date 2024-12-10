import {Component, inject, OnInit} from '@angular/core';
import {Observable} from "rxjs";
import {AsyncPipe} from "@angular/common";

import {PostItemComponent} from "../post-item/post-item.component";
import {Post} from "../../../shared/models/post.model";
import {PostService} from "../../../shared/services/post.service";

import {FilterComponent} from "../filter/filter.component";
import {Filter} from "../../../shared/models/filter.model";

@Component({
  selector: 'app-post-list',
  standalone: true,
  imports: [PostItemComponent, FilterComponent, AsyncPipe],
  templateUrl: './post-list.component.html',
  styleUrl: './post-list.component.css'
})

export class PostListComponent implements OnInit {
  filteredPosts$!: Observable<Post[]>;
  postService: PostService = inject(PostService);

  ngOnInit(): void {
    this.fetchPosts();
  }

  handleFilter(filter: Filter) {
    this.filteredPosts$ = this.postService.filterPosts(filter);
  }

  fetchPosts(): void {
    this.filteredPosts$ = this.postService.getPublishedPosts();
  }
}
