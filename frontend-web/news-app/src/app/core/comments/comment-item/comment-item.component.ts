import {Component, inject, Input} from "@angular/core";
import {Comment} from "../../../shared/models/comment.model";
import {AuthenticationService} from "../../../shared/services/authentication.service";
import {PostService} from "../../../shared/services/post.service";
import {NgClass} from "@angular/common";

@Component({
  selector: 'app-comment-item',
  standalone: true,
  imports: [
    NgClass
  ],
  templateUrl: './comment-item.component.html',
  styleUrl: './comment-item.component.css'
})

export class CommentItemComponent {
  @Input() comment!: Comment;
  @Input() isLast!: boolean;
  authenticationService: AuthenticationService = inject(AuthenticationService);
  postService: PostService = inject(PostService);
  menuIsHidden: boolean = true;

  deleteComment(id: number | undefined) {
    if (window.confirm("Are you sure you want to delete this comment?")) {
        this.postService.deleteComment(id).subscribe(() => {
          window.location.reload();
      });
    }
    this.menuIsHidden = true;
  }

  updateComment() {
    alert("Update");
  }

  openMenu() {
    if (this.comment.userId.toString() == localStorage.getItem('userId')) this.menuIsHidden = !this.menuIsHidden;
  }

  protected readonly localStorage = localStorage;
}
