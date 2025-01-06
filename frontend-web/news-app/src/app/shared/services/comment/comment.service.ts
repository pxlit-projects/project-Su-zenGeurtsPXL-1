import {inject, Injectable} from "@angular/core";
import {environment} from "../../../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {AuthenticationService} from "../authentication/authentication.service";
import {Observable} from "rxjs";
import {CommentRequest} from "../../models/comments/comment-request.model";

@Injectable({
  providedIn: 'root'
})

export class CommentService {
  api: string = environment.apiUrl + '/comment/api/comment';
  http: HttpClient = inject(HttpClient);
  authenticationService: AuthenticationService = inject(AuthenticationService);

  addComment(comment: CommentRequest): Observable<void> {
    return this.http.post<void>(this.api, comment, { headers:this.authenticationService.getHeaders() });
  }

  editComment(id: number, content: string): Observable<void> {
    return this.http.put<void>(this.api + '/' + id, content, { headers: this.authenticationService.getHeaders() });
  }

  deleteComment(id: number): Observable<void> {
    return this.http.delete<void>(this.api + '/' + id, { headers:this.authenticationService.getHeaders() });
  }
}
