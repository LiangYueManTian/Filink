package com.fiberhome.filink.workflowbusinessserver.service.impl.procinspection;

import com.fiberhome.filink.bean.*;
import com.fiberhome.filink.deviceapi.api.AreaFeign;
import com.fiberhome.filink.deviceapi.api.DeviceFeign;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import com.fiberhome.filink.userapi.api.DepartmentFeign;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcBase;
import com.fiberhome.filink.workflowbusinessserver.bean.procinspection.ProcInspection;
import com.fiberhome.filink.workflowbusinessserver.constant.ProcBaseConstants;
import com.fiberhome.filink.workflowbusinessserver.constant.ProcInspectionConstants;
import com.fiberhome.filink.workflowbusinessserver.dao.procinspection.ProcInspectionDao;
import com.fiberhome.filink.workflowbusinessserver.export.procinspection.InspectionProcCompleteExport;
import com.fiberhome.filink.workflowbusinessserver.export.procinspection.InspectionProcProcessExport;
import com.fiberhome.filink.workflowbusinessserver.req.procbase.ProcBaseReq;
import com.fiberhome.filink.workflowbusinessserver.req.procinspection.ProcInspectionReq;
import com.fiberhome.filink.workflowbusinessserver.resp.ProcBaseResp;
import com.fiberhome.filink.workflowbusinessserver.resp.ProcessInfo;
import com.fiberhome.filink.workflowbusinessserver.service.inspectiontask.InspectionTaskService;
import com.fiberhome.filink.workflowbusinessserver.service.procbase.ProcBaseService;
import com.fiberhome.filink.workflowbusinessserver.service.procbase.ProcLogService;
import com.fiberhome.filink.workflowbusinessserver.service.process.WorkflowService;
import com.fiberhome.filink.workflowbusinessserver.service.procinspection.ProcInspectionRecordService;
import mockit.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.*;

/**
 * 巡检工单逻辑层测试类
 * @author hedongwei@wistronits.com
 * @date 2019/7/25 16:56
 */
@RunWith(MockitoJUnitRunner.class)
public class ProcInspectionServiceTest {

    /**
     * 测试对象巡检工单逻辑层
     */
    @Tested
    private ProcInspectionServiceImpl procInspectionService;

    /**
     * 工单逻辑层
     */
    @Injectable
    private ProcBaseService procBaseService;

    /**
     * 日志处理类
     */
    @Injectable
    private LogProcess logProcess;


    /**
     * 工单流程逻辑层
     */
    @Injectable
    private WorkflowService workflowService;

    /**
     * 巡检工单持久层
     */
    @Injectable
    private ProcInspectionDao procInspectionDao;

    /**
     * 巡检任务逻辑层
     */
    @Injectable
    private InspectionTaskService inspectionTaskService;

    /**
     * 巡检任务记录逻辑层
     */
    @Injectable
    private ProcInspectionRecordService procInspectionRecordService;

    /**
     * 未完工巡检工单导出类
     */
    @Injectable
    private InspectionProcProcessExport inspectionProcProcessExport;

    /**
     * 已完成巡检工单导出类
     */
    @Injectable
    private InspectionProcCompleteExport inspectionProcCompleteExport;

    /**
     * 设施远程调用
     */
    @Injectable
    private DeviceFeign deviceFeign;

    /**
     * 部门远程调用
     */
    @Injectable
    private DepartmentFeign departmentFeign;

    /**
     * 区域远程调用
     */
    @Injectable
    private AreaFeign areaFeign;

    /**
     * 工单日志逻辑层
     */
    @Injectable
    private ProcLogService procLogService;

    /**
     * 最大导出条数
     */
    @Injectable
    private Integer maxExportDataSize;

    /**
     * 系统语言
     */
    @Injectable
    private SystemLanguageUtil systemLanguage;

    /**
     * 修改巡检工单
     */
    @Test
    public void updateProcInspectionInfo() {
        ProcInspection procInspection = new ProcInspection();
        procInspectionService.updateProcInspectionInfo(procInspection);
    }

