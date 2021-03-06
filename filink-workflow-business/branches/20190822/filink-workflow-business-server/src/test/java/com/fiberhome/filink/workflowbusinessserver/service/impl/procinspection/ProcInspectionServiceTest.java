package com.fiberhome.filink.workflowbusinessserver.service.impl.procinspection;

import com.fiberhome.filink.bean.*;
import com.fiberhome.filink.deviceapi.api.AreaFeign;
import com.fiberhome.filink.deviceapi.api.DeviceFeign;
import com.fiberhome.filink.deviceapi.bean.AreaInfoForeignDto;
import com.fiberhome.filink.deviceapi.bean.DeviceInfoDto;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.exportapi.bean.ExportRequestInfo;
import com.fiberhome.filink.exportapi.exception.FilinkExportDataTooLargeException;
import com.fiberhome.filink.exportapi.exception.FilinkExportNoDataException;
import com.fiberhome.filink.exportapi.exception.FilinkExportTaskNumTooBigException;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.bean.LoginUserBean;
import com.fiberhome.filink.logapi.constant.LogConstants;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import com.fiberhome.filink.userapi.api.DepartmentFeign;
import com.fiberhome.filink.userapi.bean.Department;
import com.fiberhome.filink.workflowbusinessserver.bean.inspectiontask.InspectionTask;
import com.fiberhome.filink.workflowbusinessserver.bean.inspectiontask.InspectionTaskDepartment;
import com.fiberhome.filink.workflowbusinessserver.bean.inspectiontask.InspectionTaskDetailBean;
import com.fiberhome.filink.workflowbusinessserver.bean.inspectiontask.InspectionTaskDevice;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcBase;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcRelatedDepartment;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcRelatedDevice;
import com.fiberhome.filink.workflowbusinessserver.bean.procinspection.AddInspectionTaskBean;
import com.fiberhome.filink.workflowbusinessserver.bean.procinspection.ProcInspection;
import com.fiberhome.filink.workflowbusinessserver.bean.procinspection.ProcInspectionRecord;
import com.fiberhome.filink.workflowbusinessserver.constant.InspectionTaskConstants;
import com.fiberhome.filink.workflowbusinessserver.constant.ProcBaseConstants;
import com.fiberhome.filink.workflowbusinessserver.constant.ProcInspectionConstants;
import com.fiberhome.filink.workflowbusinessserver.constant.WorkFlowBusinessConstants;
import com.fiberhome.filink.workflowbusinessserver.dao.procinspection.ProcInspectionDao;
import com.fiberhome.filink.workflowbusinessserver.export.procinspection.InspectionProcCompleteExport;
import com.fiberhome.filink.workflowbusinessserver.export.procinspection.InspectionProcProcessExport;
import com.fiberhome.filink.workflowbusinessserver.req.procbase.ProcBaseReq;
import com.fiberhome.filink.workflowbusinessserver.req.process.CompleteProcessInfoReq;
import com.fiberhome.filink.workflowbusinessserver.req.procinspection.*;
import com.fiberhome.filink.workflowbusinessserver.resp.ProcBaseResp;
import com.fiberhome.filink.workflowbusinessserver.resp.ProcessDetail;
import com.fiberhome.filink.workflowbusinessserver.resp.ProcessInfo;
import com.fiberhome.filink.workflowbusinessserver.resp.app.procbase.ProcBaseRespForApp;
import com.fiberhome.filink.workflowbusinessserver.resp.app.procbase.ProcRelatedDeviceResp;
import com.fiberhome.filink.workflowbusinessserver.resp.procinspection.ProcInspectionResp;
import com.fiberhome.filink.workflowbusinessserver.service.inspectiontask.InspectionTaskService;
import com.fiberhome.filink.workflowbusinessserver.service.procbase.ProcBaseService;
import com.fiberhome.filink.workflowbusinessserver.service.procbase.ProcLogService;
import com.fiberhome.filink.workflowbusinessserver.service.process.WorkflowService;
import com.fiberhome.filink.workflowbusinessserver.service.procinspection.ProcInspectionRecordService;
import com.fiberhome.filink.workflowbusinessserver.vo.procinspection.GetInspectionProcByIdVo;
import com.fiberhome.filink.workflowbusinessserver.vo.procinspection.GetInspectionTaskRelatedProcVo;
import com.fiberhome.filink.workflowbusinessserver.vo.procinspection.ProcInspectionRecordVo;
import com.fiberhome.filink.workflowbusinessserver.vo.procinspection.ProcInspectionVo;
import mockit.*;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.*;

/**
 * ??????????????????????????????
 * @author hedongwei@wistronits.com
 * @date 2019/7/25 16:56
 */
@RunWith(JMockit.class)
public class ProcInspectionServiceTest {

    /**
     * ?????????????????????????????????
     */
    @Tested
    private ProcInspectionServiceImpl procInspectionService;

    /**
     * ???????????????
     */
    @Injectable
    private ProcBaseService procBaseService;

    /**
     * ???????????????
     */
    @Injectable
    private LogProcess logProcess;


    /**
     * ?????????????????????
     */
    @Injectable
    private WorkflowService workflowService;

    /**
     * ?????????????????????
     */
    @Injectable
    private ProcInspectionDao procInspectionDao;

    /**
     * ?????????????????????
     */
    @Injectable
    private InspectionTaskService inspectionTaskService;

    /**
     * ???????????????????????????
     */
    @Injectable
    private ProcInspectionRecordService procInspectionRecordService;

    /**
     * ??????????????????????????????
     */
    @Injectable
    private InspectionProcProcessExport inspectionProcProcessExport;

    /**
     * ??????????????????????????????
     */
    @Injectable
    private InspectionProcCompleteExport inspectionProcCompleteExport;

    /**
     * ??????????????????
     */
    @Injectable
    private DeviceFeign deviceFeign;

    /**
     * ??????????????????
     */
    @Injectable
    private DepartmentFeign departmentFeign;

    /**
     * ??????????????????
     */
    @Injectable
    private AreaFeign areaFeign;

    /**
     * ?????????????????????
     */
    @Injectable
    private ProcLogService procLogService;

    /**
     * ??????????????????
     */
    @Injectable
    private Integer maxExportDataSize;

    /**
     * ????????????
     */
    @Injectable
    private SystemLanguageUtil systemLanguage;


    /**
     * ??????????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/8/2 14:26
     */
    @Test
    public void callCommitInspectionProcReq() {
        CommitInspectionProcReq req = new CommitInspectionProcReq();
        req.getUserId();
        req.getProcId();
    }

    /**
     * ???????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/8/2 14:32
     */
    @Test
    public void callCompleteInspectionByPageReq() {
        CompleteInspectionByPageReq req = new CompleteInspectionByPageReq();
        req.getDeviceIds();
        req.getDeviceName();
        req.getResults();
        req.getResult();
        req.getExceptionDescription();
        req.getUpdateUser();
        req.getResourceMatching();
        req.getInspectionTimes();

        InspectionTaskRelationProcByPageReq pageReq = new InspectionTaskRelationProcByPageReq();
    }

    /**
     * ???????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/8/2 10:59
     */
    @Test
    public void callProcInspectionResp() {
        ProcInspectionResp resp = new ProcInspectionResp();
        resp.getInspectionProcessCount();
        resp.getInspectionCompletedPercent();
        resp.getInspectionProcessPercent();
        resp.getNormalInspectionCount();
        resp.getNormalInspectionPercent();
        resp.getErrorInspectionCount();
        resp.getErrorInspectionPercent();
    }


    /**
     * ?????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/8/2 11:05
     */
    @Test
    public void callProcInspection() {
        ProcInspection procInspection = new ProcInspection();
        procInspection.getInspectionAreaId();
        procInspection.setInspectionAreaId("1");
        procInspection.getInspectionAreaName();
        procInspection.setInspectionAreaName("name");
        procInspection.getInspectionTaskRecordId();
        procInspection.setInspectionTaskRecordId("1");
        procInspection.setInspectionStartTime(new Date());
        procInspection.setInspectionEndTime(new Date());
        procInspection.getIsSelectAll();
        procInspection.getDeviceId();
        procInspection.getDeviceName();
        procInspection.getDeviceAreaId();
        procInspection.getDeviceAreaName();
        procInspection.getDeptType();
        procInspection.getAccountabilityDeptName();
        procInspection.getProcType();
        procInspection.getTitle();
        procInspection.getAssign();
        procInspection.getDeptType();
        procInspection.getStatus();
        procInspection.getSingleBackReason();
        procInspection.getSingleBackUserDefinedReason();
        procInspection.getProcessingScheme();
        procInspection.getProcessingUserDefinedScheme();
        procInspection.getExpectedCompletedTime();
        procInspection.getRealityCompletedTime();
        procInspection.getRemark();
        procInspection.getErrorReason();
        procInspection.getErrorUserDefinedReason();
        procInspection.getProcResourceType();
        procInspection.getTurnReason();
        procInspection.getIsTold();
        procInspection.getRefAlarm();
        procInspection.getRefAlarmCode();
        procInspection.getRefAlarmName();
        procInspection.getIsCheckSingleBack();
        procInspection.getIsCreateAlarm();
        procInspection.getRegenerateId();
        procInspection.toString();
    }

