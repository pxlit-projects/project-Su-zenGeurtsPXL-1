import {inject, Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {User} from "../models/user.model";

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {
  api: string = 'api/users';
  http: HttpClient = inject(HttpClient);

  getUsers(): Observable<User[]> {
    console.log("Fetching users...");
    return this.http.get<User[]>(this.api);
  }
}
