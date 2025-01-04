import {Component, inject, Input} from "@angular/core";
import {Comment} from "../../../shared/models/comment.model";
import {AuthenticationService} from "../../../shared/services/authentication.service";
import {PostService} from "../../../shared/services/post.service";
import {NgClass} from "@angular/common";
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {CommentRequest} from "../../../shared/models/comment-request.model";
import {Router} from "@angular/router";

@Component({
  selector: 'app-comment-item',
  standalone: true,
  imports: [
    NgClass,
    FormsModule,
    ReactiveFormsModule
  ],
  templateUrl: './comment-item.component.html',
  styleUrl: './comment-item.component.css'
})

export class CommentItemComponent {
  @Input() comment!: Comment;
  @Input() isLast!: boolean;
  protected readonly localStorage = localStorage;
  authenticationService: AuthenticationService = inject(AuthenticationService);
  postService: PostService = inject(PostService);
  menuIsHidden: boolean = true;
  isInProgress: boolean = false;
  fb: FormBuilder = inject(FormBuilder);
  router: Router = inject(Router);
  commentForm: FormGroup = this.fb.group({
    content: [ '', Validators.required]
  });

  deleteComment(id: number | undefined) {
    if (confirm("Are you sure you want to delete this comment?")) {
        this.postService.deleteComment(id!).subscribe(() => {
          location.reload();
      });
    }
    this.menuIsHidden = true;
  }

  updateComment() {
    this.menuIsHidden = true;
    this.commentForm.patchValue({
      content: this.comment.content
    });
    this.isInProgress = true;
  }

  onSubmit() {
    const editedComment: CommentRequest = {
      ...this.commentForm.value
    };

    this.postService.editComment(this.comment.id!, editedComment).subscribe({
      next: (comment) => {
        this.comment.content = comment.content;
        this.isInProgress = false;
      }
    });
  }

  cancel() {
    this.isInProgress = false;
    this.menuIsHidden = false;
  }

  openMenu() {
    if (this.comment.userId.toString() == localStorage.getItem('userId') && !this.isInProgress) this.menuIsHidden = !this.menuIsHidden;
  }
}
