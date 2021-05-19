import {Injectable} from '@angular/core';
import {CommonUtil} from '../../shared-module/util/common-util';
import {MAP_ICON_CONFIG} from '../../business-module/index/config';

@Injectable()
export class MapStoreService {
  private _mapType: string;   // 地图类型
  private _token: string;   // token
  private _mapData: any;   // 设施数据集
  private _isInitData: boolean;   // 是否初始化了设施数据
  private _isInitConfig: boolean;   // 是否初始化了配置
  private _isInitLogicAreaData: boolean;   // 是否初始化了逻辑区域数据
  private _facilityTypeConfig: any[];   // 用户的设施类型配置
  private _facilityIconSize: string; // 用户的设施图片大小
  private _facilityStatusList: any[];   // 设施状态选项列表
  private _facilityTypeList: any[];   // 设施类型选项列表
  private _logicAreaList: any[];   //  逻辑区域列表
  constructor() {
    this._facilityIconSize = MAP_ICON_CONFIG.defaultIconSize;
    this.resetData();
    this.resetConfig();
  }

  set mapType(type) {
    this._mapType = type;
  }

  get mapType(): string {
    return this._mapType;
  }

  set mapData(data) {
    this._mapData = data;
  }

  get mapData() {
    return this._mapData;
  }

  set isInitConfig(bol) {
    this._isInitConfig = bol;
  }

  get isInitConfig() {
    return this._isInitConfig;
  }

  set isInitData(bol) {
    this._isInitData = bol;
  }

  get isInitData() {
    return this._isInitData;
  }

  set isInitLogicAreaData(bol) {
    this._isInitLogicAreaData = bol;
  }

  get isInitLogicAreaData() {
    return this._isInitLogicAreaData;
  }

  set facilityTypeConfig(data) {
    this._facilityTypeConfig = CommonUtil.deepClone(data);
  }

  get facilityTypeConfig() {
    return this._facilityTypeConfig;
  }

  set facilityIconSize(data) {
    this._facilityIconSize = data;
  }

  get facilityIconSize() {
    return this._facilityIconSize;
  }

  set facilityStatusList(data) {
    this._facilityStatusList = data;
  }

  get facilityStatusList() {
    return this._facilityStatusList;
  }

  set facilityTypeList(data) {
    this._facilityTypeList = data;
  }

  get facilityTypeList() {
    return this._facilityTypeList;
  }

  set logicAreaList(data) {
    this._logicAreaList = data;
  }

  get logicAreaList() {
    return this._logicAreaList;
  }

  set token(data) {
    this._token = data;
  }

  get token() {
    return this._token;
  }

  updateMarker(facility, bol) {
    this._mapData.set(facility.deviceId, {
      info: facility,
      isShow: bol
    });
  }

  updateMarkerData(facility) {
    let bol = true;
    if (this._mapData.get(facility.deviceId)) {
      bol = this._mapData.get(facility.deviceId).show;
    }
    const position  = facility.positionBase.split(',');
    facility.lng = parseFloat(position[0]);
    facility.lat = parseFloat(position[1]);
    delete facility.positionBase;
    this.updateMarker(facility, bol);
  }

  deleteMarker(facilityId) {
    this._mapData.delete(facilityId);
  }

  getMarker(id) {
    const data = this._mapData.get(id);
    return data ? data.info : data;
  }

  resetData() {
    this._isInitData = false;
    this._mapData =  new Map();
  }

  resetConfig() {
    this._isInitConfig = false;
    this._isInitLogicAreaData = false;
    this._facilityTypeConfig = [];
    this._facilityIconSize = '';
    this._facilityTypeConfig = [];
    this._facilityStatusList = [];
    this._facilityTypeList = [];
    this._logicAreaList = [];
  }

  getFacilityInfo(id) {
    return this._mapData.get(id);
  }
}
