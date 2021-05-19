package com.fiberhome.filink.workflowbusinessserver.service.impl.procbase;

import com.fiberhome.filink.alarmcurrentapi.api.AlarmCurrentFeign;
import com.fiberhome.filink.alarmcurrentapi.bean.AlarmCurrent;
import com.fiberhome.filink.alarmhistoryapi.api.AlarmHistoryFeign;
import com.fiberhome.filink.alarmhistoryapi.bean.AlarmHistory;
import com.fiberhome.filink.bean.*;
import com.fiberhome.filink.deviceapi.api.DeviceFeign;
import com.fiberhome.filink.deviceapi.bean.AreaInfo;
import com.fiberhome.filink.deviceapi.bean.DeviceInfoDto;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import com.fiberhome.filink.userapi.api.DepartmentFeign;
import com.fiberhome.filink.userapi.api.UserFeign;
import com.fiberhome.filink.userapi.bean.Department;
import com.fiberhome.filink.userapi.bean.Role;
import com.fiberhome.filink.userapi.bean.RoleDeviceType;
import com.fiberhome.filink.userapi.bean.User;
import com.fiberhome.filink.workflowbusinessserver.bean.inspectiontask.InspectionTask;
import com.fiberhome.filink.workflowbusinessserver.bean.inspectiontask.InspectionTaskRecord;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcBase;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcRelatedAlarm;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcRelatedDepartment;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcRelatedDevice;
import com.fiberhome.filink.workflowbusinessserver.bean.procclear.ProcClearFailure;
import com.fiberhome.filink.workflowbusinessserver.bean.procinspection.ProcInspection;
import com.fiberhome.filink.workflowbusinessserver.bean.procinspection.ProcInspectionRecord;
import com.fiberhome.filink.workflowbusinessserver.component.log.AddLogForApp;
import com.fiberhome.filink.workflowbusinessserver.constant.ProcBaseConstants;
import com.fiberhome.filink.workflowbusinessserver.constant.WorkFlowBusinessConstants;
import com.fiberhome.filink.workflowbusinessserver.dao.procbase.ProcBaseDao;
import com.fiberhome.filink.workflowbusinessserver.req.inspectiontask.UpdateInspectionStatusReq;
import com.fiberhome.filink.workflowbusinessserver.req.procbase.*;
import com.fiberhome.filink.workflowbusinessserver.req.procbase.procrelated.ProcRelatedDeviceListForDeviceIdsReq;
import com.fiberhome.filink.workflowbusinessserver.req.process.CompleteProcessInfoReq;
import com.fiberhome.filink.workflowbusinessserver.req.process.StartProcessInfoReq;
import com.fiberhome.filink.workflowbusinessserver.req.process.TurnInfoReq;
import com.fiberhome.filink.workflowbusinessserver.resp.ProcBaseResp;
import com.fiberhome.filink.workflowbusinessserver.resp.ProcessDetail;
import com.fiberhome.filink.workflowbusinessserver.resp.ProcessInfo;
import com.fiberhome.filink.workflowbusinessserver.resp.app.procbase.ProcBaseRespForApp;
import com.fiberhome.filink.workflowbusinessserver.resp.app.procclear.ClearFailureDownLoadDetail;
import com.fiberhome.filink.workflowbusinessserver.resp.app.procinspection.InspectionDownLoadDetail;
import com.fiberhome.filink.workflowbusinessserver.service.alarmprocess.AlarmProcessService;
import com.fiberhome.filink.workflowbusinessserver.service.impl.process.WorkflowServiceImpl;
import com.fiberhome.filink.workflowbusinessserver.service.inspectiontask.InspectionTaskRecordService;
import com.fiberhome.filink.workflowbusinessserver.service.inspectiontask.InspectionTaskService;
import com.fiberhome.filink.workflowbusinessserver.service.procbase.ProcBaseService;
import com.fiberhome.filink.workflowbusinessserver.service.procbase.ProcLogService;
import com.fiberhome.filink.workflowbusinessserver.service.procbase.procrelated.ProcRelatedService;
import com.fiberhome.filink.workflowbusinessserver.service.procclear.ProcClearFailureService;
import com.fiberhome.filink.workflowbusinessserver.service.procinspection.ProcInspectionRecordService;
import com.fiberhome.filink.workflowbusinessserver.service.procinspection.ProcInspectionService;
import com.fiberhome.filink.workflowbusinessserver.stream.WorkflowBusinessStreams;
import com.fiberhome.filink.workflowbusinessserver.utils.procbase.ProcBaseValidate;
import com.fiberhome.filink.workflowbusinessserver.vo.procbase.TurnUserListVo;
import mockit.*;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;

import java.util.*;


/**
 * 工单管理测试类
 * @author hedongwei@wistronits.com
 * @date 2019/7/8 20:42
 */
@RunWith(JMockit.class)
public class ProcBaseServiceTest {

    @Tested
    private ProcBaseServiceImpl procBaseService;

    @Injectable
    private ProcBaseDao procBaseDao;

    @Injectable
    private ProcInspectionRecordService procInspectionRecordService;

    @Injectable
    private ProcInspectionService procInspectionService;

    @Injectable
    private ProcClearFailureService procClearFailureService;

    @Injectable
    private WorkflowServiceImpl workflowService;

    @Injectable
    private InspectionTaskRecordService inspectionTaskRecordService;

    @Injectable
    private InspectionTaskService inspectionTaskService;

    @Injectable
    private DeviceFeign deviceFeign;

    @Injectable
    private UserFeign userFeign;

    @Injectable
    private AlarmCurrentFeign alarmCurrentFeign;

    @Injectable
    private AlarmHistoryFeign alarmHistoryFeign;

    @Injectable
    private AddLogForApp addLogForApp;

    @Injectable
    private AlarmProcessService alarmProcessService;

    @Injectable
    private ProcRelatedService procRelatedService;

    @Injectable
    private ProcLogService procLogService;

    /**
     * 远程调用日志服务
     */
    @Injectable
    private LogProcess logProcess;

    /**
     * 注入上下文
     */
    @Injectable
    private ApplicationContext context;

    /**
     * 远程调用部门服务
     */
    @Injectable
    private DepartmentFeign departmentFeign;

    @Injectable
    private WorkflowBusinessStreams workflowBusinessStreams;


    /**
     * 表示代理对象，不是目标对象
     */
    @Injectable
    private ProcBaseService proxySelf;

    /**
     * 系统语言
     */
    @Injectable
    private SystemLanguageUtil systemLanguage;

    /**
     * app批量下载条数
     */
    @Injectable
    private Integer batchDownloadProcNum;


    /**
     * 调用工单信息类
     */
    @Test
    public void callProcessInfo() {
        ProcessInfo processInfo = new ProcessInfo();
        processInfo.getProcBaseResp();
        processInfo.getTitle();
    }


    /**
     * 调用工单详情类
     */
    @Test
    public void callProcessDetail() {
        ProcessDetail processDetail = new ProcessDetail();
        processDetail.getAlarmName();
        processDetail.getInspectionStartDate();
        processDetail.getInspectionStartTime();
        processDetail.getInspectionEndDate();
        processDetail.getInspectionEndTime();
        processDetail.getInspectionDeviceCount();
        processDetail.getInspectionCompletedCount();
        processDetail.getProgressSpeed();
        processDetail.getEcTime();
        processDetail.getRcTime();
        processDetail.getLastDays();
        processDetail.getIsSelectAll();
        processDetail.getcTime();
        processDetail.getuTime();
        processDetail.getProcRelatedDevices();
        processDetail.getProcRelatedDepartments();
    }


    /**
     * 调用转派用户集合返回类
     * @author hedongwei@wistronits.com
     * @date  2019/8/2 10:56
     */
    @Test
    public void callTurnUserListVoClass() {
        TurnUserListVo turnUserListVo = new TurnUserListVo();
        turnUserListVo.getId();
        turnUserListVo.getUserCode();
        turnUserListVo.getUserName();
        turnUserListVo.getUserNickname();
    }

    /**
     * 查询标题是否存在
     * @author hedongwei@wistronits.com
     * @date  2019/7/11 19:29
     */
    @Test
    public void queryTitleIsExists() {
        String procId = "";
        String title = "1";
        new Expectations() {
            {
                procBaseDao.queryTitleIsExists(anyString);
                ProcBase procBase = new ProcBase();
                procBase.setProcId("1");
                result = procBase;
            }
        };
        procBaseService.queryTitleIsExists(procId, title);

        procId = "1";
        new Expectations() {
            {
                procBaseDao.queryTitleIsExists(anyString);
                ProcBase procBase = new ProcBase();
                procBase.setProcId("1");
                result = procBase;
            }
        };
        procBaseService.queryTitleIsExists(procId, title);

        new Expectations() {
            {
                procBaseDao.queryTitleIsExists(anyString);
                ProcBase procBase = new ProcBase();
                procBase.setProcId("2");
                result = procBase;
            }
        };
        procBaseService.queryTitleIsExists(procId, title);
    }


    /**
     * 新增工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/7/11 20:15
     */
    @Test
    public void addProcBase() {
        ProcessInfo processInfo = new ProcessInfo();
        ProcBase procBase = new ProcBase();
        procBase.setProcType(ProcBaseConstants.PROC_CLEAR_FAILURE);
        procBase.setDeptType(WorkFlowBusinessConstants.DEPT_TYPE_DEPT);
        procBase.setProcResourceType(ProcBaseConstants.PROC_RESOURCE_TYPE_3);
        processInfo.setProcBase(procBase);
        new MockUp<ProcBaseServiceImpl>(){
            @Mock
            public ProcBase addProcBaseInfo(ProcessInfo processInfo) {
                return processInfo.getProcBase();
            }
        };

        new Expectations() {
            {
                workflowService.startProcess((StartProcessInfoReq) any);
                result = ResultUtils.success("name");
            }
        };
        procBaseService.addProcBase(processInfo);


        new MockUp<ProcBaseServiceImpl>(){
            @Mock
            public ProcBase addProcBaseInfo(ProcessInfo processInfo) {
                procBase.setProcResourceType(ProcBaseConstants.PROC_RESOURCE_TYPE_2);
                procBase.setProcType(ProcBaseConstants.PROC_INSPECTION);
                procBase.setProcId("1");
                return procBase;
            }
        };

        new Expectations() {
            {
                workflowService.startProcess((StartProcessInfoReq) any);
                result = ResultUtils.success("name");
            }
        };
        ProcInspection procInspection = new ProcInspection();
        procInspection.setInspectionTaskId("1");
        processInfo.setProcInspection(procInspection);
        procBaseService.addProcBase(processInfo);
    }




    /**
     * 新增工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/7/13 16:36
     */
    @Test
    public void addProcBaseInfo() {
        ProcessInfo processInfo = new ProcessInfo();
        ProcBase procBase = new ProcBase();
        procBase.setProcType(ProcBaseConstants.PROC_CLEAR_FAILURE);
        processInfo.setProcBase(procBase);
        ProcBaseReq req = new ProcBaseReq();
        BeanUtils.copyProperties(procBase, req);
        processInfo.setProcBaseReq(req);
        procBaseService.addProcBaseInfo(processInfo);
        ProcInspection procInspection = new ProcInspection();
        procInspection.setInspectionTaskId("1");
        processInfo.setProcInspection(procInspection);
        new Expectations() {
            {
                inspectionTaskService.updateInspectionStatus((UpdateInspectionStatusReq) any);
                Integer resultCode = -1;
                String msg = "";
                result = ResultUtils.warn(resultCode, msg);
            }
        };
        try {
            procBaseService.addProcBaseInfo(processInfo);
        } catch(Exception e) {

        }
    }

    /**
     * 查询工单是否能够重新生成
     * @author hedongwei@wistronits.com
     * @date  2019/7/13 16:55
     */
    @Test
    public void checkProcProcessForRegenerate() {
        ProcessInfo processInfo = new ProcessInfo();
        ProcBase procBase = new ProcBase();
        processInfo.setProcBase(procBase);
        procBaseService.checkProcProcessForRegenerate(processInfo);
        //查询工单信息
        this.expectationsSearchProc();

        procBase.setRegenerateId("1");
        procBase.setProcType(ProcBaseConstants.PROC_INSPECTION);
        processInfo.setProcBase(procBase);
        procBaseService.checkProcProcessForRegenerate(processInfo);

        //查询工单信息
        new Expectations() {
            {
                procBaseDao.queryProcessByProcId(anyString, anyLong);
                ProcBaseResp procBaseInfo = new ProcBaseResp();
                procBaseInfo.setProcId("1");
                procBaseInfo.setStatus(ProcBaseConstants.PROC_STATUS_SINGLE_BACK);
                result = procBaseInfo;

                procClearFailureService.checkProcParamsForClearFailure((ProcessInfo) any);
                Integer resultCode = -1;
                String msg = "";
                result = ResultUtils.warn(resultCode, msg);
            }
        };
        procBase.setProcType(ProcBaseConstants.PROC_CLEAR_FAILURE);
        processInfo.setProcBase(procBase);
        procBaseService.checkProcProcessForRegenerate(processInfo);
    }

    /**
     * 查询工单方法
     * @author hedongwei@wistronits.com
     * @date  2019/7/13 16:54
     */
    public void expectationsSearchProc() {
        new Expectations() {
            {
                procBaseDao.queryProcessByProcId(anyString, anyLong);
                ProcBaseResp procBaseInfo = new ProcBaseResp();
                procBaseInfo.setProcId("1");
                procBaseInfo.setStatus(ProcBaseConstants.PROC_STATUS_SINGLE_BACK);
                result = procBaseInfo;
            }
        };
    }

    /**
     * 查询工单方法
     * @author hedongwei@wistronits.com
     * @date  2019/7/13 16:54
     */
    public void expectationsSearchProc(String type) {
        new Expectations() {
            {
                procBaseDao.queryProcessByProcId(anyString, anyLong);
                ProcBaseResp procBaseInfo = new ProcBaseResp();
                procBaseInfo.setProcId("1");
                procBaseInfo.setStatus(ProcBaseConstants.PROC_STATUS_SINGLE_BACK);
                procBaseInfo.setProcType(type);
                result = procBaseInfo;
            }
        };
    }

