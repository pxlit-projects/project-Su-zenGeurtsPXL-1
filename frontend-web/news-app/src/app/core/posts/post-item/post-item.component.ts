import {Component, inject, Input} from '@angular/core';
import {NgClass} from "@angular/common";

import {Post} from "../../../shared/models/posts/post.model";
import {RouterLink, RouterLinkActive} from "@angular/router";
import {PostService} from "../../../shared/services/post/post.service";
import {AuthenticationService} from "../../../shared/services/authentication/authentication.service";
import {HelperService} from "../../../shared/services/helper/helper.service";

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
  @Input() isMine!: boolean;
  @Input() isToReview!: boolean;
  postService: PostService = inject(PostService);
  authenticationService: AuthenticationService = inject(AuthenticationService);
  helperService: HelperService = inject(HelperService);
}
