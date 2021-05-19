import {MapInterface} from './map.interface';
import {Observable} from 'rxjs/index';
import {HttpClient} from '@angular/common/http';
import {INDEX_URL} from '../index-request-url';
import {Injectable} from '@angular/core';
import {Result} from '../../../../shared-module/entity/result';

@Injectable()
export class MapService implements MapInterface {
  constructor(private $http: HttpClient) {
  }

  getALLFacilityList(): Observable<Object> {
    return this.$http.get(`${INDEX_URL.GET_FACILITY_LIST_ALL}`);
  }

  getALLAreaList(): Observable<Object> {
    return this.$http.get(`${INDEX_URL.GET_AREA_LIST_ALL}`);
  }

  getALLFacilityConfig(): Observable<Object> {
    return this.$http.get(`${INDEX_URL.GET_FACILITY_CONFIG_ALL}`);
  }

  modifyFacilityTypeConfig(body): Observable<Object> {
    return this.$http.post(`${INDEX_URL.UPDATE_FACILITY_TYPE_STATUS}`, body);
  }

  modifyFacilityIconSize(body): Observable<Object> {
    return this.$http.post(`${INDEX_URL.UPDATE_FACILITY_ICON_SIZE}`, body);
  }
}
