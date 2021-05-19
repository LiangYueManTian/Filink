package com.fiberhome.filink.workflowbusinessserver.controller.statistical;


import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.workflowbusinessserver.bean.statistical.export.*;
import com.fiberhome.filink.workflowbusinessserver.req.statistical.normal.*;
import com.fiberhome.filink.workflowbusinessserver.req.statistical.overview.*;
import com.fiberhome.filink.workflowbusinessserver.service.statistical.ProcStatisticalService;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 销障工单控制层测试类
 * @author hedongwei@wistronits.com
 * @date 2019/7/3 20:23
 */
@RunWith(JMockit.class)
public class ProcStatisticalControllerTest {

    /**
     * 测试对象ProcStatisticalController
     */
    @Tested
    private ProcStatisticalController procStatisticalController;

    /**
     * 工单统计逻辑层
     */
    @Injectable
    private ProcStatisticalService procStatisticalService;

    /**
     * 查询工单状态统计信息
     * @author hedongwei@wistronits.com
     * @date  2019/5/28 19:14
     */
    @Test
    public void queryListProcGroupByProcStatus() {
        QueryCondition<QueryListProcGroupByProcStatusReq> req = new QueryCondition<>();
        procStatisticalController.queryListProcGroupByProcStatus(req);
    }


    /**
     * 查询概览工单状态信息
     * @author hedongwei@wistronits.com
     * @date  2019/5/28 19:14
     */
    @Test
    public void queryListProcOverviewGroupByProcStatus() {
        QueryCondition<QueryListProcOverviewGroupByProcStatusReq> req = new QueryCondition<>();
        procStatisticalController.queryListProcOverviewGroupByProcStatus(req);
    }

    /**
     * 查询当前日期新增工单数量
     * @author hedongwei@wistronits.com
     * @date  2019/5/28 19:14
     */
    @Test
    public void queryNowDateAddOrderCount() {
        QueryCondition<QueryNowDateAddOrderCountReq> req = new QueryCondition<>();
        procStatisticalController.queryNowDateAddOrderCount(req);
    }


    /**
     * 查询处理方案概览统计
     * @author hedongwei@wistronits.com
     * @date  2019/5/28 19:14
     */
    @Test
    public void queryListProcOverviewGroupByProcProcessingScheme() {
        QueryCondition<QueryListProcOverviewGroupByProcessingSchemeReq> req = new QueryCondition<>();
        procStatisticalController.queryListProcOverviewGroupByProcProcessingScheme(req);
    }

    /**
     * 工单处理方案统计
     * @author hedongwei@wistronits.com
     * @date  2019/5/28 19:14
     */
    @Test
    public void queryListProcGroupByProcProcessingScheme() {
        QueryCondition<QueryListProcGroupByProcProcessingSchemeReq> req = new QueryCondition<>();
        procStatisticalController.queryListProcGroupByProcProcessingScheme(req);
    }

    /**
     * 故障原因统计
     * @author hedongwei@wistronits.com
     * @date  2019/5/28 19:14
     */
    @Test
    public void queryListProcGroupByErrorReason() {
        QueryCondition<QueryListProcGroupByProcErrorReasonReq> req = new QueryCondition<>();
        procStatisticalController.queryListProcGroupByErrorReason(req);
    }

    /**
     * 故障原因概览统计
     * @author hedongwei@wistronits.com
     * @date  2019/5/28 19:14
     */
    @Test
    public void queryListProcOverviewGroupByProcErrorReason() {
        QueryCondition<QueryListProcOverviewGroupByErrorReasonReq> req = new QueryCondition<>();
        procStatisticalController.queryListProcOverviewGroupByProcErrorReason(req);
    }

    /**
     * 设施类型统计
     * @author hedongwei@wistronits.com
     * @date  2019/5/28 19:14
     */
    @Test
    public void queryListProcGroupByDeviceType() {
        QueryCondition<QueryListProcGroupByProcDeviceTypeReq> req = new QueryCondition<>();
        procStatisticalController.queryListProcGroupByDeviceType(req);
    }


    /**
     * 设施类型概览统计
     * @author hedongwei@wistronits.com
     * @date  2019/5/28 19:14
     */
    @Test
    public void queryListProcOverviewGroupByProcDeviceType() {
        QueryCondition<QueryListProcOverviewGroupByDeviceTypeReq> req = new QueryCondition<>();
        procStatisticalController.queryListProcOverviewGroupByProcDeviceType(req);
    }