    /**
     * ???????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/8/1 15:17
     */
    @Test
    public void callProcInspectionReq() {
        ProcInspectionReq procInspectionReq = new ProcInspectionReq();
        procInspectionReq.getUserName();
        procInspectionReq.getRoleId();
        procInspectionReq.getRoleName();
        procInspectionReq.getTaskPeriod();
        procInspectionReq.getInspectionAreaId();
        procInspectionReq.getInspectionAreaName();

        ProcInspectionReq.validateProcInspectionReq(procInspectionReq, ProcInspectionConstants.OPERATE_UPDATE);
        procInspectionReq.setProcId("1");
        ProcInspectionReq.validateProcInspectionReq(procInspectionReq, ProcInspectionConstants.OPERATE_UPDATE);
        procInspectionReq.setTitle("testInfoData");
        ProcInspectionReq.validateProcInspectionReq(procInspectionReq, ProcInspectionConstants.OPERATE_UPDATE);

        procInspectionReq.setProcResourceType(ProcInspectionConstants.AUTO_ADD_PROC_INSPECTION);
        ProcInspectionReq.validateProcInspectionReq(procInspectionReq, ProcInspectionConstants.OPERATE_UPDATE);

        procInspectionReq.setIsSelectAll("0");
        ProcInspectionReq.validateProcInspectionReq(procInspectionReq, ProcInspectionConstants.OPERATE_UPDATE);

        procInspectionReq.setRemark("??????????????????");
        procInspectionReq.setInspectionAreaId("1");
        procInspectionReq.setInspectionTaskName("name");
        ProcInspectionReq.validateProcInspectionReq(procInspectionReq, ProcInspectionConstants.OPERATE_UPDATE);

        List<ProcRelatedDevice> inspectionTaskDeviceList = new ArrayList<>();
        ProcRelatedDevice procRelatedDevice = new ProcRelatedDevice();
        procRelatedDevice.setDeviceId("1");
        inspectionTaskDeviceList.add(procRelatedDevice);
        procInspectionReq.setDeviceList(inspectionTaskDeviceList);
        ProcInspectionReq.validateProcInspectionReq(procInspectionReq, ProcInspectionConstants.OPERATE_UPDATE);

        procInspectionReq.setInspectionStartDate(new Date().getTime());
        ProcInspectionReq.validateProcInspectionReq(procInspectionReq, ProcInspectionConstants.OPERATE_UPDATE);

        procInspectionReq.setInspectionEndDate(new Date().getTime());
        ProcInspectionReq.validateProcInspectionReq(procInspectionReq, ProcInspectionConstants.OPERATE_UPDATE);
    }


    /**
     * ??????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/8/1 17:30
     */
    @Test
    public void callReqMethod() {
        ProcInspectionReq req = new ProcInspectionReq();
        req.setInspectionAreaId("1");
        ProcInspectionReq.checkAreaInfo(req);
        req.setInspectionAreaName("2");
        ProcInspectionReq.checkAreaInfo(req);


        ProcInspectionReq.checkByResourceType(req);
        req.setProcResourceType(ProcInspectionConstants.AUTO_ADD_PROC_INSPECTION);
        ProcInspectionReq.checkByResourceType(req);

        new Expectations(LogProcess.class) {
            {
                LogProcess.getLoginUserBeanInfo();
                result = new LoginUserBean();
            }
        };
        req.setProcResourceType(ProcInspectionConstants.AUTO_ADD_PROC_ALARM);
        ProcInspectionReq.checkByResourceType(req);

    }


    /**
     * ???????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/8/1 17:32
     */
    @Test
    public void alarmGenerateRequired() {
        ProcInspectionReq req = new ProcInspectionReq();
        new Expectations(LogProcess.class) {
            {
                LogProcess.getLoginUserBeanInfo();
                result = new LoginUserBean();
            }
        };
        ProcInspectionReq.alarmGenerateRequired(req);
        req.setRefAlarm("1");
        new Expectations(LogProcess.class) {
            {
                LogProcess.getLoginUserBeanInfo();
                result = new LoginUserBean();
            }
        };
        ProcInspectionReq.alarmGenerateRequired(req);
    }

    /**
     * ???????????????????????????
     */
    @Test
    public void callProcInspectionVo() {
        GetInspectionProcByIdVo byIdVo = new GetInspectionProcByIdVo();
        byIdVo.getDeviceName();
        byIdVo.getInspectionDeviceCount();
        byIdVo.getDeviceName();
        byIdVo.getDeviceType();
        byIdVo.getAccountabilityDeptName();
        byIdVo.getDeviceAreaId();
        byIdVo.getDeviceAreaName();
        byIdVo.getAccountabilityDeptName();
        byIdVo.getAlarmName();
        byIdVo.getInspectionStartDate();
        byIdVo.getInspectionStartTime();
        byIdVo.getInspectionEndDate();
        byIdVo.getInspectionEndTime();
        byIdVo.getInspectionDeviceCount();
        byIdVo.getInspectionCompletedCount();
        byIdVo.getProgressSpeed();
        byIdVo.getcTime();
        byIdVo.setcTime(new Date().getTime());
        byIdVo.getuTime();
        byIdVo.setuTime(new Date().getTime());
        byIdVo.getEcTime();
        byIdVo.getRcTime();
        byIdVo.getLastDays();
        byIdVo.getProcRelatedDepartments();
        byIdVo.getProcRelatedDevices();


        GetInspectionTaskRelatedProcVo procVo = new GetInspectionTaskRelatedProcVo();
        procVo.getProcId();
        procVo.getTitle();
        procVo.getAssign();
        procVo.getStatus();
        procVo.getRemark();
        procVo.getAccountabilityDeptName();
        procVo.getAssignName();
        procVo.getDeviceType();
        procVo.getDeviceTypeName();
        procVo.getInspectionStartTime();
        procVo.getInspectionEndTime();
        procVo.getProgressSpeed();

        ProcInspectionRecordVo recordVo = new ProcInspectionRecordVo();
        recordVo.getProcInspectionRecordId();
        recordVo.getInspectionTaskName();
        recordVo.getInspectionTaskId();
        recordVo.getDeviceId();
        recordVo.getDeviceName();
        recordVo.getDeviceType();
        recordVo.getDeviceAreaId();
        recordVo.getDeviceAreaName();
        recordVo.getInspectionTime();
        recordVo.getDescription();
        recordVo.getUpdateUser();
        recordVo.getUpdateUserName();
        recordVo.getExceptionDescription();
        recordVo.getResourceMatching();
        recordVo.getIsDeleted();
        this.getProcInspectionVoAttr();
    }

    /**
     * ???????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/7/31 22:02
     */
    public void getProcInspectionVoAttr() {
        ProcInspectionVo procInspectionVo = new ProcInspectionVo();
        procInspectionVo.getDeviceTypeName();
        procInspectionVo.getInspectionStartDate();
        procInspectionVo.getInspectionStartTime();
        procInspectionVo.getInspectionEndDate();
        procInspectionVo.getInspectionEndTime();
        procInspectionVo.getInspectionDeviceCount();
        procInspectionVo.getProgressSpeed();
        procInspectionVo.getcTime();
        procInspectionVo.getuTime();
        procInspectionVo.getEcTime();
        procInspectionVo.getRcTime();
        procInspectionVo.getAssignName();
        procInspectionVo.getLastDays();
        procInspectionVo.getConcatSingleBackReason();


        procInspectionVo.differentDaysByMillisecond(new Date(), new Date());
    }

    /**
     * ??????????????????
     */
    @Test
    public void updateProcInspectionInfo() {
        ProcInspection procInspection = new ProcInspection();
        procInspectionService.updateProcInspectionInfo(procInspection);
    }

    /**
     * ????????????????????????
     */
    @Test
    public void selectProcInspectionOne() {
        ProcInspection procInspection = new ProcInspection();
        procInspectionService.selectProcInspectionOne(procInspection);

    }

    /**
     * ????????????????????????????????????
     */
    @Test
    public void selectInspectionProcByProcId() {
        String procId = "1";
        procInspectionService.selectInspectionProcByProcId(procId);
    }

    /**
     * ????????????????????????
     */
    @Test
    public void selectInspectionProcByProcIds() {
        Set<String> procIdSet = new HashSet<String>();
        procInspectionService.selectInspectionProcByProcIds(procIdSet);
    }


    /**
     * ???????????????????????????
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
     * ????????????????????????????????????
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
     * ????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/7/25 18:44
     */
    @Test
    public void getInspectionVoInfo() {
        List<ProcBaseResp> procBaseRespList = new ArrayList<>();
        procInspectionService.getInspectionVoInfo(procBaseRespList);
    }

    /**
     * ??????????????????????????????????????????
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
     * ??????????????????
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
     * ????????????????????????
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
     * ????????????????????????
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

    /**
     * ??????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/7/26 10:00
     */
    @Test
    public void updateInspectionProc() {
        new Expectations() {
            {
                procBaseService.queryProcBaseById(anyString);
                result = null;
            }
        };
        ProcInspectionReq req = new ProcInspectionReq();
        String operate = ProcInspectionConstants.OPERATE_UPDATE;
        try {
            procInspectionService.updateInspectionProc(req, operate);
        } catch (Exception e) {

        }


        this.expectationsSearchProc();
        new Expectations(ProcInspectionReq.class) {
            {
                ProcInspectionReq.validateProcInspectionReq((ProcInspectionReq) any, anyString);
                result = true;
            }
        };

        try {
            procInspectionService.updateInspectionProc(req, operate);
        } catch (Exception e) {

        }

        this.expectationsSearchProc();
        new Expectations(ProcInspectionReq.class) {
            {
                ProcInspectionReq.validateProcInspectionReq((ProcInspectionReq) any, anyString);
                result = false;
            }
        };

        try {
            procInspectionService.updateInspectionProc(req, operate);
        } catch (Exception e) {

        }

    }


    /**
     * ??????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/7/26 10:24
     */
    @Test
    public void saveProcInspection() {
        ProcInspectionReq req = new ProcInspectionReq();
        String operate = ProcInspectionConstants.OPERATE_UPDATE;
        String isAlarmViewCall = "";
        procInspectionService.saveProcInspection(req, operate, isAlarmViewCall);

        req.setProcId("1");
        new MockUp<ProcInspectionServiceImpl>(){
            @Mock
            public Result updateInspectionProc(ProcInspectionReq req, String operate) {
                return ResultUtils.success();
            }
        };
        procInspectionService.saveProcInspection(req, operate, isAlarmViewCall);


        operate = ProcInspectionConstants.OPERATE_ADD;
        new MockUp<ProcInspectionServiceImpl>(){
            @Mock
            public Result addInspectionProc(ProcInspectionReq req, String operate, String isAlarmViewCall) {
                return ResultUtils.success();
            }
        };
        procInspectionService.saveProcInspection(req, operate, isAlarmViewCall);
    }