    /**
     * 重新生成工单过程
     * @author hedongwei@wistronits.com
     * @date  2019/7/13 17:30
     */
    @Test
    public void regenerateProcProcess() {
        ProcessInfo processInfo = new ProcessInfo();
        ProcBase procBase = new ProcBase();
        procBase.setProcId("1");
        procBase.setRegenerateId("2");
        processInfo.setProcBase(procBase);


        new Expectations() {
            {
                procBaseDao.queryProcessByProcId(anyString, anyLong);
                ProcBaseResp procBaseInfo = new ProcBaseResp();
                procBaseInfo.setProcId("1");
                procBaseInfo.setStatus(ProcBaseConstants.PROC_STATUS_SINGLE_BACK);
                procBaseInfo.setProcType(ProcBaseConstants.PROC_INSPECTION);
                procBaseInfo.setStatus(ProcBaseConstants.PROC_STATUS_SINGLE_BACK);
                procBaseInfo.setIsCheckSingleBack(ProcBaseConstants.NOT_CHECK_SINGLE_BACK);
                result = procBaseInfo;
            }
        };
        new MockUp<ProcBaseServiceImpl>(){
            @Mock
            public ProcBase addProcBaseInfo(ProcessInfo processInfo) {
                procBase.setProcResourceType(ProcBaseConstants.PROC_RESOURCE_TYPE_2);
                procBase.setProcType(ProcBaseConstants.PROC_INSPECTION);
                procBase.setStatus(ProcBaseConstants.PROC_STATUS_SINGLE_BACK);
                procBase.setIsCheckSingleBack(ProcBaseConstants.NOT_CHECK_SINGLE_BACK);
                procBase.setProcId("2");
                return procBase;
            }
        };

        procBaseService.regenerateProcProcess(processInfo);

        new Expectations() {
            {
                procBaseDao.queryProcessByProcId(anyString, anyLong);
                ProcBaseResp procBaseInfo = new ProcBaseResp();
                procBaseInfo.setProcId("1");
                procBaseInfo.setStatus(ProcBaseConstants.PROC_STATUS_SINGLE_BACK);
                procBaseInfo.setProcType(ProcBaseConstants.PROC_CLEAR_FAILURE);
                procBaseInfo.setStatus(ProcBaseConstants.PROC_STATUS_SINGLE_BACK);
                procBaseInfo.setIsCheckSingleBack(ProcBaseConstants.NOT_CHECK_SINGLE_BACK);
                result = procBaseInfo;
            }
        };
        new MockUp<ProcBaseServiceImpl>(){
            @Mock
            public ProcBase addProcBaseInfo(ProcessInfo processInfo) {
                procBase.setProcResourceType(ProcBaseConstants.PROC_RESOURCE_TYPE_2);
                procBase.setProcType(ProcBaseConstants.PROC_CLEAR_FAILURE);
                procBase.setStatus(ProcBaseConstants.PROC_STATUS_SINGLE_BACK);
                procBase.setIsCheckSingleBack(ProcBaseConstants.NOT_CHECK_SINGLE_BACK);
                procBase.setProcId("2");
                return procBase;
            }
        };

        procBaseService.regenerateProcProcess(processInfo);

    }


    /**
     * 重新生成工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/7/13 17:30
     */
    @Test
    public void regenerateProc() {
        ProcessInfo processInfo = new ProcessInfo();
        ProcBase procBase = new ProcBase();
        procBase.setProcType(ProcBaseConstants.PROC_CLEAR_FAILURE);
        procBase.setRegenerateId("1");
        procBase.setProcId("2");
        procBase.setDeptType(WorkFlowBusinessConstants.DEPT_TYPE_DEPT);
        processInfo.setProcBase(procBase);

        new Expectations() {
            {
                procBaseDao.queryProcessByProcId(anyString, anyLong);
                ProcBaseResp procBaseInfo = new ProcBaseResp();
                procBaseInfo.setProcId("1");
                procBaseInfo.setStatus(ProcBaseConstants.PROC_STATUS_SINGLE_BACK);
                procBaseInfo.setProcType(ProcBaseConstants.PROC_INSPECTION);
                procBaseInfo.setStatus(ProcBaseConstants.PROC_STATUS_SINGLE_BACK);
                procBaseInfo.setIsCheckSingleBack(ProcBaseConstants.NOT_CHECK_SINGLE_BACK);
                result = procBaseInfo;
            }
        };

        new MockUp<ProcBaseServiceImpl>(){
            @Mock
            public ProcBase regenerateProcProcess(ProcessInfo processInfo) {
                procBase.setProcResourceType(ProcBaseConstants.PROC_RESOURCE_TYPE_2);
                procBase.setProcType(ProcBaseConstants.PROC_CLEAR_FAILURE);
                procBase.setStatus(ProcBaseConstants.PROC_STATUS_SINGLE_BACK);
                procBase.setIsCheckSingleBack(ProcBaseConstants.NOT_CHECK_SINGLE_BACK);
                procBase.setProcId("2");
                return procBase;
            }
        };


        new Expectations() {
            {
                workflowService.startProcess((StartProcessInfoReq) any);
                result = ResultUtils.success("name");
            }
        };
        procBaseService.regenerateProc(processInfo);

    }



    /**
     * 校验重新生成工单结果
     * @author hedongwei@wistronits.com
     * @date  2019/4/4 17:22
     */
    @Test
    public void checkRegenerateCondition() {
        String procId = null;
        procBaseService.checkRegenerateCondition(procId);
        procId = "1";
        new Expectations() {
            {
                procBaseDao.queryProcessByProcId(anyString, anyLong);
                result = null;
            }
        };
        procBaseService.checkRegenerateCondition(procId);

        new Expectations() {
            {
                procBaseDao.queryProcessByProcId(anyString, anyLong);
                ProcBaseResp resp = new ProcBaseResp();
                resp.setProcId("1");
                resp.setStatus(ProcBaseConstants.PROC_STATUS_PROCESSING);
                result = resp;
            }
        };
        try {
            procBaseService.checkRegenerateCondition(procId);
        } catch (Exception e) {

        }
    }

    /**
     * 修改工单
     * @author hedongwei@wistronits.com
     * @date  2019/7/13 17:35
     */
    @Test
    public void updateProcessById() {
        ProcessInfo processInfo = new ProcessInfo();
        ProcBase procBase = new ProcBase();
        processInfo.setProcBase(procBase);
        ProcBaseReq req = new ProcBaseReq();
        BeanUtils.copyProperties(procBase, req);
        processInfo.setProcBaseReq(req);
        procBaseService.updateProcessById(processInfo);
        procBase.setProcId("1");
        procBase.setProcType(ProcBaseConstants.PROC_INSPECTION);
        procBase.setDeptType("1");
        req = new ProcBaseReq();
        BeanUtils.copyProperties(procBase, req);
        processInfo.setProcBase(procBase);
        processInfo.setProcBaseReq(req);
        procBaseService.updateProcessById(processInfo);
        new Expectations() {
            {
                procBaseDao.queryProcessByProcId(anyString, anyLong);
                ProcBaseResp procBaseInfo = new ProcBaseResp();
                procBaseInfo.setProcId("1");
                procBaseInfo.setStatus(ProcBaseConstants.PROC_STATUS_ASSIGNED);
                result = procBaseInfo;
            }
        };
        new MockUp<ProcBaseServiceImpl>(){
            @Mock
            public void updateProcBaseByIdProcess(ProcBase procBase, ProcessInfo processInfo) {
                return;
            }
        };

        new Expectations() {
            {
                workflowService.startProcess((StartProcessInfoReq) any);
                String msg = "name";
                result = ResultUtils.success(msg);
            }
        };
        procBaseService.updateProcessById(processInfo);
    }


    /**
     * 修改工单参数
     * @author hedongwei@wistronits.com
     * @date  2019/7/13 17:35
     */
    @Test
    public void updateProcBaseByIdProcess() {
        ProcBase procBase = new ProcBase();
        procBase.setProcType(ProcBaseConstants.PROC_INSPECTION);
        ProcessInfo processInfo = new ProcessInfo();
        ProcBaseReq procBaseReq = new ProcBaseReq();
        procBase.setStatus(ProcBaseConstants.PROC_STATUS_ASSIGNED);
        procBaseReq.setProcId("1");
        processInfo.setProcBase(procBase);
        processInfo.setProcBaseReq(procBaseReq);
        procBaseService.updateProcBaseByIdProcess(procBase, processInfo);

        procBase.setStatus(ProcBaseConstants.PROC_STATUS_PENDING);
        processInfo.setProcBase(procBase);
        procBaseService.updateProcBaseByIdProcess(procBase, processInfo);

        procBase.setStatus(ProcBaseConstants.PROC_STATUS_PENDING);
        procBase.setProcType(ProcBaseConstants.PROC_CLEAR_FAILURE);
        processInfo.setProcBase(procBase);
        procBaseService.updateProcBaseByIdProcess(procBase, processInfo);
    }

    /**
     * 开启流程
     * @author hedongwei@wistronits.com
     * @date  2019/7/13 17:54
     */
    @Test
    public void startProcessInfo() {
        ProcessInfo processInfo = new ProcessInfo();
        ProcBase procBase = new ProcBase();
        procBase.setDeptType(WorkFlowBusinessConstants.DEPT_TYPE_DEPT);
        procBase.setProcId("1");
        processInfo.setProcBase(procBase);

        new Expectations() {
            {
                workflowService.startProcess((StartProcessInfoReq) any);
                Integer resultCode = -1;
                String msg = "";
                result = ResultUtils.warn(resultCode, msg);
            }
        };
        try {
            procBaseService.startProcessInfo(processInfo);
        } catch (Exception e) {

        }
    }


    /**
     * 新增巡检记录
     * @author hedongwei@wistronits.com
     * @date  2019/7/13 18:04
     */
    @Test
    public void addInspectionRecord() {
        ProcessInfo processInfo = new ProcessInfo();
        ProcInspection procInspection = new ProcInspection();
        procInspection.setInspectionTaskId("1");
        processInfo.setProcInspection(procInspection);
        new Expectations() {
            {
                inspectionTaskService.selectById(anyString);
                result = null;
            }
        };
        try {
            procBaseService.addInspectionRecord(processInfo);
        } catch (Exception e) {

        }

        new Expectations() {
            {
                inspectionTaskService.selectById(anyString);
                InspectionTask inspectionTask = new InspectionTask();
                inspectionTask.setInspectionTaskId("1");
                result = inspectionTask;

                inspectionTaskRecordService.addInspectionTaskRecord((InspectionTaskRecord) any);
                result = null;
            }
        };
        try {
            procBaseService.addInspectionRecord(processInfo);
        } catch (Exception e) {

        }
    }




    /**
     * 修改巡检任务状态
     * @author hedongwei@wistronits.com
     * @date  2019/7/13 18:17
     */
    @Test
    public void updateInspectionTaskStatus() {
        ProcessInfo processInfo = new ProcessInfo();
        String inspectionTaskStatus = "1";
        ProcInspection procInspection = new ProcInspection();
        procInspection.setInspectionTaskId("1");
        processInfo.setProcInspection(procInspection);

        new Expectations() {
            {
                inspectionTaskService.updateInspectionStatus((UpdateInspectionStatusReq) any);
                Integer resultCode = -1;
                String msg = "";
                result = ResultUtils.warn(resultCode, msg);
            }
        };
        procBaseService.updateInspectionTaskStatus(processInfo, inspectionTaskStatus);

        new Expectations() {
            {
                inspectionTaskService.updateInspectionStatus((UpdateInspectionStatusReq) any);
                String msg = "";
                result = ResultUtils.warn(ResultCode.SUCCESS, msg);
            }
        };
        procBaseService.updateInspectionTaskStatus(processInfo, inspectionTaskStatus);

    }


    /**
     * 获取启动流程的参数
     * @author hedongwei@wistronits.com
     * @date  2019/7/13 18:20
     */
    @Test
    public void getStartProcessInfoReq() {
        ProcessInfo processInfo = new ProcessInfo();
        ProcBase procBase = new ProcBase();
        procBase.setProcId("1");
        processInfo.setProcBase(procBase);
        procBaseService.getStartProcessInfoReq(processInfo);
    }


    /**
     * 删除工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/7/13 18:23
     */
    @Test
    public void updateProcessIsDeletedByIds() {
        List<String> ids = new ArrayList<>();
        ids.add("1");
        String isDeleted = "0";
        procBaseService.updateProcessIsDeletedByIds(ids, isDeleted);

        Map<String ,ProcBase> hashMap = new HashMap<>();
        ProcBase procBase = new ProcBase();
        procBase.setProcId("1");
        hashMap.put("1", procBase);
        new Expectations(ProcBaseValidate.class) {
            {
                procBaseDao.queryProcessByProcIds((ProcBaseReq) any);
                List<ProcBase> procBaseList = new ArrayList<>();
                procBaseList.add(procBase);
                result = procBaseList;
                ProcBaseValidate.checkProcIsAbleDelete((List<String>)any, anyInt ,hashMap, anyString);
                result = null;
            }
        };
        procBaseService.updateProcessIsDeletedByIds(ids, isDeleted);
    }

    /**
     * 修改流程状态信息
     * @author hedongwei@wistronits.com
     * @date  2019/7/13 18:44
     */
    @Test
    public void updateProcBaseStatusById() {
        ProcBase procBase = new ProcBase();
        procBaseService.updateProcBaseStatusById(procBase);

        String procId = "1";
        procBase.setProcId(procId);
        new Expectations(ProcBaseValidate.class) {
            {
                procBaseDao.queryProcessByProcId(anyString, anyLong);
                result = null;
            }
        };
        procBaseService.updateProcBaseStatusById(procBase);


        new Expectations(ProcBaseValidate.class) {
            {
                procBaseDao.queryProcessByProcId(anyString, anyLong);
                ProcBaseResp procBaseResp = new ProcBaseResp();
                BeanUtils.copyProperties(procBase, procBaseResp);
                result = procBaseResp;
            }
        };
        procBaseService.updateProcBaseStatusById(procBase);


        procBase.setStatus(ProcBaseConstants.PROC_STATUS_PENDING);
        procBase.setProcType(ProcBaseConstants.PROC_INSPECTION);
        new Expectations(ProcBaseValidate.class) {
            {
                procBaseDao.queryProcessByProcId(anyString, anyLong);
                ProcBaseResp procBaseResp = new ProcBaseResp();
                BeanUtils.copyProperties(procBase, procBaseResp);
                result = procBaseResp;
            }
        };

        procBaseService.updateProcBaseStatusById(procBase);


        procBase.setProcType(ProcBaseConstants.PROC_CLEAR_FAILURE);
        new Expectations(ProcBaseValidate.class) {
            {
                procBaseDao.queryProcessByProcId(anyString, anyLong);
                ProcBaseResp procBaseResp = new ProcBaseResp();
                BeanUtils.copyProperties(procBase, procBaseResp);
                result = procBaseResp;
            }
        };
        procBaseService.updateProcBaseStatusById(procBase);

    }


