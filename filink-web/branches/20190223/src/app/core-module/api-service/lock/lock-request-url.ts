import {LOCK_SERVER} from '../api-common.config';

export const LOCK_URL = {
  // 查询电子锁主控信息
  GET_LOCK_CONTROL_INFO: `${LOCK_SERVER}/control`,

  // 查询电子锁信息
  GET_LOCK_INFO: `${LOCK_SERVER}/lock`,
  // 获取主控参数
  GET_PRAMS_CONFIG: `${LOCK_SERVER}/control/getPramsConfig`,
  // 设置主控配置
  SET_CONTROL: `${LOCK_SERVER}/control/setConfig`,
  OPEN_LOCK: `${LOCK_SERVER}/lock/openLock`
};
