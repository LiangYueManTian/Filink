import {SYSTEM_SERVER, LOG_SERVER} from '../api-common.config';

const menuBaseUrl = '/menu';
const logBaseUrl = '/log';
const agreementBaseUrl = '/deviceProtocol';

export const systemSettingRequireUrl = {
  // 菜单管理相关
  getDefaultMenuTemplate: `${SYSTEM_SERVER}${menuBaseUrl}/getDefaultMenuTemplate`,
  addMenuTemplate: `${SYSTEM_SERVER}${menuBaseUrl}/addMenuTemplate`,
  queryListMenuTemplateByPage: `${SYSTEM_SERVER}${menuBaseUrl}/queryListMenuTemplateByPage`,
  openMenuTemplate: `${SYSTEM_SERVER}${menuBaseUrl}/openMenuTemplate`,
  deleteMenuTemplate: `${SYSTEM_SERVER}${menuBaseUrl}/deleteMenuTemplate`,
  getMenuTemplateByMenuTemplateId: `${SYSTEM_SERVER}${menuBaseUrl}/getMenuTemplateByMenuTemplateId`,
  updateMenuTemplate: `${SYSTEM_SERVER}${menuBaseUrl}/updateMenuTemplate`,
  getShowMenuTemplate: `${SYSTEM_SERVER}${menuBaseUrl}/getShowMenuTemplate`,

  // 日志管理相关
  findSystemLog: `${LOG_SERVER}${logBaseUrl}/findSystemLog`,
  findOperateLog: `${LOG_SERVER}${logBaseUrl}/findOperateLog`,
  findSecurityLog: `${LOG_SERVER}${logBaseUrl}/findSecurityLog`,

  // 设施管理相关
  addDeviceProtocol: `zuul/${SYSTEM_SERVER}${agreementBaseUrl}/addDeviceProtocol`,
  updateDeviceProtocol: `zuul/${SYSTEM_SERVER}${agreementBaseUrl}/updateDeviceProtocol`,
  updateProtocolName: `${SYSTEM_SERVER}${agreementBaseUrl}/updateProtocolName`,
  deleteDeviceProtocol: `${SYSTEM_SERVER}${agreementBaseUrl}/deleteDeviceProtocol`,
  queryDeviceProtocolList: `${SYSTEM_SERVER}${agreementBaseUrl}/queryDeviceProtocolList`,
  queryFileLimit: `${SYSTEM_SERVER}${agreementBaseUrl}/queryFileLimit`
};