    /**
     * 根据工单id获取工单类型
     * @author hedongwei@wistronits.com
     * @date  2019/7/13 19:09
     */
    @Test
    public void getProcTypeByProcId() {
        String procId = "";
        procBaseService.getProcTypeByProcId(procId);

        procId = "1";
        new Expectations(ProcBaseValidate.class) {
            {
                procBaseDao.queryProcessByProcId(anyString, anyLong);
                result = null;
            }
        };
        procBaseService.getProcTypeByProcId(procId);

        new Expectations(ProcBaseValidate.class) {
            {
                procBaseDao.queryProcessByProcId(anyString, anyLong);
                ProcBaseResp procBase = new ProcBaseResp();
                procBase.setProcId("1");
                procBase.setProcType(ProcBaseConstants.PROC_INSPECTION);
                procBase.setStatus(ProcBaseConstants.PROC_STATUS_ASSIGNED);
                result = procBase;
            }
        };
        procBaseService.getProcTypeByProcId(procId);

        new Expectations(ProcBaseValidate.class) {
            {
                procBaseDao.queryProcessByProcId(anyString, anyLong);
                ProcBaseResp procBase = new ProcBaseResp();
                procBase.setProcId("1");
                procBase.setProcType(ProcBaseConstants.PROC_CLEAR_FAILURE);
                procBase.setStatus(ProcBaseConstants.PROC_STATUS_ASSIGNED);
                result = procBase;
            }
        };
        procBaseService.getProcTypeByProcId(procId);

        new Expectations(ProcBaseValidate.class) {
            {
                procBaseDao.queryProcessByProcId(anyString, anyLong);
                ProcBaseResp procBase = new ProcBaseResp();
                procBase.setProcId("1");
                procBase.setProcType(ProcBaseConstants.PROC_CLEAR_FAILURE);
                procBase.setStatus(ProcBaseConstants.PROC_STATUS_SINGLE_BACK);
                result = procBase;
            }
        };
        procBaseService.getProcTypeByProcId(procId);


        new Expectations(ProcBaseValidate.class) {
            {
                procBaseDao.queryProcessByProcId(anyString, anyLong);
                ProcBaseResp procBase = new ProcBaseResp();
                procBase.setProcId("1");
                procBase.setProcType(ProcBaseConstants.PROC_INSPECTION);
                procBase.setStatus(ProcBaseConstants.PROC_STATUS_SINGLE_BACK);
                procBase.setIsCheckSingleBack(ProcBaseConstants.NOT_CHECK_SINGLE_BACK);
                result = procBase;
            }
        };
        procBaseService.getProcTypeByProcId(procId);

        new Expectations(ProcBaseValidate.class) {
            {
                procBaseDao.queryProcessByProcId(anyString, anyLong);
                ProcBaseResp procBase = new ProcBaseResp();
                procBase.setProcId("1");
                procBase.setProcType(ProcBaseConstants.PROC_CLEAR_FAILURE);
                procBase.setStatus(ProcBaseConstants.PROC_STATUS_SINGLE_BACK);
                procBase.setIsCheckSingleBack(ProcBaseConstants.NOT_CHECK_SINGLE_BACK);
                result = procBase;
            }
        };
        procBaseService.getProcTypeByProcId(procId);

        new Expectations(ProcBaseValidate.class) {
            {
                procBaseDao.queryProcessByProcId(anyString, anyLong);
                ProcBaseResp procBase = new ProcBaseResp();
                procBase.setProcId("1");
                procBase.setProcType(ProcBaseConstants.PROC_CLEAR_FAILURE);
                procBase.setStatus(ProcBaseConstants.PROC_STATUS_COMPLETED);
                result = procBase;
            }
        };
        procBaseService.getProcTypeByProcId(procId);

        new Expectations(ProcBaseValidate.class) {
            {
                procBaseDao.queryProcessByProcId(anyString, anyLong);
                ProcBaseResp procBase = new ProcBaseResp();
                procBase.setProcId("1");
                procBase.setProcType(ProcBaseConstants.PROC_INSPECTION);
                procBase.setStatus(ProcBaseConstants.PROC_STATUS_COMPLETED);
                result = procBase;
            }
        };
        procBaseService.getProcTypeByProcId(procId);

        new Expectations(ProcBaseValidate.class) {
            {
                procBaseDao.queryProcessByProcId(anyString, anyLong);
                ProcBaseResp procBase = new ProcBaseResp();
                procBase.setProcId("1");
                procBase.setProcType(ProcBaseConstants.PROC_CLEAR_FAILURE);
                procBase.setStatus(ProcBaseConstants.PROC_STATUS_SINGLE_BACK);
                procBase.setIsCheckSingleBack(ProcBaseConstants.IS_CHECK_SINGLE_BACK);
                result = procBase;
            }
        };
        procBaseService.getProcTypeByProcId(procId);

        new Expectations(ProcBaseValidate.class) {
            {
                procBaseDao.queryProcessByProcId(anyString, anyLong);
                ProcBaseResp procBase = new ProcBaseResp();
                procBase.setProcId("1");
                procBase.setProcType(ProcBaseConstants.PROC_INSPECTION);
                procBase.setStatus(ProcBaseConstants.PROC_STATUS_SINGLE_BACK);
                procBase.setIsCheckSingleBack(ProcBaseConstants.IS_CHECK_SINGLE_BACK);
                result = procBase;
            }
        };
        procBaseService.getProcTypeByProcId(procId);
    }

    /**
     * 根据id查看工单
     * @author hedongwei@wistronits.com
     * @date  2019/7/15 9:01
     */
    @Test
    public void queryProcessById() {
        String id = "";
        procBaseService.queryProcessById(id);

        id = "1";


        new Expectations() {
            {
                procBaseDao.queryProcessByProcId(anyString, anyLong);
                result = null;
            }
        };
        procBaseService.queryProcessById(id);

        new Expectations() {
            {
                procBaseDao.queryProcessByProcId(anyString, anyLong);
                ProcBaseResp procBase = new ProcBaseResp();
                procBase.setProcId("1");
                procBase.setProcType(ProcBaseConstants.PROC_INSPECTION);
                procBase.setStatus(ProcBaseConstants.PROC_STATUS_SINGLE_BACK);
                result = procBase;
            }
        };
        procBaseService.queryProcessById(id);
    }

    /**
     * 查询巡检任务关联巡检工单
     * @author hedongwei@wistronits.com
     * @date  2019/7/15 9:05
     */
    @Test
    public void queryListRelatedProcByInspectionTaskIdPage() {
        QueryCondition<ProcBaseReq> queryCondition = new QueryCondition<>();
        PageCondition pageCondition = new PageCondition();
        pageCondition.setPageSize(10);
        pageCondition.setPageNum(1);
        queryCondition.setPageCondition(pageCondition);
        procBaseService.queryListRelatedProcByInspectionTaskIdPage(queryCondition);
    }

    /**
     * 分页查询工单未完工列表
     */
    @Test
    public void queryListProcUnfinishedProcByPage() {
        QueryCondition<ProcBaseReq> queryCondition = new QueryCondition<>();
        PageCondition pageCondition = new PageCondition();
        pageCondition.setPageSize(10);
        pageCondition.setPageNum(1);
        queryCondition.setPageCondition(pageCondition);
        ProcBaseReq req = new ProcBaseReq();
        req.setProcType(ProcBaseConstants.PROC_INSPECTION);
        queryCondition.setBizCondition(req);
        this.searchUserInfo();
        procBaseService.queryListProcUnfinishedProcByPage(queryCondition);

        req.setProcType(ProcBaseConstants.PROC_CLEAR_FAILURE);
        queryCondition.setBizCondition(req);
        this.searchUserInfo();
        procBaseService.queryListProcUnfinishedProcByPage(queryCondition);
    }

    /**
     * 分页查询工单历史列表
     */
    @Test
    public void queryListProcHisProcByPage() {
        QueryCondition<ProcBaseReq> queryCondition = new QueryCondition<>();
        PageCondition pageCondition = new PageCondition();
        pageCondition.setPageSize(10);
        pageCondition.setPageNum(1);
        queryCondition.setPageCondition(pageCondition);
        ProcBaseReq req = new ProcBaseReq();
        req.setProcType(ProcBaseConstants.PROC_INSPECTION);
        queryCondition.setBizCondition(req);
        this.searchUserInfo();
        procBaseService.queryListProcHisProcByPage(queryCondition);

        req.setProcType(ProcBaseConstants.PROC_CLEAR_FAILURE);
        queryCondition.setBizCondition(req);
        this.searchUserInfo();
        procBaseService.queryListProcHisProcByPage(queryCondition);
    }


    /**
     * 工单未完工列表状态总数统计
     * @author hedongwei@wistronits.com
     * @date  2019/7/15 9:36
     */
    @Test
    public void queryCountListProcUnfinishedProcStatus() {
        QueryCondition<ProcBaseReq> queryCondition = new QueryCondition<>();
        PageCondition pageCondition = new PageCondition();
        pageCondition.setPageSize(10);
        pageCondition.setPageNum(1);
        queryCondition.setPageCondition(pageCondition);
        ProcBaseReq req = new ProcBaseReq();
        req.setProcType(ProcBaseConstants.PROC_INSPECTION);
        queryCondition.setBizCondition(req);
        this.searchUserInfo();
        procBaseService.queryCountListProcUnfinishedProcStatus(queryCondition);
    }

    /**
     * 工单列表状态统计
     * @author hedongwei@wistronits.com
     * @date  2019/7/15 9:38
     */
    @Test
    public void queryCountProcByStatus() {
        ProcBaseReq req = new ProcBaseReq();
        req.setProcType(ProcBaseConstants.PROC_INSPECTION);
        this.searchUserInfo();
        new Expectations() {
            {
                procBaseDao.queryPermissionsInfo((Set<String>) any, (Set<String>) any, (Set<String>) any);
                Set<String> procIdSet = new HashSet<>();
                procIdSet.add("1");
                result = procIdSet;
            }
        };
        procBaseService.queryCountProcByStatus(req);
    }

    /**
     * 工单列表分组统计
     * @author hedongwei@wistronits.com
     * @date  2019/7/15 9:38
     */
    @Test
    public void queryCountProcByGroup() {
        ProcBaseReq req = new ProcBaseReq();
        req.setProcType(ProcBaseConstants.PROC_INSPECTION);
        this.searchUserInfo();
        new Expectations() {
            {
                procBaseDao.queryPermissionsInfo((Set<String>) any, (Set<String>) any, (Set<String>) any);
                Set<String> procIdSet = new HashSet<>();
                procIdSet.add("1");
                result = procIdSet;
            }
        };
        procBaseService.queryCountProcByGroup(req);
    }

    /**
     * 设施类型统计的工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/7/15 9:46
     */
    @Test
    public void queryListGroupByDeviceType() {
        QueryCondition<ProcBaseReq> queryCondition = new QueryCondition<>();
        PageCondition pageCondition = new PageCondition();
        pageCondition.setPageSize(10);
        pageCondition.setPageNum(1);
        queryCondition.setPageCondition(pageCondition);
        ProcBaseReq req = new ProcBaseReq();
        req.setProcType(ProcBaseConstants.PROC_INSPECTION);
        queryCondition.setBizCondition(req);
        this.searchUserInfo();
        procBaseService.queryListGroupByDeviceType(queryCondition);
    }


    /**
     * 工单历史列表总数统计
     * @author hedongwei@wistronits.com
     * @date  2019/7/15 9:46
     */
    @Test
    public void queryCountListProcHisProc() {
        QueryCondition<ProcBaseReq> queryCondition = new QueryCondition<>();
        PageCondition pageCondition = new PageCondition();
        pageCondition.setPageSize(10);
        pageCondition.setPageNum(1);
        queryCondition.setPageCondition(pageCondition);
        ProcBaseReq req = new ProcBaseReq();
        req.setProcType(ProcBaseConstants.PROC_INSPECTION);
        queryCondition.setBizCondition(req);
        this.searchUserInfo();
        procBaseService.queryCountListProcHisProc(queryCondition);
    }
    
    /**
     * 查询ids是否有工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/7/15 9:48
     */
    @Test
    public void queryProcExitsForIds() {
        List<String> ids = new ArrayList<>();
        ids.add("1");
        //设施id
        String type = ProcBaseConstants.DEVICE_IDS;
        this.searchProcBaseInfoForIds();
        procBaseService.queryProcExitsForIds(ids, type);
        //区域id
        type = ProcBaseConstants.AREA_IDS;
        this.searchProcBaseInfoForIds();
        procBaseService.queryProcExitsForIds(ids, type);
    }

    /**
     * 下载工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/7/15 11:33
     */
    @Test
    public void updateProcByUser() {
        UpdateProcByUserReq req = new UpdateProcByUserReq();
        String procId = "";
        req.setProcId(procId);
        procBaseService.updateProcByUser(req);

        procId = "1";
        req.setProcId(procId);
        this.expectationsSearchProc();
        procBaseService.updateProcByUser(req);

        req.setUserId("1");


        new Expectations() {
            {
                procBaseDao.queryProcessByProcId(anyString, anyLong);
                ProcBaseResp procBaseResp = new ProcBaseResp();
                procBaseResp.setProcId("1");
                procBaseResp.setStatus(ProcBaseConstants.PROC_STATUS_PENDING);
                result = procBaseResp;
            }
        };
        try {
            procBaseService.updateProcByUser(req);
        } catch (Exception e) {

        }

        this.expectationsSearchProc();
        try {
            procBaseService.updateProcByUser(req);
        } catch (Exception e) {

        }

        new Expectations() {
            {
                procBaseDao.queryProcessByProcId(anyString, anyLong);
                ProcBaseResp procBaseResp = new ProcBaseResp();
                procBaseResp.setProcId("1");
                procBaseResp.setStatus(ProcBaseConstants.PROC_STATUS_PENDING);
                result = procBaseResp;

                workflowService.completeProcess((CompleteProcessInfoReq) any);
                int errorCode = -1;
                String msg = "error";
                result = ResultUtils.warn(errorCode, msg);
            }
        };
        try {
            procBaseService.updateProcByUser(req);
        } catch (Exception e) {

        }
    }

