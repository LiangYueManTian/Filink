package com.fiberhome.filink.workflowbusinessserver.service.statistical;

import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.workflowbusinessserver.bean.statistical.export.*;
import com.fiberhome.filink.workflowbusinessserver.req.statistical.normal.*;
import com.fiberhome.filink.workflowbusinessserver.req.statistical.overview.*;

/**
 * 工单统计逻辑类
 * @author hedongwei@wistronits.com
 * @date 2019/5/28 9:52
 */
public interface ProcStatisticalService {

    /**
     * 查询工单状态统计信息
     * @author hedongwei@wistronits.com
     * @date  2019/5/28 19:14
     * @param req 工单状态统计参数
     * @return 工单统计结果
     */
    Result queryListProcGroupByProcStatus(QueryCondition<QueryListProcGroupByProcStatusReq> req);

    /**
     * 查询工单状态概览
     * @author hedongwei@wistronits.com
     * @date  2019/5/30 14:03
     * @param req 工单状态概览参数
     * @return 工单状态概览数据
     */
    Result queryListProcOverviewGroupByProcStatus(QueryCondition<QueryListProcOverviewGroupByProcStatusReq> req);

    /**
     * 查询当前日期新增工单数量
     * @author hedongwei@wistronits.com
     * @date  2019/6/4 15:19
     * @param req  查询当前时间新增工单数量
     * @return 当前日期新增工单数量
     */
    Result queryNowDateAddOrderCount(QueryCondition<QueryNowDateAddOrderCountReq> req);


    /**
     * 查询工单处理方案统计信息
     * @author hedongwei@wistronits.com
     * @date  2019/5/28 19:14
     * @param req 工单处理方案统计参数
     * @return 工单处理方案结果
     */
    Result queryListProcGroupByProcProcessingScheme(QueryCondition<QueryListProcGroupByProcProcessingSchemeReq> req);

    /**
     * 查询处理方案概览统计
     * @author hedongwei@wistronits.com
     * @date  2019/6/4 19:11
     * @param req 处理方案概览参数
     * @return 查询处理方案
     */
    Result queryListProcOverviewGroupByProcProcessingScheme(QueryCondition<QueryListProcOverviewGroupByProcessingSchemeReq> req);

    /**
     * 查询工单故障原因统计信息
     * @author hedongwei@wistronits.com
     * @date  2019/5/30 21:06
     * @param req 查询工单故障原因统计参数
     * @return 工单故障原因统计信息
     */
    Result queryListProcGroupByErrorReason(QueryCondition<QueryListProcGroupByProcErrorReasonReq> req);

    /**
     * 故障原因概览统计
     * @author hedongwei@wistronits.com
     * @date  2019/6/4 21:48
     * @param req 故障原因概览统计参数
     * @return 故障原因概览统计结果
     */
    Result queryListProcOverviewGroupByProcErrorReason(QueryCondition<QueryListProcOverviewGroupByErrorReasonReq> req);

    /**
     * 查询工单设施类型信息
     * @author hedongwei@wistronits.com
     * @date  2019/5/31 14:36
     * @param req 工单设施类型统计参数
     * @return 工单设施类型统计
     */
    Result queryListProcGroupByDeviceType(QueryCondition<QueryListProcGroupByProcDeviceTypeReq> req);


    /**
     * 设施类型概览统计
     * @author hedongwei@wistronits.com
     * @date  2019/6/4 21:48
     * @param req 设施类型概览统计参数
     * @return 设施类型概览统计结果
     */
    Result queryListProcOverviewGroupByProcDeviceType(QueryCondition<QueryListProcOverviewGroupByDeviceTypeReq> req);

    /**
     * 查询工单区域比信息
     * @author hedongwei@wistronits.com
     * @date  2019/5/31 14:36
     * @param req 工单区域比参数
     * @return 工单区域比统计
     */
    Result queryListProcGroupByAreaPercent(QueryCondition<QueryListProcGroupByProcAreaPercentReq> req);