    /**
     * 查询工单巡检信息
     */
    @Test
    public void selectProcInspectionOne() {
        ProcInspection procInspection = new ProcInspection();
        procInspectionService.selectProcInspectionOne(procInspection);

    }

    /**
     * 根据流程编号查询巡检工单
     */
    @Test
    public void selectInspectionProcByProcId() {
        String procId = "1";
        procInspectionService.selectInspectionProcByProcId(procId);
    }

    /**
     * 查询巡检工单列表
     */
    @Test
    public void selectInspectionProcByProcIds() {
        Set<String> procIdSet = new HashSet<String>();
        procInspectionService.selectInspectionProcByProcIds(procIdSet);
    }


    /**
     * 查询巡检未完工列表
     * @author hedongwei@wistronits.com
     * @date  2019/7/25 18:36
     */
    @Test
    public void queryListInspectionProcessByPage() {
        QueryCondition<ProcBaseReq> queryCondition = new QueryCondition<>();
        procInspectionService.queryListInspectionProcessByPage(queryCondition);

        PageCondition pageCondition = new PageCondition();
        pageCondition.setPageNum(1);
        pageCondition.setPageSize(20);
        queryCondition.setPageCondition(pageCondition);
        queryCondition.setSortCondition(new SortCondition());
        queryCondition.setFilterConditions(new ArrayList<>());
        queryCondition.setBizCondition(new ProcBaseReq());
        procInspectionService.queryListInspectionProcessByPage(queryCondition);
    }

    /**
     * 获取巡检工单页面返回的值
     * @author hedongwei@wistronits.com
     * @date  2019/7/25 18:42
     */
    @Test
    public void getInspectionVo() {
        List<ProcBaseResp> procBaseRespList = new ArrayList<>();
        ProcBaseResp procBaseResp = new ProcBaseResp();
        procBaseResp.setProcId("1");
        procBaseRespList.add(procBaseResp);
        Result result = new Result();
        result.setData(procBaseRespList);
        procInspectionService.getInspectionVo(result);
    }


    /**
     * 获取巡检工单数据
     * @author hedongwei@wistronits.com
     * @date  2019/7/25 18:44
     */
    @Test
    public void getInspectionVoInfo() {
        List<ProcBaseResp> procBaseRespList = new ArrayList<>();
        procInspectionService.getInspectionVoInfo(procBaseRespList);
    }

    /**
     * 获取巡检任务关联工单页面显示
     * @author hedongwei@wistronits.com
     * @date  2019/7/25 18:49
     */
    @Test
    public void getInspectionTaskRelatedProcVo() {
        List<ProcBaseResp> procBaseRespList = new ArrayList<>();
        Result result = new Result();
        result.setData(procBaseRespList);
        procInspectionService.getInspectionTaskRelatedProcVo(result);
        ProcBaseResp procBaseResp = new ProcBaseResp();
        procBaseResp.setProcId("1");
        procBaseRespList.add(procBaseResp);
        result.setData(procBaseRespList);
        procInspectionService.getInspectionTaskRelatedProcVo(result);
    }

    /**
     * 新增巡检工单
     * @author hedongwei@wistronits.com
     * @date  2019/7/25 18:58
     */
    @Test
    public void addInspectionProc() {
        new MockUp<ProcInspectionServiceImpl>(){
            @Mock
            public Result addInspectionProcess(ProcInspectionReq req, String operate, boolean isRegenerate, String isAlarmViewCall) {
                return null;
            }
        };
        ProcInspectionReq req = new ProcInspectionReq();
        String operate = "";
        String isAlarmViewCall = "";
        procInspectionService.addInspectionProc(req, operate, isAlarmViewCall);

        new MockUp<ProcInspectionServiceImpl>(){
            @Mock
            public Result addInspectionProcess(ProcInspectionReq req, String operate, boolean isRegenerate, String isAlarmViewCall) {
                return ResultUtils.success();
            }
        };
        procInspectionService.addInspectionProc(req, operate, isAlarmViewCall);
    }


