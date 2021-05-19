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
  NORMAL = '1',
  ALARM = '2',
  OFFLINE = '5',
  OUT_OF_CONTACT = '4',
  UNKNOWN = '3'
}

export enum DeployStatus {
  DEPLOYED = '1',
  NODEFENCE = '2',
  NOTUSED = '3',
  MAIINTENANCE = '4',
  DISMANTLE = '5'
}

export const FacilityStatusColor = {
  '1': '#36D1C9',
  '2': '#FB7356',
  '5': '#959595',
  '4': '#F8C032',
  '3': '#35AACE'
};

export const AreaLevel = {
  AREA_LEVEL_ONE: 1,
  AREA_LEVEL_TWO: 2,
  AREA_LEVEL_THREE: 3,
  AREA_LEVEL_FOUR: 4,
  AREA_LEVEL_FIVE: 5
};
export const logType = {
  event: '1',
  alarm: '2'
};
export const alarmCleanStatus = {
  noClean: 3,
  isClean: 1,
  deviceClean: 2,
};
export const nodeObjectType = {
  controller: '1'
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
