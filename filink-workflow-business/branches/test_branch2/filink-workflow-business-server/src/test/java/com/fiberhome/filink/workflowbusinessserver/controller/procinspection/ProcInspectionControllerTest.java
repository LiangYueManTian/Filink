package com.fiberhome.filink.workflowbusinessserver.controller.procinspection;


import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.exportapi.bean.ColumnInfo;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.workflowbusinessserver.bean.inspectiontask.InspectionTask;
import com.fiberhome.filink.workflowbusinessserver.bean.procinspection.ProcInspection;
import com.fiberhome.filink.workflowbusinessserver.req.procbase.ProcBaseReq;
import com.fiberhome.filink.workflowbusinessserver.req.procinspection.CompleteInspectionByPageReq;
import com.fiberhome.filink.workflowbusinessserver.req.procinspection.CompleteInspectionProcReq;
import com.fiberhome.filink.workflowbusinessserver.req.procinspection.ProcInspectionReq;
import com.fiberhome.filink.workflowbusinessserver.service.procinspection.ProcInspectionRecordService;
import com.fiberhome.filink.workflowbusinessserver.service.procinspection.ProcInspectionService;
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
public class ProcInspectionControllerTest {

    /**
     * 测试对象ProcInspectionController
     */
    @Tested
    private ProcInspectionController procInspectionController;

    /**
     * 注入巡检工单逻辑层
     */
    @Injectable
    private ProcInspectionService procInspectionService;

    /**
     * 注入巡检记录逻辑层
     */
    @Injectable
    private ProcInspectionRecordService procInspectionRecordService;

    /**
     * 巡检未完工列表
     * @author hedongwei@wistronits.com
     * @date  2019/7/6 11:37
     */
    @Test
    public void queryListInspectionProcessByPage() {
        QueryCondition<ProcBaseReq> queryCondition = new QueryCondition<>();
        procInspectionController.queryListInspectionProcessByPage(queryCondition);
    }


    /**
     * 导出未完工巡检工单
     * @author hedongwei@wistronits.com
     * @date  2019/7/6 11:37
     */
    @Test
    public void exportListInspectionProcess() {
        ExportDto<ProcBaseReq> exportDto = new ExportDto<>();
        procInspectionController.exportListInspectionProcess(exportDto);

        ExportDto<ProcBaseReq> exportDtoParam = new ExportDto<>();
        exportDtoParam.setExcelType(1);
        List<ColumnInfo> columnInfoList = new ArrayList<>();
        ColumnInfo columnInfo = new ColumnInfo();
        columnInfo.setColumnName("test");
        columnInfoList.add(columnInfo);
        exportDtoParam.setColumnInfoList(columnInfoList);
        procInspectionController.exportListInspectionProcess(exportDtoParam);
    }

    /**
     * 巡检工单前五
     * @author hedongwei@wistronits.com
     * @date  2019/7/6 11:37
     */
    @Test
    public void queryProcInspectionTopFive() {
        String deviceId = "1";
        procInspectionController.queryProcInspectionTopFive(deviceId);
    }

    /**
     * 导出已完成巡检工单
     * @author hedongwei@wistronits.com
     * @date  2019/7/6 11:37
     */
    @Test
    public void exportListInspectionComplete() {
        ExportDto<ProcBaseReq> exportDto = new ExportDto<>();
        procInspectionController.exportListInspectionComplete(exportDto);

        ExportDto<ProcBaseReq> exportDtoParam = new ExportDto<>();
        exportDtoParam.setExcelType(1);
        List<ColumnInfo> columnInfoList = new ArrayList<>();
        ColumnInfo columnInfo = new ColumnInfo();
        columnInfo.setColumnName("test");
        columnInfoList.add(columnInfo);
        exportDtoParam.setColumnInfoList(columnInfoList);
        procInspectionController.exportListInspectionComplete(exportDtoParam);
    }


    /**
     * 新增巡检工单
     * @author hedongwei@wistronits.com
     * @date  2019/7/6 11:37
     */
    @Test
    public void addInspectionProc() {
        ProcInspectionReq addInspectionProcReq = new ProcInspectionReq();
        procInspectionController.addInspectionProc(addInspectionProcReq);
    }

    /**
     * 告警页面调用新增巡检工单
     * @author hedongwei@wistronits.com
     * @date  2019/7/6 11:37
     */
    @Test
    public void addInspectionProcForAlarm() {
        ProcInspectionReq addInspectionProcReq = new ProcInspectionReq();
        procInspectionController.addInspectionProcForAlarm(addInspectionProcReq);
    }

