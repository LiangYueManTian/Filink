import {Observable} from 'rxjs';

export interface MapInterface {
  /**
   * 查询所有设施列表
   * returns {Observable<Object>}
   */
  getALLFacilityList(): Observable<Object>;

  /**
   * 查询所有设施列表(基本数据，key值简化)
   * returns {Observable<Object>}
   */
  getALLFacilityListBase(): Observable<Object>;

  /**
   * 查询所有设施列表(key值简化)
   * returns {Observable<Object>}
   */
  getALLFacilityListSimple(): Observable<Object>;

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

  /**
   * 收藏设施
   * param id {string} 设施id
   * returns {Observable<Object>}
   */
  collectFacility(id): Observable<Object>;

  /**
   * 取消收藏设施
   * param id {string} 设施id
   * returns {Observable<Object>}
   */
  unCollectFacility(id): Observable<Object>;

  /**
   * 获取收藏设施统计
   * returns {Observable<Object>}
   */
  getCollectFacilityListStatistics(): Observable<Object>;

  /**
   * 获取收藏设施列表
   * param body
   * returns {Observable<Object>}
   */
  getCollectFacilityList(body): Observable<Object>;

  /**
   * 获取所有收藏设施列表
   * param body
   * returns {Observable<Object>}
   */
  getAllCollectFacilityList(): Observable<Object>;

  /**
   * 查询当前用户区域列表
   * param body
   * returns {Observable<Object>}
   */
  getALLAreaListForCurrentUser(): Observable<Object>;
}
