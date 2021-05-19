package com.fiberhome.filink.workflowbusinessserver.controller.statistical;

import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.workflowbusinessserver.bean.statistical.export.*;
import com.fiberhome.filink.workflowbusinessserver.req.statistical.normal.*;
import com.fiberhome.filink.workflowbusinessserver.req.statistical.overview.*;
import com.fiberhome.filink.workflowbusinessserver.service.statistical.ProcStatisticalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 工单统计
 * @author hedongwei@wistronits.com
 * @date 2019/5/28 13:45
 */
@RestController
@RequestMapping("/procStatistical")
public class ProcStatisticalController {

    @Autowired
    private ProcStatisticalService procStatisticalService;


    /**
     * 查询工单状态统计信息
     * @author hedongwei@wistronits.com
     * @date  2019/5/28 19:14
     * @param req 工单状态统计参数
     * @return 工单统计结果
     */
    @PostMapping("/queryListProcGroupByProcStatus")
    public Result queryListProcGroupByProcStatus(@RequestBody QueryCondition<QueryListProcGroupByProcStatusReq> req) {
        return procStatisticalService.queryListProcGroupByProcStatus(req);
    }

    /**
     * 查询概览工单状态信息
     * @author hedongwei@wistronits.com
     * @date  2019/6/4 15:03
     * @param req 概览工单状态信息参数
     * @return 查询概览工单状态信息
     */
    @PostMapping("/queryListProcOverviewGroupByProcStatus")
    public Result queryListProcOverviewGroupByProcStatus(@RequestBody QueryCondition<QueryListProcOverviewGroupByProcStatusReq> req) {
        return procStatisticalService.queryListProcOverviewGroupByProcStatus(req);
    }

    /**
     * 查询当前日期新增工单数量
     * @author hedongwei@wistronits.com
     * @date  2019/6/4 15:19
     * @param req 查询当前时间新增工单数量
     * @return 当前日期新增工单数量
     */
    @PostMapping("/queryNowDateAddOrderCount")
    public Result queryNowDateAddOrderCount(@RequestBody QueryCondition<QueryNowDateAddOrderCountReq> req) {
        return procStatisticalService.queryNowDateAddOrderCount(req);
    }


    /**
     * 工单处理方案统计
     * @author hedongwei@wistronits.com
     * @date  2019/5/30 18:18
     * @param req 处理方案参数
     * @return 处理方案
     */
    @PostMapping("/queryListProcGroupByProcProcessingScheme")
    public Result queryListProcGroupByProcProcessingScheme(@RequestBody QueryCondition<QueryListProcGroupByProcProcessingSchemeReq> req) {
        return procStatisticalService.queryListProcGroupByProcProcessingScheme(req);
    }

    /**
     * 查询处理方案概览统计
     * @author hedongwei@wistronits.com
     * @date  2019/6/4 16:50
     * @param req
     * @return 处理方案概览统计结果
     */
    @PostMapping("/queryListProcOverviewGroupByProcProcessingScheme")
    public Result queryListProcOverviewGroupByProcProcessingScheme(@RequestBody QueryCondition<QueryListProcOverviewGroupByProcessingSchemeReq> req) {
        return procStatisticalService.queryListProcOverviewGroupByProcProcessingScheme(req);
    }


    /**
     * 故障原因统计
     * @author hedongwei@wistronits.com
     * @date  2019/5/31 9:38
     * @param req 故障原因统计参数
     * @return 查询故障原因信息
     */
    @PostMapping("/queryListProcGroupByErrorReason")
    public Result queryListProcGroupByErrorReason(@RequestBody QueryCondition<QueryListProcGroupByProcErrorReasonReq> req) {
        return procStatisticalService.queryListProcGroupByErrorReason(req);
    }


    /**
     * 故障原因概览统计
     * @author hedongwei@wistronits.com
     * @date  2019/6/4 21:48
     * @param req 故障原因概览统计参数
     * @return 故障原因概览统计结果
     */
    @PostMapping("/queryListProcOverviewGroupByProcErrorReason")
    public Result queryListProcOverviewGroupByProcErrorReason(@RequestBody QueryCondition<QueryListProcOverviewGroupByErrorReasonReq> req) {
        return procStatisticalService.queryListProcOverviewGroupByProcErrorReason(req);
    }