    /**
     * 区域工单比统计
     * @author hedongwei@wistronits.com
     * @date  2019/5/28 19:14
     */
    @Test
    public void queryListProcGroupByAreaPercent() {
        QueryCondition<QueryListProcGroupByProcAreaPercentReq> req = new QueryCondition<>();
        procStatisticalController.queryListProcGroupByAreaPercent(req);
    }


    /**
     * 查询关联部门统计
     * @author hedongwei@wistronits.com
     * @date  2019/5/28 19:14
     */
    @Test
    public void queryDeptListGroupByAccountabilityDept() {
        QueryCondition<QueryDeptListGroupByAccountabilityDeptReq> req = new QueryCondition<>();
        procStatisticalController.queryDeptListGroupByAccountabilityDept(req);
    }

    /**
     * 查询工单关联设施最多的数量
     * @author hedongwei@wistronits.com
     * @date  2019/5/28 19:14
     */
    @Test
    public void queryTopListDeviceCountByDevice() {
        QueryCondition<QueryTopListProcGroupByProcDeviceReq> req = new QueryCondition<>();
        procStatisticalController.queryTopListDeviceCountByDevice(req);
    }

    /**
     * 查询工单新增增量统计
     * @author hedongwei@wistronits.com
     * @date  2019/5/28 19:14
     */
    @Test
    public void queryProcAddListCountGroupByDay() {
        QueryCondition<QueryProcCountByTimeReq> req = new QueryCondition<>();
        procStatisticalController.queryProcAddListCountGroupByDay(req);
    }

    /**
     * 查询工单周增量统计
     * @author hedongwei@wistronits.com
     * @date  2019/5/28 19:14
     */
    @Test
    public void queryProcAddListCountGroupByWeek() {
        QueryCondition<QueryProcCountByWeekReq> req = new QueryCondition<>();
        procStatisticalController.queryProcAddListCountGroupByWeek(req);
    }

    /**
     * 查询工单月增量统计
     * @author hedongwei@wistronits.com
     * @date  2019/5/28 19:14
     */
    @Test
    public void queryProcAddListCountGroupByMonth() {
        QueryCondition<QueryProcCountByMonthReq> req = new QueryCondition<>();
        procStatisticalController.queryProcAddListCountGroupByMonth(req);
    }

    /**
     * 查询工单年增量统计
     * @author hedongwei@wistronits.com
     * @date  2019/5/28 19:14
     */
    @Test
    public void queryProcAddListCountGroupByYear() {
        QueryCondition<QueryProcCountByYearReq> req = new QueryCondition<>();
        procStatisticalController.queryProcAddListCountGroupByYear(req);
    }

    /**
     * 查询首页工单新增增量统计
     * @author hedongwei@wistronits.com
     * @date  2019/5/28 19:14
     */
    @Test
    public void queryHomeProcAddListCountGroupByDay() {
        QueryCondition<QueryHomeProcCountByTimeReq> req = new QueryCondition<>();
        procStatisticalController.queryHomeProcAddListCountGroupByDay(req);
    }

    /**
     * 查询首页工单周增量统计
     * @author hedongwei@wistronits.com
     * @date  2019/5/28 19:14
     */
    @Test
    public void queryHomeProcAddListCountGroupByWeek() {
        QueryCondition<QueryHomeProcCountByWeekReq> req = new QueryCondition<>();
        procStatisticalController.queryHomeProcAddListCountGroupByWeek(req);
    }


    /**
     * 查询首页工单月增量统计
     * @author hedongwei@wistronits.com
     * @date  2019/5/28 19:14
     */
    @Test
    public void queryHomeProcAddListCountGroupByMonth() {
        QueryCondition<QueryHomeProcCountByMonthReq> req = new QueryCondition<>();
        procStatisticalController.queryHomeProcAddListCountGroupByMonth(req);
    }

    /**
     * 查询首页工单年增量统计
     * @author hedongwei@wistronits.com
     * @date  2019/5/28 19:14
     */
    @Test
    public void queryHomeProcAddListCountGroupByYear() {
        QueryCondition<QueryHomeProcCountByYearReq> req = new QueryCondition<>();
        procStatisticalController.queryHomeProcAddListCountGroupByYear(req);
    }

