import {MapInterface} from './map.interface';
import {Observable} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {INDEX_URL} from '../index-request-url';
import {Injectable} from '@angular/core';

@Injectable()
export class MapService implements MapInterface {
  constructor(private $http: HttpClient) {
  }

  getALLFacilityList(): Observable<Object> {
    return this.$http.get(`${INDEX_URL.GET_FACILITY_LIST_ALL}`);
  }

  getALLFacilityListBase(): Observable<Object> {
    return this.$http.get(`${INDEX_URL.GET_FACILITY_LIST_ALL_BASE}`);
  }

  getALLFacilityListSimple(): Observable<Object> {
    return this.$http.get(`${INDEX_URL.GET_FACILITY_LIST_ALL_SIMPLE}`);
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

  collectFacility(id): Observable<Object> {
    return this.$http.get(`${INDEX_URL.COLLECT_FACILITY}/${id}`);
  }

  unCollectFacility(id): Observable<Object> {
    return this.$http.get(`${INDEX_URL.CANCEL_COLLECT_FACILITY}/${id}`);
  }

  getCollectFacilityListStatistics(): Observable<Object> {
    return this.$http.get(`${INDEX_URL.GET_COLLECT_FACILITY_LIST_STATISTICS}`);
  }

  getCollectFacilityList(body): Observable<Object> {
    return this.$http.post(`${INDEX_URL.GET_COLLECT_FACILITY_LIST}`, body);
  }

  getAllCollectFacilityList(): Observable<Object> {
    return this.$http.get(`${INDEX_URL.GET_COLLECT_FACILITY_LIST_ALL}`);
  }

  getALLAreaListForCurrentUser(): Observable<Object> {
    return this.$http.get(`${INDEX_URL.GET_AREA_LIST_FOR_CURRENT_USER}`);
  }
}