    /**
     * 查询部门分组集合
     * @author hedongwei@wistronits.com
     * @date  2019/6/3 15:12
     * @param req 查询部门参数
     * @return 查询部门分组集合
     */
    Result queryDeptListGroupByAccountabilityDept(QueryCondition<QueryDeptListGroupByAccountabilityDeptReq> req);

    /**
     * 根据设施分组查询设施数量
     * @author hedongwei@wistronits.com
     * @date  2019/6/3 10:07
     * @param req 筛选参数
     * @return 设施数量
     */
    Result queryListDeviceCountGroupByDevice(QueryCondition<QueryTopListProcGroupByProcDeviceReq> req);

    /**
     * 查询工单新增增量统计
     * @author hedongwei@wistronits.com
     * @date  2019/6/6 14:07
     * @param req 工单新增增量统计
     * @return 工单新增增量统计结果
     */
    Result queryProcAddListCountGroupByDay(QueryCondition<QueryProcCountByTimeReq> req);

    /**
     * 查询周新增增量统计
     * @author hedongwei@wistronits.com
     * @date  2019/6/17 16:42
     * @param req 周新增增量统计
     * @return 周新增增量数据
     */
    Result queryProcAddListCountGroupByWeek(QueryCondition<QueryProcCountByWeekReq> req);

    /**
     * 查询月新增增量统计
     * @author hedongwei@wistronits.com
     * @date  2019/6/17 16:42
     * @param req 月新增增量统计
     * @return 月新增增量数据
     */
    Result queryProcAddListCountGroupByMonth(QueryCondition<QueryProcCountByMonthReq> req);

    /**
     * 查询年新增增量统计
     * @author hedongwei@wistronits.com
     * @date  2019/6/17 19:59
     * @param req 年新增增量统计参数
     * @return 年新增增量数据
     */
    Result queryProcAddListCountGroupByYear(QueryCondition<QueryProcCountByYearReq> req);


    /**
     * 查询首页工单增量统计
     * @author hedongwei@wistronits.com
     * @date  2019/6/12 10:39
     * @param req 首页工单增量统计参数
     * @return 工单增量统计结果
     */
    Result queryHomeProcAddListCountGroupByDay(QueryCondition<QueryHomeProcCountByTimeReq> req);

    /**
     * 查询首页工单周增量统计
     * @author hedongwei@wistronits.com
     * @date  2019/6/17 16:42
     * @param req 周统计参数
     * @return 返回首页工单周增量统计数据
     */
    Result queryHomeProcAddListCountGroupByWeek(QueryCondition<QueryHomeProcCountByWeekReq> req);

    /**
     * 查询首页工单月增量统计
     * @author hedongwei@wistronits.com
     * @date  2019/6/17 16:42
     * @param req 月统计参数
     * @return 返回首页工单月增量统计数据
     */
    Result queryHomeProcAddListCountGroupByMonth(QueryCondition<QueryHomeProcCountByMonthReq> req);

    /**
     * 查询首页工单年增量统计
     * @author hedongwei@wistronits.com
     * @date  2019/6/17 16:42
     * @param req 年统计参数
     * @return 返回首页工单年增量统计数据
     */
    Result queryHomeProcAddListCountGroupByYear(QueryCondition<QueryHomeProcCountByYearReq> req);

    /**
     * 设置每一天统计到工单日数量统计表中
     * @author hedongwei@wistronits.com
     * @date  2019/6/3 10:07
     * @return 工单数量
     */
    void setProcCountSumToProcAddCountTable();

    /**
     * 每周统计工单数量到工单周数量统计表中
     * @author hedongwei@wistronits.com
     * @date  2019/6/17 10:14
     * @return 工单数量
     */
    void setProcWeekCountSumToProcAddCountTable();

    /**
     * 每月统计工单数量到工单月份数量统计表中
     * @author hedongwei@wistronits.com
     * @date  2019/6/17 10:14
     * @return 工单数量
     */
    void setProcMonthCountSumToProcAddCountTable();