    /**
     * 销障工单状态导出
     * @author hedongwei@wistronits.com
     * @date  2019/5/28 19:14
     */
    @Test
    public void procClearStatusStatisticalExport() {
        ExportDto<ProcStatusStatisticalExportBean> exportDto = new ExportDto<>();
        procStatisticalController.procClearStatusStatisticalExport(exportDto);
    }

    /**
     * 销障工单设施类型导出
     * @author hedongwei@wistronits.com
     * @date  2019/5/28 19:14
     */
    @Test
    public void procClearDeviceTypeStatisticalExport() {
        ExportDto<ProcDeviceTypeStatisticalExportBean> exportDto = new ExportDto<>();
        procStatisticalController.procClearDeviceTypeStatisticalExport(exportDto);
    }

    /**
     * 销障工单处理方案导出
     * @author hedongwei@wistronits.com
     * @date  2019/5/28 19:14
     */
    @Test
    public void procClearProcessingSchemeStatisticalExport() {
        ExportDto<ProcProcessingSchemeStatisticalExportBean> exportDto = new ExportDto<>();
        procStatisticalController.procClearProcessingSchemeStatisticalExport(exportDto);
    }

    /**
     * 销障工单故障原因导出
     * @author hedongwei@wistronits.com
     * @date  2019/5/28 19:14
     */
    @Test
    public void procClearErrorReasonStatisticalExport() {
        ExportDto<ProcErrorReasonStatisticalExportBean> exportDto = new ExportDto<>();
        procStatisticalController.procClearErrorReasonStatisticalExport(exportDto);
    }

    /**
     * 销障工单区域比导出
     * @author hedongwei@wistronits.com
     * @date  2019/5/28 19:14
     */
    @Test
    public void procClearAreaPercentStatisticalExport() {
        ExportDto<ProcAreaPercentStatisticalExportBean> exportDto = new ExportDto<>();
        procStatisticalController.procClearAreaPercentStatisticalExport(exportDto);
    }

    /**
     * 巡检工单状态导出
     * @author hedongwei@wistronits.com
     * @date  2019/5/28 19:14
     */
    @Test
    public void procInspectionStatusStatisticalExport() {
        ExportDto<ProcStatusStatisticalExportBean> exportDto = new ExportDto<>();
        procStatisticalController.procInspectionStatusStatisticalExport(exportDto);
    }

    /**
     * 巡检工单设施类型导出
     * @author hedongwei@wistronits.com
     * @date  2019/5/28 19:14
     */
    @Test
    public void procInspectionDeviceTypeStatisticalExport() {
        ExportDto<ProcDeviceTypeStatisticalExportBean> exportDto = new ExportDto<>();
        procStatisticalController.procInspectionDeviceTypeStatisticalExport(exportDto);
    }


    /**
     * 巡检工单区域比导出
     * @author hedongwei@wistronits.com
     * @date  2019/5/28 19:14
     */
    @Test
    public void procInspectionAreaPercentStatisticalExport() {
        ExportDto<ProcAreaPercentStatisticalExportBean> exportDto = new ExportDto<>();
        procStatisticalController.procInspectionAreaPercentStatisticalExport(exportDto);
    }


    /**
     * 销障工单设施top导出
     * @author hedongwei@wistronits.com
     * @date  2019/5/28 19:14
     */
    @Test
    public void procClearTopListStatisticalExport() {
        ExportDto<ProcTopListStatisticalExportBean> exportDto = new ExportDto<>();
        procStatisticalController.procClearTopListStatisticalExport(exportDto);
    }


    /**
     * 巡检工单设施top导出
     * @author hedongwei@wistronits.com
     * @date  2019/5/28 19:14
     */
    @Test
    public void procInspectionTopListStatisticalExport() {
        ExportDto<ProcTopListStatisticalExportBean> exportDto = new ExportDto<>();
        procStatisticalController.procInspectionTopListStatisticalExport(exportDto);
    }


    /**
     * 新增测试数据
     * @author hedongwei@wistronits.com
     * @date  2019/5/28 19:14
     */
    @Test
    public void insertTest() {
        procStatisticalController.insertTest();
    }

    /**
     * 新增测试工单数据
     * @author hedongwei@wistronits.com
     * @date  2019/5/28 19:14
     */
    @Test
    public void insertTestCustomData() {
        procStatisticalController.insertTestCustomData();
    }


}