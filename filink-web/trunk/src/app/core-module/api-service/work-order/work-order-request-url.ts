import {DEVICE_SERVER, USER_SERVER, WORK_ORDER_SERVER} from '../api-common.config';

const BASE = `${WORK_ORDER_SERVER}/procBase`;
const CLEAR_BARRIER = `${WORK_ORDER_SERVER}/procClearFailure`;
const INSPECTION = `${WORK_ORDER_SERVER}/procInspection`;
const INSPECTION_TASK = `${WORK_ORDER_SERVER}/inspectionTask`;
const ALARM_SERVER = `${DEVICE_SERVER}`;
/**
 * 工单接口
 */
export const WORK_ORDER_URL = {
  // 获取历史销障工单列表
  GET_HISTORY_CLEAR_BARRIER_WORK_ORDER_LIST_ALL: `${CLEAR_BARRIER}/queryListHistoryClearFailureByPage`,
  // 获取未完成销障工单列表
  GET_UNFINISHED_CLEAR_BARRIER_WORK_ORDER_LIST_ALL: `${CLEAR_BARRIER}/queryListClearFailureProcessByPage`,
  // 新增销障工单
  ADD_CLEAR_BARRIER_WORK_ORDER: `${CLEAR_BARRIER}/addClearFailureProc`,
  // 修改销障工单
  UPDATE_CLEAR_BARRIER_WORK_ORDER: `${CLEAR_BARRIER}/updateClearFailureProcById`,
  // 销账工单列表待指派状态统计
  GET_ASSIGNED_CLEAR_BARRIER_WORK_ORDER_STATISTICS: `${CLEAR_BARRIER}/queryCountClearFailureProcByAssigned`,
  // 销账工单列表待处理状态统计
  GET_PENDING_CLEAR_BARRIER_WORK_ORDER_STATISTICS: `${CLEAR_BARRIER}/queryCountClearFailureProcByPending`,
  // 销账工单列表处理中状态统计
  // GET_PROCESSING_CLEAR_BARRIER_WORK_ORDER_STATISTICS: `${CLEAR_BARRIER}/queryCountClearFailureProcByProcessing`,
  GET_PROCESSING_CLEAR_BARRIER_WORK_ORDER_STATISTICS: `${WORK_ORDER_SERVER}/procStatistical/queryListProcOverviewGroupByProcStatus`,
  // 销账工单列表今日新增统计
  GET_INCREASE_CLEAR_BARRIER_WORK_ORDER_STATISTICS: `${WORK_ORDER_SERVER}/procStatistical/queryNowDateAddOrderCount`,
  // 销账工单未完工列表状态总数统计
  GET_UNFINISHED_CLEAR_BARRIER_WORK_ORDER_STATISTICS: `${CLEAR_BARRIER}/queryCountListProcUnfinishedProcStatus`,
  // 故障原因统计的销障工单信息
  GET_CLEAR_BARRIER_WORK_ORDER_STATISTICS_BY_ERROR_REASON: `${WORK_ORDER_SERVER}/procStatistical/queryListProcOverviewGroupByProcErrorReason`,
  // 处理方案统计的销障工单信息
  GET_CLEAR_BARRIER_WORK_ORDER_STATISTICS_BY_PROCESSING_SCHEME: `${WORK_ORDER_SERVER}/procStatistical/queryListProcOverviewGroupByProcProcessingScheme`,
  // 设施类型统计的销障工单信息
  GET_CLEAR_BARRIER_WORK_ORDER_STATISTICS_BY_DEVICE_TYPE: `${WORK_ORDER_SERVER}/procStatistical/queryListProcOverviewGroupByProcDeviceType`,
  // 工单状态统计的销障工单信息
  GET_CLEAR_BARRIER_WORK_ORDER_STATISTICS_BY_STATUS: `${WORK_ORDER_SERVER}/procStatistical/queryListProcOverviewGroupByProcStatus`,
  // 销账工单历史列表总数统计
  GET_HISTORY_CLEAR_BARRIER_WORK_ORDER_STATISTICS: `${CLEAR_BARRIER}/queryCountListProcHisProc`,
  // 校验销障工单名是否存在
  CHECK_CLEAR_BARRIER_WORK_ORDER_NAME_EXIST: `${BASE}/queryTitleIsExists`,
  // 获取销障工单详情
  GET_CLEAR_BARRIER_WORK_ORDER_BY_ID: `${BASE}/getProcessByProcId`,
  // 删除销障工单
  DELETE_CLEAR_BARRIER_WORK_ORDER: `${BASE}/deleteProc`,
  // 退单
  SEND_BACK_CLEAR_BARRIER_WORK_ORDER: `${BASE}/chargeProc`,
  // 撤回工单
  REVOKE_CLEAR_BARRIER_WORK_ORDER: `${BASE}/revokeProc`,
  // 指派工单
  ASSIGN_CLEAR_BARRIER_WORK_ORDER: `${BASE}/assignProc`,
  // 退单确认
  SINGLE_BACK_CONFIRM: `${BASE}/checkSingleBack`,
  // 查询责任单位下是否有工单
  EXISTS_WORK_ORDER_FOR_DEPT_IDS: `${BASE}/existsProcForDeptIdsAndAreaId`,
  // 导出未完工销障工单
  EXPORT_UNFINISHED_CLEAR_BARRIER_WORK_ORDER: `${CLEAR_BARRIER}/exportClearFailureProcUnfinished`,
  // 导出历史销障工单
  EXPORT_HISTORY_CLEAR_BARRIER_WORK_ORDER: `${CLEAR_BARRIER}/exportClearFailureProcHistory`,
  // 首页设施销障工单
  GET_FIVE_CLEAR_BARRIER: `${CLEAR_BARRIER}/queryClearFailureProcTopFive`,
  // 退单重新生成
  REFUND_GENERATED_AGAIN: `${CLEAR_BARRIER}/regenerateClearFailureProc`,

  // 新增巡检任务列表
  GET_INSPECTION_WORK_ORDER_LIST_ALL: `${INSPECTION_TASK}/queryListInspectionTaskByPage`,
  // 新增巡检任务路径
  ADD_INSPECTION_WORK_ORDER: `${INSPECTION_TASK}/insertInspectionTask`,
  // 删除巡检任务
  DELETE_INSPECTION_WORK_ORDER: `${INSPECTION_TASK}/deleteInspectionTaskByIds`,
  // 编辑巡检任务
  UPDATE_INSPECTION_WORK_ORDER: `${INSPECTION_TASK}/updateInspectionTask`,
  // 查询巡检任务接口
  INQUIRE_INSPECTION_WORK_ORDER: `${INSPECTION_TASK}/getInspectionTaskById`,
  // 启用巡检任务状态
  ENABLE_INSPECTION_TASKS: `${INSPECTION_TASK}/openInspectionTaskBatch`,
  // 停用巡检任务状态
  DISABLE_INSPECTION_TASKS: `${INSPECTION_TASK}/closeInspectionTaskBatch`,
  // 巡检任务关联工单
  ASSOCIATED_WORK_ORDER: `${WORK_ORDER_SERVER}/procInspection/queryListInspectionTaskRelationProcByPage`,
  // 巡检任务关联巡检设施
  INSPECTION_FACILITY: `${INSPECTION_TASK}/inspectionTaskRelationDeviceList`,
  // 巡检任务导出接口
  EXPORT_INSPECTION_TASK: `${INSPECTION_TASK}/exportInspectionTask`,
  // 巡检任务名称校验接口
  QUERY_INSPECTION_TASK_IS_EXISTS: `${INSPECTION_TASK}/queryInspectionTaskIsExists`,
  // 未完工巡检工单列表
  GET_INSPECTION_WORK_UNFINISHED_LIST_ALL: `${WORK_ORDER_SERVER}/procInspection/queryListInspectionProcessByPage`,
  // 新增巡检工单
  ADD_INSPECTION_WORK_UNFINISHED: `${WORK_ORDER_SERVER}/procInspection/addInspectionProc`,
  // 编辑巡检工单
  UPDATE_INSPECTION_WORK_UNFINISHED: `${WORK_ORDER_SERVER}/procInspection/updateInspectionProc`,
  // 编辑巡检工单后台返回数据
  GET_UPDATE_INSPECTION_WORK_UNFINISHED_LIST: `${WORK_ORDER_SERVER}/procInspection/getInspectionProcById`,
  // 删除未完成巡检工单
  DELETE_INSPECTION_WORK_UNFINISHED: `${WORK_ORDER_SERVER}/procBase/deleteProc`,
  // 已完成巡检信息列表
  GET_INSPECTION_COMPLETE_UNFINISHED_LIST: `${WORK_ORDER_SERVER}/procInspection/queryListCompleteInspectionByPage`,
  // 巡检完工记录列表
  GET_INSPECTION_WORK_FINISHED_LIST_ALL: `${WORK_ORDER_SERVER}//procInspection/queryListInspectionCompleteRecordByPage`,
  // 退单确认
  SINGLE_BACK_TO_CONFIRM_UNFINISHED: `${WORK_ORDER_SERVER}/procBase/checkSingleBack`,
  // 指派
  ASSIGNED_UNFINISHED: `${WORK_ORDER_SERVER}/procBase/assignProc`,
  // 重新生成
  INSPECTION_WORK_UNFINISHED_REGENERATE: `${WORK_ORDER_SERVER}/procInspection/regenerateInspectionProc`,
  // 未完工工单撤回
  UNFINISHED_WORK_ORDER_WITHDRAWAL: `${WORK_ORDER_SERVER}/procBase/revokeProc`,
  // 未完工导出
  UNFINISHED_EXPORT: `${WORK_ORDER_SERVER}/procInspection/exportListInspectionProcess`,
  // 完工记录导出接口
  COMPLETION_RECORD_EXPORT: `${WORK_ORDER_SERVER}/procInspection/exportListInspectionComplete`,
  // 首页设施巡检工单
  GET_FIVE_INSPECTION: `${INSPECTION}/queryProcInspectionTopFive`,
  // 根据区域ID查询责任单位
  QUERY_RESPONSIBILITY_UNIT: `${USER_SERVER}/department/queryAllDepartmentForPageSelection`,
  // 告警转工单责任单位查询
  ALARM_QUERY_RESPONSIBILITY_UNIT: `${ALARM_SERVER}/areaInfo/getCommonDeptByAreaId`,
  // 查询已巡检数量和未巡检数量
  QUERY_PROC_INSPECTION_BY_PROC_INSPECTION: `${WORK_ORDER_SERVER}/procInspection/queryProcInspectionByProcInspectionId`,
  // 查询所有用户
  QUERY_ALL_USER_INFO: `${USER_SERVER}/user/queryAllUserInfo`,
};
