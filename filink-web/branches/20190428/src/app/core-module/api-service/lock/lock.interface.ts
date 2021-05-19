import {Observable} from 'rxjs';

export interface LockInterface {
  /**
   * 查询电子锁主控信息
   * param id
   * returns {Observable<Object>}
   */
  getLockControlInfo(id): Observable<Object>;

  /**
   * 查询电子锁信息
   * param id
   * returns {Observable<Object>}
   */
  getLockInfo(id): Observable<Object>;

  /**
   * 获取主控配置
   * param body
   * returns {Observable<Object>}
   */
  getPramsConfig(body): Observable<Object>;

  /**
   * 设置主控配置
   * param body
   * returns {Observable<Object>}
   */
  setControl(body): Observable<Object>;

  /**
   * 远程开锁
   * param body
   * returns {Observable<Object>}
   */
  openLock(body): Observable<Object>;

  /**
   * 更新设施状态
   * returns {Observable<Object>}
   */
  updateDeviceStatus(body): Observable<Object>;
}
