import {inject, Injectable} from "@angular/core";
import {environment} from "../../../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {AuthenticationService} from "../authentication/authentication.service";
import {ReviewRequest} from "../../models/reviews/reviewRequest.model";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})

export class ReviewService {
  api: string = environment.apiUrl + '/review/api/review';
  http: HttpClient = inject(HttpClient);
  authenticationService: AuthenticationService = inject(AuthenticationService);

  reviewPost(type: string, review: ReviewRequest): Observable<void> {
    return this.http.post<void>(this.api + '/' + type, review, { headers:this.authenticationService.getHeaders() });
  }
}
