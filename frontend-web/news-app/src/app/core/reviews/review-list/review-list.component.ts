import {Component, inject, Input, OnInit} from "@angular/core";
import {Review} from "../../../shared/models/reviews/review.model";
import {AuthenticationService} from "../../../shared/services/authentication/authentication.service";
import {ReviewItemComponent} from "../review-item/review-item.component";
import {HelperService} from "../../../shared/services/helper/helper.service";

@Component({
  selector: 'app-review-list',
  standalone: true,
  imports: [
    ReviewItemComponent
  ],
  templateUrl: './review-list.component.html',
  styleUrl: './review-list.component.css'
})

export class ReviewListComponent implements OnInit {
  @Input() reviews!: Review[];
  authenticationService: AuthenticationService = inject(AuthenticationService);
  helperService: HelperService = inject(HelperService);

  ngOnInit(): void {
    this.reviews = this.reviews.sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime());
    }
}