    /**
     * 设施类型统计
     * @author hedongwei@wistronits.com
     * @date  2019/5/31 14:33
     * @param req 设施类型统计参数
     * @return 设施类型结果
     */
    @PostMapping("/queryListProcGroupByDeviceType")
    public Result queryListProcGroupByDeviceType(@RequestBody QueryCondition<QueryListProcGroupByProcDeviceTypeReq> req) {
        return procStatisticalService.queryListProcGroupByDeviceType(req);
    }

    /**
     * 设施类型概览统计
     * @author hedongwei@wistronits.com
     * @date  2019/6/4 21:48
     * @param req 设施类型概览统计参数
     * @return 设施类型概览统计结果
     */
    @PostMapping("/queryListProcOverviewGroupByProcDeviceType")
    public Result queryListProcOverviewGroupByProcDeviceType(@RequestBody QueryCondition<QueryListProcOverviewGroupByDeviceTypeReq> req) {
        return procStatisticalService.queryListProcOverviewGroupByProcDeviceType(req);
    }

    /**
     * 区域工单比统计
     * @author hedongwei@wistronits.com
     * @date  2019/5/31 14:33
     * @param req 区域工单比统计参数
     * @return 区域工单比统计信息
     */
    @PostMapping("/queryListProcGroupByAreaPercent")
    public Result queryListProcGroupByAreaPercent(@RequestBody QueryCondition<QueryListProcGroupByProcAreaPercentReq> req) {
        return procStatisticalService.queryListProcGroupByAreaPercent(req);
    }


    /**
     * 查询关联部门统计
     * @author hedongwei@wistronits.com
     * @date  2019/6/3 15:10
     * @param req 关联部门统计参数
     * @return 查询关联部门信息
     */
    @PostMapping("/queryDeptListGroupByAccountabilityDept")
    public Result queryDeptListGroupByAccountabilityDept(@RequestBody QueryCondition<QueryDeptListGroupByAccountabilityDeptReq> req) {
        return procStatisticalService.queryDeptListGroupByAccountabilityDept(req);
    }



    /**
     * 查询工单关联设施最多的数量
     * @author hedongwei@wistronits.com
     * @date  2019/5/31 18:02
     * @param req 工单关联设施统计参数
     * @return 查询工单
     */
    @PostMapping("/queryTopListDeviceCountGroupByDevice")
    public Result queryTopListDeviceCountByDevice(@RequestBody QueryCondition<QueryTopListProcGroupByProcDeviceReq> req) {
        return procStatisticalService.queryListDeviceCountGroupByDevice(req);
    }


    /**
     * 查询工单新增增量统计
     * @author hedongwei@wistronits.com
     * @date  2019/6/6 14:07
     * @param req 工单新增增量统计
     * @return 工单新增增量统计结果
     */
    @PostMapping("/queryProcAddListCountGroupByDay")
    public Result queryProcAddListCountGroupByDay(@RequestBody QueryCondition<QueryProcCountByTimeReq> req) {
        return procStatisticalService.queryProcAddListCountGroupByDay(req);
    }

    /**
     * 查询工单周增量统计
     * @author hedongwei@wistronits.com
     * @date  2019/6/6 14:07
     * @param req 工单周增量统计
     * @return 工单周增量统计结果
     */
    @PostMapping("/queryProcAddListCountGroupByWeek")
    public Result queryProcAddListCountGroupByWeek(@RequestBody QueryCondition<QueryProcCountByWeekReq> req) {
        return procStatisticalService.queryProcAddListCountGroupByWeek(req);
    }

    /**
     * 查询工单月增量统计
     * @author hedongwei@wistronits.com
     * @date  2019/6/6 14:07
     * @param req 工单月增量统计
     * @return 工单月增量统计结果
     */
    @PostMapping("/queryProcAddListCountGroupByMonth")
    public Result queryProcAddListCountGroupByMonth(@RequestBody QueryCondition<QueryProcCountByMonthReq> req) {
        return procStatisticalService.queryProcAddListCountGroupByMonth(req);
    }