    /**
     * ????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/7/26 10:45
     */
    @Test
    public void logicDeleteProcInspection() {
        String isDeleted = ProcBaseConstants.IS_DELETED_1;
        List<String> procIds = new ArrayList<>();
        procIds.add("1");
        //??????????????????
        procInspectionService.logicDeleteProcInspection(isDeleted, procIds);
    }

    /**
     * ??????????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/7/26 10:48
     */
    @Test
    public void saveProcInspectionSpecific() {
        ProcInspection procInspection = new ProcInspection();
        new Expectations() {
            {
                procInspectionDao.selectInspectionProcByProcId(anyString);
                result = null;

                procInspectionDao.insert((ProcInspection) any);
            }
        };
        procInspectionService.saveProcInspectionSpecific(procInspection);

        new Expectations() {
            {
                procInspectionDao.selectInspectionProcByProcId(anyString);
                ProcInspection procInfo = new ProcInspection();
                procInfo.setProcId("1");
                result = procInfo;

                procInspectionDao.updateById((ProcInspection) any);
            }
        };
        procInspectionService.saveProcInspectionSpecific(procInspection);
    }

    /**
     * ????????????????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/7/26 10:54
     */
    @Test
    public void queryListInspectionCompleteRecordByPage() {
        QueryCondition<ProcBaseReq> queryCondition = new QueryCondition<>();
        procInspectionService.queryListInspectionCompleteRecordByPage(queryCondition);

        PageCondition pageCondition = new PageCondition();
        pageCondition.setPageNum(1);
        pageCondition.setPageSize(20);
        queryCondition.setPageCondition(pageCondition);
        queryCondition.setSortCondition(new SortCondition());
        queryCondition.setFilterConditions(new ArrayList<>());
        queryCondition.setBizCondition(new ProcBaseReq());


        new MockUp<ProcInspectionServiceImpl>(){
            @Mock
            public QueryCondition<ProcBaseReq> setProcTypeToInspection(QueryCondition<ProcBaseReq> queryCondition){
                return new QueryCondition<ProcBaseReq>();
            }
        };

        new Expectations() {
            {
                procBaseService.queryListProcHisProcByPage((QueryCondition<ProcBaseReq>) any);
                result = ResultUtils.success();
            }
        };

        new MockUp<ProcInspectionServiceImpl>(){
            @Mock
            public Result getInspectionVo(Result result) {
                return ResultUtils.success();
            }
        };

        procInspectionService.queryListInspectionCompleteRecordByPage(queryCondition);
    }


    /**
     * ????????????????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/7/26 11:04
     */
    @Test
    public void queryListInspectionTaskRelationProcByPage() {
        QueryCondition<ProcBaseReq> queryCondition = new QueryCondition<>();
        procInspectionService.queryListInspectionTaskRelationProcByPage(queryCondition);

        PageCondition pageCondition = new PageCondition();
        pageCondition.setPageNum(1);
        pageCondition.setPageSize(20);
        queryCondition.setPageCondition(pageCondition);
        queryCondition.setSortCondition(new SortCondition());
        queryCondition.setFilterConditions(new ArrayList<>());
        queryCondition.setBizCondition(new ProcBaseReq());

        new MockUp<ProcInspectionServiceImpl>(){
            @Mock
            public QueryCondition<ProcBaseReq> setProcTypeToInspection(QueryCondition<ProcBaseReq> queryCondition){
                return new QueryCondition<ProcBaseReq>();
            }
        };

        new Expectations() {
            {
                procBaseService.queryListRelatedProcByInspectionTaskIdPage((QueryCondition<ProcBaseReq>) any);
                result = ResultUtils.success();
            }
        };

        new MockUp<ProcInspectionServiceImpl>(){
            @Mock
            public Result getInspectionVo(Result result) {
                return ResultUtils.success();
            }
        };

        procInspectionService.queryListInspectionTaskRelationProcByPage(queryCondition);
    }

    /**
     * ????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/7/26 11:11
     */
    @Test
    public void queryProcInspectionInfo() {
        QueryCondition queryCondition = new QueryCondition();
        procInspectionService.queryProcInspectionInfo(queryCondition);
    }

    /**
     * ????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/7/26 11:11
     */
    @Test
    public void queryProcInspectionInfoCount() {
        QueryCondition queryCondition = new QueryCondition();
        procInspectionService.queryProcInspectionInfoCount(queryCondition);
    }


    /**
     * ????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/7/26 11:11
     */
    @Test
    public void getInspectionProcById() {
        String procId = "1";
        new Expectations() {
            {
                procBaseService.queryProcessById(procId);
                Result resultInfo = new Result();
                ProcessDetail processDetail = new ProcessDetail();
                processDetail.setAlarmName("alarmName");
                Integer code = 0;
                resultInfo.setCode(code);
                resultInfo.setData(processDetail);
                result = resultInfo;
            }
        };
        QueryCondition queryCondition = new QueryCondition();
        procInspectionService.getInspectionProcById(procId);
    }

    /**
     * ????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/7/26 13:15
     */
    @Test
    public void completeInspectionProc() {
        new MockUp<ProcInspectionServiceImpl>(){
            @Mock
            public Result checkCompleteInspectionProc(CompleteInspectionProcReq completeReq) {
                return ResultUtils.success();
            }
        };

        CompleteInspectionProcReq completeReq = new CompleteInspectionProcReq();
        procInspectionService.completeInspectionProc(completeReq);




        List<ProcInspectionRecordReq> recordReqList = new ArrayList<>();
        ProcInspectionRecordReq procInspectionRecord = new ProcInspectionRecordReq();
        procInspectionRecord.setDeviceId("1");
        procInspectionRecord.setProcId("1");
        procInspectionRecord.setResult(ProcInspectionConstants.INSPECTION_RESULT_SUCESS);
        recordReqList.add(procInspectionRecord);
        completeReq.setProcInspectionRecords(recordReqList);
        completeReq.setOperate(WorkFlowBusinessConstants.COMPLETE_TYPE_COMMIT);

        new MockUp<ProcInspectionServiceImpl>(){
            @Mock
            public ProcInspection getProcInspectionByProcId(String procId) {
                ProcInspection procInspection = new ProcInspection();
                procInspection.setInspectionTaskId("1");
                return procInspection;
            }
        };

        new MockUp<ProcInspectionServiceImpl>(){
            @Mock
            public boolean checkInspectionTaskStatusComplete(ProcInspection procInspectionOne) {
                return true;
            }
        };


        new MockUp<ProcInspectionServiceImpl>(){
            @Mock
            public Result checkCompleteInspectionProc(CompleteInspectionProcReq completeReq) {
                return null;
            }
        };


        new MockUp<ProcInspectionServiceImpl>(){
            @Mock
            public void updateInspectionCompleteCount(ProcInspection procInspectionOne, List<ProcInspectionRecord> completeInspectionList) {
                return;
            }
        };

        new Expectations() {
            {
                procInspectionRecordService.updateInspectionRecordBatch((List<ProcInspectionRecord>) any);
                result = 1;
            }
        };

        new MockUp<ProcInspectionServiceImpl>(){
            @Mock
            public void updateProcBaseForCompleteInspection(CompleteInspectionProcReq completeReq, Date nowDate) {
                return;
            }
        };


        new MockUp<ProcInspectionServiceImpl>(){
            @Mock
            public void completeProcInspectionDeviceRecord(CompleteInspectionProcReq completeReq) {
                return;
            }
        };


        new MockUp<ProcInspectionServiceImpl>(){
            @Mock
            public boolean updateInspectionTaskStatus(ProcInspection procInspectionOne) {
                return true;
            }
        };




        new Expectations() {
            {
                procLogService.getAddProcOperateLogParamForApp((ProcBase) any, anyString, anyString);
                AddLogBean addLogBean = new AddLogBean();
                addLogBean.setOptObj("1");
                result = addLogBean;
            }
        };

        new Expectations() {
            {
                logProcess.addOperateLogInfoToCall((AddLogBean) any, anyString);
            }
        };


        procInspectionService.completeInspectionProc(completeReq);

        new MockUp<ProcInspectionServiceImpl>(){
            @Mock
            public ProcInspection getProcInspectionByProcId(String procId) {
                ProcInspection procInspection = new ProcInspection();
                procInspection.setInspectionTaskId("1");
                return procInspection;
            }
        };

        new MockUp<ProcInspectionServiceImpl>(){
            @Mock
            public boolean checkInspectionTaskStatusComplete(ProcInspection procInspectionOne) {
                return true;
            }
        };


        new MockUp<ProcInspectionServiceImpl>(){
            @Mock
            public Result checkCompleteInspectionProc(CompleteInspectionProcReq completeReq) {
                return null;
            }
        };


        new MockUp<ProcInspectionServiceImpl>(){
            @Mock
            public void updateInspectionCompleteCount(ProcInspection procInspectionOne, List<ProcInspectionRecord> completeInspectionList) {
                return;
            }
        };

        new Expectations() {
            {
                procInspectionRecordService.updateInspectionRecordBatch((List<ProcInspectionRecord>) any);
                result = -1;
            }
        };

        try {
            procInspectionService.completeInspectionProc(completeReq);
        } catch (Exception e ) {

        }


        new MockUp<ProcInspectionServiceImpl>(){
            @Mock
            public ProcInspection getProcInspectionByProcId(String procId) {
                ProcInspection procInspection = new ProcInspection();
                procInspection.setInspectionTaskId("1");
                return procInspection;
            }
        };

        new MockUp<ProcInspectionServiceImpl>(){
            @Mock
            public boolean checkInspectionTaskStatusComplete(ProcInspection procInspectionOne) {
                return true;
            }
        };


        new MockUp<ProcInspectionServiceImpl>(){
            @Mock
            public Result checkCompleteInspectionProc(CompleteInspectionProcReq completeReq) {
                return null;
            }
        };


        new MockUp<ProcInspectionServiceImpl>(){
            @Mock
            public void updateInspectionCompleteCount(ProcInspection procInspectionOne, List<ProcInspectionRecord> completeInspectionList) {
                return;
            }
        };

        new Expectations() {
            {
                procInspectionRecordService.updateInspectionRecordBatch((List<ProcInspectionRecord>) any);
                result = 1;
            }
        };

        new MockUp<ProcInspectionServiceImpl>(){
            @Mock
            public void updateProcBaseForCompleteInspection(CompleteInspectionProcReq completeReq, Date nowDate) {
                return;
            }
        };


        new MockUp<ProcInspectionServiceImpl>(){
            @Mock
            public void completeProcInspectionDeviceRecord(CompleteInspectionProcReq completeReq) {
                return;
            }
        };


        new MockUp<ProcInspectionServiceImpl>(){
            @Mock
            public boolean updateInspectionTaskStatus(ProcInspection procInspectionOne) {
                return false;
            }
        };

        try {
            procInspectionService.completeInspectionProc(completeReq);
        } catch (Exception e ) {

        }
    }

