package com.fiberhome.filink.workflowbusinessserver.dao.statistical;

import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.workflowbusinessserver.bean.statistical.normal.*;
import com.fiberhome.filink.workflowbusinessserver.bean.statistical.overview.*;
import com.fiberhome.filink.workflowbusinessserver.req.statistical.normal.QueryProcCountSumReq;

import java.util.List;

/**
 * 工单导出dao
 * @author hedongwei@wistronits.com
 * @date 2019/5/28 19:20
 */
public interface ProcStatisticalDao {

    /**
     * 巡检工单状态统计信息
     * @author hedongwei@wistronits.com
     * @date  2019/5/28 19:29
     * @param queryCondition 巡检工单状态统计条件
     * @return 巡检状态统计的数据
     */
    List<ProcStatusStatistical> queryListProcInspectionGroupByProcStatus(QueryCondition queryCondition);

    /**
     * 销障工单状态统计信息
     * @author hedongwei@wistronits.com
     * @date  2019/5/28 19:29
     * @param queryCondition 销障工单状态统计条件
     * @return 销障工单状态统计的数据
     */
    List<ProcStatusStatistical> queryListProcClearGroupByProcStatus(QueryCondition queryCondition);

    /**
     * 查询工单状态统计信息
     * @author hedongwei@wistronits.com
     * @date  2019/5/28 19:29
     * @param queryCondition 查询工单状态统计条件
     * @return 工单状态统计的数据
     */
    List<ProcStatusStatistical> queryListProcGroupByProcStatus(QueryCondition queryCondition);


    /**
     * 当天新增巡检工单数量
     * @author hedongwei@wistronits.com
     * @date  2019/6/4 16:01
     * @param queryCondition 当天新增巡检工单条件
     * @return 当天新增巡检工单数量
     */
    List<ProcNowDateAddOrderCountOverview> queryNowDateInspectionAddOrderCount(QueryCondition queryCondition);


    /**
     * 当天新增销障工单数量
     * @author hedongwei@wistronits.com
     * @date  2019/6/4 16:01
     * @param queryCondition 查询当天新增销障工单条件
     * @return 当天新增销障工单数量
     */
    List<ProcNowDateAddOrderCountOverview> queryNowDateClearAddOrderCount(QueryCondition queryCondition);

    /**
     * 查询当天新增工单数量
     * @author hedongwei@wistronits.com
     * @date  2019/6/4 16:01
     * @param queryCondition 查询当天新增工单条件
     * @return 当天新增工单数量
     */
    List<ProcNowDateAddOrderCountOverview> queryNowDateAddOrderCount(QueryCondition queryCondition);


    /**
     * 巡检工单状态概览统计信息
     * @author hedongwei@wistronits.com
     * @date  2019/5/28 19:29
     * @param queryCondition 巡检工单状态概览统计条件
     * @return 巡检工单状态概览统计的数据
     */
    List<ProcStatusOverviewStatistical> queryListProcInspectionOverviewGroupByProcStatus(QueryCondition queryCondition);

    /**
     * 销障工单状态概览统计信息
     * @author hedongwei@wistronits.com
     * @date  2019/5/28 19:29
     * @param queryCondition 销障工单状态概览统计条件
     * @return 销障工单状态概览统计的数据
     */
    List<ProcStatusOverviewStatistical> queryListProcClearOverviewGroupByProcStatus(QueryCondition queryCondition);

    /**
     * 查询工单状态概览统计信息
     * @author hedongwei@wistronits.com
     * @date  2019/5/28 19:29
     * @param queryCondition 查询工单状态概览统计条件
     * @return 工单状态概览统计的数据
     */
    List<ProcStatusOverviewStatistical> queryListProcOverviewGroupByProcStatus(QueryCondition queryCondition);

    /**
     * 巡检工单处理方案统计信息
     * @author hedongwei@wistronits.com
     * @date  2019/5/28 19:29
     * @param queryCondition 巡检工单处理方案统计
     * @return 巡检工单处理方案统计的数据
     */
    List<ProcProcessingSchemeStatistical> queryListProcInspectionGroupByProcProcessingScheme(QueryCondition queryCondition);

    /**
     * 销障工单处理方案统计信息
     * @author hedongwei@wistronits.com
     * @date  2019/5/28 19:29
     * @param queryCondition 销障工单处理方案统计
     * @return 销障工单处理方案统计的数据
     */
    List<ProcProcessingSchemeStatistical> queryListProcClearGroupByProcProcessingScheme(QueryCondition queryCondition);

    /**
     * 查询处理方案统计信息
     * @author hedongwei@wistronits.com
     * @date  2019/5/28 19:29
     * @param queryCondition 查询工单处理方案统计
     * @return 工单处理方案统计的数据
     */
    List<ProcProcessingSchemeStatistical> queryListProcGroupByProcProcessingScheme(QueryCondition queryCondition);