    /**
     * 查询工单年增量统计
     * @author hedongwei@wistronits.com
     * @date  2019/6/6 14:07
     * @param req 工单年增量统计
     * @return 工单年增量统计结果
     */
    @PostMapping("/queryProcAddListCountGroupByYear")
    public Result queryProcAddListCountGroupByYear(@RequestBody QueryCondition<QueryProcCountByYearReq> req) {
        return procStatisticalService.queryProcAddListCountGroupByYear(req);
    }

    /**
     * 查询首页工单新增增量统计
     * @author hedongwei@wistronits.com
     * @date  2019/6/6 14:07
     * @param req 工单新增增量统计
     * @return 首页工单新增增量统计结果
     */
    @PostMapping("/queryHomeProcAddListCountGroupByDay")
    public Result queryHomeProcAddListCountGroupByDay(@RequestBody QueryCondition<QueryHomeProcCountByTimeReq> req) {
        return procStatisticalService.queryHomeProcAddListCountGroupByDay(req);
    }

    /**
     * 查询首页工单周增量统计
     * @author hedongwei@wistronits.com
     * @date  2019/6/6 14:07
     * @param req 工单周增量统计
     * @return 首页工单周增量统计结果
     */
    @PostMapping("/queryHomeProcAddListCountGroupByWeek")
    public Result queryHomeProcAddListCountGroupByWeek(@RequestBody QueryCondition<QueryHomeProcCountByWeekReq> req) {
        return procStatisticalService.queryHomeProcAddListCountGroupByWeek(req);
    }

    /**
     * 查询首页工单月增量统计
     * @author hedongwei@wistronits.com
     * @date  2019/6/6 14:07
     * @param req 工单月增量统计
     * @return 首页工单月增量统计结果
     */
    @PostMapping("/queryHomeProcAddListCountGroupByMonth")
    public Result queryHomeProcAddListCountGroupByMonth(@RequestBody QueryCondition<QueryHomeProcCountByMonthReq> req) {
        return procStatisticalService.queryHomeProcAddListCountGroupByMonth(req);
    }

    /**
     * 查询首页工单年增量统计
     * @author hedongwei@wistronits.com
     * @date  2019/6/6 14:07
     * @param req 工单新增增量统计
     * @return 首页工单新增增量统计结果
     */
    @PostMapping("/queryHomeProcAddListCountGroupByYear")
    public Result queryHomeProcAddListCountGroupByYear(@RequestBody QueryCondition<QueryHomeProcCountByYearReq> req) {
        return procStatisticalService.queryHomeProcAddListCountGroupByYear(req);
    }


    /**
     * 销障工单状态导出
     * @author hedongwei@wistronits.com
     * @date  2019/6/20 16:16
     * @param exportDto 导出的数据参数
     * @return 销障工单状态导出
     */
    @PostMapping("/procClearStatusStatisticalExport")
    public Result procClearStatusStatisticalExport(@RequestBody ExportDto<ProcStatusStatisticalExportBean> exportDto) {
        return procStatisticalService.procClearStatusStatisticalExport(exportDto);
    }

    /**
     * 销障工单设施类型导出
     * @author hedongwei@wistronits.com
     * @date  2019/6/20 16:16
     * @param exportDto 导出的数据参数
     * @return 销障工单设施类型导出
     */
    @PostMapping("/procClearDeviceTypeStatisticalExport")
    public Result procClearDeviceTypeStatisticalExport(@RequestBody ExportDto<ProcDeviceTypeStatisticalExportBean> exportDto) {
        return procStatisticalService.procClearDeviceTypeStatisticalExport(exportDto);
    }


    /**
     * 销障工单处理方案导出
     * @author hedongwei@wistronits.com
     * @date  2019/6/20 16:16
     * @param exportDto 导出的数据参数
     * @return 销障工单处理方案导出
     */
    @PostMapping("/procClearProcessingSchemeStatisticalExport")
    public Result procClearProcessingSchemeStatisticalExport(@RequestBody ExportDto<ProcProcessingSchemeStatisticalExportBean> exportDto) {
        return procStatisticalService.procClearProcessingSchemeStatisticalExport(exportDto);
    }

