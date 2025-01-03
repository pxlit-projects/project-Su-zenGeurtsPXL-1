import {Component, inject, OnInit} from '@angular/core';
import {Observable} from "rxjs";
import {AsyncPipe} from "@angular/common";
import {ActivatedRoute, UrlSegment} from "@angular/router";

import {PostItemComponent} from "../post-item/post-item.component";
import {Post} from "../../../shared/models/post.model";
import {PostService} from "../../../shared/services/post.service";

import {FilterComponent} from "../../filter/filter.component";
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
  isMine: boolean = this.url[0].path === 'myPost';
  isToReview: boolean = this.url[0].path === 'review';
  posts$!: Observable<Post[]>;
  postService: PostService = inject(PostService);

  ngOnInit(): void {
    this.fetchPosts();
  }

  handleFilter(filter: Filter) {
    if (this.isMine) {
      this.posts$ = this.postService.filterMyPosts(filter);
    } else if (this.isToReview) {
      this.posts$ = this.postService.filterReviewablePosts(filter);
    } else {
      this.posts$ = this.postService.filterPublishedPosts(filter);
    }

    this.posts$ = this.postService.orderToMostRecent(this.posts$);
  }

  fetchPosts(): void {
    if (this.isMine) {
      this.posts$ = this.postService.getMyPosts();
    } else if (this.isToReview) {
      this.posts$ = this.postService.getReviewablePosts();
    } else {
      this.posts$ = this.postService.getPublishedPosts();
    }

    this.posts$ = this.postService.orderToMostRecent(this.posts$);
  }
}