    /**
     * ?????????????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/7/26 14:10
     */
    @Test
    public void checkCompleteInspectionProc() {
        CompleteInspectionProcReq req = new CompleteInspectionProcReq();
        req.setOperate(WorkFlowBusinessConstants.COMPLETE_TYPE_COMMIT);
        new Expectations(CompleteInspectionProcReq.class) {
            {
                CompleteInspectionProcReq.checkCompleteInspectionProc((CompleteInspectionProcReq) any);
                result = ResultUtils.success();
            }
        };
        procInspectionService.checkCompleteInspectionProc(req);

        new Expectations(CompleteInspectionProcReq.class) {
            {
                CompleteInspectionProcReq.checkCompleteInspectionProc((CompleteInspectionProcReq) any);
                result = null;
            }
        };

        new Expectations() {
            {
                procBaseService.checkProcId(anyString);
                result = ResultUtils.success();
            }
        };
        procInspectionService.checkCompleteInspectionProc(req);


        new Expectations(CompleteInspectionProcReq.class) {
            {
                CompleteInspectionProcReq.checkCompleteInspectionProc((CompleteInspectionProcReq) any);
                result = null;
            }
        };

        new Expectations() {
            {
                procBaseService.checkProcId(anyString);
                result = ResultUtils.success();
            }
        };

        new Expectations() {
            {
                procBaseService.getProcBaseByProcId(anyString);
                result = null;
            }
        };
        try {
            procInspectionService.checkCompleteInspectionProc(req);
        } catch (Exception e) {

        }


        new Expectations(CompleteInspectionProcReq.class) {
            {
                CompleteInspectionProcReq.checkCompleteInspectionProc((CompleteInspectionProcReq) any);
                result = null;
            }
        };

        new Expectations() {
            {
                procBaseService.checkProcId(anyString);
                result = null;
            }
        };

        new Expectations() {
            {
                procBaseService.getProcBaseByProcId(anyString);
                ProcBase procBase = new ProcBase();
                procBase.setStatus(ProcBaseConstants.PROC_STATUS_PROCESSING);
                result = procBase;
            }
        };

        new MockUp<ProcInspectionServiceImpl>(){
            @Mock
            public Result isInspectionDeviceAll(CompleteInspectionProcReq req) {
                return ResultUtils.success();
            }
        };


        try {
            procInspectionService.checkCompleteInspectionProc(req);
        } catch (Exception e) {

        }

        req.setOperate(WorkFlowBusinessConstants.COMPLETE_TYPE_SAVED);
        new Expectations(CompleteInspectionProcReq.class) {
            {
                CompleteInspectionProcReq.checkCompleteInspectionProc((CompleteInspectionProcReq) any);
                result = null;
            }
        };

        new Expectations() {
            {
                procBaseService.checkProcId(anyString);
                result = null;
            }
        };

        new Expectations() {
            {
                procBaseService.getProcBaseByProcId(anyString);
                result = null;
            }
        };
        try {
            procInspectionService.checkCompleteInspectionProc(req);
        } catch (Exception e) {

        }

        new Expectations(CompleteInspectionProcReq.class) {
            {
                CompleteInspectionProcReq.checkCompleteInspectionProc((CompleteInspectionProcReq) any);
                result = null;
            }
        };

        new Expectations() {
            {
                procBaseService.checkProcId(anyString);
                result = null;
            }
        };

        new Expectations() {
            {
                procBaseService.getProcBaseByProcId(anyString);
                ProcBase procBase = new ProcBase();
                procBase.setStatus(ProcBaseConstants.PROC_STATUS_ASSIGNED);
                result = procBase;
            }
        };
        try {
            procInspectionService.checkCompleteInspectionProc(req);
        } catch (Exception e) {

        }

        new Expectations(CompleteInspectionProcReq.class) {
            {
                CompleteInspectionProcReq.checkCompleteInspectionProc((CompleteInspectionProcReq) any);
                result = null;
            }
        };

        new Expectations() {
            {
                procBaseService.checkProcId(anyString);
                result = null;
            }
        };

        new Expectations() {
            {
                procBaseService.getProcBaseByProcId(anyString);
                ProcBase procBase = new ProcBase();
                procBase.setStatus(ProcBaseConstants.PROC_STATUS_PROCESSING);
                result = procBase;
            }
        };
        try {
            procInspectionService.checkCompleteInspectionProc(req);
        } catch (Exception e) {

        }

    }

    /**
     * app????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/7/26 14:36
     */
    @Test
    public void updateProcBaseForCompleteInspection() {
        CompleteInspectionProcReq completeReq = new CompleteInspectionProcReq();
        completeReq.setOperate(WorkFlowBusinessConstants.COMPLETE_TYPE_COMMIT);
        completeReq.setProcId("1");
        completeReq.setUserId("1");
        Date nowDate = new Date();

        new MockUp<ProcInspectionServiceImpl>(){
            @Mock
            public int updateProcInspectionInfo(ProcInspection procInspection) {
                return -1;
            }
        };

        try {
            procInspectionService.updateProcBaseForCompleteInspection(completeReq, nowDate);
        } catch (Exception e) {

        }
    }


    /**
     * ????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/7/26 14:43
     */
    @Test
    public void updateInspectionCompleteCount() {
        ProcInspection procInspectionOne = new ProcInspection();
        procInspectionOne.setInspectionCompletedCount(2);
        procInspectionOne.setProcId("1");

        List<ProcInspectionRecord> completeInspectionList = new ArrayList<>();

        new MockUp<ProcInspectionServiceImpl>(){
            @Mock
            public int getCompleteInspectionCount(ProcInspection procInspectionOne, List<ProcInspectionRecord> completeInspectionList) {
                return 1;
            }
        };
        procInspectionService.updateInspectionCompleteCount(procInspectionOne, completeInspectionList);
    }

    /**
     * ???????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/7/26 14:55
     */
    @Test
    public void getCompleteInspectionCount() {
        ProcInspection procInspectionOne = new ProcInspection();
        procInspectionOne.setProcId("1");

        new Expectations() {
            {
                procInspectionRecordService.queryIsInspectionDeviceList(procInspectionOne.getProcId());
                List<ProcInspectionRecord> completeInspectionListInfo = new ArrayList<>();
                ProcInspectionRecord inspectionRecordInfo = new ProcInspectionRecord();
                inspectionRecordInfo.setProcId("1");
                inspectionRecordInfo.setDeviceId("1");
                completeInspectionListInfo.add(inspectionRecordInfo);
                inspectionRecordInfo = new ProcInspectionRecord();
                inspectionRecordInfo.setProcId("1");
                inspectionRecordInfo.setDeviceId("4");
                completeInspectionListInfo.add(inspectionRecordInfo);
                inspectionRecordInfo = new ProcInspectionRecord();
                inspectionRecordInfo.setProcId("1");
                inspectionRecordInfo.setDeviceId("5");
                completeInspectionListInfo.add(inspectionRecordInfo);
                inspectionRecordInfo = new ProcInspectionRecord();
                inspectionRecordInfo.setProcId("1");
                inspectionRecordInfo.setDeviceId("2");
                completeInspectionListInfo.add(inspectionRecordInfo);
                result = completeInspectionListInfo;
            }
        };

        List<ProcInspectionRecord> completeInspectionList = new ArrayList<>();
        procInspectionService.getCompleteInspectionCount(procInspectionOne, completeInspectionList);


        ProcInspectionRecord inspectionRecord = new ProcInspectionRecord();
        inspectionRecord.setProcId("1");
        inspectionRecord.setDeviceId("3");
        completeInspectionList.add(inspectionRecord);
        inspectionRecord = new ProcInspectionRecord();
        inspectionRecord.setProcId("1");
        inspectionRecord.setDeviceId("2");
        completeInspectionList.add(inspectionRecord);
        procInspectionService.getCompleteInspectionCount(procInspectionOne, completeInspectionList);

        new Expectations() {
            {
                procInspectionRecordService.queryIsInspectionDeviceList(procInspectionOne.getProcId());
                result = null;
            }
        };
        procInspectionService.getCompleteInspectionCount(procInspectionOne, completeInspectionList);


    }

