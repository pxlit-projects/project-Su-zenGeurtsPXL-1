import {Component, inject, Input} from "@angular/core";
import {Comment} from "../../../shared/models/comment.model";
import {AuthenticationService} from "../../../shared/services/authentication.service";
import {PostService} from "../../../shared/services/post.service";

@Component({
  selector: 'app-comment-item',
  standalone: true,
  imports: [],
  templateUrl: './comment-item.component.html',
  styleUrl: './comment-item.component.css'
})

export class CommentItemComponent {
  @Input() comment!: Comment;
  @Input() isLast!: boolean;
  authenticationService: AuthenticationService = inject(AuthenticationService);
  postService: PostService = inject(PostService);
}
