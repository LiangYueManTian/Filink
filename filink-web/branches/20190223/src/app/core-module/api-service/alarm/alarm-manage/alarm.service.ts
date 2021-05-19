import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AlarmInterface } from './alarm.interface';
import { Observable } from 'rxjs';
import {
  QUERY_ALARM_CURRENT_LIST,
  QUERY_ALARM_CURRENT_INFO_BY_ID,
  UPDATE_ALARM_REMARK,
  UPDATE_ALARM_CONFIRM_STATUS,
  UPDATE_ALARM_CLEAN_STATUS,
  QUERY_EVERY_ALARM_COUNT,
  QUERY_ALARM_HISTORY_LIST,
  QUERY_ALARM_FILTRATION,
  QUERY_ALARM_HISTORY_INFO_BY_ID,
  QUERY_ALARM_LEVEL_LIST,
  QUERY_ALARM_CURRENT_SET_LIST,
  UPDATE_ALARM_COLOR_AND_SOUND,
  UPDATE_ALARM_LEVEL,
  INSERR_ALARM_CURRENTSET,
  UPDATE_ALARM_CURRENTSET,
  QUERY_ALARM_LEVEL_SET_BY_ID,
  DELETE_ALARM_CURRENTSET,
  DELETE_ALARM_FILTRATION,
  QUERY_ALARM_LEVEL_BY_ID,
  QUERY_ALARM_DELAY,
  QUERY_ALARM_NAME,
  QUERY_ALARM_LEVEL,
  UPDATE_ALARM_DELAY,
  SELECT_ALARM_ENUM,
  QUERY_ALARM_FILTRATION_OBJ,
  WEBSOCKET,
  QUERY_ALARM_DEVICE_ID
} from '../alarm-request-url';

@Injectable()
export class AlarmService implements AlarmInterface {

  constructor(private $http: HttpClient) { }

  queryCurrentAlarmList(body): Observable<Object> {
    return this.$http.post(`${QUERY_ALARM_CURRENT_LIST}`, body);
  }

  queryCurrentAlarmInfoById(body): Observable<Object> {
    return this.$http.post(`${QUERY_ALARM_CURRENT_INFO_BY_ID}`, body);
  }

  updateAlarmRemark(body): Observable<Object> {
    return this.$http.put(`${UPDATE_ALARM_REMARK}`, body);
  }


  updateAlarmConfirmStatus(body): Observable<Object> {
    return this.$http.post(`${UPDATE_ALARM_CONFIRM_STATUS}`, body);
  }

  updateAlarmCleanStatus(body): Observable<Object> {
    return this.$http.post(`${UPDATE_ALARM_CLEAN_STATUS}`, body);
  }

  queryEveryAlarmCount(): Observable<Object> {
    return this.$http.post(`${QUERY_EVERY_ALARM_COUNT}`, {});
  }

  queryAlarmHistoryList(body): Observable<Object> {
    return this.$http.post(`${QUERY_ALARM_HISTORY_LIST}`, body);
  }

  queryAlarmFiltration(): Observable<Object> {
    return this.$http.post(`${QUERY_ALARM_FILTRATION}`, {});
  }

  queryAlarmObjectList(body): Observable<Object> {
    return this.$http.post(`${QUERY_ALARM_FILTRATION_OBJ}`, body);
  }

  queryAlarmHistoryInfoById(body): Observable<Object> {
    return this.$http.post(`${QUERY_ALARM_HISTORY_INFO_BY_ID}`, body);
  }


  queryAlarmLevelList(body): Observable<Object> {
    return this.$http.post(`${QUERY_ALARM_LEVEL_LIST}`, body);
  }

  queryAlarmCurrentSetList(body): Observable<Object> {
    return this.$http.post(`${QUERY_ALARM_CURRENT_SET_LIST}`, body);
  }


  updateAlarmColorAndSound(body): Observable<Object> {
    return this.$http.put(`${UPDATE_ALARM_COLOR_AND_SOUND}`, body);
  }

  updateAlarmLevel(body): Observable<Object> {
    return this.$http.post(`${UPDATE_ALARM_LEVEL}`, body);
  }

  insertAlarmCurrentSet(body): Observable<Object> {
    return this.$http.post(`${INSERR_ALARM_CURRENTSET}`, body);
  }

  updateAlarmCurrentSet(body): Observable<Object> {
    return this.$http.post(`${UPDATE_ALARM_CURRENTSET}`, body);
  }

  queryAlarmLevelSetById(id: string): Observable<Object> {
    return this.$http.post(`${QUERY_ALARM_LEVEL_SET_BY_ID}` + `${id}`, null);
  }

  deleteAlarmCurrentSet(body): Observable<Object> {
    return this.$http.post(`${DELETE_ALARM_CURRENTSET}`, body);
  }

  deleteAlarmFiltration(body): Observable<Object> {
    return this.$http.post(`${DELETE_ALARM_FILTRATION}`, body);
  }

  queryAlarmLevelById(id: string): Observable<Object> {
    return this.$http.post(`${QUERY_ALARM_LEVEL_BY_ID}` + `${id}`, null);
  }

  queryAlarmDelay(body): Observable<Object> {
    return this.$http.post(`${QUERY_ALARM_DELAY}`, body);
  }

  updateAlarmDelay(body): Observable<Object> {
    return this.$http.post(`${UPDATE_ALARM_DELAY}`, body);
  }

  selectAlarmEnum(body): Observable<Object> {
    return this.$http.post(`${SELECT_ALARM_ENUM}`, body);
  }

  websocket(): Observable<Object> {
    return this.$http.get(`${WEBSOCKET}`);
  }

  queryAlarmName(): Observable<Object> {
    return this.$http.post(`${QUERY_ALARM_NAME}`, {});
  }

  queryAlarmLevel(): Observable<Object> {
    return this.$http.post(`${QUERY_ALARM_LEVEL}`, {});
  }

  queryAlarmDeviceId(id): Observable<Object> {
    return this.$http.post(`${QUERY_ALARM_DEVICE_ID}/${id}`, {});
  }
}