    /**
     * ??????????????????????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/7/26 15:20
     */
    @Test
    public void isInspectionDeviceAll() {
        CompleteInspectionProcReq req = new CompleteInspectionProcReq();
        new Expectations() {
            {
                procInspectionRecordService.queryNotInspectionDeviceList(anyString);
                result = null;
            }
        };
        procInspectionService.isInspectionDeviceAll(req);

        ProcInspectionRecord procInspectionRecord = new ProcInspectionRecord();
        List<ProcInspectionRecordReq> inspectionRecordList = new ArrayList<>();
        ProcInspectionRecordReq recordReq = new ProcInspectionRecordReq();
        recordReq.setProcId("1");
        recordReq.setDeviceId("1");
        inspectionRecordList.add(recordReq);
        recordReq.setProcId("1");
        recordReq.setDeviceId("2");
        inspectionRecordList.add(recordReq);
        req.setProcInspectionRecords(inspectionRecordList);

        new Expectations() {
            {
                procInspectionRecordService.queryNotInspectionDeviceList(anyString);
                List<ProcInspectionRecord> procInspectionRecordList = new ArrayList<>();
                ProcInspectionRecord record = new ProcInspectionRecord();
                record.setDeviceId("1");
                record.setProcId("1");
                procInspectionRecordList.add(record);
                record = new ProcInspectionRecord();
                record.setDeviceId("2");
                record.setProcId("1");
                procInspectionRecordList.add(record);
                record = new ProcInspectionRecord();
                record.setDeviceId("3");
                record.setProcId("1");
                procInspectionRecordList.add(record);
                result = procInspectionRecordList;
            }
        };
        procInspectionService.isInspectionDeviceAll(req);


        new Expectations() {
            {
                procInspectionRecordService.queryNotInspectionDeviceList(anyString);
                List<ProcInspectionRecord> procInspectionRecordList = new ArrayList<>();
                ProcInspectionRecord record = new ProcInspectionRecord();
                record.setDeviceId("1");
                record.setProcId("1");
                procInspectionRecordList.add(record);
                record = new ProcInspectionRecord();
                record.setDeviceId("2");
                record.setProcId("1");
                procInspectionRecordList.add(record);
                record = new ProcInspectionRecord();
                record.setDeviceId("3");
                record.setProcId("1");
                procInspectionRecordList.add(record);
                result = procInspectionRecordList;
            }
        };
        req.setProcInspectionRecords(new ArrayList<>());
        procInspectionService.isInspectionDeviceAll(req);

    }


    /**
     * ??????????????????????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/7/26 15:43
     */
    @Test
    public void getProcInspectionByProcId() {
        String procId = "1";
        procInspectionService.getProcInspectionByProcId(procId);
    }
    
    /**
     * ??????????????????????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/7/26 15:45
     */
    @Test
    public void selectInspectionProcForProcIds() {
        ProcBaseReq req = new ProcBaseReq();
        procInspectionService.selectInspectionProcForProcIds(req);
    }

    /**
     * ?????????????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/7/26 15:47
     */
    @Test
    public void checkInspectionTaskStatusComplete() {
        ProcInspection procInspectionOne = new ProcInspection();
        procInspectionOne.setProcId("1");
        new MockUp<ProcInspectionServiceImpl>(){
            @Mock
            public List<ProcInspection> selectNotInspectionProcInspection(ProcInspection procInspection) {
                return null;
            }
        };
        procInspectionService.checkInspectionTaskStatusComplete(procInspectionOne);


        new MockUp<ProcInspectionServiceImpl>(){
            @Mock
            public List<ProcInspection> selectNotInspectionProcInspection(ProcInspection procInspection) {
                List<ProcInspection> procInspectionList = new ArrayList<>();
                ProcInspection procInspectionInfo = new ProcInspection();
                procInspectionInfo.setProcId("1");
                procInspectionList.add(procInspectionInfo);
                procInspectionInfo = new ProcInspection();
                procInspectionInfo.setProcId("2");
                procInspectionList.add(procInspectionInfo);
                return procInspectionList;
            }
        };
        procInspectionService.checkInspectionTaskStatusComplete(procInspectionOne);

        new MockUp<ProcInspectionServiceImpl>(){
            @Mock
            public List<ProcInspection> selectNotInspectionProcInspection(ProcInspection procInspection) {
                List<ProcInspection> procInspectionList = new ArrayList<>();
                ProcInspection procInspectionInfo = new ProcInspection();
                procInspectionInfo.setProcId("2");
                procInspectionList.add(procInspectionInfo);
                return procInspectionList;
            }
        };
        procInspectionService.checkInspectionTaskStatusComplete(procInspectionOne);

        new MockUp<ProcInspectionServiceImpl>(){
            @Mock
            public List<ProcInspection> selectNotInspectionProcInspection(ProcInspection procInspection) {
                List<ProcInspection> procInspectionList = new ArrayList<>();
                ProcInspection procInspectionInfo = new ProcInspection();
                procInspectionInfo.setProcId("1");
                procInspectionList.add(procInspectionInfo);
                return procInspectionList;
            }
        };
        procInspectionService.checkInspectionTaskStatusComplete(procInspectionOne);
    }

    /**
     * ?????????????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/7/26 16:05
     */
    @Test
    public void updateInspectionTaskStatus() {
        ProcInspection procInspectionOne = new ProcInspection();
        new Expectations() {
            {
                procBaseService.updateInspectionTaskStatus((ProcessInfo) any, anyString);
                result = false;
            }
        };
        procInspectionService.updateInspectionTaskStatus(procInspectionOne);

        new Expectations() {
            {
                procBaseService.updateInspectionTaskStatus((ProcessInfo) any, anyString);
                result = true;
            }
        };
        procInspectionService.updateInspectionTaskStatus(procInspectionOne);
    }


    @Test
    public void completeProcInspectionDeviceRecord() {
        CompleteInspectionProcReq completeReq = new CompleteInspectionProcReq();
        completeReq.setOperate(WorkFlowBusinessConstants.COMPLETE_TYPE_COMMIT);
        completeReq.setProcId("1");
        completeReq.setUserId("1");

        new MockUp<ProcInspectionServiceImpl>(){
            @Mock
            public int updateProcInspectionInfo(ProcInspection procInspection) {
                return 0;
            }
        };


        new MockUp<ProcInspectionServiceImpl>(){
            @Mock
            public Result commitProcInspection(CompleteInspectionProcReq completeReq) {
                return  ResultUtils.success();
            }
        };
        procInspectionService.completeProcInspectionDeviceRecord(completeReq);

        new MockUp<ProcInspectionServiceImpl>(){
            @Mock
            public int updateProcInspectionInfo(ProcInspection procInspection) {
                return 0;
            }
        };


        new MockUp<ProcInspectionServiceImpl>(){
            @Mock
            public Result commitProcInspection(CompleteInspectionProcReq completeReq) {
                return null;
            }
        };
        try {
            procInspectionService.completeProcInspectionDeviceRecord(completeReq);
        } catch (Exception e) {

        }
    }


    /**
     * ??????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/7/26 16:29
     */
    @Test
    public void commitProcInspection() {
        CompleteInspectionProcReq completeReq = new CompleteInspectionProcReq();

        new Expectations() {
            {
                workflowService.completeProcess((CompleteProcessInfoReq) any);
                result = ResultUtils.success();
            }
        };
        procInspectionService.commitProcInspection(completeReq);
    }

    /**
     * ?????????????????????????????????processInfo
     * @author hedongwei@wistronits.com
     * @date  2019/7/26 16:36
     */
    @Test
    public void saveInspectionProcParamToProcessInfo() {
        ProcInspectionReq req = new ProcInspectionReq();
        Date nowDate = new Date();
        String operate = ProcInspectionConstants.OPERATE_ADD;
        procInspectionService.saveInspectionProcParamToProcessInfo(req, nowDate, operate);
    }

    /**
     * ????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/7/26 16:39
     */
    @Test
    public void addProcInspectionLog() {
        ProcInspectionReq req = new ProcInspectionReq();
        List<ProcBase> procBaseList = new ArrayList<>();
        String isAlarmViewCall = ProcInspectionConstants.IS_ALARM_VIEW_CALL;


        new MockUp<ProcInspectionServiceImpl>(){
            @Mock
            public void addInspectionProcLogBatch(List<ProcBase> procBaseList, String logType, String dataOptType, String functionCode) {
                return;
            }
        };
        procInspectionService.addProcInspectionLog(req, procBaseList, isAlarmViewCall);

        isAlarmViewCall = "";
        req.setProcResourceType(ProcBaseConstants.PROC_RESOURCE_TYPE_1);
        new MockUp<ProcInspectionServiceImpl>(){
            @Mock
            public void addInspectionProcLogBatch(List<ProcBase> procBaseList, String logType, String dataOptType, String functionCode) {
                return;
            }
        };
        procInspectionService.addProcInspectionLog(req, procBaseList, isAlarmViewCall);


        req.setProcResourceType(ProcBaseConstants.PROC_RESOURCE_TYPE_2);
        new MockUp<ProcInspectionServiceImpl>(){
            @Mock
            public void addInspectionProcLogBatch(List<ProcBase> procBaseList, String logType, String dataOptType, String functionCode) {
                return;
            }
        };
        procInspectionService.addProcInspectionLog(req, procBaseList, isAlarmViewCall);

    }


    /**
     * ??????????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/7/26 16:39
     */
    @Test
    public void regenerateProcInspectionLog() {
        ProcInspectionReq req = new ProcInspectionReq();
        List<ProcBase> procBaseList = new ArrayList<>();
        String isAlarmViewCall = ProcInspectionConstants.IS_ALARM_VIEW_CALL;


        new MockUp<ProcInspectionServiceImpl>(){
            @Mock
            public void addInspectionProcLogBatch(List<ProcBase> procBaseList, String logType, String dataOptType, String functionCode) {
                return;
            }
        };
        procInspectionService.regenerateProcInspectionLog(req, procBaseList, isAlarmViewCall);

        isAlarmViewCall = "";
        req.setProcResourceType(ProcBaseConstants.PROC_RESOURCE_TYPE_1);
        new MockUp<ProcInspectionServiceImpl>(){
            @Mock
            public void addInspectionProcLogBatch(List<ProcBase> procBaseList, String logType, String dataOptType, String functionCode) {
                return;
            }
        };
        procInspectionService.regenerateProcInspectionLog(req, procBaseList, isAlarmViewCall);


        req.setProcResourceType(ProcBaseConstants.PROC_RESOURCE_TYPE_2);
        new MockUp<ProcInspectionServiceImpl>(){
            @Mock
            public void addInspectionProcLogBatch(List<ProcBase> procBaseList, String logType, String dataOptType, String functionCode) {
                return;
            }
        };
        procInspectionService.regenerateProcInspectionLog(req, procBaseList, isAlarmViewCall);

    }

