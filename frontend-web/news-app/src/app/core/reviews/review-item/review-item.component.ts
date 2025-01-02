import {Component, inject, Input} from "@angular/core";
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

export class ReviewItemComponent {
  @Input() review!: Review;
  @Input() last!: boolean;
  authenticationService: AuthenticationService = inject(AuthenticationService);
  postService: PostService = inject(PostService);
}
