import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {AlarmInterface} from './alarm.interface';
import {Observable} from 'rxjs';
import {
  QUERY_ALARM_CURRENT_LIST,
  QUERY_ALARM_CURRENT_INFO_BY_ID,
  UPDATE_ALARM_REMARK,
  UPDATE_ALARM_CONFIRM_STATUS,
  UPDATE_ALARM_CLEAN_STATUS,
  QUERY_EVERY_ALARM_COUNT,
  QUERY_ALARM_HISTORY_LIST,
  QUERY_ALARM_HISTORY_INFO_BY_ID,
  QUERY_ALARM_LEVEL_LIST,
  QUERY_ALARM_CURRENT_SET_LIST,
  UPDATE_ALARM_COLOR_AND_SOUND,
  UPDATE_ALARM_LEVEL,
  INSERR_ALARM_CURRENTSET,
  UPDATE_ALARM_CURRENTSET,
  QUERY_ALARM_LEVEL_SET_BY_ID,
  DELETE_ALARM_CURRENTSET,
  QUERY_ALARM_LEVEL_BY_ID,
  QUERY_ALARM_DELAY,
  QUERY_ALARM_NAME,
  QUERY_ALARM_LEVEL,
  UPDATE_ALARM_DELAY,
  SELECT_ALARM_ENUM,
  QUERY_ALARM_FILTRATION,
  DELETE_ALARM_FILTRATION,
  QUERY_ALARM_FILTRATION_OBJ,
  UPDATE_ALARM_FILTRATION,
  UPDATE_ALARM_FILTRATION_STORED,
  ADD_ALARM_FILTRATION,
  QUERY_ALARM_INFO_BY_ID,
  APDATE_ALARM_FILTRATION,
  QUERY_ALARM_REMOTE,
  UPDATE_ALARM_FILTRATION_REMOTE_STORED,
  DELETE_ALARM_REMOTE,
  QUERY_ALARM_WORK,
  UPDATE_ALARM_FILTRATION_WORK_STORED,
  DELETE_ALARM_WORK,
  ADD_ALARM_WORK,
  APDATE_ALARM_WORK,
  QUERY_ALARM_WORK_BY_ID,
  QUERY_ALARM_USER,
  QUERY_ALARM_REMOTE_BY_ID,
  ADD_ALARM_REMOTE,
  QUERY_TEMPLATE,
  AREA_LIST_BY_ID,
  DELETE_ALARM_TEMPLATE_LIST,
  WEBSOCKET,
  QUERY_ALARM_DEVICE_ID,
  QUERY_ALARM_HISTORY_DEVICE_ID,
  QUERY_DEPARTMENT_ID,
  SELECT_FOREIGN_AREA_INFO,
  SELECT_FOREIGN_DEVICE_TYPE_INFO,
  UPDATE_ALARM_REMOTE,
  EXPORT_ALARM_LIST,
  ADD_CLEAR_FAILURE_PROC,
  EXAMINE_PICTURE,
  ALARM_QUERY_TEMPLATE,
  ADD_ALARM_TEMPLATE,
  UPDATE_ALARM_TEMPLATE,
  QUERY_ALARM_TEMPLATE_BY_ID,
  UPDATE_HISTORY_ALARM_REMARK,
  EXAMINE_PICTURE_HISTORY,
  EXPORT_HISTORY_ALARM_LIST,
  QUERY_ALARM_SET_LIST,
  UPDATE_ALARM_FILTRATION_REMOTE_PUSHTYPE,
  QUERY_ALARM_CURRENT_PAGE,
  AREA_GET_UNIT,
  QUERY_DEVICE_TYPE_BY_AREAIDS,
  QUERY_USER_INFOBY_DEPT_AND_DEVICE_TYPE, QUERY_DEPARTMENT_HISTORY
} from '../alarm-request-url';

@Injectable()
export class AlarmService implements AlarmInterface {

  constructor(private $http: HttpClient) {
  }

  queryCurrentAlarmList(body): Observable<Object> {
    return this.$http.post(`${QUERY_ALARM_CURRENT_LIST}`, body);
  }

  queryCurrentAlarmInfoById(body): Observable<Object> {
    return this.$http.post(`${QUERY_ALARM_CURRENT_INFO_BY_ID}`, body);
  }

  // ???????????? ????????????
  updateAlarmRemark(body): Observable<Object> {
    return this.$http.post(`${UPDATE_ALARM_REMARK}`, body);
  }


  updateAlarmConfirmStatus(body): Observable<Object> {
    return this.$http.post(`${UPDATE_ALARM_CONFIRM_STATUS}`, body);
  }

  updateAlarmCleanStatus(body): Observable<Object> {
    return this.$http.post(`${UPDATE_ALARM_CLEAN_STATUS}`, body);
  }