    /**
     * ????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/7/26 16:39
     */
    @Test
    public void updateProcInspectionLog() {
        ProcInspectionReq req = new ProcInspectionReq();
        List<ProcBase> procBaseList = new ArrayList<>();
        String isAlarmViewCall = ProcInspectionConstants.IS_ALARM_VIEW_CALL;


        new MockUp<ProcInspectionServiceImpl>(){
            @Mock
            public void addInspectionProcLogBatch(List<ProcBase> procBaseList, String logType, String dataOptType, String functionCode) {
                return;
            }
        };
        procInspectionService.updateProcInspectionLog(req, procBaseList);
    }


    /**
     * ??????????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/7/27 20:38
     */
    @Test
    public void addInspectionProcLogBatch() {
        new Expectations() {
            {
                systemLanguage.querySystemLanguage();
            }
        };

        List<ProcBase> procBaseList = new ArrayList<>();
        ProcBase procBase = new ProcBase();
        procBase.setProcId("1");
        procBase.setTitle("title");
        procBaseList.add(procBase);

        String logType = LogConstants.LOG_TYPE_SYSTEM;
        String dataOptType = LogConstants.DATA_OPT_TYPE_UPDATE;
        String functionCode = "222";
        procInspectionService.addInspectionProcLogBatch(procBaseList, logType, dataOptType, functionCode);

    }

    /**
     * ??????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/7/27 21:11
     */
    @Test
    public void getProcInspectionRecordList() {
        ProcInspectionReq req = new ProcInspectionReq();
        req.setProcResourceType(ProcBaseConstants.PROC_RESOURCE_TYPE_2);
        req.setInspectionTaskId("1");
        req.setInspectionTaskName("name");
        List<ProcRelatedDevice> deviceList = new ArrayList<>();
        ProcRelatedDevice procRelatedDevice = new ProcRelatedDevice();
        procRelatedDevice.setProcId("1");
        procRelatedDevice.setDeviceId("1");
        deviceList.add(procRelatedDevice);
        procInspectionService.getProcInspectionRecordList(req, deviceList);
    }

    /**
     * ???????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/7/27 21:12
     */
    @Test
    public void getInsertDepartmentList() {
        ProcInspectionReq req = new ProcInspectionReq();
        Date nowDate = new Date();
        String userId = "1";

        List<ProcRelatedDepartment> departmentList = new ArrayList<>();
        ProcRelatedDepartment departmentOne = new ProcRelatedDepartment();
        departmentOne.setProcId("1");
        departmentList.add(departmentOne);
        req.setDeptList(departmentList);
        procInspectionService.getInsertDepartmentList(req , nowDate, userId);
    }

    /**
     * ??????????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/7/27 21:26
     */
    @Test
    public void getInspectionDeviceList() {
        List<ProcRelatedDevice> procRelatedDeviceList = new ArrayList<>();
        ProcRelatedDevice procRelatedDevice = new ProcRelatedDevice();
        procRelatedDevice.setDeviceId("1");
        procRelatedDevice.setProcId("1");
        procRelatedDeviceList.add(procRelatedDevice);
        ProcInspectionReq req = new ProcInspectionReq();
        req.setDeviceList(procRelatedDeviceList);
        Date nowDate = new Date();
        String userId = "1";
        procInspectionService.getInspectionDeviceList(req, nowDate, userId);
    }


    /**
     * ????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/7/27 21:28
     */
    @Test
    public void getInspectionEndTime() {
        ProcInspectionReq req = new ProcInspectionReq();
        req.setProcResourceType(ProcBaseConstants.PROC_RESOURCE_TYPE_2);
        req.setInspectionEndDate(new Date().getTime());
        Date nowDate = new Date();
        procInspectionService.getInspectionEndTime(req, nowDate);

        req.setProcResourceType(ProcBaseConstants.PROC_RESOURCE_TYPE_3);
        req.setAlarmCompleteTime("2");
        procInspectionService.getInspectionEndTime(req, nowDate);

        req.setProcResourceType("");
        procInspectionService.getInspectionEndTime(req, nowDate);
    }

    /**
     * ??????????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/7/27 21:44
     */
    @Test
    public void getUpdateProcBase() {
        ProcInspectionReq req = new ProcInspectionReq();
        req.setInspectionEndDate(new Date().getTime());
        Date nowDate = new Date();
        procInspectionService.getUpdateProcBase(req, nowDate);
    }

    /**
     * ?????????????????????????????????
     */
    @Test
    public void  setProcTypeToInspection() {
        QueryCondition<ProcBaseReq> queryCondition = new QueryCondition<>();
        procInspectionService.setProcTypeToInspection(queryCondition);
    }


    /**
     * app??????????????????
     */
    @Test
    public void downLoadInspectionProcForApp() {
        List<ProcBaseRespForApp> procBaseRespForApps = new ArrayList<>();
        ProcBaseRespForApp procBaseRespForApp = new ProcBaseRespForApp();
        procBaseRespForApp.setProcId("1");
        procBaseRespForApp.setTitle("1");
        procBaseRespForApp.setCreateTime(new Date());
        procBaseRespForApp.setExpectedCompletedTime(new Date());


        ProcInspectionRecord record = new ProcInspectionRecord();
        record.setProcId("1");
        record.setDeviceId("1");
        List<ProcInspectionRecord> procNotInspectionDeviceList = new ArrayList<>();
        procNotInspectionDeviceList.add(record);
        procBaseRespForApp.setProcNotInspectionDeviceList(procNotInspectionDeviceList);

        List<ProcRelatedDeviceResp> procRelatedDeviceRespList = new ArrayList<>();
        ProcRelatedDeviceResp resp = new ProcRelatedDeviceResp();
        resp.setProcId("1");
        resp.setDeviceId("1");
        procRelatedDeviceRespList.add(resp);
        ProcRelatedDeviceResp respInfo = new ProcRelatedDeviceResp();
        respInfo.setProcId("1");
        respInfo.setDeviceId("2");
        procRelatedDeviceRespList.add(respInfo);
        procBaseRespForApp.setProcRelatedDeviceRespList(procRelatedDeviceRespList);

        procBaseRespForApps.add(procBaseRespForApp);
        procInspectionService.downLoadInspectionProcForApp(procBaseRespForApps);
    }

    /**
     * ????????????????????????????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/4/26 9:00
     */
    @Test
    public void manualAddInspectionProc() {
        String inspectionTaskId = "1";
        new Expectations() {
            {
                inspectionTaskService.getInspectionTaskOne((InspectionTask) any);
                InspectionTask inspectionTaskOne = new InspectionTask();
                Long time = new Date().getTime();
                Long diffTime = time - 1;
                inspectionTaskOne.setTaskEndTime(new Date(diffTime));
            }
        };

        new MockUp<ProcInspectionServiceImpl>(){
            @Mock
            public void jobAddInspectionProc(InspectionTask inspectionTask) {
                return;
            }
        };
        procInspectionService.manualAddInspectionProc(inspectionTaskId);
    }

    /**
     * ????????????????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/7/27 22:43
     */
    @Test
    public void jobAddInspectionProc() {
        InspectionTask inspectionTask = new InspectionTask();
        inspectionTask.setInspectionTaskId("1");
        new Expectations() {
            {
                inspectionTaskService.getInspectionDetailInfo(anyString);
                InspectionTaskDetailBean taskDetail = new InspectionTaskDetailBean();
                taskDetail.setTaskEndTime(new Date());
                taskDetail.setTaskStartTime(new Date());
                taskDetail.setTaskPeriod(1);
                taskDetail.setIsOpen(InspectionTaskConstants.IS_OPEN);
                result = taskDetail;
            }
        };

        new MockUp<ProcInspectionServiceImpl>(){
            @Mock
            public void inspectionTaskInsertProcInspection(Long taskStartTime ,Long taskEndTime, Long nowDateTime, InspectionTaskDetailBean taskDetail) {
                return;
            }
        };

        new MockUp<ProcInspectionServiceImpl>(){
            @Mock
            public void inspectionTaskStatusIsClose(Long taskEndTime, Long nowDateTime, String inspectionTaskId) {
                return;
            }
        };
        procInspectionService.jobAddInspectionProc(inspectionTask);
    }


    /**
     * ??????????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/7/27 23:03
     */
    @Test
    public void inspectionTaskInsertProcInspection() {
        Long taskStartTime = new Date().getTime();
        Long taskEndTime = new Date().getTime() + 1L;
        Long nowDateTime = new Date().getTime() + 2L;
        InspectionTaskDetailBean taskDetail = new InspectionTaskDetailBean();

        new MockUp<ProcInspectionServiceImpl>(){
            @Mock
            public Result addInspectionProcForJob(InspectionTaskDetailBean detailBean) {
                return null;
            }
        };

        procInspectionService.inspectionTaskInsertProcInspection(taskStartTime, taskEndTime, nowDateTime, taskDetail);



        taskEndTime = null;
        new MockUp<ProcInspectionServiceImpl>(){
            @Mock
            public Result addInspectionProcForJob(InspectionTaskDetailBean detailBean) {
                return null;
            }
        };
        procInspectionService.inspectionTaskInsertProcInspection(taskStartTime, taskEndTime, nowDateTime, taskDetail);
    }


    /**
     * ??????????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/7/27 23:15
     */
    @Test
    public void inspectionTaskStatusIsClose() {
        Long nowDateTime = new Date().getTime();
        Long taskEndTime =  nowDateTime - 1L;
        String inspectionTaskId = "1";

        new Expectations() {
            {
                inspectionTaskService.updateById((InspectionTask) any);
            }
        };
        procInspectionService.inspectionTaskStatusIsClose(taskEndTime, nowDateTime, inspectionTaskId);
    }