    /**
     * 每年统计工单数量到工单年份数量统计表中
     * @author hedongwei@wistronits.com
     * @date  2019/6/17 10:14
     * @return 工单数量
     */
    void setProcYearCountSumToProcAddCountTable();


    /**
     * 销障工单状态导出
     * @author hedongwei@wistronits.com
     * @date  2019/6/20 16:26
     * @param exportDto 查询条件
     * @return 销障工单状态导出结果
     */
    Result procClearStatusStatisticalExport(ExportDto<ProcStatusStatisticalExportBean> exportDto);

    /**
     * 销障工单设施类型导出
     * @author hedongwei@wistronits.com
     * @date  2019/6/20 16:26
     * @param exportDto 查询条件
     * @return 销障工单设施类型导出结果
     */
    Result procClearDeviceTypeStatisticalExport(ExportDto<ProcDeviceTypeStatisticalExportBean> exportDto);

    /**
     * 销障工单处理方案导出
     * @author hedongwei@wistronits.com
     * @date  2019/6/20 16:26
     * @param exportDto 查询条件
     * @return 销障工单处理方案导出结果
     */
    Result procClearProcessingSchemeStatisticalExport(ExportDto<ProcProcessingSchemeStatisticalExportBean> exportDto);

    /**
     * 销障工单故障原因导出
     * @author hedongwei@wistronits.com
     * @date  2019/6/20 16:26
     * @param exportDto 查询条件
     * @return 销障工单故障原因导出结果
     */
    Result procClearErrorReasonStatisticalExport(ExportDto<ProcErrorReasonStatisticalExportBean> exportDto);


    /**
     * 销障工单区域比导出
     * @author hedongwei@wistronits.com
     * @date  2019/6/20 16:26
     * @param exportDto 查询条件
     * @return 销障工单区域比导出结果
     */
    Result procClearAreaPercentStatisticalExport(ExportDto<ProcAreaPercentStatisticalExportBean> exportDto);

    /**
     * 巡检工单状态导出
     * @author hedongwei@wistronits.com
     * @date  2019/6/20 16:26
     * @param exportDto 查询条件
     * @return 巡检工单状态导出结果
     */
    Result procInspectionStatusStatisticalExport(ExportDto<ProcStatusStatisticalExportBean> exportDto);

    /**
     * 巡检工单设施类型导出
     * @author hedongwei@wistronits.com
     * @date  2019/6/20 16:26
     * @param exportDto 查询条件
     * @return 巡检工单设施类型导出结果
     */
    Result procInspectionDeviceTypeStatisticalExport(ExportDto<ProcDeviceTypeStatisticalExportBean> exportDto);

    /**
     * 巡检工单区域比导出
     * @author hedongwei@wistronits.com
     * @date  2019/6/20 16:26
     * @param exportDto 查询条件
     * @return 巡检工单区域比导出结果
     */
    Result procInspectionAreaPercentStatisticalExport(ExportDto<ProcAreaPercentStatisticalExportBean> exportDto);

    /**
     * 销障工单设施top导出
     * @author hedongwei@wistronits.com
     * @date  2019/6/21 11:35
     * @param exportDto 导出参数
     * @return 返回导出的值
     */
    Result procClearTopListStatisticalExport(ExportDto<ProcTopListStatisticalExportBean> exportDto);


    /**
     * 巡检工单设施top导出
     * @author hedongwei@wistronits.com
     * @date  2019/6/21 11:35
     * @param exportDto 导出参数
     * @return 返回导出的值
     */
    Result procInspectionTopListStatisticalExport(ExportDto<ProcTopListStatisticalExportBean> exportDto);

    /**
     * 测试新增销障数据
     * @author hedongwei@wistronits.com
     * @date  2019/6/14 10:00
     */
    void insertClearFailureTest();

    /**
     * 测试新增巡检工单数据
     * @author hedongwei@wistronits.com
     * @date  2019/6/15 10:43
     */
    void insertInspectionTest();

}
