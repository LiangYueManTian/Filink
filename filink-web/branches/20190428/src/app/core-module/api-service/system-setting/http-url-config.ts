import {SYSTEM_SERVER, LOG_SERVER} from '../api-common.config';

// 菜单
const menuBaseUrl = '/menu';
// 日志
const logBaseUrl = '/log';
// 设施协议
const agreementBaseUrl = '/deviceProtocol';
// 安全协议
const securityStrategy = '/securityStrategy';
// license
const licenseBaseUrl = '/licenseInfo';
// 协议http配置
const protocolBaseUrl = '/protocol';
const systemParamUrl = '/systemParam';
// 系统参数设置
const systemParameter = '/systemParameter';

// 关于
const aboutBaseUrl = '/about';

// 访问控制
const ipRangeBaseUrl = '/ipRange';

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
  queryMenuTemplateNameIsExists: `${SYSTEM_SERVER}${menuBaseUrl}/queryMenuTemplateNameIsExists`,

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
  queryFileLimit: `${SYSTEM_SERVER}${agreementBaseUrl}/queryFileLimit`,
  checkDeviceProtocolNameRepeat: `${SYSTEM_SERVER}${agreementBaseUrl}/checkDeviceProtocolNameRepeat`,

  // 安全策略
  queryPasswordPresent: `${SYSTEM_SERVER}${systemParamUrl}/queryParam`,
  updatePasswordStrategy: `${SYSTEM_SERVER}${securityStrategy}/updatePasswordStrategy`,
  queryAccountPresent: `${SYSTEM_SERVER}${securityStrategy}/queryAccount`,
  updateAccountStrategy: `${SYSTEM_SERVER}${securityStrategy}/updateAccountStrategy`,
  queryAccountSecurity: `${SYSTEM_SERVER}${securityStrategy}/queryAccountSecurity`,
  queryPasswordSecurity: `${SYSTEM_SERVER}${securityStrategy}/queryPasswordSecurity`,

  // 安全策略 访问控制
  queryRanges: `${SYSTEM_SERVER}${ipRangeBaseUrl}/queryRanges`,
  deleteRanges: `${SYSTEM_SERVER}${ipRangeBaseUrl}/deleteRanges`,
  addIpRange: `${SYSTEM_SERVER}${ipRangeBaseUrl}/addIpRange`,
  updateIpRange: `${SYSTEM_SERVER}${ipRangeBaseUrl}/updateIpRange`,
  updateRangeStatus: `${SYSTEM_SERVER}${ipRangeBaseUrl}/updateRangeStatus`,
  updateAllRangesStatus: `${SYSTEM_SERVER}${ipRangeBaseUrl}/updateAllRangesStatus`,
  queryIpRangeById: `${SYSTEM_SERVER}${ipRangeBaseUrl}/queryIpRangeById`,
  // License
  getLicenseDetail: `${SYSTEM_SERVER}${licenseBaseUrl}/getLicenseDetail`,
  uploadLicense: `zuul/${SYSTEM_SERVER}${licenseBaseUrl}/uploadLicense`,

  // 设施协议http配置
  queryProtocol: `${SYSTEM_SERVER}${systemParamUrl}/queryParam`,
  updateProtocol: `zuul/${SYSTEM_SERVER}${protocolBaseUrl}/updateProtocol`,


  // 系统参数
  updateMassage: `${SYSTEM_SERVER}${systemParameter}/updateMessageNotification`,
  updateMail: `${SYSTEM_SERVER}${systemParameter}/updateMail`,
  updateNote: `${SYSTEM_SERVER}${systemParameter}/updateMessage`,
  updatePush: `${SYSTEM_SERVER}${systemParameter}/updateMobilePush`,
  updateShow: `${SYSTEM_SERVER}${systemParameter}/updateDisplaySettings`,
  updateFtp: `${SYSTEM_SERVER}${systemParameter}/updateFtpSettings`,
  queryLanguageAll: `${SYSTEM_SERVER}${systemParameter}/queryLanguageAll`,
  selectDisplaySettingsParamForPageCollection: `${SYSTEM_SERVER}${systemParameter}/selectDisplaySettingsParamForPageCollection`,
  sendMailTest: `${SYSTEM_SERVER}${systemParameter}/sendMailTest`,
  sendMessageTest: `${SYSTEM_SERVER}${systemParameter}/sendMessageTest`,
  ftpSettingsTest: `${SYSTEM_SERVER}${systemParameter}/ftpSettingsTest`,
  // 关于
  about: `${SYSTEM_SERVER}${aboutBaseUrl}/get`,
  // 导出相关
  exportOperateLogExport: `${LOG_SERVER}${logBaseUrl}/exportOperateLog`,
  exportSysLogExport: `${LOG_SERVER}${logBaseUrl}/exportSysLog`,
  exportSecurityLogExport: `${LOG_SERVER}${logBaseUrl}/exportSecurityLog`,
};