    /**
     * 指派工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/7/15 13:37
     */
    @Test
    public void assignProc() {
        AssignProcReq req = new AssignProcReq();
        req.setProcId("1");
        new Expectations() {
            {
                procBaseDao.queryProcessByProcId(anyString, anyLong);
                result = null;
            }
        };
        procBaseService.assignProc(req);

        this.expectationsSearchProc();
        try {
            procBaseService.assignProc(req);
        } catch (Exception e) {

        }

        List<ProcRelatedDepartment> departmentList = new ArrayList<>();
        ProcRelatedDepartment department = new ProcRelatedDepartment();
        department.setProcId("1");
        departmentList.add(department);
        req.setDepartmentList(departmentList);
        this.expectationsSearchProc();

        try {
            procBaseService.assignProc(req);
        } catch (Exception e) {

        }


        new Expectations() {
            {
                procBaseDao.queryProcessByProcId(anyString, anyLong);
                ProcBaseResp procBaseInfo = new ProcBaseResp();
                procBaseInfo.setProcId("1");
                procBaseInfo.setStatus(ProcBaseConstants.PROC_STATUS_ASSIGNED);
                procBaseInfo.setProcType(ProcBaseConstants.PROC_CLEAR_FAILURE);
                result = procBaseInfo;
            }
        };


        new MockUp<ProcBaseServiceImpl>(){
            @Mock
            public ProcessInfo assignProcProcess(AssignProcReq req,ProcBase procBase) {
                ProcessInfo processInfo = new ProcessInfo();
                ProcBase procBaseInfo = new ProcBase();
                procBaseInfo.setProcId("1");
                procBaseInfo.setProcType(ProcBaseConstants.PROC_INSPECTION);
                processInfo.setProcId("1");
                processInfo.setProcBase(procBaseInfo);
                return processInfo;
            }
        };
        procBaseService.assignProc(req);


        new Expectations() {
            {
                procBaseDao.queryProcessByProcId(anyString, anyLong);
                ProcBaseResp procBaseInfo = new ProcBaseResp();
                procBaseInfo.setProcId("1");
                procBaseInfo.setStatus(ProcBaseConstants.PROC_STATUS_ASSIGNED);
                procBaseInfo.setProcType(ProcBaseConstants.PROC_INSPECTION);
                result = procBaseInfo;
            }
        };


        new MockUp<ProcBaseServiceImpl>(){
            @Mock
            public ProcessInfo assignProcProcess(AssignProcReq req,ProcBase procBase) {
                ProcessInfo processInfo = new ProcessInfo();
                ProcBase procBaseInfo = new ProcBase();
                procBaseInfo.setProcId("1");
                procBaseInfo.setProcType(ProcBaseConstants.PROC_CLEAR_FAILURE);
                processInfo.setProcId("1");
                processInfo.setProcBase(procBaseInfo);
                return processInfo;
            }
        };
        procBaseService.assignProc(req);
    }




    /**
     * 指派工单过程
     * @author hedongwei@wistronits.com
     * @date  2019/7/15 14:01
     */
    @Test
    public void assignProcProcess() {
        AssignProcReq req = new AssignProcReq();
        req.setProcId("1");
        List<ProcRelatedDepartment> procRelatedDepartmentList = new ArrayList<>();
        ProcRelatedDepartment procRelatedDepartment = new ProcRelatedDepartment();
        procRelatedDepartment.setAccountabilityDept("1");
        procRelatedDepartment.setProcId("1");
        procRelatedDepartmentList.add(procRelatedDepartment);
        req.setDepartmentList(procRelatedDepartmentList);

        ProcBase procBase = new ProcBase();
        procBase.setProcId("1");
        procBase.setProcType(ProcBaseConstants.PROC_INSPECTION);
        procBaseService.assignProcProcess(req, procBase);

        procBase.setProcType(ProcBaseConstants.PROC_CLEAR_FAILURE);
        procBaseService.assignProcProcess(req, procBase);

    }

    /**
     * 转派工单结果
     * @author hedongwei@wistronits.com
     * @date  2019/3/28 13:57
     */
    @Test
    public void turnProc() {
        TurnProcReq req = new TurnProcReq();
        procBaseService.turnProc(req);
        req.setProcId("1");
        this.expectationsSearchProc();
        procBaseService.turnProc(req);
        req.setTurnReason("name");
        try {
            procBaseService.turnProc(req);
        } catch (Exception e) {

        }

        new Expectations() {
            {
                procBaseDao.queryProcessByProcId(anyString, anyLong);
                ProcBaseResp procBaseInfo = new ProcBaseResp();
                procBaseInfo.setProcId("1");
                procBaseInfo.setStatus(ProcBaseConstants.PROC_STATUS_PROCESSING);
                result = procBaseInfo;
            }
        };


        try {
            procBaseService.turnProc(req);
        } catch (Exception e) {

        }




        new Expectations() {
            {
                procBaseDao.queryProcessByProcId(anyString, anyLong);
                ProcBaseResp procBaseInfo = new ProcBaseResp();
                procBaseInfo.setProcId("1");
                procBaseInfo.setStatus(ProcBaseConstants.PROC_STATUS_PROCESSING);
                procBaseInfo.setProcType(ProcBaseConstants.PROC_INSPECTION);
                result = procBaseInfo;
            }
        };
        new MockUp<ProcBaseServiceImpl>(){
            @Mock
            public Result checkTurnUser(TurnProcReq req) {
                return null;
            }
        };


        new MockUp<ProcBaseServiceImpl>(){
            @Mock
            public Result turnProcess(TurnInfoReq turnInfoReq) {
                String msg = "info";
                return ResultUtils.success(msg);
            }
        };
        try {
            procBaseService.turnProc(req);
        } catch (Exception e) {

        }


        new Expectations() {
            {
                procBaseDao.queryProcessByProcId(anyString, anyLong);
                ProcBaseResp procBaseInfo = new ProcBaseResp();
                procBaseInfo.setProcId("1");
                procBaseInfo.setStatus(ProcBaseConstants.PROC_STATUS_PROCESSING);
                procBaseInfo.setProcType(ProcBaseConstants.PROC_CLEAR_FAILURE);
                result = procBaseInfo;
            }
        };
        new MockUp<ProcBaseServiceImpl>(){
            @Mock
            public Result checkTurnUser(TurnProcReq req) {
                return null;
            }
        };


        new MockUp<ProcBaseServiceImpl>(){
            @Mock
            public Result turnProcess(TurnInfoReq turnInfoReq) {
                String msg = "info";
                return ResultUtils.success(msg);
            }
        };
        try {
            procBaseService.turnProc(req);
        } catch (Exception e) {

        }

        new Expectations() {
            {
                procBaseDao.queryProcessByProcId(anyString, anyLong);
                ProcBaseResp procBaseInfo = new ProcBaseResp();
                procBaseInfo.setProcId("1");
                procBaseInfo.setStatus(ProcBaseConstants.PROC_STATUS_PROCESSING);
                procBaseInfo.setProcType(ProcBaseConstants.PROC_INSPECTION);
                result = procBaseInfo;
            }
        };
        new MockUp<ProcBaseServiceImpl>(){
            @Mock
            public Result checkTurnUser(TurnProcReq req) {
                return null;
            }
        };


        new Expectations() {
            {
                workflowService.turnProcess((TurnInfoReq) any);
                Integer error = -1;
                result = ResultUtils.warn(error);
            }
        };


        try {
            procBaseService.turnProc(req);
        } catch (Exception e) {

        }
    }

    /**
     * 转派过程
     * @author hedongwei@wistronits.com
     * @date  2019/7/15 14:18
     */
    @Test
    public void turnProcProcess() {
        TurnProcReq req = new TurnProcReq();
        req.setTurnReason("info");
        req.setUserId("1");
        req.setProcId("1");
        ProcBase procBase = new ProcBase();
        procBase.setProcId("1");
        procBaseService.turnProcProcess(req, procBase);
    }


    /**
     * 获取转办用户
     * @author hedongwei@wistronits.com
     * @date  2019/7/15 14:21
     */
    @Test
    public void getTurnUser() {
        GetTurnUserReq req = new GetTurnUserReq();
        req.setProcId("");
        procBaseService.getTurnUser(req);

        req.setProcId("1");

        this.searchTurnInfo();

        new Expectations() {
            {
                userFeign.queryUserByDeptList((List<String>) any);
                List<User> userList = new ArrayList<>();
                User userInfo = new User();
                userInfo.setId("info");
                userList.add(userInfo);
                userInfo.setId("1");
                userList.add(userInfo);
                result = userList;
            }
        };

        searchTurnUserInfo();

        procBaseService.getTurnUser(req);

        this.searchTurnInfo();

        new Expectations() {
            {
                userFeign.queryUserByDeptList((List<String>) any);
                List<User> userList = new ArrayList<>();
                User userInfo = new User();
                userInfo.setId("info");
                userList.add(userInfo);
                result = userList;
            }
        };

        this.searchTurnUserInfo();

        procBaseService.getTurnUser(req);
    }

    /**
     * 获得转办工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/7/15 14:37
     */
    public void searchTurnInfo() {
        new Expectations() {
            {
                procBaseDao.queryProcessByProcId(anyString, anyLong);
                ProcBaseResp procBaseInfo = new ProcBaseResp();
                procBaseInfo.setProcId("1");
                procBaseInfo.setAssign("info");
                procBaseInfo.setStatus(ProcBaseConstants.PROC_STATUS_SINGLE_BACK);
                result = procBaseInfo;
            }
        };
    }

    /**
     * 获得转办用户信息
     * @author hedongwei@wistronits.com
     * @date  2019/7/15 14:37
     */
    public void searchTurnUserInfo() {
        new Expectations() {
            {
                userFeign.queryUserByIdList((List<String>) any);
                List<User> userList = new ArrayList<>();
                User userInfo = new User();
                userInfo.setId("1");
                userList.add(userInfo);
                result = userList;
            }
        };
    }


    /**
     * 获取转办用户集合
     * @author hedongwei@wistronits.com
     * @date  2019/7/15 14:42
     */
    @Test
    public void getTurnUserList() {
        List<User> userList = new ArrayList<>();
        User user = new User();
        user.setId("1");
        Role role = new Role();
        role.setId("1");
        List<RoleDeviceType> roleDeviceTypeList = new ArrayList<>();
        RoleDeviceType roleDeviceType = new RoleDeviceType();
        roleDeviceType.setRoleId("1");
        roleDeviceType.setDeviceTypeId("001");
        roleDeviceTypeList.add(roleDeviceType);
        role.setRoleDevicetypeList(roleDeviceTypeList);
        user.setRole(role);
        userList.add(user);

        List<String> deviceTypeList = new ArrayList<>();
        deviceTypeList.add("001");
        List<String> departmentList = new ArrayList<>();
        procBaseService.getTurnUserList(userList, deviceTypeList, departmentList);
    }


    /**
     * 获取转办关联用户类型
     * @author hedongwei@wistronits.com
     * @date  2019/7/15 14:52
     */
    @Test
    public void getTurnRelatedDeviceTypeList() {
        ProcBase procBase = new ProcBase();
        procBase.setDeviceType("001");
        procBaseService.getTurnRelatedDeviceTypeList(procBase);
    }


    /**
     * 获取转办关联部门编号集合
     * @author hedongwei@wistronits.com
     * @date  2019/4/9 22:05
     */
    @Test
    public void getTurnRelatedDepartmentList() {
        ProcBase procBase = new ProcBase();
        procBase.setProcType(ProcBaseConstants.PROC_INSPECTION);
        procBaseService.getTurnRelatedDepartmentList(procBase);


        procBase.setProcType(ProcBaseConstants.PROC_CLEAR_FAILURE);

        /*new Expectations() {
            {
                procBaseDao.queryProcRelateDeptByProcIds((List<String>) any);
                result = null;

                procInspectionService.queryInspectionRelateDeptByProcIds((List<String>) any);
                List<ProcInspection> procInspectionList = new ArrayList<>();
                ProcInspection procInspection = new ProcInspection();
                procInspection.setAccountabilityDept("info");
                procInspectionList.add(procInspection);
                result = procInspectionList;
            }
        };*/
        procBaseService.getTurnRelatedDepartmentList(procBase);
    }

    /**
     * 校验转派工单用户
     * @author hedongwei@wistronits.com
     * @date  2019/7/15 15:20
     */
    @Test
    public void checkTurnUser() {
        TurnProcReq req = new TurnProcReq();
        try {
            procBaseService.checkTurnUser(req);
        } catch (Exception e) {

        }

        req.setUserId("1");
        new Expectations() {
            {
                procBaseDao.queryProcessByProcId(anyString, anyLong);
                ProcBaseResp procBaseResp = new ProcBaseResp();
                procBaseResp.setAssign("1");
                result = procBaseResp;
            }
        };

        try {
            procBaseService.checkTurnUser(req);
        } catch (Exception e) {

        }

        new Expectations() {
            {
                procBaseDao.queryProcessByProcId(anyString, anyLong);
                ProcBaseResp procBaseResp = new ProcBaseResp();
                procBaseResp.setAssign("info");
                result = procBaseResp;
            }
        };

        this.searchTurnUserInfo();

        try {
            procBaseService.checkTurnUser(req);
        } catch (Exception e) {

        }

        req.setUserId("infoTest");
        new Expectations() {
            {
                procBaseDao.queryProcessByProcId(anyString, anyLong);
                ProcBaseResp procBaseResp = new ProcBaseResp();
                procBaseResp.setAssign("test");
                result = procBaseResp;
            }
        };

        try {
            procBaseService.checkTurnUser(req);
        } catch (Exception e) {

        }

    }

