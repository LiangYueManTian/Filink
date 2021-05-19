import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {LoginInterface} from './login.interface';
import {loginRequireUrl} from './http-url-config';
@Injectable()
export class LoginService implements LoginInterface {

  constructor(private $http: HttpClient) { }

  login(params): Observable<Object> {
    return this.$http.post(loginRequireUrl.login, params);
  }
}