    /**
     * 巡检处理方案概览统计信息
     * @author hedongwei@wistronits.com
     * @date  2019/5/28 19:29
     * @param queryCondition 巡检工单处理方案概览统计
     * @return 巡检工单处理方案概览统计的数据
     */
    List<ProcProcessingSchemeOverviewStatistical> queryListProcInspectionOverviewGroupByProcProcessingScheme(QueryCondition queryCondition);


    /**
     * 查询处理方案概览统计信息
     * @author hedongwei@wistronits.com
     * @date  2019/5/28 19:29
     * @param queryCondition 查询工单处理方案概览统计
     * @return 工单处理方案概览统计的数据
     */
    List<ProcProcessingSchemeOverviewStatistical> queryListProcClearOverviewGroupByProcProcessingScheme(QueryCondition queryCondition);

    /**
     * 查询处理方案概览统计信息
     * @author hedongwei@wistronits.com
     * @date  2019/5/28 19:29
     * @param queryCondition 查询工单处理方案概览统计
     * @return 工单处理方案概览统计的数据
     */
    List<ProcProcessingSchemeOverviewStatistical> queryListProcOverviewGroupByProcProcessingScheme(QueryCondition queryCondition);


    /**
     * 巡检故障原因统计信息
     * @author hedongwei@wistronits.com
     * @date  2019/5/28 19:29
     * @param queryCondition 巡检工单故障原因统计
     * @return 巡检工单故障原因统计的数据
     */
    List<ProcErrorReasonStatistical> queryListProcInspectionGroupByErrorReason(QueryCondition queryCondition);

    /**
     * 销障故障原因统计信息
     * @author hedongwei@wistronits.com
     * @date  2019/5/28 19:29
     * @param queryCondition 销障工单故障原因统计
     * @return 销障工单故障原因统计的数据
     */
    List<ProcErrorReasonStatistical> queryListProcClearGroupByErrorReason(QueryCondition queryCondition);

    /**
     * 查询故障原因统计信息
     * @author hedongwei@wistronits.com
     * @date  2019/5/28 19:29
     * @param queryCondition 查询工单故障原因统计
     * @return 工单故障原因统计的数据
     */
    List<ProcErrorReasonStatistical> queryListProcGroupByErrorReason(QueryCondition queryCondition);

    /**
     * 巡检故障原因概览统计
     * @author hedongwei@wistronits.com
     * @date  2019/6/4 21:48
     * @param queryCondition 巡检故障原因概览统计参数
     * @return 巡检故障原因概览统计结果
     */
    List<ProcErrorReasonOverviewStatistical> queryListProcInspectionOverviewGroupByErrorReason(QueryCondition queryCondition);


    /**
     * 销障故障原因概览统计
     * @author hedongwei@wistronits.com
     * @date  2019/6/4 21:48
     * @param queryCondition 销障故障原因概览统计参数
     * @return 销障故障原因概览统计结果
     */
    List<ProcErrorReasonOverviewStatistical> queryListProcClearOverviewGroupByErrorReason(QueryCondition queryCondition);

    /**
     * 故障原因概览统计
     * @author hedongwei@wistronits.com
     * @date  2019/6/4 21:48
     * @param queryCondition 故障原因概览统计参数
     * @return 故障原因概览统计结果
     */
    List<ProcErrorReasonOverviewStatistical> queryListProcOverviewGroupByErrorReason(QueryCondition queryCondition);

    /**
     * 查询区域巡检工单比统计
     * @author hedongwei@wistronits.com
     * @date  2019/5/29 9:46
     * @param queryCondition 查询区域巡检工单比统计条件
     * @return 区域巡检工单比统计
     */
    List<ProcAreaPercentStatistical> queryListProcInspectionGroupByAreaPercent(QueryCondition queryCondition);


    /**
     * 查询区域销障工单比统计
     * @author hedongwei@wistronits.com
     * @date  2019/5/29 9:46
     * @param queryCondition 查询区域销障工单比统计条件
     * @return 区域销障工单比统计
     */
    List<ProcAreaPercentStatistical> queryListProcClearGroupByAreaPercent(QueryCondition queryCondition);

    /**
     * 查询区域工单比统计
     * @author hedongwei@wistronits.com
     * @date  2019/5/29 9:46
     * @param queryCondition 查询区域工单比统计条件
     * @return 区域工单比统计
     */
    List<ProcAreaPercentStatistical> queryListProcGroupByAreaPercent(QueryCondition queryCondition);


    /**
     * 查询巡检设施类型分组统计
     * @author hedongwei@wistronits.com
     * @date  2019/5/31 15:22
     * @param queryCondition 巡检设施类型分组统计条件
     * @return 巡检设施类型分组统计
     */
    List<ProcDeviceTypeStatistical> queryListProcInspectionGroupByDeviceType(QueryCondition queryCondition);


