package com.fiberhome.filink.workflowbusinessserver.controller.procclear;


import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.exportapi.bean.ColumnInfo;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.workflowbusinessserver.req.app.procclear.ReceiptClearFailureReq;
import com.fiberhome.filink.workflowbusinessserver.req.procbase.ProcBaseReq;
import com.fiberhome.filink.workflowbusinessserver.req.procclear.InsertClearFailureReq;
import com.fiberhome.filink.workflowbusinessserver.req.procclear.UpdateClearFailureReq;
import com.fiberhome.filink.workflowbusinessserver.service.procclear.ProcClearFailureService;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

/**
 * 销障工单控制层测试类
 * @author hedongwei@wistronits.com
 * @date 2019/7/3 20:23
 */
@RunWith(JMockit.class)
public class ProcClearFailureControllerTest {

    /**
     * 测试对象ProcClearFailureController
     */
    @Tested
    private ProcClearFailureController procClearFailureController;

    /**
     * 注入销障工单逻辑层
     */
    @Injectable
    private ProcClearFailureService procClearFailureService;

    /**
     * 新增销障工单
     * @author hedongwei@wistronits.com
     * @date  2019/7/5 13:47
     */
    @Test
    public void addClearFailureProc() throws Exception{
        InsertClearFailureReq insertClearFailureReq = new InsertClearFailureReq();
        procClearFailureController.addClearFailureProc(insertClearFailureReq);
    }

    /**
     * 修改销障工单
     * @author hedongwei@wistronits.com
     * @date  2019/7/5 13:54
     */
    @Test
    public void updateClearFailureProc() throws Exception {
        UpdateClearFailureReq updateClearFailureReq = new UpdateClearFailureReq();
        procClearFailureController.updateClearFailureProc(updateClearFailureReq);
    }

    /**
     * 分页查询销障工单未完工列表
     * @author hedongwei@wistronits.com
     * @date  2019/7/5 13:54
     */
    @Test
    public void queryListClearFailureProcessByPage() {
        QueryCondition<ProcBaseReq> queryCondition = new QueryCondition<>();
        procClearFailureController.queryListClearFailureProcessByPage(queryCondition);
    }


    /**
     * 查询历史销障工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/7/5 13:54
     */
    @Test
    public void queryListHistoryClearFailureByPage() {
        QueryCondition<ProcBaseReq> queryCondition = new QueryCondition<>();
        procClearFailureController.queryListHistoryClearFailureByPage(queryCondition);
    }

    /**
     * 销障工单列表待指派状态统计
     * @author hedongwei@wistronits.com
     * @date  2019/7/5 13:54
     */
    @Test
    public void queryCountClearFailureProcByAssigned() {
        procClearFailureController.queryCountClearFailureProcByAssigned();
    }

    /**
     * 销障工单列表待处理状态统计
     * @author hedongwei@wistronits.com
     * @date  2019/7/5 13:54
     */
    @Test
    public void queryCountClearFailureProcByPending() {
        procClearFailureController.queryCountClearFailureProcByPending();
    }

    /**
     * 销障工单列表处理中状态统计
     * @author hedongwei@wistronits.com
     * @date  2019/7/6 11:02
     */
    @Test
    public void queryCountClearFailureProcByProcessing() {
        procClearFailureController.queryCountClearFailureProcByProcessing();
    }

    /**
     * 销障工单列表今日新增统计
     * @author hedongwei@wistronits.com
     * @date  2019/7/6 11:02
     */
    @Test
    public void queryCountClearFailureProcByToday() {
        procClearFailureController.queryCountClearFailureProcByToday();
    }

    /**
     * 销障工单未完工列表状态总数统计
     * @author hedongwei@wistronits.com
     * @date  2019/7/6 11:02
     */
    @Test
    public void queryCountListProcUnfinishedProcStatus() {
        procClearFailureController.queryCountListProcUnfinishedProcStatus();
    }

    /**
     * 故障原因统计的销障工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/7/6 11:02
     */
    @Test
    public void queryListClearFailureGroupByErrorReason() {
        procClearFailureController.queryListClearFailureGroupByErrorReason();
    }

    /**
     * 处理方案统计的销障工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/7/6 11:02
     */
    @Test
    public void queryListClearFailureGroupByProcessingScheme() {
        procClearFailureController.queryListClearFailureGroupByProcessingScheme();
    }

    /**
     * 工单状态统计的销障工单信息
     *
     * @return Result
     */
    @Test
    public void queryListClearFailureGroupByDeviceType() {
        procClearFailureController.queryListClearFailureGroupByDeviceType();
    }


    /**
     * 工单状态统计的销障工单信息
     *
     * @return Result
     */
    @Test
    public void queryListClearFailureByStatus() {
        procClearFailureController.queryListClearFailureByStatus();
    }

    /**
     * 销障工单历史列表总数统计
     *
     * @return Result
     */
    @Test
    public void queryCountListProcHisProc() {
        procClearFailureController.queryCountListProcHisProc();
    }


    /**
     * 销障工单未完工列表导出
     * @author hedongwei@wistronits.com
     * @date  2019/7/6 11:02
     */
    @Test
    public void exportClearFailureProcUnfinished() {
        ExportDto<ProcBaseReq> exportDto = new ExportDto<>();
        procClearFailureController.exportClearFailureProcUnfinished(exportDto);



        ExportDto<ProcBaseReq> exportDtoParam = new ExportDto<>();
        exportDtoParam.setExcelType(1);
        List<ColumnInfo> columnInfoList = new ArrayList<>();
        ColumnInfo columnInfo = new ColumnInfo();
        columnInfo.setColumnName("test");
        columnInfoList.add(columnInfo);
        exportDtoParam.setColumnInfoList(columnInfoList);
        procClearFailureController.exportClearFailureProcUnfinished(exportDtoParam);
    }

    /**
     * 销障工单历史列表导出
     * @author hedongwei@wistronits.com
     * @date  2019/7/6 11:02
     */
    @Test
    public void exportClearFailureProcHistory() {
        ExportDto<ProcBaseReq> exportDto = new ExportDto<>();
        procClearFailureController.exportClearFailureProcHistory(exportDto);

        ExportDto<ProcBaseReq> exportDtoParam = new ExportDto<>();
        exportDtoParam.setExcelType(1);
        List<ColumnInfo> columnInfoList = new ArrayList<>();
        ColumnInfo columnInfo = new ColumnInfo();
        columnInfo.setColumnName("test");
        columnInfoList.add(columnInfo);
        exportDtoParam.setColumnInfoList(columnInfoList);
        procClearFailureController.exportClearFailureProcHistory(exportDtoParam);
    }


    /**
     * 销障工单前五
     * @author hedongwei@wistronits.com
     * @date  2019/7/6 11:02
     */
    @Test
    public void queryClearFailureProcTopFive() {
        String deviceId = "info";
        procClearFailureController.queryClearFailureProcTopFive(deviceId);
    }

    /**
     * 重新生成销障工单
     * @author hedongwei@wistronits.com
     * @date  2019/7/6 11:02
     */
    @Test
    public void regenerateClearFailureProc() throws Exception {
        InsertClearFailureReq req = new InsertClearFailureReq();
        procClearFailureController.regenerateClearFailureProc(req);
    }

    /**
     * app销障工单回单
     * @author hedongwei@wistronits.com
     * @date  2019/7/6 11:02
     */
    @Test
    public void receiptClearFailureProcForApp() throws Exception {
        ReceiptClearFailureReq receiptClearFailureReq = new ReceiptClearFailureReq();
        procClearFailureController.receiptClearFailureProcForApp(receiptClearFailureReq);
    }




}