    /**
     * 查询工单详情
     * @author hedongwei@wistronits.com
     * @date  2019/7/15 15:38
     */
    @Test
    public void queryProcBaseById() {
        String procId = "1";
        procBaseService.queryProcBaseById(procId);
    }

    /**
     * 退单信息
     * @author hedongwei@wistronits.com
     * @date  2019/7/15 16:05
     */
    @Test
    public void singleBackProc() {
        SingleBackProcReq req = new SingleBackProcReq();
        req.setProcId("");
        procBaseService.singleBackProc(req);

        req.setProcId("1");
        this.expectationsSearchProc();
        try {
            procBaseService.singleBackProc(req);
        } catch (Exception e) {

        }


        req.setSingleBackReason("1");
        this.expectationsSearchProc();
        try {
            procBaseService.singleBackProc(req);
        } catch (Exception e) {

        }

        new Expectations() {
            {
                procBaseDao.queryProcessByProcId(anyString, anyLong);
                ProcBaseResp procBaseInfo = new ProcBaseResp();
                procBaseInfo.setProcId("1");
                procBaseInfo.setStatus(ProcBaseConstants.PROC_STATUS_PROCESSING);
                result = procBaseInfo;
            }
        };
        try {
            procBaseService.singleBackProc(req);
        } catch (Exception e) {

        }


    }

    /**
     * 修改工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/7/23 14:55
     */
    @Test
    public void updateProcBase() {
        ProcBase procBase = new ProcBase();
        procBase.setProcType(ProcBaseConstants.PROC_INSPECTION);
        ProcBase updateProcParam = new ProcBase();
        procBaseService.updateProcBase(procBase, updateProcParam);
    }

    /**
     * 撤销单据
     * @author hedongwei@wistronits.com
     * @date  2019/7/23 14:58
     */
    @Test
    public void revokeProc() {
        RevokeProcReq req = new RevokeProcReq();
        req.setProcId("1");
        this.expectationsSearchProc();
        try {
            procBaseService.revokeProc(req);
        } catch (Exception e) {

        }


        new Expectations() {
            {
                procBaseDao.queryProcessByProcId(anyString, anyLong);
                ProcBaseResp procBaseInfo = new ProcBaseResp();
                procBaseInfo.setProcId("1");
                procBaseInfo.setStatus(ProcBaseConstants.PROC_STATUS_PENDING);
                procBaseInfo.setProcType(ProcBaseConstants.PROC_CLEAR_FAILURE);
                result = procBaseInfo;
            }
        };

        try {
            procBaseService.revokeProc(req);
        } catch (Exception e) {

        }

        new Expectations() {
            {
                procBaseDao.queryProcessByProcId(anyString, anyLong);
                ProcBaseResp procBaseInfo = new ProcBaseResp();
                procBaseInfo.setProcId("1");
                procBaseInfo.setStatus(ProcBaseConstants.PROC_STATUS_PENDING);
                procBaseInfo.setProcType(ProcBaseConstants.PROC_INSPECTION);
                result = procBaseInfo;
            }
        };

        try {
            procBaseService.revokeProc(req);
        } catch (Exception e) {

        }

    }

    /**
     * 确认退单
     * @author hedongwei@wistronits.com
     * @date  2019/7/23 15:21
     */
    @Test
    public void checkSingleBack() {
        CheckSingleBackReq req = new CheckSingleBackReq();
        req.setProcId("1");

        new Expectations() {
            {
                procBaseDao.queryProcessByProcId(anyString, anyLong);
                result = null;
            }
        };

        try {
            procBaseService.checkSingleBack(req);
        } catch (Exception e) {

        }

        this.expectationsSearchProc();
        try {
            procBaseService.checkSingleBack(req);
        } catch (Exception e) {

        }


        new Expectations() {
            {
                procBaseDao.queryProcessByProcId(anyString, anyLong);
                ProcBaseResp procBaseInfo = new ProcBaseResp();
                procBaseInfo.setProcId("1");
                procBaseInfo.setStatus(ProcBaseConstants.PROC_STATUS_SINGLE_BACK);
                procBaseInfo.setIsCheckSingleBack(ProcBaseConstants.NOT_CHECK_SINGLE_BACK);
                procBaseInfo.setProcType(ProcBaseConstants.PROC_CLEAR_FAILURE);
                result = procBaseInfo;
            }
        };

        try {
            procBaseService.checkSingleBack(req);
        } catch (Exception e) {

        }

        new Expectations() {
            {
                procBaseDao.queryProcessByProcId(anyString, anyLong);
                ProcBaseResp procBaseInfo = new ProcBaseResp();
                procBaseInfo.setProcId("1");
                procBaseInfo.setStatus(ProcBaseConstants.PROC_STATUS_SINGLE_BACK);
                procBaseInfo.setIsCheckSingleBack(ProcBaseConstants.NOT_CHECK_SINGLE_BACK);
                procBaseInfo.setProcType(ProcBaseConstants.PROC_INSPECTION);
                result = procBaseInfo;
            }
        };

        try {
            procBaseService.checkSingleBack(req);
        } catch (Exception e) {

        }
    }

    /**
     * 工单关联设施集合
     * @author hedongwei@wistronits.com
     * @date  2019/7/23 15:37
     */
    @Test
    public void procRelationDeviceList() {
        ProcRelationDeviceListReq req = new ProcRelationDeviceListReq();
        req.setProcId("1");

        new Expectations() {
            {
                procBaseDao.queryProcessByProcId(anyString, anyLong);
                result = null;
            }
        };
        procBaseService.procRelationDeviceList(req);

        new Expectations() {
            {
                procBaseDao.queryProcessByProcId(anyString, anyLong);
                ProcBaseResp procBaseInfo = new ProcBaseResp();
                procBaseInfo.setProcId("1");
                procBaseInfo.setStatus(ProcBaseConstants.PROC_STATUS_SINGLE_BACK);
                procBaseInfo.setProcType(ProcBaseConstants.PROC_CLEAR_FAILURE);
                procBaseInfo.setDeviceId("1");
                result = procBaseInfo;


                procBaseDao.queryProcRelateDevice((ProcBaseReq) any);
                List<ProcRelatedDevice> deviceList = new ArrayList<>();
                ProcRelatedDevice procRelatedDevice = new ProcRelatedDevice();
                procRelatedDevice.setDeviceId("1");
                deviceList.add(procRelatedDevice);
                result = deviceList;

                deviceFeign.getDeviceByIds((String []) any);
                List<DeviceInfoDto> deviceInfoDtoList = new ArrayList<>();
                DeviceInfoDto deviceInfoDto = new DeviceInfoDto();
                deviceInfoDto.setDeviceId("1");
                deviceInfoDtoList.add(deviceInfoDto);
                result = deviceInfoDtoList;
            }
        };
        procBaseService.procRelationDeviceList(req);

        new Expectations() {
            {
                procBaseDao.queryProcessByProcId(anyString, anyLong);
                ProcBaseResp procBaseInfo = new ProcBaseResp();
                procBaseInfo.setProcId("1");
                procBaseInfo.setStatus(ProcBaseConstants.PROC_STATUS_SINGLE_BACK);
                procBaseInfo.setProcType(ProcBaseConstants.PROC_INSPECTION);
                procBaseInfo.setDeviceId("1");
                result = procBaseInfo;

                procBaseDao.queryProcRelateDevice((ProcBaseReq) any);
                List<ProcRelatedDevice> deviceList = new ArrayList<>();
                ProcRelatedDevice procRelatedDevice = new ProcRelatedDevice();
                procRelatedDevice.setDeviceId("1");
                deviceList.add(procRelatedDevice);
                result = deviceList;

                deviceFeign.getDeviceByIds((String []) any);
                List<DeviceInfoDto> deviceInfoDtoList = new ArrayList<>();
                DeviceInfoDto deviceInfoDto = new DeviceInfoDto();
                deviceInfoDto.setDeviceId("1");
                deviceInfoDtoList.add(deviceInfoDto);
                result = deviceInfoDtoList;
            }
        };
        procBaseService.procRelationDeviceList(req);


    }

    /**
     * 校验部门信息有无关联工单
     * @author hedongwei@wistronits.com
     * @date  2019/7/23 22:10
     */
    @Test
    public void queryProcIdListByDeptIds() {
        new Expectations() {
            {
                procClearFailureService.queryClearProcListByDeptIds((List<String>) any);
                List<ProcClearFailure> procClearFailureList = new ArrayList<>();
                ProcClearFailure procClearFailure = new ProcClearFailure();
                procClearFailure.setAccountabilityDept("1");
                procClearFailureList.add(procClearFailure);
                procClearFailureList.add(procClearFailure);
                result = procClearFailureList;


                procInspectionService.queryInspectionProcListByDeptIds((List<String>) any);
                List<ProcInspection> procInspectionList = new ArrayList<>();
                ProcInspection procInspection = new ProcInspection();
                procInspection.setAccountabilityDept("1");
                procInspectionList.add(procInspection);
                procInspectionList.add(procInspection);
                result = procInspectionList;
            }
        };
        List<String> deptIds = new ArrayList<>();
        deptIds.add("1");
        procBaseService.queryProcIdListByDeptIds(deptIds);
    }


    /**
     * 根据部门编号查询工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/7/29 17:18
     */
    @Test
    public void existsProcForDeptIds() {
        new Expectations() {
            {
                procClearFailureService.queryClearProcListByDeptIds((List<String>) any);
                List<ProcClearFailure> procClearFailureList = new ArrayList<>();
                ProcClearFailure procClearFailure = new ProcClearFailure();
                procClearFailure.setAccountabilityDept("1");
                procClearFailureList.add(procClearFailure);
                procClearFailureList.add(procClearFailure);
                result = procClearFailureList;
            }
        };

        List<String> deptIds = new ArrayList<>();
        deptIds.add("1");
        procBaseService.queryProcIdListByDeptIds(deptIds);


        new Expectations() {
            {
                procClearFailureService.queryClearProcListByDeptIds((List<String>) any);
                result = null;

                procInspectionService.queryInspectionProcListByDeptIds((List<String>) any);
                List<ProcInspection> procInspectionList = new ArrayList<>();
                ProcInspection procInspection = new ProcInspection();
                procInspection.setAccountabilityDept("1");
                procInspectionList.add(procInspection);
                procInspectionList.add(procInspection);
                result = procInspectionList;
            }
        };
        procBaseService.queryProcIdListByDeptIds(deptIds);


        new Expectations() {
            {
                procClearFailureService.queryClearProcListByDeptIds((List<String>) any);
                result = null;

                procInspectionService.queryInspectionProcListByDeptIds((List<String>) any);
                result = null;
            }
        };
        procBaseService.queryProcIdListByDeptIds(deptIds);
    }

    /**
     * 根据用户查询是否存在正在执行的工单
     * @author hedongwei@wistronits.com
     * @date  2019/7/23 22:20
     */
    @Test
    public void queryIsExistsAssignUser() {
        new Expectations() {
            {
                //查询是否存在进行中的巡检工单
                procInspectionService.queryExistsProcForUserList((List<String>) any);
                List<ProcClearFailure> procClearFailureList = new ArrayList<>();
                ProcClearFailure procClearFailure = new ProcClearFailure();
                procClearFailure.setAccountabilityDept("1");
                procClearFailureList.add(procClearFailure);
                procClearFailureList.add(procClearFailure);
                result = procClearFailureList;

                //查询是否存在进行中的销障工单
                procClearFailureService.queryExistsProcForUserList((List<String>) any);
                List<ProcInspection> procInspectionList = new ArrayList<>();
                ProcInspection procInspection = new ProcInspection();
                procInspection.setAccountabilityDept("1");
                procInspectionList.add(procInspection);
                procInspectionList.add(procInspection);
                result = procInspectionList;
            }
        };
        List<String> userIdList = new ArrayList<>();
        userIdList.add("1");
        procBaseService.queryIsExistsAssignUser(userIdList);


        new Expectations() {
            {
                //查询是否存在进行中的巡检工单
                procInspectionService.queryExistsProcForUserList((List<String>) any);
                result = null;

                procClearFailureService.queryExistsProcForUserList((List<String>) any);
                result = null;

            }
        };
        procBaseService.queryIsExistsAssignUser(userIdList);

    }

    /**
     * 获取没有责任单位的数据
     * @author hedongwei@wistronits.com
     * @date  2019/7/23 22:3
     */
    @Test
    public void getNoAssignProcBaseResp() {
        List<ProcBaseRespForApp> procBaseRespForApps = new ArrayList<>();
        procBaseService.getNoAssignProcBaseResp(procBaseRespForApps);
        ProcBaseRespForApp resp = new ProcBaseRespForApp();
        resp.setDeviceId("1");
        procBaseRespForApps.add(resp);
        procBaseService.getNoAssignProcBaseResp(procBaseRespForApps);
    }

    /**
     * 设置工单编号到请求参数类中
     * @author hedongwei@wistronits.com
     * @date  2019/7/23 22:3
     */
    @Test
    public void setProcIdsToProcBaseReq() {
        ProcBaseReq procBaseReq = new ProcBaseReq();
        Set<String> procIdList = new HashSet<>();
        procIdList.add("1");
        procBaseReq.setProcIds(procIdList);
        procBaseReq.setProcId("1");
        procBaseService.setProcIdsToProcBaseReq(procBaseReq);
    }


    /**
     * 查询工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/3/25 22:19
     */
    @Test
    public void queryProcBaseInfoList() {
        ProcBase procBase = new ProcBase();
        procBaseService.queryProcBaseInfoList(procBase);
    }

