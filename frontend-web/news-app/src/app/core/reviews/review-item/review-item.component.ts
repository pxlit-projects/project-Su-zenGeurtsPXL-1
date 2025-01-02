import {Component, inject, Input, OnInit} from "@angular/core";
import {Review} from "../../../shared/models/review.model";
import {AuthenticationService} from "../../../shared/services/authentication.service";
import {PostService} from "../../../shared/services/post.service";

@Component({
  selector: 'app-review-item',
  standalone: true,
  imports: [],
  templateUrl: './review-item.component.html',
  styleUrl: './review-item.component.css'
})

export class ReviewItemComponent implements OnInit {
  @Input() review!: Review;
  @Input() last!: boolean;
  authenticationService: AuthenticationService = inject(AuthenticationService);
  postService: PostService = inject(PostService);
  ngOnInit(): void {
  }

  protected readonly localStorage = localStorage;
}
