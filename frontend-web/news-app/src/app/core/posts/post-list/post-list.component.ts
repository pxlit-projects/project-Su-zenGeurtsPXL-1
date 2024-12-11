import {Component, inject, OnInit} from '@angular/core';
import {Observable} from "rxjs";
import {AsyncPipe} from "@angular/common";
import {ActivatedRoute, UrlSegment} from "@angular/router";

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
  route: ActivatedRoute = inject(ActivatedRoute);
  url: UrlSegment[] = this.route.snapshot.url;
  mine: boolean = this.url.length == 2;
  posts$!: Observable<Post[]>;
  postService: PostService = inject(PostService);

  ngOnInit(): void {
    this.fetchPosts();
  }

  handleFilter(filter: Filter) {
    this.posts$ = this.postService.filterPosts(filter, this.mine);
  }

  fetchPosts(): void {
      this.posts$ = this.mine ? this.postService.getPostsByUserId(localStorage.getItem("userId")) : this.postService.getPublishedPosts();
  }
}