    /**
     * 查询关联设施信息
     * @author hedongwei@wistronits.com
     * @date  2019/3/27 12:56
     */
    @Test
    public void queryProcRelateDeviceByProcIds() {
        List<String> procIds = new ArrayList<>();
        procIds.add("1");

        new Expectations() {
            {
                //查询工单关联设施信息
                List<ProcRelatedDevice> procRelatedDeviceList = procBaseDao.queryProcRelateDeviceByProcIds((List<String>) procIds);
                result = null;

                //查询销障工单关联设施信息
                procClearFailureService.queryProcRelateDeviceByProcIds((List<String>) procIds);
                List<ProcClearFailure> procClearFailureList = new ArrayList<>();
                ProcClearFailure procClearFailure = new ProcClearFailure();
                procClearFailure.setDeviceId("1");
                procClearFailureList.add(procClearFailure);
                result = procClearFailureList;
            }
        };
        procBaseService.queryProcRelateDeviceByProcIds(procIds);


        new Expectations() {
            {
                //查询工单关联设施信息
                procBaseDao.queryProcRelateDeviceByProcIds((List<String>) procIds);
                List<ProcRelatedDevice> procRelatedDeviceList = new ArrayList<>();
                ProcRelatedDevice procRelatedDevice = new ProcRelatedDevice();
                procRelatedDevice.setDeviceId("1");
                procRelatedDeviceList.add(procRelatedDevice);
                result = procRelatedDeviceList;

                //查询销障工单关联设施信息
                procClearFailureService.queryProcRelateDeviceByProcIds((List<String>) procIds);
                List<ProcClearFailure> procClearFailureList = new ArrayList<>();
                ProcClearFailure procClearFailure = new ProcClearFailure();
                procClearFailure.setDeviceId("1");
                procClearFailureList.add(procClearFailure);
                result = procClearFailureList;
            }
        };
        procBaseService.queryProcRelateDeviceByProcIds(procIds);

    }

    /**
     * 查询关联部门信息
     * @author hedongwei@wistronits.com
     * @date  2019/3/27 12:56
     */
    @Test
    public void queryProcRelateDeptByProcIds() {
        List<String> procIds = new ArrayList<>();
        procIds.add("1");

        new Expectations() {
            {
                //查询工单关联设施信息
                procClearFailureService.queryClearRelateDeptByProcIds((List<String>) procIds);
                result = null;

                //查询销障工单关联设施信息
                procInspectionService.queryInspectionRelateDeptByProcIds(procIds);
                List<ProcInspection> procInspectionList = new ArrayList<>();
                ProcInspection procInspection = new ProcInspection();
                procInspection.setProcId("1");
                procInspection.setAccountabilityDept("name");
                procInspectionList.add(procInspection);
                result = procInspectionList;
            }
        };
        procBaseService.queryProcRelateDeptByProcIds(procIds);


        new Expectations() {
            {
                //查询工单关联设施信息
                procClearFailureService.queryClearRelateDeptByProcIds((List<String>) procIds);
                List<ProcClearFailure> procClearFailureList = new ArrayList<>();
                ProcClearFailure procClearFailure = new ProcClearFailure();
                procClearFailure.setProcId("1");
                procClearFailure.setAccountabilityDept("1");
                procClearFailureList.add(procClearFailure);
                result = procClearFailureList;

                //查询销障工单关联设施信息
                procInspectionService.queryInspectionRelateDeptByProcIds(procIds);
                List<ProcInspection> procInspectionList = new ArrayList<>();
                ProcInspection procInspection = new ProcInspection();
                procInspection.setAccountabilityDept("name");
                procInspection.setProcId("1");
                procInspectionList.add(procInspection);
                result = procInspectionList;
            }
        };
        procBaseService.queryProcRelateDeptByProcIds(procIds);

    }

    /**
     * 工单回单
     * @author chaofanrong@wistronits.com
     * @date  2019/4/3 17:33
     */
    @Test
    public void receiptProc() {
        ProcBase procBase = new ProcBase();
        procBaseService.receiptProc(procBase);
    }

    /**
     * 查询流程是否有没有办结的工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/7/23 16:42
     */
    @Test
    public void selectProcBaseListByProcInspection() {
        ProcInspection procInspection = new ProcInspection();
        procBaseService.selectProcBaseListByProcInspection(procInspection);
    }


    /**
     * app工单下载
     */
    @Test
    public void downLoadProcForApp() {
        ProcBaseReq req = new ProcBaseReq();
        procBaseService.downLoadProcForApp(req);

        req.setProcId("1");
        req.setProcType(ProcBaseConstants.PROC_INSPECTION);
        //查询工单信息
        new Expectations() {
            {
                procBaseDao.queryProcessByProcId(anyString, anyLong);
                result = null;
            }
        };

        try {
            procBaseService.downLoadProcForApp(req);
        } catch (Exception e) {

        }


        this.expectationsSearchProc();
        this.searchUserInfo();
        new Expectations() {
            {
                procClearFailureService.queryClearFailureListForApp((ProcBaseReq) any);
                List<ProcBaseRespForApp> procBaseRespForApps = new ArrayList<>();
                ProcBaseRespForApp procBaseRespForApp = new ProcBaseRespForApp();
                procBaseRespForApp.setProcId("1");
                procBaseRespForApp.setStatus("name");
                procBaseRespForApp.setDeviceId("1");
                procBaseRespForApp.setProcType(ProcBaseConstants.PROC_CLEAR_FAILURE);
                procBaseRespForApps.add(procBaseRespForApp);
                result = procBaseRespForApps;
            }
        };

        this.searchDeviceInfo();

        try {
            procBaseService.downLoadProcForApp(req);
        } catch (Exception e) {

        }

        new Expectations() {
            {
                procBaseDao.queryProcessByProcId(anyString, anyLong);
                ProcBaseResp procBaseInfo = new ProcBaseResp();
                procBaseInfo.setProcId("1");
                procBaseInfo.setStatus(ProcBaseConstants.PROC_STATUS_SINGLE_BACK);
                procBaseInfo.setProcType(ProcBaseConstants.PROC_INSPECTION);
                result = procBaseInfo;
            }
        };
        this.searchUserInfo();
        new Expectations() {
            {
                procInspectionService.queryInspectionListForApp((ProcBaseReq) any);
                List<ProcBaseRespForApp> procBaseRespForApps = new ArrayList<>();
                ProcBaseRespForApp procBaseRespForApp = new ProcBaseRespForApp();
                procBaseRespForApp.setProcId("1");
                procBaseRespForApp.setStatus("name");
                procBaseRespForApp.setDeviceId("1");
                procBaseRespForApps.add(procBaseRespForApp);
                result = procBaseRespForApps;
            }
        };

        try {
            procBaseService.downLoadProcForApp(req);
        } catch (Exception e) {

        }

        req.setProcType(ProcBaseConstants.PROC_CLEAR_FAILURE);
        this.expectationsSearchProc(ProcBaseConstants.PROC_CLEAR_FAILURE);
        this.searchUserInfo();
        new Expectations() {
            {
                procClearFailureService.queryClearFailureListForApp((ProcBaseReq) any);
                result = null;
            }
        };

        try {
            procBaseService.downLoadProcForApp(req);
        } catch (Exception e) {

        }
    }

    /**
     * 当前登录用户下载工单
     * @author hedongwei@wistronits.com
     * @date  2019/7/24 22:47
     */
    @Test
    public void downloadProcByLoginUserForApp() {
        ProcBaseReq req = new ProcBaseReq();
        procBaseService.downloadProcByLoginUserForApp(req);

        req.setProcId("1");
        req.setProcType(ProcBaseConstants.PROC_INSPECTION);


        try {
            procBaseService.downloadProcByLoginUserForApp(req);
        } catch (Exception e) {

        }

        new Expectations() {
            {
                procClearFailureService.queryLoginUserClearFailureListForApp((ProcBaseReq) any);
                List<ProcBaseRespForApp> procBaseRespForApps = new ArrayList<>();
                ProcBaseRespForApp procBaseRespForApp = new ProcBaseRespForApp();
                procBaseRespForApp.setProcId("1");
                procBaseRespForApp.setStatus("name");
                procBaseRespForApp.setDeviceId("1");
                procBaseRespForApp.setProcType(ProcBaseConstants.PROC_CLEAR_FAILURE);
                procBaseRespForApps.add(procBaseRespForApp);
                result = procBaseRespForApps;
            }
        };


        try {
            procBaseService.downloadProcByLoginUserForApp(req);
        } catch (Exception e) {

        }


        this.searchUserInfo();
        new Expectations() {
            {
                procInspectionService.queryLoginUserInspectionListForApp((ProcBaseReq) any);
                List<ProcBaseRespForApp> procBaseRespForApps = new ArrayList<>();
                ProcBaseRespForApp procBaseRespForApp = new ProcBaseRespForApp();
                procBaseRespForApp.setProcId("1");
                procBaseRespForApp.setStatus("name");
                procBaseRespForApp.setDeviceId("1");
                procBaseRespForApps.add(procBaseRespForApp);
                result = procBaseRespForApps;
            }
        };

        try {
            procBaseService.downloadProcByLoginUserForApp(req);
        } catch (Exception e) {

        }

        req.setProcType(ProcBaseConstants.PROC_CLEAR_FAILURE);
        new Expectations() {
            {
                procClearFailureService.queryLoginUserClearFailureListForApp((ProcBaseReq) any);
                result = null;
            }
        };

        try {
            procBaseService.downloadProcByLoginUserForApp(req);
        } catch (Exception e) {

        }
    }

    /**
     * 设置工单关联告警
     * @author hedongwei@wistronits.com
     * @date  2019/7/24 9:06
     */
    @Test
    public void setProcAlarmToRespForApp() {
        List<ProcBaseRespForApp> procBaseRespForApps = new ArrayList<>();
        try {
            procBaseService.setProcAlarmToRespForApp(procBaseRespForApps);
        } catch (Exception e) {

        }
        ProcBaseRespForApp respOne = new ProcBaseRespForApp();
        respOne.setProcId("1");
        respOne.setRefAlarm("1");
        procBaseRespForApps.add(respOne);
        respOne = new ProcBaseRespForApp();
        respOne.setProcId("2");
        respOne.setRefAlarm("2");
        procBaseRespForApps.add(respOne);

        AlarmCurrent alarmCurrent = new AlarmCurrent();
        alarmCurrent.setId("1");
        alarmCurrent.setAlarmName("1");
        alarmCurrent.setAlarmBeginTime(System.currentTimeMillis());
        alarmCurrent.setAlarmCode("1");
        alarmCurrent.setDoorName("doorName");
        alarmCurrent.setDoorNumber("111");

        AlarmHistory alarmHistory = new AlarmHistory();
        alarmHistory.setId("2");
        alarmHistory.setAlarmName("1");
        alarmHistory.setAlarmBeginTime(System.currentTimeMillis());
        alarmHistory.setAlarmCode("1");
        alarmHistory.setDoorName("doorName");
        alarmHistory.setDoorNumber("111");

        AlarmHistory alarmHistoryInfo = new AlarmHistory();
        BeanUtils.copyProperties(alarmHistory, alarmHistoryInfo);
        alarmHistoryInfo.setId("1");

        AlarmHistory alarmHistoryInfoData = new AlarmHistory();
        BeanUtils.copyProperties(alarmHistory, alarmHistoryInfo);
        alarmHistoryInfo.setId("5");

        new Expectations() {
            {
                alarmCurrentFeign.queryAlarmDoorByIdsFeign((List<String>) any);
                List<AlarmCurrent> alarmCurrentList = new ArrayList<>();
                alarmCurrentList.add(alarmCurrent);
                result = alarmCurrentList;


                alarmHistoryFeign.queryAlarmHistoryByIdsFeign((List<String>) any);
                List<AlarmHistory> alarmHistoryList = new ArrayList<>();
                alarmHistoryList.add(alarmHistory);
                result = alarmHistoryList;
            }
        };
        try {
            procBaseService.setProcAlarmToRespForApp(procBaseRespForApps);
        } catch (Exception e) {

        }

        new Expectations() {
            {
                alarmCurrentFeign.queryAlarmDoorByIdsFeign((List<String>) any);
                result = null;


                alarmHistoryFeign.queryAlarmHistoryByIdsFeign((List<String>) any);
                List<AlarmHistory> alarmHistoryList = new ArrayList<>();
                alarmHistoryList.add(alarmHistory);
                alarmHistoryList.add(alarmHistoryInfo);
                result = alarmHistoryList;
            }
        };
        try {
            procBaseService.setProcAlarmToRespForApp(procBaseRespForApps);
        } catch (Exception e) {

        }

        new Expectations() {
            {
                alarmCurrentFeign.queryAlarmDoorByIdsFeign((List<String>) any);
                result = null;


                alarmHistoryFeign.queryAlarmHistoryByIdsFeign((List<String>) any);
                result = null;
            }
        };
        try {
            procBaseService.setProcAlarmToRespForApp(procBaseRespForApps);
        } catch (Exception e) {

        }


        new Expectations() {
            {
                alarmCurrentFeign.queryAlarmDoorByIdsFeign((List<String>) any);
                result = null;


                alarmHistoryFeign.queryAlarmHistoryByIdsFeign((List<String>) any);
                List<AlarmHistory> alarmHistoryList = new ArrayList<>();
                alarmHistoryList.add(alarmHistory);
                alarmHistoryList.add(alarmHistoryInfoData);
                result = alarmHistoryList;
            }
        };
        try {
            procBaseService.setProcAlarmToRespForApp(procBaseRespForApps);
        } catch (Exception e) {

        }

    }

    /**
     * 查询设施信息
     * @author hedongwei@wistronits.com
     * @date  2019/7/23 21:51
     */
    public void searchDeviceInfo() {
        new Expectations() {
            {
                deviceFeign.getDeviceByIds((String []) any);
                List<DeviceInfoDto> deviceInfoDtoList = new ArrayList<>();
                DeviceInfoDto deviceInfoDto = new DeviceInfoDto();
                deviceInfoDto.setDeviceId("1");
                deviceInfoDto.setDeviceType(ProcBaseConstants.DEVICE_TYPE_001);
                AreaInfo areaInfo = new AreaInfo();
                areaInfo.setLevel(1);
                areaInfo.setAreaId("1");
                areaInfo.setAreaName("区域名称");
                deviceInfoDto.setAreaInfo(areaInfo);
                deviceInfoDtoList.add(deviceInfoDto);
                result = deviceInfoDtoList;
            }
        };
    }