  queryEveryAlarmCount(id: number): Observable<Object> {
    return this.$http.post(`${QUERY_EVERY_ALARM_COUNT}`, id);
  }

  queryAlarmHistoryList(body): Observable<Object> {
    return this.$http.post(`${QUERY_ALARM_HISTORY_LIST}`, body);
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

  queryAlarmCurrentPage(body): Observable<Object> {
    return this.$http.post(`${QUERY_ALARM_CURRENT_PAGE}`, body);
  }

  // ??????????????????
  queryAlarmSetList(body): Observable<Object> {
    return this.$http.post(`${QUERY_ALARM_SET_LIST}`, body);
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
    return this.$http.post(`${QUERY_ALARM_LEVEL_SET_BY_ID}`, [id]);
  }

  deleteAlarmCurrentSet(body): Observable<Object> {
    return this.$http.post(`${DELETE_ALARM_CURRENTSET}`, body);
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

  queryDepartmentId(body): Observable<Object> {
    return this.$http.post(`${QUERY_DEPARTMENT_ID}`, body);
  }

  queryDepartmentHistory(body): Observable<Object> {
    return this.$http.post(QUERY_DEPARTMENT_HISTORY, body);
  }

  queryAlarmObjectList(body): Observable<Object> {
    return this.$http.post(`${QUERY_ALARM_FILTRATION_OBJ}`, body);
  }

  queryAlarmFiltration(body): Observable<Object> {
    return this.$http.post(`${QUERY_ALARM_FILTRATION}`, body);
  }

  // ??????????????????????????????????????????
  queryAlarmRemote(body): Observable<Object> {
    return this.$http.post(`${QUERY_ALARM_REMOTE}`, body);
  }

  // ?????????????????????????????????
  queryAlarmWorkOrder(body): Observable<Object> {
    return this.$http.post(`${QUERY_ALARM_WORK}`, body);
  }

  deleteAlarmFiltration(body): Observable<Object> {
    return this.$http.post(`${DELETE_ALARM_FILTRATION}`, body);
  }

  // ?????????????????? ??????
  deleteAlarmRemote(body): Observable<Object> {
    return this.$http.post(`${DELETE_ALARM_REMOTE}`, body);
  }

  // ???????????????
  deleteAlarmWork(body): Observable<Object> {
    return this.$http.post(`${DELETE_ALARM_WORK}`, body);
  }

  updateStatus(status: number, idArray: any): Observable<Object> {
    return this.$http.post(`${UPDATE_ALARM_FILTRATION}?status=${status}&idArray=${idArray}`, {});
  }

  // ?????????????????? ??????????????????
  updateRemoteStatus(status: number, idArray: any): Observable<Object> {
    return this.$http.post(`${UPDATE_ALARM_FILTRATION_REMOTE_STORED}?status=${status}&idArray=${idArray}`, {});
  }

  // ??????????????? ??????????????????
  updateWorkStatus(status: number, idArray: any): Observable<Object> {
    return this.$http.post(`${UPDATE_ALARM_FILTRATION_WORK_STORED}?status=${status}&idArray=${idArray}`, {});
  }

  updateAlarmStorage(storage: number, idArray: any): Observable<Object> {
    return this.$http.post(`${UPDATE_ALARM_FILTRATION_STORED}?stored=${storage}&idArray=${idArray}`, {});
  }

  // ?????????????????? ??????????????????
  updateAlarmRemotePushType(storage: number, idArray: any): Observable<Object> {
    return this.$http.post(`${UPDATE_ALARM_FILTRATION_REMOTE_PUSHTYPE}?pushType=${storage}&idArray=${idArray}`, {});
  }

  addAlarmFiltration(body): Observable<Object> {
    return this.$http.post(`${ADD_ALARM_FILTRATION}`, body);
  }

  // ????????????????????????
  updateAlarmFiltration(body): Observable<Object> {
    return this.$http.post(`${APDATE_ALARM_FILTRATION}`, body);
  }

  // ??????id??????????????????
  queryAlarmById(id: string[]): Observable<Object> {
    return this.$http.get(`${QUERY_ALARM_INFO_BY_ID}` + `/${id}`);
  }

  // ??????????????? ??????
  addAlarmWork(body): Observable<Object> {
    return this.$http.post(`${ADD_ALARM_WORK}`, body);
  }

  // ??????????????? ??????
  updateAlarmWork(body): Observable<Object> {
    return this.$http.post(`${APDATE_ALARM_WORK}`, body);
  }

  queryAlarmDeviceId(id): Observable<Object> {
    return this.$http.post(`${QUERY_ALARM_DEVICE_ID}/${id}`, {});
  }

  queryAlarmHistoryDeviceId(id): Observable<Object> {
    return this.$http.post(`${QUERY_ALARM_HISTORY_DEVICE_ID}/${id}`, {});
  }

  // ??????????????? ??????id??????????????????
  queryAlarmWorkById(id: string[]): Observable<Object> {
    return this.$http.get(`${QUERY_ALARM_WORK_BY_ID}` + `/${id}`);
  }

  // ?????????????????? ???????????? ???????????????
  queryUser(body): Observable<Object> {
    return this.$http.post(`${QUERY_ALARM_USER}`, body);
  }

  // ?????????????????? ???????????? ???????????? ???????????? ???????????????
  queryUserInfoByDeptAndDeviceType(body): Observable<Object> {
    return this.$http.post(`${QUERY_USER_INFOBY_DEPT_AND_DEVICE_TYPE}`, body);
  }

  // ?????????????????? ??????
  addAlarmRemote(body): Observable<Object> {
    return this.$http.post(`${ADD_ALARM_REMOTE}`, body);
  }

  // ?????????????????? ??????
  updateAlarmRemarklist(body): Observable<Object> {
    return this.$http.post(`${UPDATE_ALARM_REMOTE}`, body);
  }

  // ?????????????????? ??????id??????????????????
  queryAlarmRemoteById(id: string[]): Observable<Object> {
    return this.$http.get(`${QUERY_ALARM_REMOTE_BY_ID}` + `/${id}`);
  }

  // ?????????????????? ??????ID?????? ?????????????????????
  queryUserById(id: string[]): Observable<Object> {
    return this.$http.post(`${QUERY_ALARM_REMOTE_BY_ID}` + `/${id}`, null);
  }

  // ?????????????????? ???????????? ????????????????????? ??????
  areaListById(id: string[]): Observable<Object> {
    return this.$http.post(`${AREA_LIST_BY_ID}` + `/${id}`, null);
  }

  // ???????????? ????????????????????????
  queryAlarmTemplateList(body): Observable<Object> {
    return this.$http.post(`${QUERY_TEMPLATE}`, body);
  }

  // ???????????? ???????????? ????????????
  deleteAlarmTemplateList(id: string[]): Observable<Object> {
    return this.$http.post(`${DELETE_ALARM_TEMPLATE_LIST}`, id);
  }

  // ??????????????? ??????????????????
  getArea(id: string[]): Observable<Object> {
    return this.$http.post(`${SELECT_FOREIGN_AREA_INFO}`, id);
  }

  // ?????????????????? ????????????????????????
  areaGtUnit(body): Observable<Object> {
    return this.$http.post(`${AREA_GET_UNIT}`, body);
  }

  // ?????????????????? ??????????????????????????????
  getDeviceType(body): Observable<Object> {
    return this.$http.post(`${QUERY_DEVICE_TYPE_BY_AREAIDS}`, body);
  }

  // ??????????????? ????????????????????????
  getDeviceTypeList(id: string[]): Observable<Object> {
    return this.$http.post(`${SELECT_FOREIGN_DEVICE_TYPE_INFO}`, id);
  }

  // ???????????? ??????
  exportAlarmList(body): Observable<Object> {
    return this.$http.post(`${EXPORT_ALARM_LIST}`, body);
  }

  // ???????????? ??????
  exportHistoryAlarmList(body): Observable<Object> {
    return this.$http.post(`${EXPORT_HISTORY_ALARM_LIST}`, body);
  }

  // ???????????? ????????????
  examinePicture(alarmId): Observable<Object> {
    return this.$http.get(`${EXAMINE_PICTURE}` + `/${alarmId}`, {});
  }

  // ???????????? ????????????
  examinePictureHistory(alarmId): Observable<Object> {
    return this.$http.get(`${EXAMINE_PICTURE_HISTORY}` + `/${alarmId}`, {});
  }

  // ???????????? ????????????
  addClearFailureProc(body): Observable<Object> {
    return this.$http.post(`${ADD_CLEAR_FAILURE_PROC}`, body);
  }

  // ???????????? ????????????
  alarmQueryTemplateById(id: string, pageCondition): Observable<Object> {
    return this.$http.post(`${ALARM_QUERY_TEMPLATE}` + `/${id}`, pageCondition);
  }

  // ???????????? ????????????
  addAlarmTemplate(body): Observable<Object> {
    return this.$http.post(`${ADD_ALARM_TEMPLATE}`, body);
  }

  // ???????????? ????????????
  updataAlarmTemplate(body): Observable<Object> {
    return this.$http.post(`${UPDATE_ALARM_TEMPLATE}`, body);
  }

  // ???????????? ???????????? ??????ID????????????
  queryAlarmTemplateById(id: string[]): Observable<Object> {
    return this.$http.post(`${QUERY_ALARM_TEMPLATE_BY_ID}` + `/${id}`, null);
  }

  // ???????????? ????????????
  updateHistoryAlarmRemark(body): Observable<Object> {
    return this.$http.post(`${UPDATE_HISTORY_ALARM_REMARK}`, body);
  }
}
