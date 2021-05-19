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

  // 当前告警 修改备注
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

  // 当前告警设置
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

  // 查询告警远程通知列表所有信息
  queryAlarmRemote(body): Observable<Object> {
    return this.$http.post(`${QUERY_ALARM_REMOTE}`, body);
  }

  // 查询告警转工单列表信息
  queryAlarmWorkOrder(body): Observable<Object> {
    return this.$http.post(`${QUERY_ALARM_WORK}`, body);
  }

  deleteAlarmFiltration(body): Observable<Object> {
    return this.$http.post(`${DELETE_ALARM_FILTRATION}`, body);
  }

  // 告警远程通知 删除
  deleteAlarmRemote(body): Observable<Object> {
    return this.$http.post(`${DELETE_ALARM_REMOTE}`, body);
  }

  // 告警转工单
  deleteAlarmWork(body): Observable<Object> {
    return this.$http.post(`${DELETE_ALARM_WORK}`, body);
  }

  updateStatus(status: number, idArray: any): Observable<Object> {
    return this.$http.post(`${UPDATE_ALARM_FILTRATION}?status=${status}&idArray=${idArray}`, {});
  }

  // 告警远程通知 修改启禁状态
  updateRemoteStatus(status: number, idArray: any): Observable<Object> {
    return this.$http.post(`${UPDATE_ALARM_FILTRATION_REMOTE_STORED}?status=${status}&idArray=${idArray}`, {});
  }

  // 告警转工单 修改启禁状态
  updateWorkStatus(status: number, idArray: any): Observable<Object> {
    return this.$http.post(`${UPDATE_ALARM_FILTRATION_WORK_STORED}?status=${status}&idArray=${idArray}`, {});
  }

  updateAlarmStorage(storage: number, idArray: any): Observable<Object> {
    return this.$http.post(`${UPDATE_ALARM_FILTRATION_STORED}?stored=${storage}&idArray=${idArray}`, {});
  }

  // 告警远程通知 修改推送方式
  updateAlarmRemotePushType(storage: number, idArray: any): Observable<Object> {
    return this.$http.post(`${UPDATE_ALARM_FILTRATION_REMOTE_PUSHTYPE}?pushType=${storage}&idArray=${idArray}`, {});
  }

  addAlarmFiltration(body): Observable<Object> {
    return this.$http.post(`${ADD_ALARM_FILTRATION}`, body);
  }

  // 修改告警过滤信息
  updateAlarmFiltration(body): Observable<Object> {
    return this.$http.post(`${APDATE_ALARM_FILTRATION}`, body);
  }

  // 根据id查询整条数据
  queryAlarmById(id: string[]): Observable<Object> {
    return this.$http.get(`${QUERY_ALARM_INFO_BY_ID}` + `/${id}`);
  }

  // 告警转工单 新增
  addAlarmWork(body): Observable<Object> {
    return this.$http.post(`${ADD_ALARM_WORK}`, body);
  }

  // 告警转工单 编辑
  updateAlarmWork(body): Observable<Object> {
    return this.$http.post(`${APDATE_ALARM_WORK}`, body);
  }

  queryAlarmDeviceId(id): Observable<Object> {
    return this.$http.post(`${QUERY_ALARM_DEVICE_ID}/${id}`, {});
  }

  queryAlarmHistoryDeviceId(id): Observable<Object> {
    return this.$http.post(`${QUERY_ALARM_HISTORY_DEVICE_ID}/${id}`, {});
  }

  // 告警转工单 根据id查询整条数据
  queryAlarmWorkById(id: string[]): Observable<Object> {
    return this.$http.get(`${QUERY_ALARM_WORK_BY_ID}` + `/${id}`);
  }

  // 告警远程通知 新增页面 请求通知人
  queryUser(body): Observable<Object> {
    return this.$http.post(`${QUERY_ALARM_USER}`, body);
  }

  // 告警远程通知 新增页面 通过区域 设施类型 查询通知人
  queryUserInfoByDeptAndDeviceType(body): Observable<Object> {
    return this.$http.post(`${QUERY_USER_INFOBY_DEPT_AND_DEVICE_TYPE}`, body);
  }

  // 告警远程通知 新增
  addAlarmRemote(body): Observable<Object> {
    return this.$http.post(`${ADD_ALARM_REMOTE}`, body);
  }

  // 告警远程通知 编辑
  updateAlarmRemarklist(body): Observable<Object> {
    return this.$http.post(`${UPDATE_ALARM_REMOTE}`, body);
  }

  // 告警远程通知 根据id查询整条数据
  queryAlarmRemoteById(id: string[]): Observable<Object> {
    return this.$http.get(`${QUERY_ALARM_REMOTE_BY_ID}` + `/${id}`);
  }

  // 告警远程通知 通过ID查询 通知人相关信息
  queryUserById(id: string[]): Observable<Object> {
    return this.$http.post(`${QUERY_ALARM_REMOTE_BY_ID}` + `/${id}`, null);
  }

  // 告警远程通知 新增页面 根据通知人查询 区域
  areaListById(id: string[]): Observable<Object> {
    return this.$http.post(`${AREA_LIST_BY_ID}` + `/${id}`, null);
  }

  // 当前告警 查询模板列表信息
  queryAlarmTemplateList(body): Observable<Object> {
    return this.$http.post(`${QUERY_TEMPLATE}`, body);
  }

  // 当前告警 模板列表 删除数据
  deleteAlarmTemplateList(id: string[]): Observable<Object> {
    return this.$http.post(`${DELETE_ALARM_TEMPLATE_LIST}`, id);
  }

  // 告警转工单 获取相关区域
  getArea(id: string[]): Observable<Object> {
    return this.$http.post(`${SELECT_FOREIGN_AREA_INFO}`, id);
  }

  // 告警远程通知 通知区域获取单位
  areaGtUnit(body): Observable<Object> {
    return this.$http.post(`${AREA_GET_UNIT}`, body);
  }

  // 告警远程通知 通过区域获取设施类型
  getDeviceType(body): Observable<Object> {
    return this.$http.post(`${QUERY_DEVICE_TYPE_BY_AREAIDS}`, body);
  }

  // 告警转工单 获取相关设施类型
  getDeviceTypeList(id: string[]): Observable<Object> {
    return this.$http.post(`${SELECT_FOREIGN_DEVICE_TYPE_INFO}`, id);
  }

  // 当前告警 导出
  exportAlarmList(body): Observable<Object> {
    return this.$http.post(`${EXPORT_ALARM_LIST}`, body);
  }

  // 历史告警 导出
  exportHistoryAlarmList(body): Observable<Object> {
    return this.$http.post(`${EXPORT_HISTORY_ALARM_LIST}`, body);
  }

  // 当前告警 查看图片
  examinePicture(alarmId): Observable<Object> {
    return this.$http.get(`${EXAMINE_PICTURE}` + `/${alarmId}`, {});
  }

  // 历史告警 查看图片
  examinePictureHistory(alarmId): Observable<Object> {
    return this.$http.get(`${EXAMINE_PICTURE_HISTORY}` + `/${alarmId}`, {});
  }

  // 当前告警 创建工单
  addClearFailureProc(body): Observable<Object> {
    return this.$http.post(`${ADD_CLEAR_FAILURE_PROC}`, body);
  }

  // 当前告警 模板查询
  alarmQueryTemplateById(id: string, pageCondition): Observable<Object> {
    return this.$http.post(`${ALARM_QUERY_TEMPLATE}` + `/${id}`, pageCondition);
  }

  // 当前告警 新增模板
  addAlarmTemplate(body): Observable<Object> {
    return this.$http.post(`${ADD_ALARM_TEMPLATE}`, body);
  }

  // 当前告警 编辑模板
  updataAlarmTemplate(body): Observable<Object> {
    return this.$http.post(`${UPDATE_ALARM_TEMPLATE}`, body);
  }

  // 当前告警 告警模板 通过ID查询数据
  queryAlarmTemplateById(id: string[]): Observable<Object> {
    return this.$http.post(`${QUERY_ALARM_TEMPLATE_BY_ID}` + `/${id}`, null);
  }

  // 历史告警 修改备注
  updateHistoryAlarmRemark(body): Observable<Object> {
    return this.$http.post(`${UPDATE_HISTORY_ALARM_REMARK}`, body);
  }
}