    /**
     * 查询销障设施类型分组统计
     * @author hedongwei@wistronits.com
     * @date  2019/5/31 15:22
     * @param queryCondition 销障设施类型分组统计条件
     * @return 销障设施类型分组统计
     */
    List<ProcDeviceTypeStatistical> queryListProcClearGroupByDeviceType(QueryCondition queryCondition);

    /**
     * 查询设施类型分组统计
     * @author hedongwei@wistronits.com
     * @date  2019/5/31 15:22
     * @param queryCondition 设施类型分组统计条件
     * @return 设施类型分组统计
     */
    List<ProcDeviceTypeStatistical> queryListProcGroupByDeviceType(QueryCondition queryCondition);

    /**
     * 查询巡检设施类型概览统计
     * @author hedongwei@wistronits.com
     * @date  2019/5/31 15:22
     * @param queryCondition 巡检设施类型概览统计条件
     * @return 巡检设施类型概览统计
     */
    List<ProcDeviceTypeOverviewStatistical> queryListProcInspectionOverviewGroupByDeviceTypeList(QueryCondition queryCondition);


    /**
     * 查询销障设施类型概览统计
     * @author hedongwei@wistronits.com
     * @date  2019/5/31 15:22
     * @param queryCondition 销障设施类型概览统计条件
     * @return 销障设施类型概览统计
     */
    List<ProcDeviceTypeOverviewStatistical> queryListProcClearOverviewGroupByDeviceTypeList(QueryCondition queryCondition);


    /**
     * 查询设施类型概览统计
     * @author hedongwei@wistronits.com
     * @date  2019/5/31 15:22
     * @param queryCondition 设施类型概览统计条件
     * @return 设施类型概览统计
     */
    List<ProcDeviceTypeOverviewStatistical> queryListProcOverviewGroupByDeviceTypeList(QueryCondition queryCondition);


    /**
     * 查询巡检责任单位统计信息
     * @author hedongwei@wistronits.com
     * @date  2019/5/28 19:29
     * @param queryCondition 查询巡检工单责任单位统计条件
     * @return 巡检工单责任单位统计的数据
     */
    List<ProcDepartmentStatistical> queryInspectionDeptListGroupByAccountabilityDept(QueryCondition queryCondition);


    /**
     * 查询销障责任单位统计信息
     * @author hedongwei@wistronits.com
     * @date  2019/5/28 19:29
     * @param queryCondition 查询销障工单责任单位统计条件
     * @return 销障工单责任单位统计的数据
     */
    List<ProcDepartmentStatistical> queryClearDeptListGroupByAccountabilityDept(QueryCondition queryCondition);

    /**
     * 查询责任单位统计信息
     * @author hedongwei@wistronits.com
     * @date  2019/5/28 19:29
     * @param queryCondition 查询工单责任单位统计条件
     * @return 工单责任单位统计的数据
     */
    List<ProcDepartmentStatistical> queryDeptListGroupByAccountabilityDept(QueryCondition queryCondition);

    /**
     * 根据巡检设施分组查询设施数量
     * @author hedongwei@wistronits.com
     * @date  2019/6/3 10:03
     * @param queryCondition 查询条件
     * @return 巡检设施数量
     */
    List<ProcDeviceTopStatistical> queryInspectionListDeviceCountGroupByDevice(QueryCondition queryCondition);

    /**
     * 根据销障设施分组查询设施数量
     * @author hedongwei@wistronits.com
     * @date  2019/6/3 10:03
     * @param queryCondition 查询条件
     * @return 销障设施数量
     */
    List<ProcDeviceTopStatistical> queryClearListDeviceCountGroupByDevice(QueryCondition queryCondition);

    /**
     * 根据设施分组查询设施数量
     * @author hedongwei@wistronits.com
     * @date  2019/6/3 10:03
     * @param queryCondition 查询条件
     * @return 设施数量
     */
    List<ProcDeviceTopStatistical> queryListDeviceCountGroupByDevice(QueryCondition queryCondition);

    /**
     * 查询当天统计巡检工单的增量数量
     * @author hedongwei@wistronits.com
     * @date  2019/6/5 21:17
     * @param req 查询当天统计巡检工单的筛选条件
     * @return  查询当天统计巡检工单的增量数量
     */
    List<ProcInfoDateStatistical> queryProcInspectionListByNowDate(QueryProcCountSumReq req);

    /**
     * 查询当天统计销障工单的增量数量
     * @author hedongwei@wistronits.com
     * @date  2019/6/5 21:17
     * @param req 查询当天统计销障工单的筛选条件
     * @return  查询当天统计销障工单的增量数量
     */
    List<ProcInfoDateStatistical> queryProcCLearListByNowDate(QueryProcCountSumReq req);


    /**
     * 查询当天统计工单的增量数量
     * @author hedongwei@wistronits.com
     * @date  2019/6/5 21:17
     * @param req 查询当天统计的筛选条件
     * @return  查询当天统计工单的增量数量
     */
    List<ProcInfoDateStatistical> queryProcListByNowDate(QueryProcCountSumReq req);

}