    /**
     * 销障工单故障原因导出
     * @author hedongwei@wistronits.com
     * @date  2019/6/20 16:16
     * @param exportDto 导出的数据参数
     * @return 销障工单故障原因导出
     */
    @PostMapping("/procClearErrorReasonStatisticalExport")
    public Result procClearErrorReasonStatisticalExport(@RequestBody ExportDto<ProcErrorReasonStatisticalExportBean> exportDto) {
        return procStatisticalService.procClearErrorReasonStatisticalExport(exportDto);
    }

    /**
     * 销障工单区域比导出
     * @author hedongwei@wistronits.com
     * @date  2019/6/20 16:16
     * @param exportDto 导出的数据参数
     * @return 销障工单区域比导出
     */
    @PostMapping("/procClearAreaPercentStatisticalExport")
    public Result procClearAreaPercentStatisticalExport(@RequestBody ExportDto<ProcAreaPercentStatisticalExportBean> exportDto) {
        return procStatisticalService.procClearAreaPercentStatisticalExport(exportDto);
    }


    /**
     * 巡检工单状态导出
     * @author hedongwei@wistronits.com
     * @date  2019/6/20 16:16
     * @param exportDto 导出的数据参数
     * @return 巡检工单状态导出
     */
    @PostMapping("/procInspectionStatusStatisticalExport")
    public Result procInspectionStatusStatisticalExport(@RequestBody ExportDto<ProcStatusStatisticalExportBean> exportDto) {
        return procStatisticalService.procInspectionStatusStatisticalExport(exportDto);
    }

    /**
     * 巡检工单设施类型导出
     * @author hedongwei@wistronits.com
     * @date  2019/6/20 16:16
     * @param exportDto 导出的数据参数
     * @return 巡检工单设施类型导出
     */
    @PostMapping("/procInspectionDeviceTypeStatisticalExport")
    public Result procInspectionDeviceTypeStatisticalExport(@RequestBody ExportDto<ProcDeviceTypeStatisticalExportBean> exportDto) {
        return procStatisticalService.procInspectionDeviceTypeStatisticalExport(exportDto);
    }

    /**
     * 巡检工单区域比导出
     * @author hedongwei@wistronits.com
     * @date  2019/6/20 16:16
     * @param exportDto 导出的数据参数
     * @return 巡检工单区域比导出
     */
    @PostMapping("/procInspectionAreaPercentStatisticalExport")
    public Result procInspectionAreaPercentStatisticalExport(@RequestBody ExportDto<ProcAreaPercentStatisticalExportBean> exportDto) {
        return procStatisticalService.procInspectionAreaPercentStatisticalExport(exportDto);
    }

    /**
     * 销障工单设施top导出
     * @author hedongwei@wistronits.com
     * @date  2019/6/21 11:35
     * @param exportDto 导出参数
     * @return 返回导出的值
     */
    @PostMapping("/procClearTopListStatisticalExport")
    public Result procClearTopListStatisticalExport(@RequestBody ExportDto<ProcTopListStatisticalExportBean> exportDto) {
        return procStatisticalService.procClearTopListStatisticalExport(exportDto);
    }

    /**
     * 巡检工单设施top导出
     * @author hedongwei@wistronits.com
     * @date  2019/6/21 11:35
     * @param exportDto 导出参数
     * @return 返回导出的值
     */
    @PostMapping("/procInspectionTopListStatisticalExport")
    public Result procInspectionTopListStatisticalExport(@RequestBody ExportDto<ProcTopListStatisticalExportBean> exportDto) {
        return procStatisticalService.procInspectionTopListStatisticalExport(exportDto);
    }


    /**
     * 新增销障工单数据
     */
    @PostMapping("/insertClearFailureTest")
    public void insertClearFailureTest() {
        procStatisticalService.insertClearFailureTest();
    }

    /**
     * 新增巡检工单数据
     */
    @PostMapping("/insertInspectionTest")
    public void insertInspectionTest() {
        procStatisticalService.insertInspectionTest();
    }

}
