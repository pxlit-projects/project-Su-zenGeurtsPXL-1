import {Component, EventEmitter, inject, Input, Output} from "@angular/core";
import {Comment} from "../../../shared/models/comments/comment.model";
import {AuthenticationService} from "../../../shared/services/authentication/authentication.service";
import {NgClass} from "@angular/common";
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {Router} from "@angular/router";
import {CommentService} from "../../../shared/services/comment/comment.service";
import {HelperService} from "../../../shared/services/helper/helper.service";

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
  authenticationService: AuthenticationService = inject(AuthenticationService);
  commentService: CommentService = inject(CommentService);
  helperService: HelperService = inject(HelperService);

  router: Router = inject(Router);
  fb: FormBuilder = inject(FormBuilder);

  @Input() comment!: Comment;
  @Input() isLast!: boolean;
  @Output() deleted = new EventEmitter<void>();
  menuIsHidden: boolean = true;
  isInProgress: boolean = false;

  commentForm: FormGroup = this.fb.group({
    content: [ '', Validators.required]
  });

  protected readonly localStorage = localStorage;

  deleteComment() {
    if (confirm("Are you sure you want to delete this comment?")) {
      this.commentService.deleteComment(this.comment.id!).subscribe(() => {
        this.deleted.emit();
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
    const content: string = this.commentForm.value.content;

    this.commentService.editComment(this.comment.id!, content).subscribe({
      next: () => {
        this.comment.content = content;
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
