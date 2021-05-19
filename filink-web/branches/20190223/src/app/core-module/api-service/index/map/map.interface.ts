import {Observable} from 'rxjs/index';

export interface MapInterface {
  /**
   * 查询所有设施列表
   * returns {Observable<Object>}
   */
  getALLFacilityList(): Observable<Object>;

  /**
   * 查询所有区域列表
   * returns {Observable<Object>}
   */
  getALLAreaList(): Observable<Object>;


  /**
   * 获取设施类型配置信息
   * returns {Observable<Object>}
   */
  getALLFacilityConfig(): Observable<Object>;

  /**
   * 修改用户地图设施类型配置的设施类型启用状态
   * param body
   * returns {Observable<Object>}
   */
  modifyFacilityTypeConfig(body): Observable<Object>;

  /**
   * 修改用户地图设施类型配置的设施图标尺寸
   * param body
   * returns {Observable<Object>}
   */
  modifyFacilityIconSize(body): Observable<Object>;
}
