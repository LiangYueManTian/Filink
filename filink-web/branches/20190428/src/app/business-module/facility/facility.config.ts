/**
 * Created by xiaoconghu on 2019/1/14.
 */
import {NzI18nService} from 'ng-zorro-antd';

export const DATE_FORMAT_STR = 'yyyy-MM-dd HH:mm:ss';

export enum DeviceTypeCode {
  Optical_Box = '001',
  Well = '030',
  Distribution_Frame = '060',
  Junction_Box = '090',
  // Optical_Cable = '120', // 光缆段
  Splitting_Box = '150',
  // Parts = '180' // 配件
}

export enum DeviceStatusCode {
  UNKNOWN = '1', // 未配置
  NORMAL = '2', // 正常
  ALARM = '3',  // 告警
  OFFLINE = '4', // 离线
  OUT_OF_CONTACT = '5', // 失联
}

export enum PartsTypeCode {
  bluetoothKey = '1',
  mechanicalKey = '2',
  labelGun = '3'

}

export enum DeployStatus {
  DEPLOYED = '1', // 已部防
  NODEFENCE = '2', // 未部防
  NOTUSED = '3', // 停用
  MAIINTENANCE = '4', // 维护
  DISMANTLE = '5', // 拆除
  DEPLOYING = '6' // 部署中
}

export const FacilityDeployStatusClassName = {
  '1': {
    className: 'icon-l icon_deploy_arm',
    name: 'arm'
  },        // 布防
  '2': {
    className: 'icon-l icon_deploy_unarm',
    name: 'unarm'
  },      // 未布防
  '3': {
    className: 'icon-l icon_deploy_disable',
    name: 'disable'
  },    // 停用
  '4': {
    className: 'icon-l icon_deploy_maintain',
    name: 'maintain'
  },   // 维护
  '5': {
    className: 'icon-l icon_deploy_remove',
    name: 'remove'
  },     // 拆除
  '6': {
    className: 'iconfont icon-fiLink fiLink-deploying',
    name: 'deploying'
  }
};

export const FacilityStatusColor = {
  '2': '#36D1C9', // 正常
  '3': '#FB7356', // 告警
  '4': '#959595', // 离线
  '5': '#F8C032', // 失联
  '1': '#35AACE' // 未配置
};

export const AreaLevel = {
  AREA_LEVEL_ONE: 1,
  AREA_LEVEL_TWO: 2,
  AREA_LEVEL_THREE: 3,
  AREA_LEVEL_FOUR: 4,
  AREA_LEVEL_FIVE: 5
};

export enum AreaLevelEnum {
  AREA_LEVEL_ONE = 1,
  AREA_LEVEL_TWO = 2,
  AREA_LEVEL_THREE = 3,
  AREA_LEVEL_FOUR = 4,
  AREA_LEVEL_FIVE = 5
}

export const logType = {
  event: '1',
  // alarm: '2'
};
export const alarmCleanStatus = {
  noClean: 3,
  isClean: 1,
  deviceClean: 2,
};
export const nodeObjectType = {
  PASSIVE_LOCK: '0', // 无源锁
  MECHANICAL_LOCK_CYLINDER: '1', // 机械锁芯
  ELECTRONIC_LOCK_CYLINDER: '2' // 电子锁芯
};

export function getNodeObjectType(i18n: NzI18nService, code = null) {
  return codeTranslate(nodeObjectType, i18n, code);
}

export function getAlarmCleanStatus(i18n: NzI18nService, code = null) {
  return codeTranslate(alarmCleanStatus, i18n, code);
}

export function getDeviceType(i18n: NzI18nService, code = null): any {
  return codeTranslate(DeviceTypeCode, i18n, code);
}

export function getPartsType(i18n: NzI18nService, code = null): any {
  return codeTranslate(PartsTypeCode, i18n, code);
}

export function getDeviceStatus(i18n: NzI18nService, code = null) {
  return codeTranslate(DeviceStatusCode, i18n, code);
}

export function getDeployStatus(i18n: NzI18nService, code = null) {
  return codeTranslate(DeployStatus, i18n, code);
}

export function getAreaLevel(i18n: NzI18nService, code = null) {
  return codeTranslate(AreaLevel, i18n, code);
}

export function getLogType(i18n: NzI18nService, code = null) {
  return codeTranslate(logType, i18n, code);
}

/**
 * 枚举翻译
 * param codeEnum
 * param {NzI18nService} i18n
 * param {any} code
 * returns {any}
 */
function codeTranslate(codeEnum, i18n: NzI18nService, code = null) {
  if (code !== null) {
    for (const i of Object.keys(codeEnum)) {
      if (codeEnum[i] === code) {
        return i18n.translate(`facility.config.${i}`);
      }
    }
  } else {
    return Object.keys(codeEnum)
      .map(key => ({label: i18n.translate(`facility.config.${key}`), code: codeEnum[key]}));
  }
}
