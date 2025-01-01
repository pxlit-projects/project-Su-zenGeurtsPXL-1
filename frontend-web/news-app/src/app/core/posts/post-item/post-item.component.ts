import {Component, inject, Input} from '@angular/core';
import {NgClass} from "@angular/common";

import {Post} from "../../../shared/models/post.model";
import {RouterLink, RouterLinkActive} from "@angular/router";
import {PostService} from "../../../shared/services/post.service";
import {AuthenticationService} from "../../../shared/services/authentication.service";

@Component({
  selector: 'app-post-item',
  standalone: true,
  imports: [
    NgClass,
    RouterLinkActive,
    RouterLink
  ],
  templateUrl: './post-item.component.html',
  styleUrl: './post-item.component.css'
})
export class PostItemComponent {
  @Input() post!: Post;
  @Input() mine!: boolean;
  postService: PostService = inject(PostService);
  authenticationService: AuthenticationService = inject(AuthenticationService);
}
