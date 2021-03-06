import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {FacilityInterface} from './facility.interface';
import {
  ADD_CABLE,
  ADD_DEVICE,
  DELETE_CABLE_BY_ID,
  DELETE_DEVICE_BY_IDS,
  DEVICE_CAN_CHANGE_DETAIL,
  DEVICE_LIST_BY_PAGE,
  DEVICE_LOG_LIST_BY_PAGE,
  DEVICE_LOG_TIME,
  EXPORT_DEVICE_LIST,
  EXPORT_LOG_LIST,
  FIND_DEVICE_BY_ID,
  GET_CABLE_LIST,
  GET_CABLE_SEGMENT_LIST,
  GET_DETAIL_CODE,
  GET_DEVICE_STRATEGY,
  GET_PRAMS_CONFIG,
  GET_SMART_LABEL_LIST,
  PIC_RELATION_INFO,
  QUERY_CABLE_BY_ID,
  QUERY_DEVICE_NAME_IS_EXIST,
  QUERY_DEVICE_TYPE_COUNT,
  QUERY_HEARTBEAT_TIME,
  SET_DEVICE_STRATEGY,
  UPDATE_CABLE,
  UPDATE_DEVICE_BY_ID,
  GET_THE_FUSE_INFORMATION,
  GET_THE_FUSED_FIBER_INFORMATION,
  SAVE_THE_CORE_INFORMATION,
  QUERY_TOPOLOGY_BY_ID,
  GET_TOPOLGY,
  GET_OPTICCABLESECTIONID,
  EXPORT_CABLE_LIST,
  EXPORT_CABLE_SECTION_LIST,
  UPDATE_CABLEQUERYBYID,
  GET_PORT_CABLE_CORE_INFORMATION,
  FIND_FO_BYOPTIC_CABLE,
  DELETE_CABLE_SECTION_BY_ID,
  CHECK_CABLE_NAME,
  DELETE_RFID_INFO_BY_ID,
  DEVICEID_CHECK_USER_IF_DEVICE_PERMISSION,
  DEVICEID_CHECK_USER_IF_DEVICEDATA,
  DEVICE_LIST_BY_Lock_PAGE
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

  deviceListOfLockByPage(body): Observable<Object> {
    return this.$http.post(`${DEVICE_LIST_BY_Lock_PAGE}`, body);
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

  // ???????????????????????? GET_CABLE_SEGMENT_LIST
  getCableList(body): Observable<Object> {
    return this.$http.post(`${GET_CABLE_LIST}`, body);
  }

  // ??????????????????
  addCable(body): Observable<Object> {
    return this.$http.post(`${ADD_CABLE}`, body);
  }

  // ????????????id????????????
  queryCableById(id): Observable<Object> {
    return this.$http.get(`${QUERY_CABLE_BY_ID}/${id}`);
  }

  // ??????????????????
  checkCableName(name, id): Observable<Object> {
    return this.$http.post(`${CHECK_CABLE_NAME}`, {opticCableName: name, opticCableId: id});
  }

  // ??????????????????
  updateCable(body): Observable<Object> {
    return this.$http.post(`${UPDATE_CABLE}`, body);
  }

  // ?????????????????? DELETE_CABLE_SECTION_BY_ID
  deleteCableById(id): Observable<Object> {
    return this.$http.get(`${DELETE_CABLE_BY_ID}/${id}`);
  }

  // ??????????????????
  exportCableList(body): Observable<Object> {
    return this.$http.post(`${EXPORT_CABLE_LIST}`, body);
  }

  // ???????????????????????????
  getCableSegmentList(body): Observable<Object> {
    return this.$http.post(`${GET_CABLE_SEGMENT_LIST}`, body);
  }

  // ?????????????????????
  deleteCableSectionById(id): Observable<Object> {
    return this.$http.get(`${DELETE_CABLE_SECTION_BY_ID}/${id}`);
  }

  // ??????????????????
  exportCableSectionList(body): Observable<Object> {
    return this.$http.post(`${EXPORT_CABLE_SECTION_LIST}`, body);
  }

  // ??????????????????
  opticCableSectionByIdForTopology(body): Observable<object> {
    return this.$http.post(`${GET_TOPOLGY}`, body);
  }

  // ????????????????????????
  getSmartLabelList(body): Observable<Object> {
    return this.$http.post(`${GET_SMART_LABEL_LIST}`, body);
  }

  // ??????????????????????????????
  deleteSmartLabelInfo(body): Observable<Object> {
    return this.$http.post(`${DELETE_RFID_INFO_BY_ID}`, body);
  }

  // ??????????????????
  getTheFuseInformation(body): Observable<Object> {
    return this.$http.post(`${GET_THE_FUSE_INFORMATION}`, body);
  }

  // ??????????????????????????????
  getTheFusedFiberInformation(body): Observable<Object> {
    return this.$http.post(`${GET_THE_FUSED_FIBER_INFORMATION}`, body);
  }

  // ?????????????????????????????????
  getPortCableCoreInformation(body): Observable<Object> {
    return this.$http.post(`${GET_PORT_CABLE_CORE_INFORMATION}`, body);
  }

  // ??????????????????
  saveTheCoreInformation(body): Observable<Object> {
    return this.$http.post(`${SAVE_THE_CORE_INFORMATION}`, body);
  }

  // ????????????id????????????????????????????????????????????????
  deviceIdCheckUserIfDevicePermission(body): Observable<Object> {
    return this.$http.post(`${DEVICEID_CHECK_USER_IF_DEVICE_PERMISSION}`, body);
  }

  // ????????????id??????????????????????????????????????????
  deviceIdCheckUserIfDeviceData(body): Observable<Object> {
    return this.$http.post(`${DEVICEID_CHECK_USER_IF_DEVICEDATA}`, body);
  }

  deviceLogTime(id): Observable<Object> {
    return this.$http.get(`${DEVICE_LOG_TIME}/${id}`);
  }

  queryDeviceTypeCount(): Observable<Object> {
    return this.$http.get(`${QUERY_DEVICE_TYPE_COUNT}`);
  }

  queryTopologyById(id): Observable<Object> {
    return this.$http.get(`${QUERY_TOPOLOGY_BY_ID}/${id}`);
  }

  cableSectionId(id): Observable<Object> {
    return this.$http.get(`${GET_OPTICCABLESECTIONID}/${id}`);
  }

  updateCableQueryById(body): Observable<Object> {
    return this.$http.post(`${UPDATE_CABLEQUERYBYID}`, body);
  }

  findFindCable(id): Observable<Object> {
    return this.$http.get(`${FIND_FO_BYOPTIC_CABLE}/${id}`);
  }
}
