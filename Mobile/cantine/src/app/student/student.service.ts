import {Injectable} from '@angular/core';
import {Login} from "../shared-module/models/login";
import {HttpClient} from "@angular/common/http";
import {AuthObject} from "../shared-module/models/authObject";

@Injectable({
  providedIn: 'root'
})
export class StudentService {
  private USER_AUTHENTICATION_ENDPOINT = "http://localhost:8080/" + 'user/login';

  constructor(private httpclient: HttpClient) {
  }


  signIn(login: Login) {
    return this.httpclient.post <AuthObject>(this.USER_AUTHENTICATION_ENDPOINT, login);
  }
}
