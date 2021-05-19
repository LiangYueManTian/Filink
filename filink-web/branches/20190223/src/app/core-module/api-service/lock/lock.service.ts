import {LockInterface} from './lock.interface';
import {Observable} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {LOCK_URL} from './lock-request-url';

@Injectable()
export class LockService implements LockInterface {
  constructor(private $http: HttpClient) {
  }

  getLockControlInfo(id): Observable<Object> {
    return this.$http.get(`${LOCK_URL.GET_LOCK_CONTROL_INFO}/${id}`);
  }

  getLockInfo(id): Observable<Object> {
    return this.$http.get(`${LOCK_URL.GET_LOCK_INFO}/${id}`);
  }

  getPramsConfig(body): Observable<Object> {
    return this.$http.get(`${LOCK_URL.GET_PRAMS_CONFIG}/${body}`);
  }

  setControl(body): Observable<Object> {
    return this.$http.post(`${LOCK_URL.SET_CONTROL}`, body);
  }

  openLock(body): Observable<Object> {
    return this.$http.post(`${LOCK_URL.OPEN_LOCK}`, body);
  }
}