    /**
     * ??????????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/7/27 23:16
     */
    @Test
    public void addInspectionProcForJob() {
        InspectionTaskDetailBean detailBean = new InspectionTaskDetailBean();
        detailBean.setInspectionAreaId("1");

        new MockUp<ProcInspectionServiceImpl>(){
            @Mock
            public String getInspectionAreaName(String areaId) {
                return "2211111";
            }
        };

        new MockUp<ProcInspectionServiceImpl>(){
            @Mock
            public List<String> getDeviceIds(List<InspectionTaskDevice> deviceList) {
                List<String> deviceIds = new ArrayList<>();
                deviceIds.add("1");
                return deviceIds;
            }
        };


        new MockUp<ProcInspectionServiceImpl>(){
            @Mock
            public List<DeviceInfoDto> getDeviceInfoDtoList(List<String> deviceIds) {
                List<DeviceInfoDto> deviceInfoDtoList = new ArrayList<>();
                DeviceInfoDto dto = new DeviceInfoDto();
                dto.setDeviceId("1");
                deviceInfoDtoList.add(dto);
                return deviceInfoDtoList;
            }
        };

        new MockUp<ProcInspectionServiceImpl>(){
            @Mock
            public Result generateInspectionProc(AddInspectionTaskBean addInspectionTaskBean, String areaName) {
                return ResultUtils.success();
            }
        };

        procInspectionService.addInspectionProcForJob(detailBean);
    }


    /**
     * ??????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/7/27 23:43
     */
    @Test
    public void generateInspectionProc() {
        AddInspectionTaskBean addInspectionTaskBean = new AddInspectionTaskBean();

        InspectionTaskDetailBean inspectionTaskDetailBean = new InspectionTaskDetailBean();
        inspectionTaskDetailBean.setInspectionTaskId("1");
        inspectionTaskDetailBean.setInspectionTaskName("222");
        inspectionTaskDetailBean.setProcPlanDate(2);
        addInspectionTaskBean.setDetailBean(inspectionTaskDetailBean);

        List<InspectionTaskDepartment> deptList = new ArrayList<>();
        InspectionTaskDepartment deptOne = new InspectionTaskDepartment();
        deptOne.setAccountabilityDept("1");
        deptOne.setInspectionTaskId("1");
        deptList.add(deptOne);
        addInspectionTaskBean.setDeptList(deptList);

        List<InspectionTaskDevice> inspectionTaskDeviceList = new ArrayList<>();
        InspectionTaskDevice inspectionTaskDevice = new InspectionTaskDevice();
        inspectionTaskDevice.setDeviceId("1");
        inspectionTaskDevice.setInspectionTaskId("1");
        inspectionTaskDeviceList.add(inspectionTaskDevice);
        inspectionTaskDevice = new InspectionTaskDevice();
        inspectionTaskDevice.setDeviceId("2");
        inspectionTaskDevice.setInspectionTaskId("1");
        inspectionTaskDeviceList.add(inspectionTaskDevice);
        addInspectionTaskBean.setDeviceList(inspectionTaskDeviceList);


        Map<String, DeviceInfoDto> deviceInfoDtoMap = new HashMap<>();
        DeviceInfoDto deviceInfoDto = new DeviceInfoDto();
        deviceInfoDto.setDeviceId("1");
        deviceInfoDto.setDeviceName("name");
        deviceInfoDto.setDeviceType("001");
        deviceInfoDtoMap.put(deviceInfoDto.getDeviceId(), deviceInfoDto);
        deviceInfoDto = new DeviceInfoDto();
        deviceInfoDto.setDeviceId("2");
        deviceInfoDto.setDeviceName("name");
        deviceInfoDto.setDeviceType("001");
        deviceInfoDtoMap.put(deviceInfoDto.getDeviceId(), deviceInfoDto);
        addInspectionTaskBean.setDeviceInfoDtoMap(deviceInfoDtoMap);


        new MockUp<ProcInspectionServiceImpl>(){
            @Mock
            public void createProcInspectionBatchForJob(Map<String, List<ProcRelatedDevice>> deviceTypeMap, ProcInspectionReq inspectionReq) {
                return ;
            }
        };
        String areaName = "1";
        procInspectionService.generateInspectionProc(addInspectionTaskBean, areaName);
    }


    /**
     * ????????????????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/7/28 8:59
     */
    @Test
    public void createProcInspectionBatchForJob() {
        Map<String, List<ProcRelatedDevice>> deviceTypeMap = new HashMap<>();
        List<ProcRelatedDevice> procRelatedDeviceList = new ArrayList<>();
        ProcRelatedDevice procRelatedDevice = new ProcRelatedDevice();
        procRelatedDevice.setProcId("1");
        procRelatedDevice.setDeviceId("1");
        procRelatedDevice.setDeviceType("001");
        procRelatedDeviceList.add(procRelatedDevice);
        deviceTypeMap.put(procRelatedDevice.getProcId(), procRelatedDeviceList);


        ProcInspectionReq inspectionReq = new ProcInspectionReq();
        new MockUp<ProcInspectionServiceImpl>(){
            @Mock
            public Result saveProcInspection(ProcInspectionReq req, String operate, String isAlarmViewCall) {
                return ResultUtils.success() ;
            }
        };

        procInspectionService.createProcInspectionBatchForJob(deviceTypeMap, inspectionReq);
    }


    /**
     * ????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/7/28 9:08
     */
    @Test
    public void getInspectionAreaName() {
        String areaId = "1";
        new Expectations() {
            {
                areaFeign.selectAreaInfoByIds((List<String>) any);
                List<AreaInfoForeignDto> areaInfoList = new ArrayList<>();
                AreaInfoForeignDto areaInfoForeignDto = new AreaInfoForeignDto();
                areaInfoForeignDto.setAreaId("1");
                areaInfoForeignDto.setAreaName("areaName");
                areaInfoList.add(areaInfoForeignDto);
                result = areaInfoList;
            }
        };
        procInspectionService.getInspectionAreaName(areaId);
    }


    /**
     * ??????????????????????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/7/28 9:17
     */
    @Test
    public void getDeviceIds() {
        List<InspectionTaskDevice> deviceList = new ArrayList<>();
        InspectionTaskDevice inspectionTaskDevice = new InspectionTaskDevice();
        inspectionTaskDevice.setDeviceId("1");
        deviceList.add(inspectionTaskDevice);
        procInspectionService.getDeviceIds(deviceList);
    }


    /**
     * ??????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/7/28 9:18
     */
    @Test
    public void getDeviceInfoDtoList() {
        List<String> deviceIds = new ArrayList<>();
        deviceIds.add("1");
        new Expectations() {
            {
                deviceFeign.getDeviceByIds((String []) any);
                result = null;
            }
        };
        procInspectionService.getDeviceInfoDtoList(deviceIds);
    }

    /**
     * ????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/7/28 9:23
     */
    @Test
    public void getDeptIds() {
        List<InspectionTaskDepartment> deptList = new ArrayList<>();
        InspectionTaskDepartment department = new InspectionTaskDepartment();
        department.setInspectionTaskId("1");
        department.setAccountabilityDept("1");
        deptList.add(department);
        procInspectionService.getDeptIds(deptList);
    }


    /**
     * ????????????map
     * @author hedongwei@wistronits.com
     * @date  2019/7/28 9:25
     */
    @Test
    public void getDepartmentMap() {
        List<Department> departmentList = new ArrayList<>();
        Department department = new Department();
        department.setId("1");
        department.setDeptName("deptName");
        departmentList.add(department);
        procInspectionService.getDepartmentMap(departmentList);
    }


    /**
     * ?????????????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/7/28 9:25
     */
    @Test
    public void exportListProcInspectionProcess() {
        ExportDto exportDto = new ExportDto();
        new Expectations() {
            {
                inspectionProcProcessExport.insertTask(exportDto, anyString, anyString);
                result = new FilinkExportNoDataException();
            }
        };

        try  {
            procInspectionService.exportListInspectionProcess(exportDto);
        } catch (Exception e) {

        }



        new Expectations() {
            {
                inspectionProcProcessExport.insertTask(exportDto, anyString, anyString);
                result = new FilinkExportDataTooLargeException("1");
            }
        };


        try  {
            procInspectionService.exportListInspectionProcess(exportDto);
        } catch (Exception e) {

        }

        new Expectations() {
            {
                inspectionProcProcessExport.insertTask(exportDto, anyString, anyString);
                result = new FilinkExportTaskNumTooBigException();
            }
        };

        try  {
            procInspectionService.exportListInspectionProcess(exportDto);
        } catch (Exception e) {

        }


        new Expectations() {
            {
                inspectionProcProcessExport.insertTask(exportDto, anyString, anyString);
                result = new Exception();
            }
        };

        try  {
            procInspectionService.exportListInspectionProcess(exportDto);
        } catch (Exception e) {

        }

        new Expectations() {
            {
                inspectionProcProcessExport.insertTask(exportDto, anyString, anyString);
                result = null;
            }
        };

        new Expectations() {
            {
                inspectionProcProcessExport.exportData((ExportRequestInfo) any);
            }
        };


        new Expectations() {
            {
                procLogService.addLogByExport((ExportDto) any, anyString);
            }
        };

        try  {
            procInspectionService.exportListInspectionProcess(exportDto);
        } catch (Exception e) {

        }
    }

