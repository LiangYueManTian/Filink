import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {FacilityInterface} from './facility.interface';
import {
  ADD_DEVICE,
  DELETE_DEVICE_BY_IDS,
  DEVICE_CAN_CHANGE_DETAIL,
  DEVICE_LIST_BY_PAGE,
  DEVICE_LOG_LIST_BY_PAGE, DEVICE_LOG_TIME,
  EXPORT_DEVICE_LIST,
  EXPORT_LOG_LIST,
  FIND_DEVICE_BY_ID,
  GET_DETAIL_CODE,
  GET_DEVICE_STRATEGY,
  GET_PRAMS_CONFIG, PIC_RELATION_INFO,
  QUERY_DEVICE_NAME_IS_EXIST,
  QUERY_HEARTBEAT_TIME,
  SET_DEVICE_STRATEGY,
  UPDATE_DEVICE_BY_ID
} from '../facility-request-url';
import {Observable} from 'rxjs';

/**
 * Created by xiaoconghu on 2019/1/14.
 */
@Injectable()
export class FacilityService implements FacilityInterface {
  constructor(private $http: HttpClient) {
  }

  deviceListByPage(body): Observable<Object> {
    return this.$http.post(`${DEVICE_LIST_BY_PAGE}`, body);
  }

  addDevice(body): Observable<Object> {
    return this.$http.post(`${ADD_DEVICE}`, body);

  }

  deleteDeviceDyIds(body): Observable<Object> {
    return this.$http.post(`${DELETE_DEVICE_BY_IDS}`, body);
  }

  updateDeviceById(body): Observable<Object> {
    return this.$http.put(`${UPDATE_DEVICE_BY_ID}`, body);

  }

  queryDeviceById(body): Observable<Object> {
    return this.$http.get(`${FIND_DEVICE_BY_ID}/${body}`);

  }

  getDeviceStrategy(body): Observable<Object> {
    return this.$http.post(`${GET_DEVICE_STRATEGY}`, body);

  }

  setDeviceStrategy(body): Observable<Object> {
    return this.$http.post(`${SET_DEVICE_STRATEGY}`, body);

  }

  queryDeviceNameIsExist(body): Observable<Object> {
    return this.$http.post(`${QUERY_DEVICE_NAME_IS_EXIST}`, body);

  }

  queryDeviceLogListByPage(body): Observable<Object> {
    return this.$http.post(`${DEVICE_LOG_LIST_BY_PAGE}`, body);
  }

  getDetailCode(body): Observable<Object> {
    return this.$http.post(`${GET_DETAIL_CODE}`, body);
  }

  deviceCanChangeDetail(body): Observable<Object> {
    return this.$http.get(`${DEVICE_CAN_CHANGE_DETAIL}/${body}`);
  }

  getPramsConfig(body): Observable<Object> {
    return this.$http.get(`${GET_PRAMS_CONFIG}/${body}`);
  }

  exportDeviceList(body): Observable<Object> {
    return this.$http.post(`${EXPORT_DEVICE_LIST}`, body);
  }

  exportLogList(body): Observable<Object> {
    return this.$http.post(`${EXPORT_LOG_LIST}`, body);
  }

  queryHeartbeatTime(body): Observable<Object> {
    return this.$http.get(`${QUERY_HEARTBEAT_TIME}/${body}`);
  }

  picRelationInfo(body): Observable<Object> {
    return this.$http.post(`${PIC_RELATION_INFO}`, body);
  }

  deviceLogTime(id): Observable<Object> {
    return this.$http.get(`${DEVICE_LOG_TIME}/${id}`);
  }
}