    /**
     * 重新生成巡检工单
     * @author hedongwei@wistronits.com
     * @date  2019/7/6 11:37
     */
    @Test
    public void regenerateInspectionProc() {
        ProcInspectionReq addInspectionProcReq = new ProcInspectionReq();
        procInspectionController.regenerateInspectionProc(addInspectionProcReq);
    }


    /**
     * 修改巡检工单
     * @author hedongwei@wistronits.com
     * @date  2019/7/6 11:37
     */
    @Test
    public void updateInspectionProc() {
        ProcInspectionReq updateInspectionProcReq = new ProcInspectionReq();
        procInspectionController.updateInspectionProc(updateInspectionProcReq);
    }


    /**
     * 办理巡检工单请求
     * @author hedongwei@wistronits.com
     * @date  2019/7/6 11:37
     */
    @Test
    public void completeInspectionProcForApp() {
        CompleteInspectionProcReq completeReq = new CompleteInspectionProcReq();
        procInspectionController.completeInspectionProcForApp(completeReq);
    }


    /**
     * 巡检工单详情
     * @author hedongwei@wistronits.com
     * @date  2019/7/6 11:37
     */
    @Test
    public void getInspectionProcById() {
        String procId = "1";
        procInspectionController.getInspectionProcById(procId);
    }


    /**
     * 查询已完工巡检记录
     * @author hedongwei@wistronits.com
     * @date  2019/7/6 11:37
     */
    @Test
    public void queryListInspectionCompleteRecordByPage() {
        QueryCondition<ProcBaseReq> queryCondition = new QueryCondition<>();
        procInspectionController.queryListInspectionCompleteRecordByPage(queryCondition);
    }

    /**
     * 查询已完成的巡检信息
     * @author hedongwei@wistronits.com
     * @date  2019/7/6 11:37
     */
    @Test
    public void queryListCompleteInspectionByPage() {
        QueryCondition<CompleteInspectionByPageReq> req = new QueryCondition<>();
        procInspectionController.queryListCompleteInspectionByPage(req);


        QueryCondition<CompleteInspectionByPageReq> reqParam = new QueryCondition<>();
        CompleteInspectionByPageReq pageReq = new CompleteInspectionByPageReq();
        pageReq.setDeviceName("1");
        reqParam.setBizCondition(pageReq);
        procInspectionController.queryListCompleteInspectionByPage(reqParam);


        QueryCondition<CompleteInspectionByPageReq> reqParamInfo = new QueryCondition<>();
        CompleteInspectionByPageReq pageReqInfo = new CompleteInspectionByPageReq();
        pageReqInfo.setProcId("1");
        reqParamInfo.setBizCondition(pageReqInfo);
        procInspectionController.queryListCompleteInspectionByPage(reqParamInfo);
    }

    /**
     * 获取巡检工单设施数量
     * @author hedongwei@wistronits.com
     * @date  2019/7/6 11:37
     */
    @Test
    public void queryProcInspectionByProcInspectionId() {
        ProcInspection procInspection = new ProcInspection();
        procInspectionController.queryProcInspectionByProcInspectionId(procInspection);
    }


    /**
     * 巡检任务关联工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/7/6 11:37
     */
    @Test
    public void queryListInspectionTaskRelationProcByPage() {
        QueryCondition<ProcBaseReq> req = new QueryCondition<>();
        procInspectionController.queryListInspectionTaskRelationProcByPage(req);


        QueryCondition<ProcBaseReq> reqParam = new QueryCondition<>();
        ProcBaseReq procBaseReq = new ProcBaseReq();
        procBaseReq.setRemark("1");
        reqParam.setBizCondition(procBaseReq);
        procInspectionController.queryListInspectionTaskRelationProcByPage(reqParam);

        QueryCondition<ProcBaseReq> reqParamInfo = new QueryCondition<>();
        ProcBaseReq procBaseReqInfo = new ProcBaseReq();
        procBaseReqInfo.setInspectionTaskId("1");
        reqParamInfo.setBizCondition(procBaseReqInfo);
        procInspectionController.queryListInspectionTaskRelationProcByPage(reqParamInfo);
    }


    /**
     * 巡检任务新增巡检工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/7/6 11:37
     */
    @Test
    public void jobAddInspectionProc() {
        InspectionTask inspectionTask = new InspectionTask();
        procInspectionController.jobAddInspectionProc(inspectionTask);
    }



}