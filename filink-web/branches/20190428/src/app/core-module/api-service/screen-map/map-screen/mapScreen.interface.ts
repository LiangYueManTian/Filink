import {Observable} from 'rxjs';

export interface MapScreenInterface {
  /**
   *查询地区信息
   */
  queryGetArea(body): Observable<Object>;

  /**
   * 获取所有的省
   * returns {Observable<Object>}
   */
  getAllProvince(): Observable<Object>;

  /**
   * 获取所有的城市信息
   * returns {Observable<Object>}
   */
  getAllCityInfo(): Observable<Object>;
}