    /**
     * 根据工单类型查询当前用户需要的下载信息
     * @author hedongwei@wistronits.com
     * @date  2019/7/15 16:32
     */
    @Test
    public void searchLoginUserDownloadInfoForProcType() {
        //巡检工单
        String procType = ProcBaseConstants.PROC_INSPECTION;
        ProcBaseReq procBaseReq = new ProcBaseReq();
        procBaseReq.setPageNumber(1);
        this.searchUserInfo();
        procBaseService.searchLoginUserDownloadInfoForProcType(procType, procBaseReq);

        //销障工单
        procType = ProcBaseConstants.PROC_CLEAR_FAILURE;
        this.searchUserInfo();
        procBaseService.searchLoginUserDownloadInfoForProcType(procType, procBaseReq);
    }

    /**
     * 设置工单关联信息
     * @author hedongwei@wistronits.com
     * @date  2019/7/23 22:47
     */
    @Test
    public void setProcRelatedInfoToRespForApp() {
        List<ProcRelatedDevice> procRelatedDevices = new ArrayList<>();
        List<ProcBaseRespForApp> procBaseRespForApps = new ArrayList<>();
        procBaseService.setProcRelatedInfoToRespForApp(procRelatedDevices, procBaseRespForApps);

        ProcRelatedDevice deviceOne = new ProcRelatedDevice();
        deviceOne.setDeviceId("1");
        procRelatedDevices.add(deviceOne);
        ProcBaseRespForApp resp = new ProcBaseRespForApp();
        resp.setDeviceId("1");
        procBaseRespForApps.add(resp);

        new Expectations() {
            {
                deviceFeign.getDeviceByIds((String []) any);
                result = null;
            }
        };
        try {
            procBaseService.setProcRelatedInfoToRespForApp(procRelatedDevices, procBaseRespForApps);
        } catch (Exception e) {

        }


        this.searchDeviceInfo();
        try {
            procBaseService.setProcRelatedInfoToRespForApp(procRelatedDevices, procBaseRespForApps);
        } catch (Exception e) {

        }


        new Expectations() {
            {
                deviceFeign.getDeviceByIds((String []) any);
                List<DeviceInfoDto> deviceInfoDtoList = new ArrayList<>();
                DeviceInfoDto deviceInfoDto = new DeviceInfoDto();
                deviceInfoDto.setDeviceId("2");
                deviceInfoDto.setDeviceType(ProcBaseConstants.DEVICE_TYPE_001);
                AreaInfo areaInfo = new AreaInfo();
                areaInfo.setLevel(1);
                areaInfo.setAreaId("1");
                areaInfo.setAreaName("区域名称");
                deviceInfoDto.setAreaInfo(areaInfo);
                deviceInfoDtoList.add(deviceInfoDto);
                result = deviceInfoDtoList;
            }
        };

        try {
            procBaseService.setProcRelatedInfoToRespForApp(procRelatedDevices, procBaseRespForApps);
        } catch (Exception e) {

        }
    }

    /**
     * 销障关联信息添加
     * @author hedongwei@wistronits.com
     * @date  2019/7/24 8:47
     */
    @Test
    public void setProcClearFailureRelatedInfoToRespForApp() {
        List<ProcBaseRespForApp> procBaseRespForApps = new ArrayList<>();
        ProcBaseRespForApp appResp = new ProcBaseRespForApp();
        appResp.setProcId("1");
        procBaseRespForApps.add(appResp);
        try {
            procBaseService.setProcClearFailureRelatedInfoToRespForApp(procBaseRespForApps);
        } catch (Exception e) {

        }

        procBaseRespForApps = new ArrayList<>();
        appResp = new ProcBaseRespForApp();
        appResp.setDeviceId("1");
        procBaseRespForApps.add(appResp);

        new Expectations() {
            {
                deviceFeign.getDeviceByIds((String []) any);
                result = null;
            }
        };

        try {
            procBaseService.setProcClearFailureRelatedInfoToRespForApp(procBaseRespForApps);
        } catch (Exception e) {

        }


        new Expectations() {
            {
                deviceFeign.getDeviceByIds((String []) any);
                List<DeviceInfoDto> deviceInfoDtoList = new ArrayList<>();
                DeviceInfoDto deviceInfoDto = new DeviceInfoDto();
                deviceInfoDto.setDeviceId("2");
                deviceInfoDto.setDeviceType(ProcBaseConstants.DEVICE_TYPE_001);
                deviceInfoDtoList.add(deviceInfoDto);
                result = deviceInfoDtoList;
                result = null;
            }
        };

        try {
            procBaseService.setProcClearFailureRelatedInfoToRespForApp(procBaseRespForApps);
        } catch (Exception e) {

        }
    }
    
    /**
     * 合并数据
     * @author hedongwei@wistronits.com
     * @date  2019/7/24 9:58
     */
    @Test
    public void unionDownloadDataList() {
        List<ClearFailureDownLoadDetail> clearFailureDownLoadDetails = new ArrayList<>();
        ClearFailureDownLoadDetail clearFailure = new ClearFailureDownLoadDetail();
        clearFailure.setProcId("1");
        clearFailureDownLoadDetails.add(clearFailure);
        List<InspectionDownLoadDetail > inspectionDownLoadDetails = new ArrayList<>();
        InspectionDownLoadDetail inspectionDownLoadDetail = new InspectionDownLoadDetail();
        inspectionDownLoadDetail.setProcId("1");
        inspectionDownLoadDetails.add(inspectionDownLoadDetail);
        procBaseService.unionDownloadDataList(clearFailureDownLoadDetails, inspectionDownLoadDetails);
    }

    /**
     * 保存工单关联信息
     * @return void
     */
    @Test
    public void saveProcRelate() {
        ProcessInfo processInfo = new ProcessInfo();
        ProcBase procBase = new ProcBase();
        procBase.setCreateTime(new Date());
        procBase.setProcType(ProcBaseConstants.PROC_INSPECTION);
        processInfo.setProcBase(procBase);
        List<ProcRelatedDevice> procRelatedDeviceList = new ArrayList<>();
        ProcRelatedDevice procRelatedDevice = new ProcRelatedDevice();
        procRelatedDevice.setDeviceId("1");
        procRelatedDeviceList.add(procRelatedDevice);
        processInfo.setProcRelatedDevices(procRelatedDeviceList);
        List<ProcInspectionRecord> procInspectionRecordList = new ArrayList<>();
        ProcInspectionRecord procInspectionRecord = new ProcInspectionRecord();
        procInspectionRecord.setDeviceId("1");
        procInspectionRecordList.add(procInspectionRecord);
        processInfo.setProcInspectionRecords(procInspectionRecordList);

        new Expectations() {
            {
                procInspectionService.selectProcInspectionOne((ProcInspection) any);
                result = null;
            }
        };
        procBaseService.saveProcRelate(processInfo);

        new Expectations() {
            {
                procInspectionService.selectProcInspectionOne((ProcInspection) any);
                ProcInspection procInspection = new ProcInspection();
                procInspection.setCreateTime(new Date());
                procInspection.setProcId("1");
                result = procInspection;
            }
        };
        procBaseService.saveProcRelate(processInfo);

    }


    /**
     * 组装流程主表及关系表数据
     * @author hedongwei@wistronits.com
     * @date  2019/7/24 10:28
     */
    @Test
    public void assemblyProcAndRelatedDataList() {

        List<ProcessInfo> processInfoList = new ArrayList<>();
        ProcessInfo processInfo = new ProcessInfo();
        ProcBaseResp resp = new ProcBaseResp();
        resp.setProcType(ProcBaseConstants.PROC_INSPECTION);
        resp.setProcId("1");
        processInfo.setProcBaseResp(resp);
        processInfoList.add(processInfo);

        resp = new ProcBaseResp();
        resp.setProcType(ProcBaseConstants.PROC_INSPECTION);
        resp.setProcId("2");
        processInfo.setProcBaseResp(resp);
        processInfoList.add(processInfo);

        List<ProcRelatedDevice> procRelatedDevices = new ArrayList<>();
        ProcRelatedDevice procRelatedDevice = new ProcRelatedDevice();
        procRelatedDevice.setProcId("1");
        procRelatedDevice.setDeviceId("1");
        procRelatedDevices.add(procRelatedDevice);
        procRelatedDevice = new ProcRelatedDevice();
        procRelatedDevice.setProcId("2");
        procRelatedDevice.setDeviceId("2");
        procRelatedDevices.add(procRelatedDevice);
        procRelatedDevice = new ProcRelatedDevice();
        procRelatedDevice.setProcId("1");
        procRelatedDevice.setDeviceId("2");
        procRelatedDevices.add(procRelatedDevice);
        List<ProcRelatedDepartment> procRelatedDepartments = new ArrayList<>();
        ProcRelatedDepartment procRelatedDepartment = new ProcRelatedDepartment();
        procRelatedDepartment.setProcId("1");
        procRelatedDepartment.setAccountabilityDept("1");
        procRelatedDepartments.add(procRelatedDepartment);
        procRelatedDepartment = new ProcRelatedDepartment();
        procRelatedDepartment.setProcId("1");
        procRelatedDepartment.setAccountabilityDept("1");
        procRelatedDepartments.add(procRelatedDepartment);
        List<Department> departmentList = new ArrayList<>();

        String methodType = ProcBaseConstants.PROC_METHOD_DETAIL;
        procBaseService.assemblyProcAndRelatedDataList(processInfoList, procRelatedDevices, procRelatedDepartments, departmentList, methodType);

    }

    /**
     * 设置单位名称
     * @author hedongwei@wistronits.com
     * @date  2019/7/24 10:48
     */
    @Test
    public void setAccountabilityUnitName() {
        ProcessInfo processInfo = new ProcessInfo();
        List<ProcRelatedDepartment> departmentList = new ArrayList<>();


        ProcRelatedDepartment department = new ProcRelatedDepartment();

        String methodType = ProcBaseConstants.PROC_METHOD_DETAIL;

        List<Department> departmentInfoList = new ArrayList<>();

        procBaseService.setAccountabilityUnitName(processInfo, departmentInfoList, methodType);

        department.setAccountabilityDept("1");
        department.setProcId("1");
        departmentList.add(department);

        department = new ProcRelatedDepartment();
        department.setAccountabilityDept("2");
        department.setProcId("1");
        departmentList.add(department);

        department = new ProcRelatedDepartment();
        department.setAccountabilityDept("2");
        department.setProcId("2");
        departmentList.add(department);

        processInfo.setProcRelatedDepartments(departmentList);

        procBaseService.setAccountabilityUnitName(processInfo, departmentInfoList, methodType);

        ProcBaseResp resp = new ProcBaseResp();
        resp.setProcId("1");
        processInfo.setProcBaseResp(resp);

        Department departmentOne = new Department();
        departmentOne.setId("1");
        departmentOne.setDeptName("name");
        departmentInfoList.add(departmentOne);
        departmentOne = new Department();
        departmentOne.setId("2");
        departmentOne.setDeptName("name");
        departmentInfoList.add(departmentOne);

        new Expectations() {
            {
                departmentFeign.queryDepartmentFeignById((List<String>) any);
                List<Department> departmentList = new ArrayList<>();
                Department departmentInfo = new Department();
                departmentInfo.setId("1");
                departmentInfo.setDeptName("deptName");
                departmentList.add(departmentInfo);
                departmentInfo = new Department();
                departmentInfo.setId("2");
                departmentInfo.setDeptName("deptName");
                departmentList.add(departmentInfo);
                result = departmentList;
            }
        };
        procBaseService.setAccountabilityUnitName(processInfo, departmentInfoList, methodType);

        new Expectations() {
            {
                departmentFeign.queryDepartmentFeignById((List<String>) any);
                result = null;
            }
        };
        try {
            procBaseService.setAccountabilityUnitName(processInfo, departmentInfoList, methodType);
        } catch (Exception e) {

        }


    }

    /**
     * 保存工单特性信息
     * @author hedongwei@wistronits.com
     * @date  2019/7/24 11:21
     */
    @Test
    public void saveProcSpecific() {
        ProcessInfo processInfo = new ProcessInfo();
        ProcBase procBase = new ProcBase();
        procBase.setProcType(ProcBaseConstants.PROC_CLEAR_FAILURE);
        procBase.setProcId("1");
        processInfo.setProcBase(procBase);
        ProcBaseReq req = new ProcBaseReq();
        processInfo.setProcBaseReq(req);
        List<ProcRelatedDepartment> procRelatedDepartmentList = new ArrayList<>();
        ProcRelatedDepartment procRelatedDepartment = new ProcRelatedDepartment();
        procRelatedDepartment.setAccountabilityDept("1");
        procRelatedDepartment.setProcId("1");
        procRelatedDepartmentList.add(procRelatedDepartment);
        processInfo.setProcRelatedDepartments(procRelatedDepartmentList);
        procBaseService.saveProcSpecific(processInfo);

        procBase = new ProcBase();
        procBase.setProcType(ProcBaseConstants.PROC_INSPECTION);
        procBase.setProcId("1");
        processInfo.setProcBase(procBase);
        req = new ProcBaseReq();
        processInfo.setProcBaseReq(req);

        procBaseService.saveProcSpecific(processInfo);
    }


    /**
     * 获取工单特性信息
     * @author hedongwei@wistronits.com
     * @date  2019/7/24 11:33
     */
    @Test
    public void queryProcSpecific() {
        List<ProcessInfo> processInfoList = new ArrayList<>();
        ProcessInfo processInfo = new ProcessInfo();
        processInfo.setProcId("1");
        processInfoList.add(processInfo);
        ProcessInfo processInfoParam = new ProcessInfo();
        processInfoParam.setProcId("2");
        processInfoList.add(processInfoParam);
        Set<String> procIds = new HashSet<>();
        procIds.add("1");
        procIds.add("2");

        String procType = ProcBaseConstants.PROC_CLEAR_FAILURE;

        new Expectations() {
            {
                procClearFailureService.queryProcClearFailureSpecific((Set<String>) any);
                List<ProcClearFailure> procClearFailures = new ArrayList<>();
                ProcClearFailure procClearFailure = new ProcClearFailure();
                procClearFailure.setProcId("1");
                procClearFailures.add(procClearFailure);
                procClearFailure = new ProcClearFailure();
                procClearFailure.setProcId("2");
                procClearFailures.add(procClearFailure);
                result = procClearFailures;
            }
        };

        procBaseService.queryProcSpecific(processInfoList, procIds, procType);

        procType = ProcBaseConstants.PROC_INSPECTION;

        new Expectations() {
            {
                procInspectionService.selectInspectionProcByProcIds((Set<String>) any);
                List<ProcInspection> procInspectionList = new ArrayList<>();
                ProcInspection procInspection = new ProcInspection();
                procInspection.setProcId("1");
                procInspectionList.add(procInspection);
                procInspection = new ProcInspection();
                procInspection.setProcId("2");
                procInspectionList.add(procInspection);
                result = procInspectionList;
            }
        };

        procBaseService.queryProcSpecific(processInfoList, procIds, procType);

    }


