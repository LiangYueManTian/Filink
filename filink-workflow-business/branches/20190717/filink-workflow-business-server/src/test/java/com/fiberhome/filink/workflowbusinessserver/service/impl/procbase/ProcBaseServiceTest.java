package com.fiberhome.filink.workflowbusinessserver.service.impl.procbase;

import com.fiberhome.filink.alarmcurrentapi.api.AlarmCurrentFeign;
import com.fiberhome.filink.alarmhistoryapi.api.AlarmHistoryFeign;
import com.fiberhome.filink.bean.PageCondition;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.deviceapi.api.DeviceFeign;
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
import com.fiberhome.filink.workflowbusinessserver.component.log.AddLogForApp;
import com.fiberhome.filink.workflowbusinessserver.constant.ProcBaseConstants;
import com.fiberhome.filink.workflowbusinessserver.constant.WorkFlowBusinessConstants;
import com.fiberhome.filink.workflowbusinessserver.dao.procbase.ProcBaseDao;
import com.fiberhome.filink.workflowbusinessserver.req.inspectiontask.UpdateInspectionStatusReq;
import com.fiberhome.filink.workflowbusinessserver.req.procbase.*;
import com.fiberhome.filink.workflowbusinessserver.req.process.CompleteProcessInfoReq;
import com.fiberhome.filink.workflowbusinessserver.req.process.StartProcessInfoReq;
import com.fiberhome.filink.workflowbusinessserver.resp.ProcBaseResp;
import com.fiberhome.filink.workflowbusinessserver.resp.ProcessInfo;
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
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
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
    /*@Test
    public void addProcBase() {
        ProcessInfo processInfo = new ProcessInfo();
        ProcBase procBase = new ProcBase();
        procBase.setProcType(ProcBaseConstants.PROC_CLEAR_FAILURE);
        processInfo.setProcBase(procBase);
        procBaseService.addProcBase(processInfo);



       *//* procBase.setProcType(ProcBaseConstants.PROC_INSPECTION);
        processInfo.setProcBase(procBase);
        procBaseService.addProcBase(processInfo);*//*
    }*/




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
                procBaseDao.queryProcessByProcId(anyString);
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
                procBaseDao.queryProcessByProcId(anyString);
                ProcBaseResp procBaseInfo = new ProcBaseResp();
                procBaseInfo.setProcId("1");
                procBaseInfo.setStatus(ProcBaseConstants.PROC_STATUS_SINGLE_BACK);
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

    }


    /**
     * 重新生成工单信息
     * @author hedongwei@wistronits.com
     * @date  2019/7/13 17:30
     */
    @Test
    public void regenerateProc() {

    }

    /**
     * 校验重新生成工单结果
     * @author hedongwei@wistronits.com
     * @date  2019/4/4 17:22
     */
    @Test
    public void checkRegenerateCondition() {
       String procId = "" ;
        procBaseService.checkRegenerateCondition(procId);
        procId = "1";
        new Expectations() {
            {
                procBaseDao.queryProcessByProcId(anyString);
                result = null;
            }
        };
        procBaseService.checkRegenerateCondition(procId);
    }

    /**
     * 修改工单
     * @author hedongwei@wistronits.com
     * @date  2019/7/13 17:35
     */
    @Test
    public void updateProcessById() {

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
                procBaseDao.queryProcessByProcId(anyString);
                result = null;
            }
        };
        procBaseService.updateProcBaseStatusById(procBase);


        new Expectations(ProcBaseValidate.class) {
            {
                procBaseDao.queryProcessByProcId(anyString);
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
                procBaseDao.queryProcessByProcId(anyString);
                ProcBaseResp procBaseResp = new ProcBaseResp();
                BeanUtils.copyProperties(procBase, procBaseResp);
                result = procBaseResp;
            }
        };

        procBaseService.updateProcBaseStatusById(procBase);


        procBase.setProcType(ProcBaseConstants.PROC_CLEAR_FAILURE);
        new Expectations(ProcBaseValidate.class) {
            {
                procBaseDao.queryProcessByProcId(anyString);
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
                procBaseDao.queryProcessByProcId(anyString);
                result = null;
            }
        };
        procBaseService.getProcTypeByProcId(procId);

        new Expectations(ProcBaseValidate.class) {
            {
                procBaseDao.queryProcessByProcId(anyString);
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
                procBaseDao.queryProcessByProcId(anyString);
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
                procBaseDao.queryProcessByProcId(anyString);
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
                procBaseDao.queryProcessByProcId(anyString);
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
                procBaseDao.queryProcessByProcId(anyString);
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
                procBaseDao.queryProcessByProcId(anyString);
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
                procBaseDao.queryProcessByProcId(anyString);
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
                procBaseDao.queryProcessByProcId(anyString);
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
                procBaseDao.queryProcessByProcId(anyString);
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
                procBaseDao.queryProcessByProcId(anyString);
                result = null;
            }
        };
        procBaseService.queryProcessById(id);

        new Expectations() {
            {
                procBaseDao.queryProcessByProcId(anyString);
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
                procBaseDao.queryProcessByProcId(anyString);
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
                procBaseDao.queryProcessByProcId(anyString);
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
    /*@Test
    public void assignProc() {
        AssignProcReq req = new AssignProcReq();
        req.setProcId("1");
        new Expectations() {
            {
                procBaseDao.queryProcessByProcId(anyString);
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
                procBaseDao.queryProcessByProcId(anyString);
                ProcBaseResp procBaseInfo = new ProcBaseResp();
                procBaseInfo.setProcId("1");
                procBaseInfo.setStatus(ProcBaseConstants.PROC_STATUS_ASSIGNED);
                result = procBaseInfo;
            }
        };
        proxySelf = procBaseService;
        procBaseService.assignProc(req);

    }*/

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
                procBaseDao.queryProcessByProcId(anyString);
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
                procBaseDao.queryProcessByProcId(anyString);
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
                procBaseDao.queryProcessByProcId(anyString);
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
                procBaseDao.queryProcessByProcId(anyString);
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
                procBaseDao.queryProcessByProcId(anyString);
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
     * @return Result
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
