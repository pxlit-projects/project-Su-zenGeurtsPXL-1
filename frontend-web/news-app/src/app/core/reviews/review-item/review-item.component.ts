import {Component, inject, Input} from "@angular/core";
import {Review} from "../../../shared/models/reviews/review.model";
import {AuthenticationService} from "../../../shared/services/authentication/authentication.service";
import {HelperService} from "../../../shared/services/helper/helper.service";

@Component({
  selector: 'app-review-item',
  standalone: true,
  imports: [],
  templateUrl: './review-item.component.html',
  styleUrl: './review-item.component.css'
})

export class ReviewItemComponent {
  @Input() review!: Review;
  @Input() isLast!: boolean;
  authenticationService: AuthenticationService = inject(AuthenticationService);
  helperService: HelperService = inject(HelperService);
}