    /**
     * 远程获取用户信息
     * @author hedongwei@wistronits.com
     * @date  2019/7/24 12:24
     */
    @Test
    public void queryUserByIdList() {
        List<ProcBaseResp> procBaseRespList = new ArrayList<>();
        ProcBaseResp procBaseResp = new ProcBaseResp();
        procBaseResp.setAssign("1");
        procBaseRespList.add(procBaseResp);
        this.searchUserInfo();
        procBaseService.queryUserByIdList(procBaseRespList);
    }


    /**
     * 根据告警查询是否存在有告警的工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/7/24 12:31
     */
    @Test
    public void queryExistsProcForAlarmList() {
        List<String> alarmProcList = new ArrayList<>();
        procBaseService.queryExistsProcForAlarmList(alarmProcList);
        alarmProcList.add("1");
        alarmProcList.add("2");
        procBaseService.queryExistsProcForAlarmList(alarmProcList);
    }

    /**
     * 根据告警查询存在的工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/7/24 12:34
     */
    @Test
    public void queryExistsProcForAlarm() {
        ProcBase procBase = new ProcBase();
        String operateType = "";
        new Expectations() {
            {
                procClearFailureService.queryExistsProcClearFailureForAlarmList((List<String>) any);
                List<ProcClearFailure> procClearList = new ArrayList<>();
                ProcClearFailure procClearFailure = new ProcClearFailure();
                procClearFailure.setProcId("1");
                procClearList.add(procClearFailure);
                result = procClearList;
            }
        };
        procBaseService.queryExistsProcForAlarm(procBase, operateType);

        procBase.setProcId("1");
        new Expectations() {
            {
                procClearFailureService.queryExistsProcClearFailureForAlarmList((List<String>) any);
                List<ProcClearFailure> procClearList = new ArrayList<>();
                ProcClearFailure procClearFailure = new ProcClearFailure();
                procClearFailure.setProcId("1");
                procClearList.add(procClearFailure);
                result = procClearList;
            }
        };
        procBaseService.queryExistsProcForAlarm(procBase, operateType);

        new Expectations() {
            {
                procClearFailureService.queryExistsProcClearFailureForAlarmList((List<String>) any);
                List<ProcClearFailure> procClearList = new ArrayList<>();
                ProcClearFailure procClearFailure = new ProcClearFailure();
                procClearFailure.setProcId("2");
                procClearList.add(procClearFailure);
                result = procClearList;
            }
        };
        procBaseService.queryExistsProcForAlarm(procBase, operateType);

        procBase.setRegenerateId("1");
        operateType = ProcBaseConstants.OPERATOR_TYPE_REGENERATE;
        new Expectations() {
            {
                procClearFailureService.queryExistsProcClearFailureForAlarmList((List<String>) any);
                List<ProcClearFailure> procClearList = new ArrayList<>();
                ProcClearFailure procClearFailure = new ProcClearFailure();
                procClearFailure.setProcId("2");
                procClearList.add(procClearFailure);
                result = procClearList;
            }
        };
        procBaseService.queryExistsProcForAlarm(procBase, operateType);

    }

    /**
     * 获取拥有权限信息（导出）
     * @author hedongwei@wistronits.com
     * @date  2019/7/24 12:51
     */
    @Test
    public void getPermissionsInfoForExport() {
        QueryCondition<ProcBaseReq> queryCondition = new QueryCondition<>();
        try {
            procBaseService.getPermissionsInfoForExport(queryCondition);
        } catch (Exception e) {

        }
    }

    /**
     * 获取拥有权限信息
     * @author hedongwei@wistronits.com
     * @date  2019/7/24 12:53
     */
    @Test
    public void getPermissionsInfo() {
        String userId = "1";
        new Expectations() {
            {
                userFeign.queryUserByIdList((List<String>) any);
            }
        };
        QueryCondition<ProcBaseReq> queryCondition = new QueryCondition<>();
        try {
            procBaseService.getPermissionsInfo(queryCondition, userId);
        } catch (Exception e) {

        }

        new Expectations() {
            {
                userFeign.queryUserByIdList((List<String>) any);
                User user = new User();
                user.setId("1");
                Department department = new Department();
                department.setId("1");
                Role role = new Role();
                List<RoleDeviceType> roleDeviceTypeList = new ArrayList<>();
                RoleDeviceType roleDeviceType = new RoleDeviceType();
                roleDeviceType.setDeviceTypeId("1");
                roleDeviceType.setRoleId("1");
                roleDeviceType.setId("1");
                roleDeviceTypeList.add(roleDeviceType);
                role.setId("1");
                role.setRoleDevicetypeList(roleDeviceTypeList);
                user.setDepartment(department);
                user.setRole(role);
                List<User> userList = new ArrayList<>();
                userList.add(user);
                result = userList;
            }
        };
        procBaseService.getPermissionsInfo(queryCondition, userId);
    }

    /**
     * 根据设施编号集合删除工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/7/24 12:59
     */
    @Test
    public void deleteProcBaseForDeviceList(){
        DeleteProcBaseForDeviceReq req = new DeleteProcBaseForDeviceReq();
        List<String> deviceIdList = new ArrayList<>();
        deviceIdList.add("1");
        req.setDeviceIdList(deviceIdList);
        procBaseService.deleteProcBaseForDeviceList(req);
    }
    
    
    /**
     * 删除巡检工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/7/24 13:36
     */
    @Test
    public void deleteInspectionInfo() {
        DeleteProcBaseForDeviceReq req = new DeleteProcBaseForDeviceReq();

        List<ProcRelatedDevice> procRelatedDeviceList = new ArrayList<>();
        ProcRelatedDevice procRelatedDevice = new ProcRelatedDevice();
        procRelatedDevice.setDeviceId("1");
        procRelatedDevice.setProcId("1");
        procRelatedDeviceList.add(procRelatedDevice);

        procRelatedDevice = new ProcRelatedDevice();
        procRelatedDevice.setDeviceId("2");
        procRelatedDevice.setProcId("2");
        procRelatedDeviceList.add(procRelatedDevice);


        ProcRelatedDeviceListForDeviceIdsReq relatedDeviceReq = new ProcRelatedDeviceListForDeviceIdsReq();
        List<String> deviceReq = new ArrayList<>();
        deviceReq.add("1");
        deviceReq.add("2");
        deviceReq.add("3");
        relatedDeviceReq.setDeviceIdList(deviceReq);

        req.setDeviceIdList(deviceReq);

        new Expectations() {
            {
                procBaseDao.selectRelatedDeviceListForProcIds((ProcBaseReq) any);
                result = procRelatedDeviceList;
            }
        };

        new Expectations() {
            {
                procBaseDao.queryProcessByProcIds((ProcBaseReq) any);
                List<ProcBase> procBaseList = new ArrayList<>();
                ProcBase procBase = new ProcBase();
                procBase.setProcId("1");
                procBaseList.add(procBase);
                procBase = new ProcBase();
                procBase.setProcId("2");
                procBaseList.add(procBase);
                procBase = new ProcBase();
                procBase.setProcId("3");
                procBaseList.add(procBase);
                procBase.setProcId("4");
                procBaseList.add(procBase);
                result = procBaseList;
            }
        };

        procBaseService.deleteInspectionInfo(req, procRelatedDeviceList, relatedDeviceReq);


        new Expectations() {
            {
                procBaseDao.selectRelatedDeviceListForProcIds((ProcBaseReq) any);
                List<ProcRelatedDevice> procRelatedDeviceInfoList = new ArrayList<>();
                ProcRelatedDevice procRelatedDevice = new ProcRelatedDevice();
                procRelatedDevice.setDeviceId("3");
                procRelatedDevice.setProcId("3");
                procRelatedDeviceInfoList.add(procRelatedDevice);
                result = procRelatedDeviceInfoList;
            }
        };
        procBaseService.deleteInspectionInfo(req, procRelatedDeviceList, relatedDeviceReq);
    }

    /**
     * 删除销障工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/7/24 22:33
     */
    @Test
    public void deleteClearFailureInfo() {
        DeleteProcBaseForDeviceReq req = new DeleteProcBaseForDeviceReq();
        req.setIsDeleted(WorkFlowBusinessConstants.IS_DELETED);
        List<ProcClearFailure> procClearList = new ArrayList<>();
        ProcClearFailure procClearFailure = new ProcClearFailure();
        procClearFailure.setProcId("1");
        procClearList.add(procClearFailure);
        procBaseService.deleteClearFailureInfo(req, procClearList);
    }

    /**
     * 删除工单
     * @author hedongwei@wistronits.com
     * @date  2019/7/24 22:41
     */
    @Test
    public void deleteProcProcess() {
        ProcBaseReq procBaseReq = new ProcBaseReq();
        List<ProcBaseReq> procBaseReqList = new ArrayList<>();
        ProcBaseReq procBaseReqOne = new ProcBaseReq();
        procBaseReqOne.setProcId("1");
        procBaseReqOne.setProcType(ProcBaseConstants.PROC_INSPECTION);
        procBaseReqList.add(procBaseReqOne);
        procBaseReqOne = new ProcBaseReq();
        procBaseReqOne.setProcId("2");
        procBaseReqOne.setProcType(ProcBaseConstants.PROC_CLEAR_FAILURE);
        procBaseReqList.add(procBaseReqOne);
        String isDeleted = WorkFlowBusinessConstants.IS_DELETED;
        procBaseService.deleteProcProcess(procBaseReq, procBaseReqList, isDeleted);
    }

    /**
     * 新增告警id及工单id
     * @author hedongwei@wistronits.com
     * @date  2019/7/15 14:14
     */
    @Test
    public void addProcRelatedAlarm() {
        ProcRelatedAlarm procRelatedAlarm = new ProcRelatedAlarm();
        procRelatedAlarm.setProcId("1");
        procRelatedAlarm.setRefAlarmId("1");
        try {
            procBaseService.addProcRelatedAlarm(procRelatedAlarm);
        } catch (Exception e) {

        }
        procRelatedAlarm.setRefAlarmCode("1");
        procBaseService.addProcRelatedAlarm(procRelatedAlarm);
    }


    /**
     * 根据编号集合查询工单是否存在
     * @author hedongwei@wistronits.com
     * @date  2019/7/15 11:24
     */
    public void searchProcBaseInfoForIds() {
        new Expectations() {
            {
                //获取所有关系表信息并转成map
                procBaseDao.queryProcRelateDevice((ProcBaseReq) any);
                List<ProcRelatedDevice> procRelatedDevices = new ArrayList<>();
                ProcRelatedDevice procRelatedDevice = new ProcRelatedDevice();
                procRelatedDevice.setDeviceAreaId("1");
                procRelatedDevice.setDeviceId("1");
                procRelatedDevice.setProcRelatedDeviceId("1");
                procRelatedDevices.add(procRelatedDevice);
                result = procRelatedDevices;
                //查询销障工单信息
                procClearFailureService.queryClearFailureProcRelateDevice((ProcBaseReq) any);
                List<ProcClearFailure> procClearFailureList = new ArrayList<>();
                ProcClearFailure procClearFailure = new ProcClearFailure();
                procClearFailure.setDeviceId("1");
                procClearFailure.setDeviceAreaId("1");
                procClearFailure.setProcId("1");
                procClearFailureList.add(procClearFailure);
                result = procClearFailureList;
            }
        };
    }

    /**
     * 查询用户信息
     * @author hedongwei@wistronits.com
     * @date  2019/7/15 9:22
     */
    public void searchUserInfo() {
        new Expectations() {
            {
                userFeign.queryUserByIdList((List<String>) any);
                User user = new User();
                user.setId("1");
                List<String> areaIds = new ArrayList<>();
                areaIds.add("1");
                Department department = new Department();
                department.setId("1");
                department.setAreaIdList(areaIds);
                Role role = new Role();
                List<RoleDeviceType> roleDeviceTypeList = new ArrayList<>();
                RoleDeviceType roleDeviceType = new RoleDeviceType();
                roleDeviceType.setDeviceTypeId("1");
                roleDeviceType.setRoleId("1");
                roleDeviceType.setId("1");
                roleDeviceTypeList.add(roleDeviceType);
                role.setId("1");
                role.setRoleDevicetypeList(roleDeviceTypeList);
                user.setDepartment(department);
                user.setRole(role);
                List<User> userList = new ArrayList<>();
                userList.add(user);
                result = userList;
            }
        };
    }

    /**
     * 设置用户信息
     *
     */
    @Test
    public void setUserInfo() {
        ProcessInfo processInfo = new ProcessInfo();
        ProcBase procBase = new ProcBase();
        ProcBaseReq req = new ProcBaseReq();
        procBase.setProcType(ProcBaseConstants.PROC_CLEAR_FAILURE);
        procBase.setProcResourceType("1");
        processInfo.setProcBase(procBase);
        BeanUtils.copyProperties(procBase, req);
        processInfo.setProcBaseReq(req);
        procBaseService.setUserInfo(processInfo, WorkFlowBusinessConstants.OPERATE_TYPE_ADD);

        BeanUtils.copyProperties(procBase, req);
        processInfo.setProcBaseReq(req);
        procBaseService.setUserInfo(processInfo, WorkFlowBusinessConstants.OPERATE_TYPE_UPDATE);


        BeanUtils.copyProperties(procBase, req);
        procBase.setProcType(ProcBaseConstants.PROC_INSPECTION);
        processInfo.setProcBaseReq(req);
        procBaseService.setUserInfo(processInfo, WorkFlowBusinessConstants.OPERATE_TYPE_UPDATE);
    }

}
