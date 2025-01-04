import {Component, inject, Input, OnInit} from "@angular/core";
import {Comment} from "../../../shared/models/comment.model";
import {AuthenticationService} from "../../../shared/services/authentication.service";
import {CommentItemComponent} from "../comment-item/comment-item.component";

@Component({
  selector: 'app-comment-list',
  standalone: true,
  imports: [
    CommentItemComponent
  ],
  templateUrl: './comment-list.component.html',
  styleUrl: './comment-list.component.css'
})

export class CommentListComponent implements OnInit {
  @Input() comments!: Comment[];
  authenticationService: AuthenticationService = inject(AuthenticationService);

  ngOnInit(): void {
    this.comments = this.comments.sort((a, b) => new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime());
  }
}
