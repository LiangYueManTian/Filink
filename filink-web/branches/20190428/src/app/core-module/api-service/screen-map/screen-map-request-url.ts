import {MPA_SERVER} from '../api-common.config';

// 查询地区信息
export const GET_AREA = `${MPA_SERVER}/map/getSonArea`;

// 查询所有的省
export const GET_ALL_PROVINCE = `${MPA_SERVER}/map/queryAllProvince`;

// 获取所有的城市信息
export const GET_ALL_CITYINFO = `${MPA_SERVER}/map/queryAllCityInfo`;
