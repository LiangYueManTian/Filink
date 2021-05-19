/**
 * Created by wh1709040 on 2019/1/7.
 */
import {COMMON_PREFIX, DEVICE_SERVER } from '../api-common.config';

// 获取区域列表

const AREA_INFO = `${COMMON_PREFIX}${DEVICE_SERVER}/areaInfo`;
const DEVICE_PREFIX = `${COMMON_PREFIX}${DEVICE_SERVER}/deviceInfo`;
const DEVICE_CONFIG_PREFIX = `${COMMON_PREFIX}${DEVICE_SERVER}/deviceConfig`;
const DEVICE_LOG = `${COMMON_PREFIX}${DEVICE_SERVER}/deviceLog`;

export const GET_AREA_LIST = `${AREA_INFO}/areaListByPage`;
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
export const QUERY_DEVICE_NAME_IS_EXSIT = `${DEVICE_PREFIX}/queryDeviceNameIsExsit`;
// 查询设施日志
export const DEVICE_LOG_LIST_BY_PAGE = `${DEVICE_LOG}/deviceLogListByPage`;
// 获取详情页的code码
export const GET_DETAIL_CODE = `${DEVICE_CONFIG_PREFIX}/getDetailCode`;
// 获取配置策略配置项
export const GET_PRAMS_CONFIG = `${DEVICE_CONFIG_PREFIX}/getPramsConfig`;

