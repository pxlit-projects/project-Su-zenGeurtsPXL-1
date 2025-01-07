import {Component, inject, OnInit} from '@angular/core';
import {interval, map, Observable} from "rxjs";
import {AsyncPipe} from "@angular/common";
import {ActivatedRoute, UrlSegment} from "@angular/router";

import {PostItemComponent} from "../post-item/post-item.component";
import {Post} from "../../../shared/models/posts/post.model";
import {PostService} from "../../../shared/services/post/post.service";

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
  posts$!: Post[] | undefined;
  postService: PostService = inject(PostService);
  filter: Filter | undefined;
  title: string = '';

  ngOnInit(): void {
  if (this.isMine) this.title = 'My posts';
  else if (this.isToReview) this.title = 'Reviews';
  else this.title = 'Posts';

  this.fetchPosts();
  this.fetch(this.postService.getPosts(this.isMine, this.isToReview));

  this.fetchPosts();
  interval(1000).subscribe(() => this.fetchPosts());
  }

  handleFilter(filter: Filter) {
    this.fetch(this.postService.filterPosts(filter, this.isMine, this.isToReview))
    this.filter = filter;
  }

  fetchPosts(): void {
    const emptyFilter = { content: '', author: '', date: '' };
    if (this.filter == undefined || this.filter == emptyFilter) this.fetch(this.postService.getPosts(this.isMine, this.isToReview));
  }

  fetch(data: Observable<Post[]>): void {
    data
      .pipe(map(
        (posts: Post[]) =>
          posts.sort((a, b) =>
            new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()
          )
        )
      )
      .subscribe(posts => {
        this.posts$ = posts;
      });
  }
}