    /**
     * 重新生成巡检工单
     * @author hedongwei@wistronits.com
     * @date  2019/7/25 19:03
     */
    @Test
    public void regenerateInspectionProc() {
        new MockUp<ProcInspectionServiceImpl>(){
            @Mock
            public Result addInspectionProcess(ProcInspectionReq req, String operate, boolean isRegenerate, String isAlarmViewCall) {
                return null;
            }
        };
        ProcInspectionReq req = new ProcInspectionReq();
        String operate = "";
        String isAlarmViewCall = "";
        procInspectionService.regenerateInspectionProc(req, operate);

        new MockUp<ProcInspectionServiceImpl>(){
            @Mock
            public Result addInspectionProcess(ProcInspectionReq req, String operate, boolean isRegenerate, String isAlarmViewCall) {
                return ResultUtils.success();
            }
        };
        procInspectionService.regenerateInspectionProc(req, operate);
    }


    /**
     * 新增巡检工单过程
     * @author hedongwei@wistronits.com
     * @date  2019/7/25 19:14
     */
    @Test
    public void addInspectionProcess() {
        ProcInspectionReq req = new ProcInspectionReq();
        req.setProcId("1");
        String operate = ProcInspectionConstants.OPERATE_UPDATE;
        req.setProcResourceType(ProcBaseConstants.PROC_RESOURCE_TYPE_2);
        boolean isRegenerate = true;
        String isAlarmViewCall = "";

        try {
            procInspectionService.addInspectionProcess(req, operate, isRegenerate, isAlarmViewCall);
        } catch (Exception e) {

        }


        operate = ProcInspectionConstants.OPERATE_ADD;

        new Expectations(ProcInspectionReq.class) {
            {
                ProcInspectionReq.validateProcInspectionReq((ProcInspectionReq) any, anyString);
                result = true;
            }
        };

        new MockUp<ProcInspectionServiceImpl>(){
            @Mock
            public ProcessInfo saveInspectionProcParamToProcessInfo(ProcInspectionReq req, Date nowDate, String operate) {
                ProcessInfo processInfo = new ProcessInfo();
                ProcBase procBase = new ProcBase();
                procBase.setProcId("1");
                processInfo.setProcBase(procBase);
                return processInfo;
            }
        };


        new Expectations() {
            {
                 procBaseService.regenerateProc((ProcessInfo) any);
                 result = ResultUtils.success();
            }
        };

        new MockUp<ProcInspectionServiceImpl>(){
            @Mock
            public void regenerateProcInspectionLog(ProcInspectionReq req, List<ProcBase> procBaseList, String isAlarmViewCall) {
                return;
            }
        };
        procInspectionService.addInspectionProcess(req, operate, isRegenerate, isAlarmViewCall);


        isRegenerate = false;

        new Expectations(ProcInspectionReq.class) {
            {
                ProcInspectionReq.validateProcInspectionReq((ProcInspectionReq) any, anyString);
                result = true;
            }
        };

        new MockUp<ProcInspectionServiceImpl>(){
            @Mock
            public ProcessInfo saveInspectionProcParamToProcessInfo(ProcInspectionReq req, Date nowDate, String operate) {
                ProcessInfo processInfo = new ProcessInfo();
                ProcBase procBase = new ProcBase();
                procBase.setProcId("1");
                processInfo.setProcBase(procBase);
                return processInfo;
            }
        };


        new Expectations() {
            {
                procBaseService.addProcBase((ProcessInfo) any);
                result = ResultUtils.success();
            }
        };

        new MockUp<ProcInspectionServiceImpl>(){
            @Mock
            public void addProcInspectionLog(ProcInspectionReq req, List<ProcBase> procBaseList, String isAlarmViewCall) {
                return;
            }
        };
        procInspectionService.addInspectionProcess(req, operate, isRegenerate, isAlarmViewCall);

        operate = ProcInspectionConstants.OPERATE_UPDATE;
        req.setProcResourceType(ProcBaseConstants.PROC_RESOURCE_TYPE_2);
        isRegenerate = true;
        isAlarmViewCall = "";

        try {
            procInspectionService.addInspectionProcess(req, operate, isRegenerate, isAlarmViewCall);
        } catch (Exception e) {

        }
    }


}
