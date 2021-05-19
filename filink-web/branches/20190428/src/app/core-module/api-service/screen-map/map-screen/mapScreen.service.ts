import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {MapScreenInterface} from './mapScreen.interface';
import {Observable} from 'rxjs';
import {GET_ALL_CITYINFO, GET_ALL_PROVINCE, GET_AREA} from '../screen-map-request-url';


@Injectable()
export class MapScreenService implements MapScreenInterface {
  constructor(private $http: HttpClient) {
  }

  queryGetArea(city): Observable<Object> {
    return this.$http.post(`${GET_AREA}`, {areaName: city});
  }

  getAllProvince(): Observable<Object> {
    return this.$http.get(`${GET_ALL_PROVINCE}`);
  }

  getAllCityInfo(): Observable<Object> {
    return this.$http.get(`${GET_ALL_CITYINFO}`);
  }
}