    /**
     * ???????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/7/28 9:37
     */
    @Test
    public void queryProcInspectionTopFive() {
        String deviceId = "1";
        new MockUp<ProcInspectionServiceImpl>(){
            @Mock
            public QueryCondition<ProcBaseReq> setProcTypeToInspection(QueryCondition<ProcBaseReq> queryCondition){
                List<DeviceInfoDto> deviceInfoDtoList = new ArrayList<>();
                return null;
            }
        };

        new Expectations() {
            {
                procBaseService.queryListProcUnfinishedProcByPage((QueryCondition<ProcBaseReq>) any);
                Result resultInfo = new Result();
                List<ProcBaseResp> procBaseRespList = new ArrayList<>();
                ProcBaseResp procBaseResp = new ProcBaseResp();
                procBaseResp.setProcId("1");
                procBaseRespList.add(procBaseResp);
                resultInfo.setData(procBaseRespList);
                result = resultInfo;
            }
        };

        new Expectations() {
            {
                procInspectionRecordService.queryInspectionRecordByProcIds((List<String>) any) ;
                List<ProcInspectionRecordVo> recordVoList = new ArrayList<>();
                ProcInspectionRecordVo procInspectionRecordVo = new ProcInspectionRecordVo();
                procInspectionRecordVo.setDeviceId("1");
                procInspectionRecordVo.setDeviceName("deviceName");
                procInspectionRecordVo.setInspectionTaskId("1");
                procInspectionRecordVo.setProcId("1");
                recordVoList.add(procInspectionRecordVo);
                procInspectionRecordVo = new ProcInspectionRecordVo();
                procInspectionRecordVo.setDeviceId("2");
                procInspectionRecordVo.setDeviceName("deviceName");
                procInspectionRecordVo.setInspectionTaskId("1");
                procInspectionRecordVo.setProcId("1");
                recordVoList.add(procInspectionRecordVo);
                result = recordVoList;
            }
        };
        procInspectionService.queryProcInspectionTopFive(deviceId);

    }


    /**
     * ??????????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/7/28 9:25
     */
    @Test
    public void exportListInspectionComplete() {
        ExportDto exportDto = new ExportDto();
        new Expectations() {
            {
                inspectionProcCompleteExport.insertTask(exportDto, anyString, anyString);
                result = new FilinkExportNoDataException();
            }
        };

        try  {
            procInspectionService.exportListInspectionComplete(exportDto);
        } catch (Exception e) {

        }



        new Expectations() {
            {
                inspectionProcCompleteExport.insertTask(exportDto, anyString, anyString);
                result = new FilinkExportDataTooLargeException("1");
            }
        };


        try  {
            procInspectionService.exportListInspectionComplete(exportDto);
        } catch (Exception e) {

        }

        new Expectations() {
            {
                inspectionProcCompleteExport.insertTask(exportDto, anyString, anyString);
                result = new FilinkExportTaskNumTooBigException();
            }
        };

        try  {
            procInspectionService.exportListInspectionComplete(exportDto);
        } catch (Exception e) {

        }


        new Expectations() {
            {
                inspectionProcCompleteExport.insertTask(exportDto, anyString, anyString);
                result = new Exception();
            }
        };

        try  {
            procInspectionService.exportListInspectionComplete(exportDto);
        } catch (Exception e) {

        }

        new Expectations() {
            {
                inspectionProcCompleteExport.insertTask(exportDto, anyString, anyString);
                result = null;
            }
        };

        new Expectations() {
            {
                inspectionProcCompleteExport.exportData((ExportRequestInfo) any);
            }
        };


        new Expectations() {
            {
                procLogService.addLogByExport((ExportDto) any, anyString);
            }
        };

        try  {
            procInspectionService.exportListInspectionComplete(exportDto);
        } catch (Exception e) {

        }
    }


    /**
     * ??????????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/7/28 9:54
     */
    @Test
    public void updateProcInspectionDeviceCountBatch() {
        List<ProcInspection> list = new ArrayList<>();
        ProcInspection procInspection = new ProcInspection();
        procInspection.setProcId("1");
        list.add(procInspection);


        new Expectations() {
            {
                procInspectionDao.updateProcInspectionDeviceCountBatch((List<ProcInspection>) any);
                result = 1;
            }
        };

        try {
            procInspectionService.updateProcInspectionDeviceCountBatch(list);
        } catch (Exception e) {

        }

    }

    /**
     * ??????????????????????????????????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/7/28 10:06
     */
    @Test
    public void queryProcInspectionByProcInspectionId() {
        ProcInspection procInspection = new ProcInspection();
        procInspection.setInspectionDeviceCount(5);
        procInspection.setInspectionCompletedCount(2);
        new Expectations() {
            {
                procInspectionDao.queryProcInspectionByProcInspectionId((ProcInspection) any);
                ProcInspectionResp resp = new ProcInspectionResp();
                resp.setProcId("1");
                resp.setInspectionDeviceCount(5);
                resp.setInspectionCompletedCount(2);
                result = resp;
            }
        };

        new Expectations() {
            {
                procInspectionRecordService.queryInspectionRecordByProcIds((List<String>) any);
                List<ProcInspectionRecordVo> procInspectionRecordVoList = new ArrayList<>();
                ProcInspectionRecordVo recordVo = new ProcInspectionRecordVo();
                recordVo.setProcId("1");
                recordVo.setDeviceId("1");
                recordVo.setResult(ProcInspectionConstants.INSPECTION_RESULT_SUCESS);
                procInspectionRecordVoList.add(recordVo);
                recordVo = new ProcInspectionRecordVo();
                recordVo.setProcId("1");
                recordVo.setDeviceId("2");
                recordVo.setResult(ProcInspectionConstants.INSPECTION_RESULT_EXCEPTION);
                procInspectionRecordVoList.add(recordVo);
                result = procInspectionRecordVoList;
            }
        };
        procInspectionService.queryProcInspectionByProcInspectionId(procInspection);
    }


    /**
     * ????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/7/28 10:14
     */
    @Test
    public void updateProcInspectionStatusById() {
        ProcInspection procInspection = new ProcInspection();
        procInspectionService.updateProcInspectionStatusById(procInspection);
    }

    /**
     * ????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/7/28 10:17
     */
    @Test
    public void updateProcInspectionRemarkById() {
        ProcInspection procInspection = new ProcInspection();
        procInspectionService.updateProcInspectionRemarkById(procInspection);
    }

    /**
     * ??????id????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/7/28 10:18
     */
    @Test
    public void updateInspectionIsDeletedByIds() {
        ProcBaseReq req = new ProcBaseReq();
        procInspectionService.updateInspectionIsDeletedByIds(req);
    }

    /**
     * app??????????????????????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/7/28 10:18
     */
    @Test
    public void queryLoginUserInspectionListForApp() {
        ProcBaseReq procBaseReq = new ProcBaseReq();
        procInspectionService.queryLoginUserInspectionListForApp(procBaseReq);
    }

    /**
     * app??????????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/7/28 10:19
     */
    @Test
    public void queryInspectionListForApp() {
        ProcBaseReq procBaseReq = new ProcBaseReq();
        procInspectionService.queryInspectionListForApp(procBaseReq);
    }


    /**
     * ????????????????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/7/28 10:21
     */
    @Test
    public void updateProcInspectionAssignBatch() {
        ProcBase procBase = new ProcBase();
        List<String> list = new ArrayList<>();
        procInspectionService.updateProcInspectionAssignBatch(procBase, list);
    }


    /**
     * ??????????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/7/28 10:22
     */
    @Test
    public void queryInspectionProcListByDeptIds() {
        List<String> deptIds = new ArrayList<>();
        procInspectionService.queryInspectionProcListByDeptIds(deptIds);
    }


    /**
     * ????????????????????????????????????????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/7/28 10:23
     */
    @Test
    public void queryExistsProcForUserList() {
        List<String> userIdList = new ArrayList<>();
        procInspectionService.queryExistsProcForUserList(userIdList);
    }


    /**
     * ??????????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/7/28 10:24
     */
    @Test
    public void queryInspectionRelateDeptByProcIds() {
        List<String> list = new ArrayList<>();
        procInspectionService.queryInspectionRelateDeptByProcIds(list);
    }


    /**
     * ???????????????????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/7/28 10:25
     */
    @Test
    public void queryCountListProcInspectionUnfinishedProc() {
        QueryCondition<ProcBaseReq> queryCondition = new QueryCondition<>();
        procInspectionService.queryCountListProcInspectionUnfinishedProc(queryCondition);
    }


    /**
     * ???????????????????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/7/28 10:27
     */
    @Test
    public void queryListProcInspectionUnfinishedProcByPage() {
        QueryCondition<ProcBaseReq> queryCondition = new QueryCondition<>();
        procInspectionService.queryListProcInspectionUnfinishedProcByPage(queryCondition);
    }


    /**
     * ????????????????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/7/28 10:27
     */
    @Test
    public void queryCountListProcInspectionHisProc() {
        QueryCondition<ProcBaseReq> queryCondition = new QueryCondition<>();
        procInspectionService.queryCountListProcInspectionHisProc(queryCondition);
    }

    /**
     * ????????????????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/7/28 10:29
     */
    @Test
    public void queryListProcInspectionHisProcByPage() {
        QueryCondition<ProcBaseReq> queryCondition = new QueryCondition<>();
        procInspectionService.queryListProcInspectionHisProcByPage(queryCondition);
    }

    /**
     * ????????????????????????????????????????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/7/28 10:30
     */
    @Test
    public void selectNotInspectionProcInspection() {
        ProcInspection procInspection = new ProcInspection();
        procInspectionService.selectNotInspectionProcInspection(procInspection);
    }

    /**
     * ????????????????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/7/28 10:31
     */
    @Test
    public void queryCountListRelatedProcByInspectionTaskId() {
        QueryCondition<ProcBaseReq> queryCondition = new QueryCondition<>();
        procInspectionService.queryCountListRelatedProcByInspectionTaskId(queryCondition);
    }

    /**
     * ??????????????????????????????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/7/28 10:32
     */
    @Test
    public void queryListRelatedProcByInspectionTaskIdPage() {
        QueryCondition<ProcBaseReq> queryCondition = new QueryCondition<>();
        procInspectionService.queryListRelatedProcByInspectionTaskIdPage(queryCondition);
    }


    /**
     * ??????????????????
     * @author hedongwei@wistronits.com
     * @date  2019/7/13 16:54
     */
    public void expectationsSearchProc() {
        new Expectations() {
            {
                procBaseService.queryProcBaseById(anyString);
                ProcBaseResp procBaseInfo = new ProcBaseResp();
                procBaseInfo.setProcId("1");
                procBaseInfo.setStatus(ProcBaseConstants.PROC_STATUS_SINGLE_BACK);
                result = procBaseInfo;
            }
        };
    }

}
