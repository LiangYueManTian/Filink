import { ALARM_CURRENT_SERVER } from '../api-common.config';
import { ALARM_HISTORY_SERVER } from '../api-common.config';
import { ALARM_SET_SERVER, DEVICEINFO } from '../api-common.config';

const ALARM_CURRENT = `${ALARM_CURRENT_SERVER}/alarmCurrent`;

// const ALARM_CURRENT = 'alarmCurrent';
// 查询当前告警列表信息
export const QUERY_ALARM_CURRENT_LIST = `${ALARM_CURRENT}/queryAlarmCurrentList`;
// 查询单个当前告警的信息
export const QUERY_ALARM_CURRENT_INFO_BY_ID = `${ALARM_CURRENT}/queryAlarmCurrentInfoById`;
// 批量修改当前告警备注信息
export const UPDATE_ALARM_REMARK = `${ALARM_CURRENT}/updateAlarmRemark`;
// 批量设置当前告警的告警确认状态
export const UPDATE_ALARM_CONFIRM_STATUS = `${ALARM_CURRENT}/updateAlarmConfirmStatus`;
// 批量设置当前告警的告警清除状态
export const UPDATE_ALARM_CLEAN_STATUS = `${ALARM_CURRENT}/updateAlarmCleanStatus`;
// 查询各级别告警总数
export const QUERY_EVERY_ALARM_COUNT = `${ALARM_CURRENT}/queryEveryAlarmCount`;
// websocket
export const WEBSOCKET = `${ALARM_CURRENT}/websocket`;
// 首页设施详情查看当前告警
export const QUERY_ALARM_DEVICE_ID = `${ALARM_CURRENT}/queryAlarmDeviceId`;

const ALARM_HISTORY = `${ALARM_HISTORY_SERVER}/alarmHistory`;
// const ALARM_HISTORY = 'alarmHistory';
// 查询历史告警列表信息
export const QUERY_ALARM_HISTORY_LIST = `${ALARM_HISTORY}/queryAlarmHistoryList`;
// 查询单个历史告警的信息
export const QUERY_ALARM_HISTORY_INFO_BY_ID = `${ALARM_HISTORY}/queryAlarmHistoryInfoById`;


const ALARM_SET = `${ALARM_SET_SERVER}/alarmSet`;
// const ALARM_SET = 'alarmSet';
// 查询告警级别列表信息
export const QUERY_ALARM_LEVEL_LIST = `${ALARM_SET}/queryAlarmLevelList`;
// 查询当前告警设置列表信息
export const QUERY_ALARM_CURRENT_SET_LIST = `${ALARM_SET}/queryAlarmNameList`;
// 修改告警级别设置信息
export const UPDATE_ALARM_COLOR_AND_SOUND = `${ALARM_SET}/updateAlarmColorAndSound`;
// 告警级别设置
export const UPDATE_ALARM_LEVEL = `${ALARM_SET}/updateAlarmLevel`;

// 新增当前告警设置
export const INSERR_ALARM_CURRENTSET = `${ALARM_SET}/addAlarmName`;
// 修改当前告警设置
export const UPDATE_ALARM_CURRENTSET = `${ALARM_SET}/updateAlarmCurrentSet`;
// 查询单个当前告警设置信息
export const QUERY_ALARM_LEVEL_SET_BY_ID = `${ALARM_SET}/queryAlarmCurrentSetById/`;
// 删除当前告警设置
export const DELETE_ALARM_CURRENTSET = `${ALARM_SET}/deleteAlarmCurrentSet`;
// 查询单个当前告警级别设置信息
export const QUERY_ALARM_LEVEL_BY_ID = `${ALARM_SET}/queryAlarmLevelById/`;
// 查询历史告警设置信息
export const QUERY_ALARM_DELAY = `${ALARM_SET}/selectAlarmDelay`;
// 历史告警设置
export const UPDATE_ALARM_DELAY = `${ALARM_SET}/updateAlarmDelay`;
// 告警提示音选择
export const SELECT_ALARM_ENUM = `${ALARM_SET}/selectAlarmEnum`;
// 查询告警名称
export const QUERY_ALARM_NAME = `${ALARM_SET}/queryAlarmName`;
// 查询告警级别
export const QUERY_ALARM_LEVEL = `${ALARM_SET}/queryAlarmLevel`;

const ALARM_FILTER_RULE = `${ALARM_SET_SERVER}/alarmFilterRule`;
// 查询告警过滤
export const QUERY_ALARM_FILTRATION = `${ALARM_FILTER_RULE}/queryAlarmFilterRuleList`;

// 删除告警过滤
export const DELETE_ALARM_FILTRATION = `${ALARM_FILTER_RULE}/batchDeleteAlarmFilterRule`;

// 查询告警对象信息
export const QUERY_ALARM_OBJECT = `${ALARM_FILTER_RULE}/queryDeviceList`;
// 获取告警过滤对象 告警对象
export const QUERY_ALARM_FILTRATION_OBJ = `${DEVICEINFO}/deviceListByPage`;
