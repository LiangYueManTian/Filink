import { Injectable } from '@angular/core';
import {AgreementManageInterface} from './agreement-manage.interface';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable} from 'rxjs';

import {systemSettingRequireUrl} from '../http-url-config';

@Injectable()
export class AgreementManageService implements AgreementManageInterface {

  constructor(private $http: HttpClient) { }
  /**
   * 新增设施协议
   * param params
   */
  addDeviceProtocol(params): Observable<Object> {
    return this.$http.post(systemSettingRequireUrl.addDeviceProtocol, params);
  }

  /**
   * 修改设施协议
   * param params
   */
  updateDeviceProtocol(params): Observable<Object> {
    return this.$http.post(systemSettingRequireUrl.updateDeviceProtocol, params);
  }

  /**
   * 实施协议文件校验
   * param params
   */
  queryFileLimit(): Observable<Object> {
    return this.$http.get(`${systemSettingRequireUrl.queryFileLimit}`);
  }

  /**
   * 修改设施协议名称
   * param params
   */
  updateProtocolName(params): Observable<Object> {
    return this.$http.post(systemSettingRequireUrl.updateProtocolName, params);
  }

  /**
   * 删除设施协议
   * param protocolId
   */
  deleteDeviceProtocol(params): Observable<Object> {
    return this.$http.post(`${systemSettingRequireUrl.deleteDeviceProtocol}` , params);
  }

  /**
   * 查询设施列表
   * param params
   */
  queryDeviceProtocolList(): Observable<Object> {
    return this.$http.get(systemSettingRequireUrl.queryDeviceProtocolList);
  }
}
