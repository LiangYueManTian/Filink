/**
 * Created by wh1709040 on 2019/1/7.
 */
import {ALARM_CURRENT_SERVER, COMMON_PREFIX, DEVICE_SERVER, USER_SERVER, WORK_ORDER_SERVER} from '../api-common.config';
// import {COMMON_PREFIX, DEVICE_SERVER, DEVICE_PICTURE} from '../api-common.config';

// 获取区域列表
const USER = `${USER_SERVER}/user`;
const AREA_INFO = `${COMMON_PREFIX}${DEVICE_SERVER}/areaInfo`;
const DEVICE_PREFIX = `${COMMON_PREFIX}${DEVICE_SERVER}/deviceInfo`;
const PARTS_PREFIX = `${COMMON_PREFIX}${DEVICE_SERVER}/partInfo`;
const DEVICE_CONFIG_PREFIX = `${COMMON_PREFIX}${DEVICE_SERVER}/deviceConfig`;
const DEVICE_LOG = `${COMMON_PREFIX}${DEVICE_SERVER}/deviceLog`;
const PICTURE_VIEW = `${COMMON_PREFIX}${DEVICE_SERVER}/picRelationInfo`;

export const GET_AREA_LIST = `${AREA_INFO}/areaListByPage`;
export const SELECT_FOREIGN_AREA_INFO = `${AREA_INFO}/selectForeignAreaInfo`;
export const SELECT_FOREIGN_AREA_INFO_FOR_PAGE_COLLECTION = `${AREA_INFO}/selectForeignAreaInfoForPageSelection`;
// 新增区域信息
export const ADD_AREA = `${AREA_INFO}/addArea`;
// 修改区域信息
export const UPDATE_AREA_BY_ID = `${AREA_INFO}/updateAreaById`;
// 删除区域信息
export const DELETE_AREA_BY_IDS = `${AREA_INFO}/deleteAreaByIds`;
// 查看区域详情信息
export const QUERY_AREA_BY_ID = `${AREA_INFO}/queryAreaById`;
// 查询区域是否可以更改
export const QUERY_NAME_CAN_CHANGE = `${AREA_INFO}/queryAreaDetailsCanChange`;
// 关联设施信息
export const SET_AREA_DEVICE = `${AREA_INFO}/setAreaDevice`;
// 查询区域名称是否存在
export const QUERY_AREA_NAME_IS_EXIST = `${AREA_INFO}/queryAreaNameIsExist`;
// 导出区域列表数据
export const EXPORT_AREA_DATA = `${AREA_INFO}/exportData`;
// 根据部门id查区域信息
export const QUERY_AREA_BY_DEPT_ID = `${AREA_INFO}/selectAreaInfoByDeptIdsForView`;

// 查询设施列表
export const DEVICE_LIST_BY_PAGE = `${DEVICE_PREFIX}/deviceListByPage`;
// 新增设施信息
export const ADD_DEVICE = `${DEVICE_PREFIX}/addDevice`;
// 删除设施
export const DELETE_DEVICE_BY_IDS = `${DEVICE_PREFIX}/deleteDeviceByIds`;
// 修改设施信息
export const UPDATE_DEVICE_BY_ID = `${DEVICE_PREFIX}/updateDeviceById`;
// 查询设施详情是否可以修改
export const DEVICE_CAN_CHANGE_DETAIL = `${DEVICE_PREFIX}/deviceCanChangeDetail`;
// 查看设施详情
export const FIND_DEVICE_BY_ID = `${DEVICE_PREFIX}/findDeviceById`;
// 获取设施配置策略
export const GET_DEVICE_STRATEGY = `${DEVICE_PREFIX}/getDeviceStrategy`;
// 设置设施配置策略
export const SET_DEVICE_STRATEGY = `${DEVICE_PREFIX}/setDeviceStrategy`;
// 查询设施名称是否存在
export const QUERY_DEVICE_NAME_IS_EXIST = `${DEVICE_PREFIX}/queryDeviceNameIsExist`;
// 查询设施日志
export const DEVICE_LOG_LIST_BY_PAGE = `${DEVICE_LOG}/deviceLogListByPage`;
// 最后一条设施日志的时间
export const DEVICE_LOG_TIME = `${DEVICE_LOG}/queryRecentDeviceLogTime`;
// 获取详情页的code码
export const GET_DETAIL_CODE = `${DEVICE_CONFIG_PREFIX}/getDetailCode`;
// 获取配置策略配置项
export const GET_PRAMS_CONFIG = `${DEVICE_CONFIG_PREFIX}/getPramsConfig`;
// 导出设施列表数据
export const EXPORT_DEVICE_LIST = `${DEVICE_PREFIX}/exportDeviceList`;
// 查询设施心跳时间
export const QUERY_HEARTBEAT_TIME = `${DEVICE_LOG}/queryRecentDeviceLogTime`;
// 导出设施日志列表
export const EXPORT_LOG_LIST = `${DEVICE_LOG}/exportDeviceLogList`;

// 设施图片
export const PIC_RELATION_INFO = `${PICTURE_VIEW}/getPicInfoByDeviceId`;

// 获取配件列表
export const PARTS_LIST_PAGE = `${PARTS_PREFIX}/partListByPage`;
// 添加配件
export const ADD_PARTS = `${PARTS_PREFIX}/addPart`;
// 异步校验配件名称
export const PART_NAME_XSI = `${PARTS_PREFIX}/queryPartNameIsExisted`;
// 删除配件
export const DElETE_PARTS = `${PARTS_PREFIX}/deletePartByIds`;
// 查询单个配件详情
export const FIND_PART_BY_ID = `${PARTS_PREFIX}/findPartById`;
// 修改单个配件
export const UPDATE_PARTS = `${PARTS_PREFIX}/updatePartById`;
// 导出配件列表
export const EXPORT_PARTS = `${PARTS_PREFIX}/exportPartList`;
// 根单位查询所属人域查询
export const QUERY_USER_BY_DEPT = `${USER}/queryUserByDept`;
// 图片查看相关httpUrl
export const pictureViewHttpUrl = {
  imageListByPage: `${PICTURE_VIEW}/imageListByPage`,
  deleteImageIsDeletedByIds: `${PICTURE_VIEW}/deleteImageIsDeletedByIds`,
  batchDownLoadImages: `${PICTURE_VIEW}/batchDownLoadImages`,
  getPicUrlByAlarmIdAndDeviceId: `${PICTURE_VIEW}/getPicUrlByAlarmIdAndDeviceId`,
  getPicUrlByAlarmId: `${PICTURE_VIEW}/getPicUrlByAlarmId`,
  getProcessByProcId: `${WORK_ORDER_SERVER}/procBase/getProcTypeByProcId/`,
  queryIsStatus: `${ALARM_CURRENT_SERVER}/alarmCurrent/queryIsStatus/`
};


