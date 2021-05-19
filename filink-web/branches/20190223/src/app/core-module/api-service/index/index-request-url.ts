import {DEVICE_SERVER as COMMON_PREFIX} from '../api-common.config';

const AREA_PREFIX = `${COMMON_PREFIX}/areaInfo`;
const FACILITY_PREFIX = `${COMMON_PREFIX}/deviceInfo`;
const MAP_PREFIX = `${COMMON_PREFIX}/deviceMapConfig`;

export const INDEX_URL = {
  // 查询所有设施列表
  GET_FACILITY_LIST_ALL: `${FACILITY_PREFIX}/queryDeviceAreaList`,

  // 查询区域列表
  GET_AREA_LIST_ALL: `${AREA_PREFIX}/queryAreaListAll`,

  // 获取设施类型配置信息
  GET_FACILITY_CONFIG_ALL: `${MAP_PREFIX}/queryDeviceConfigAll`,

  // 修改用户地图设施类型配置的设施类型启用状态
  UPDATE_FACILITY_TYPE_STATUS: `${MAP_PREFIX}/updateDeviceTypeStatusAll`,

  // 修改用户地图设施类型配置的设施图标尺寸
  UPDATE_FACILITY_ICON_SIZE: `${MAP_PREFIX}/updateDeviceIconSize`,
};
