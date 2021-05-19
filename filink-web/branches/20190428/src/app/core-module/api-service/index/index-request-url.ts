import {DEVICE_SERVER} from '../api-common.config';

const AREA_PREFIX = `${DEVICE_SERVER}/areaInfo`;
const FACILITY_PREFIX = `${DEVICE_SERVER}/deviceInfo`;
const MAP_PREFIX = `${DEVICE_SERVER}/deviceMapConfig`;
const COLLECTION_PREFIX = `${DEVICE_SERVER}/deviceCollecting`;

export const INDEX_URL = {
  // 查询所有设施列表
  GET_FACILITY_LIST_ALL: `${FACILITY_PREFIX}/queryDeviceAreaList`,

  // 查询所有设施列表(基本数据，key值简化)
  GET_FACILITY_LIST_ALL_BASE: `${FACILITY_PREFIX}/queryDeviceAreaListBase`,

  // 查询所有设施列表(key值简化)
  GET_FACILITY_LIST_ALL_SIMPLE: `${FACILITY_PREFIX}/queryDeviceAreaListSimple`,

  // 查询区域列表
  GET_AREA_LIST_ALL: `${AREA_PREFIX}/queryAreaListAll`,

  // 查询当前用户区域列表
  GET_AREA_LIST_FOR_CURRENT_USER: `${AREA_PREFIX}/queryAreaListForPageSelection`,

  // 获取设施类型配置信息
  GET_FACILITY_CONFIG_ALL: `${MAP_PREFIX}/queryDeviceConfigAll`,

  // 修改用户地图设施类型配置的设施类型启用状态
  UPDATE_FACILITY_TYPE_STATUS: `${MAP_PREFIX}/updateDeviceTypeStatusAll`,

  // 修改用户地图设施类型配置的设施图标尺寸
  UPDATE_FACILITY_ICON_SIZE: `${MAP_PREFIX}/updateDeviceIconSize`,

  // 收藏设施
  COLLECT_FACILITY: `${COLLECTION_PREFIX}/focus`,

  // 取消收藏设施
  CANCEL_COLLECT_FACILITY: `${COLLECTION_PREFIX}/unFollow`,

  // 获取收藏设施统计
  GET_COLLECT_FACILITY_LIST_STATISTICS: `${COLLECTION_PREFIX}/count`,

  // 获取收藏设施列表
  GET_COLLECT_FACILITY_LIST: `${COLLECTION_PREFIX}/attentionListByPage`,

  // 获取所有收藏的设施
  GET_COLLECT_FACILITY_LIST_ALL: `${COLLECTION_PREFIX}/attentionList`,
};
