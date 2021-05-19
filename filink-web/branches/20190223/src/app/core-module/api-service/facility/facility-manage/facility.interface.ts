import {Observable} from 'rxjs';

/**
 * Created by xiaoconghu on 2019/1/14.
 */
export interface FacilityInterface {
  /**
   * 查询设施列表
   * param body
   * returns {Observable<Object>}
   */
  deviceListByPage(body): Observable<Object>;

  /**
   * 新增设施信息
   * param body
   * returns {Observable<Object>}
   */
  addDevice(body): Observable<Object>;

  /**
   * 删除设施
   * param body
   * returns {Observable<Object>}
   */
  deleteDeviceDyIds(body): Observable<Object>;

  /**
   * 修改设施信息
   * param body
   * returns {Observable<Object>}
   */
  updateDeviceById(body): Observable<Object>;

  /**
   * 查看设施详情
   * param body
   * returns {Observable<Object>}
   */
  queryDeviceById(body): Observable<Object>;

  /**
   * 获取设施配置策略
   * param body
   * returns {Observable<Object>}
   */
  getDeviceStrategy(body): Observable<Object>;

  /**
   * 设置设施配置策略
   * param body
   * returns {Observable<Object>}
   */
  setDeviceStrategy(body): Observable<Object>;

  /**
   * 查询设施名称是否存在
   * param body
   * returns {Observable<Object>}
   */
  queryDeviceNameIsExist(body): Observable<Object>;

  /**
   * 查询设施日志
   * param body
   * returns {Observable<Object>}
   */
  queryDeviceLogListByPage(body): Observable<Object>;

  /**
   * 获取详情页的code码
   * param body
   * returns {Observable<Object>}
   */
  getDetailCode(body): Observable<Object>;

  /**
   * 查询设施详情是否可以修改
   * param body
   * returns {Observable<Object>}
   */
  deviceCanChangeDetail(body): Observable<Object>;

  /**
   * 获取配置参数
   * param body
   * returns {Observable<Object>}
   */
  getPramsConfig(body): Observable<Object>;
}
