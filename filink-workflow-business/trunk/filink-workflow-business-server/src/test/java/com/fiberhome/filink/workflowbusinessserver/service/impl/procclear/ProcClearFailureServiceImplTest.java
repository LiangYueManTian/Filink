package com.fiberhome.filink.workflowbusinessserver.service.impl.procclear;

import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.RequestInfoUtils;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.deviceapi.api.DevicePicFeign;
import com.fiberhome.filink.deviceapi.bean.DevicePicResp;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.exportapi.bean.ExportRequestInfo;
import com.fiberhome.filink.exportapi.exception.FilinkExportDataTooLargeException;
import com.fiberhome.filink.exportapi.exception.FilinkExportNoDataException;
import com.fiberhome.filink.exportapi.exception.FilinkExportTaskNumTooBigException;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.constant.LogConstants;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcBase;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcRelatedDevice;
import com.fiberhome.filink.workflowbusinessserver.bean.procclear.ProcClearFailure;
import com.fiberhome.filink.workflowbusinessserver.constant.WorkFlowBusinessConstants;
import com.fiberhome.filink.workflowbusinessserver.constant.WorkflowBusinessResultCode;
import com.fiberhome.filink.workflowbusinessserver.dao.procclear.ProcClearFailureDao;
import com.fiberhome.filink.workflowbusinessserver.export.procclear.ClearFailureProcHistoryExport;
import com.fiberhome.filink.workflowbusinessserver.export.procclear.ClearFailureProcUnfinishedExport;
import com.fiberhome.filink.workflowbusinessserver.req.app.procclear.ReceiptClearFailureReq;
import com.fiberhome.filink.workflowbusinessserver.req.procbase.ProcBaseReq;
import com.fiberhome.filink.workflowbusinessserver.req.procbase.procrelated.ProcRelatedDeviceListForDeviceIdsReq;
import com.fiberhome.filink.workflowbusinessserver.req.procclear.InsertClearFailureReq;
import com.fiberhome.filink.workflowbusinessserver.req.procclear.UpdateClearFailureReq;
import com.fiberhome.filink.workflowbusinessserver.resp.ProcBaseResp;
import com.fiberhome.filink.workflowbusinessserver.resp.ProcessInfo;
import com.fiberhome.filink.workflowbusinessserver.resp.app.procbase.ProcBaseRespForApp;
import com.fiberhome.filink.workflowbusinessserver.resp.app.procbase.ProcRelatedDeviceResp;
import com.fiberhome.filink.workflowbusinessserver.resp.app.procclear.ClearFailureDownLoadDetail;
import com.fiberhome.filink.workflowbusinessserver.service.impl.procbase.ProcBaseServiceImpl;
import com.fiberhome.filink.workflowbusinessserver.service.procbase.ProcLogService;
import com.fiberhome.filink.workflowbusinessserver.service.process.WorkflowService;
import mockit.Expectations;
import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 * ProcClearFailureServiceImplTest
 *
 * @author congcongsun2@wistronits.com
 * @since 2019/7/10
 */
@RunWith(MockitoJUnitRunner.class)
public class ProcClearFailureServiceImplTest {

    @InjectMocks
    private ProcClearFailureServiceImpl procClearFailureService;
    @Mock
    private ProcClearFailureDao procClearFailureDao;
    @Mock
    private ProcBaseServiceImpl procBaseService;
    /**
     * 注入销障工单未完成列表导出类
     */
    @Mock
    private ClearFailureProcUnfinishedExport clearFailureProcUnfinishedExport;
    /**
     * 注入销障工单历史列表导出类
     */
    @Mock
    private ClearFailureProcHistoryExport clearFailureProcHistoryExport;
    /**
     * 注入远程访问图片feign
     */
    @Mock
    private DevicePicFeign devicePicFeign;
    /**
     * 注入远程访问工单服务
     */
    @Mock
    private WorkflowService workflowService;
    /**
     * 工单日志服务
     */
    @Mock
    private ProcLogService procLogService;
    /**
     * 日志处理服务
     */
    @Mock
    private LogProcess logProcess;
    /**
     * 系统语言
     */
    @Mock
    private SystemLanguageUtil systemLanguage;

    I18nUtils i18nUtils = spy(I18nUtils.class);

    @Before
    public void setUp() throws Exception {
        ReflectionTestUtils.setField(procClearFailureService, "maxExportDataSize", 10000);
    }


    /**
     * 调用销障工单下载详情类
     * @author hedongwei@wistronits.com
     * @date  2019/8/2 10:33
     */
    @Test
    public void callClearFailureDownLoadDetail() {
        ClearFailureDownLoadDetail detail = new ClearFailureDownLoadDetail();
        detail.getAddress();
        detail.getAlarmTime();
        detail.getAssign();
        detail.getDeviceId();
        detail.getDeviceName();
        detail.getDeviceType();
        detail.getDoorName();
        detail.getDoorNo();
        detail.getEndTime();
        detail.getPositionBase();
        detail.getProcId();
        detail.getProcType();
        detail.getRefAlarmCode();
        detail.getRefAlarmName();
        detail.getStartTime();
        detail.getTitle();
    }


    @Test
    public void updateClearFailure() {
        ProcClearFailure procClearFailure = new ProcClearFailure();
        when(procClearFailureDao.updateById(procClearFailure)).thenReturn(1);
        Assert.assertEquals(1, procClearFailureService.updateClearFailure(procClearFailure));
    }

    @Test
    public void selectClearFailureProcOne() {
        ProcClearFailure procClearFailure = new ProcClearFailure();
        when(procClearFailureDao.selectOne(procClearFailure)).thenReturn(procClearFailure);
        Assert.assertEquals(procClearFailure, procClearFailureService.selectClearFailureProcOne(procClearFailure));
    }

    @Test
    public void addClearFailureProc() throws Exception {
        when(systemLanguage.querySystemLanguage()).thenReturn("CH");
        InsertClearFailureReq insertClearFailureReq = new InsertClearFailureReq();
        getProcessInfoByClearFailure();
        procClearFailureService.addClearFailureProc(insertClearFailureReq);
    }

    private void getProcessInfoByClearFailure() throws Exception {
        new Expectations(PropertyUtils.class) {
            {
                PropertyUtils.getProperty(any, "ecTime");
                result = new Date();
            }

            {
                PropertyUtils.getProperty(any, "accountabilityDeptList");
                result = new ArrayList<>();
            }
        };
    }

    @Test
    public void updateClearFailureProcById() throws Exception {
        when(systemLanguage.querySystemLanguage()).thenReturn("CH");
        UpdateClearFailureReq updateClearFailureReq = new UpdateClearFailureReq();
        getProcessInfoByClearFailure();
        procClearFailureService.updateClearFailureProcById(updateClearFailureReq);

    }

    @Test
    public void clearFailureProcParamsInit() {
    }

    private void i18nUtilsExpectation() {
        new Expectations(I18nUtils.class) {
            {
                I18nUtils.getSystemString(anyString);
                result = "demo";
            }
        };
    }

    @Test
    public void exportClearFailureProcUnfinished() {
        i18nUtilsExpectation();
        ExportDto exportDto = new ExportDto();
        when(clearFailureProcUnfinishedExport.insertTask(exportDto, WorkFlowBusinessConstants.SERVER_NAME, "demo"))
                .thenThrow(FilinkExportNoDataException.class)
                .thenThrow(FilinkExportDataTooLargeException.class)
                .thenThrow(FilinkExportTaskNumTooBigException.class)
                .thenThrow(Exception.class)
                .thenReturn(new ExportRequestInfo());
        Result result = procClearFailureService.exportClearFailureProcUnfinished(exportDto);
        Assert.assertTrue(result.getCode() == WorkflowBusinessResultCode.EXPORT_NO_DATA);

        result = procClearFailureService.exportClearFailureProcUnfinished(exportDto);
        Assert.assertTrue(result.getCode() == WorkflowBusinessResultCode.EXPORT_DATA_TOO_LARGE);

        result = procClearFailureService.exportClearFailureProcUnfinished(exportDto);
        Assert.assertTrue(result.getCode() == WorkflowBusinessResultCode.EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS);

        result = procClearFailureService.exportClearFailureProcUnfinished(exportDto);
        Assert.assertTrue(result.getCode() == WorkflowBusinessResultCode.FAILED_TO_CREATE_EXPORT_TASK);

        when(logProcess.generateAddLogToCallParam(LogConstants.LOG_TYPE_OPERATE)).thenReturn(new AddLogBean());
        result = procClearFailureService.exportClearFailureProcUnfinished(exportDto);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());

    }

    @Test
    public void exportClearFailureProcHistory() {
        i18nUtilsExpectation();
        ExportDto exportDto = new ExportDto();
        when(clearFailureProcHistoryExport.insertTask(exportDto, WorkFlowBusinessConstants.SERVER_NAME, "demo"))
                .thenThrow(FilinkExportNoDataException.class)
                .thenThrow(FilinkExportDataTooLargeException.class)
                .thenThrow(FilinkExportTaskNumTooBigException.class)
                .thenThrow(Exception.class)
                .thenReturn(new ExportRequestInfo());
        Result result = procClearFailureService.exportClearFailureProcHistory(exportDto);
        Assert.assertTrue(result.getCode() == WorkflowBusinessResultCode.EXPORT_NO_DATA);

        result = procClearFailureService.exportClearFailureProcHistory(exportDto);
        Assert.assertTrue(result.getCode() == WorkflowBusinessResultCode.EXPORT_DATA_TOO_LARGE);

        result = procClearFailureService.exportClearFailureProcHistory(exportDto);
        Assert.assertTrue(result.getCode() == WorkflowBusinessResultCode.EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS);

        result = procClearFailureService.exportClearFailureProcHistory(exportDto);
        Assert.assertTrue(result.getCode() == WorkflowBusinessResultCode.FAILED_TO_CREATE_EXPORT_TASK);

        when(logProcess.generateAddLogToCallParam(LogConstants.LOG_TYPE_OPERATE)).thenReturn(new AddLogBean());
        result = procClearFailureService.exportClearFailureProcHistory(exportDto);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());

    }

    @Test
    public void queryListClearFailureUnfinishedProcByPage() {
        QueryCondition<ProcBaseReq> queryCondition = new QueryCondition<>();
        Result result = new Result();
        when(procBaseService.queryListProcUnfinishedProcByPage(any())).thenReturn(result);
        Assert.assertEquals(result, procClearFailureService.queryListClearFailureUnfinishedProcByPage(queryCondition));
    }

    @Test
    public void queryListClearFailureHisProcByPage() {
        QueryCondition<ProcBaseReq> queryCondition = new QueryCondition<>();
        Result result = new Result();
        when(procBaseService.queryListProcHisProcByPage(any())).thenReturn(result);
        Assert.assertEquals(result, procClearFailureService.queryListClearFailureHisProcByPage(queryCondition));
    }

    @Test
    public void saveProcClearFailureSpecific() {
        new Expectations(RequestInfoUtils.class) {
            {
                RequestInfoUtils.getUserId();
                result = "userId";
            }
        };
        ProcClearFailure procClearFailure = new ProcClearFailure();
        procClearFailure.setProcId("procId");
        ProcClearFailure procClearFailureInfo = new ProcClearFailure();

        when(procClearFailureDao.selectOne(any())).thenReturn(procClearFailureInfo);
        when(procClearFailureDao.updateById(any())).thenReturn(1);
        when(procClearFailureDao.insert(any())).thenReturn(1);
        Assert.assertEquals(1, procClearFailureService.saveProcClearFailureSpecific(procClearFailure));
        when(procClearFailureDao.selectOne(any())).thenReturn(null);
        Assert.assertEquals(1, procClearFailureService.saveProcClearFailureSpecific(procClearFailure));
    }

    @Test
    public void updateProcClearFailureSpecificIsDeleted() {
        String procId = "procId";
        String isDeleted = "isDeleted";
        when(procClearFailureDao.updateProcClearFailureSpecificIsDeleted(procId, isDeleted)).thenReturn(1);
        Assert.assertEquals(1, procClearFailureService.updateProcClearFailureSpecificIsDeleted(procId, isDeleted));

    }

    @Test
    public void updateProcClearFailureSpecificIsDeletedBatch() {
        ProcBaseReq procBaseReq = new ProcBaseReq();
        when(procClearFailureDao.updateProcClearFailureSpecificIsDeletedBatch(procBaseReq)).thenReturn(1);
        Assert.assertEquals(1, procClearFailureService.updateProcClearFailureSpecificIsDeletedBatch(procBaseReq));

    }

    @Test
    public void queryProcClearFailureSpecific() {
        Set<String> procIds = new HashSet<>();
        procClearFailureService.queryProcClearFailureSpecific(procIds);
    }

    @Test
    public void queryCountListProcUnfinishedProcStatus() {
        procClearFailureService.queryCountListProcUnfinishedProcStatus();

    }

    @Test
    public void queryCountClearFailureProcByAssigned() {
        procClearFailureService.queryCountClearFailureProcByAssigned();
    }

    @Test
    public void queryCountClearFailureProcByPending() {
        procClearFailureService.queryCountClearFailureProcByPending();
    }

    @Test
    public void queryCountClearFailureProcByProcessing() {
        procClearFailureService.queryCountClearFailureProcByProcessing();
    }

    @Test
    public void queryCountClearFailureProcByToday() {
        procClearFailureService.queryCountClearFailureProcByToday();
    }

    @Test
    public void queryListClearFailureGroupByErrorReason() {
        procClearFailureService.queryListClearFailureGroupByErrorReason();
    }

    @Test
    public void queryListClearFailureGroupByProcessingScheme() {
        procClearFailureService.queryListClearFailureGroupByProcessingScheme();

    }

    @Test
    public void regenerateClearFailureProc() throws Exception {
        InsertClearFailureReq insertClearFailureReq = new InsertClearFailureReq();
        when(systemLanguage.querySystemLanguage()).thenReturn("zh");
        getProcessInfoByClearFailure();
        Assert.assertEquals(160104, procClearFailureService.regenerateClearFailureProc(insertClearFailureReq).getCode());
        ProcClearFailure procClearFailure = new ProcClearFailure();
        procClearFailure.setIsCheckSingleBack("2");
        when(procClearFailureDao.selectById(any())).thenReturn(procClearFailure);
        when(procBaseService.addProcBase(any())).thenReturn(new Result());
        when(procBaseService.regenerateProc(any())).thenReturn(new Result());
        Assert.assertEquals(0, procClearFailureService.regenerateClearFailureProc(insertClearFailureReq).getCode());
        procClearFailure.setIsCheckSingleBack("1");
        when(procClearFailureDao.selectById(any())).thenReturn(procClearFailure);
        Assert.assertEquals(0, procClearFailureService.regenerateClearFailureProc(insertClearFailureReq).getCode());
    }

    @Test
    public void queryClearFailureProcTopFive() {
        ProcBaseResp procBaseResp = new ProcBaseResp();
        procBaseResp.setDeviceId("deviceId");
        procBaseResp.setProcId("proId");

        List<ProcBaseResp> procBaseRespList = new ArrayList<>();
        procBaseRespList.add(procBaseResp);
        Result<List> result = new Result<>();
        result.setData(procBaseRespList);
        when(procBaseService.queryListProcUnfinishedProcByPage(any())).thenReturn(result);
        when(devicePicFeign.getPicUrlByProcIds(any())).thenReturn(new Result());
        Assert.assertEquals(-1, procClearFailureService.queryClearFailureProcTopFive("sz").getCode());
        List<Object> objects = new ArrayList<>();
        DevicePicResp devicePicResp = new DevicePicResp();
        devicePicResp.setDeviceId("sc");
        devicePicResp.setResource("Resource");
        devicePicResp.setResourceId("ResourceId");
        objects.add(devicePicResp);
        Result<List> result1 = new Result<>();
        result1.setData(objects);
        when(devicePicFeign.getPicUrlByProcIds(any())).thenReturn(result1);
        Assert.assertEquals(0, procClearFailureService.queryClearFailureProcTopFive("sz").getCode());

    }

    @Test
    public void receiptClearFailureProcForApp() {

        ReceiptClearFailureReq receiptClearFailureReq = new ReceiptClearFailureReq();
        receiptClearFailureReq.setErrorReason("errorReason");
        receiptClearFailureReq.setUserDefinedErrorReason("userDefinedErrorReason");
        receiptClearFailureReq.setProcessingScheme("ProcessingScheme");
        receiptClearFailureReq.setUserDefinedProcessingScheme("UserDefinedProcessingScheme");
        Assert.assertEquals(160103, procClearFailureService.receiptClearFailureProcForApp(receiptClearFailureReq).getCode());
        receiptClearFailureReq.setProcId("proId");

        ProcBase procBase = new ProcBase();
        Result<ProcBase> procBaseResult = new Result<>();
        procBaseResult.setData(procBase);
        when(procBaseService.queryProcessById(any())).thenReturn(procBaseResult);
        Assert.assertEquals(160104, procClearFailureService.receiptClearFailureProcForApp(receiptClearFailureReq).getCode());

        procBase.setProcId("procId");
        procBaseResult.setData(procBase);
        when(procBaseService.queryProcessById(any())).thenReturn(procBaseResult);
        when(procClearFailureDao.receiptProcClearFailureById(any())).thenReturn(0);
        Assert.assertEquals(-1, procClearFailureService.receiptClearFailureProcForApp(receiptClearFailureReq).getCode());

        i18nUtilsExpectation();
        when(procClearFailureDao.receiptProcClearFailureById(any())).thenReturn(1);
        when(workflowService.completeProcess(any())).thenReturn(new Result());
        when(procLogService.getAddProcOperateLogParamForApp(any(), any(), any())).thenReturn(new AddLogBean());
        Assert.assertEquals(0, procClearFailureService.receiptClearFailureProcForApp(receiptClearFailureReq).getCode());

    }

    @Test
    public void commitProcClearFailure() {
    }

    @Test
    public void downLoadClearFailureProcForApp() {
        List<ProcBaseRespForApp> procBaseRespForApps = new ArrayList<>();
        ProcBaseRespForApp procBaseRespForApp = new ProcBaseRespForApp();
        List<ProcRelatedDeviceResp> procRelatedDeviceResps = new ArrayList<>();
        ProcRelatedDeviceResp procRelatedDeviceResp = new ProcRelatedDeviceResp();
        procRelatedDeviceResps.add(procRelatedDeviceResp);
        procBaseRespForApp.setProcRelatedDeviceRespList(procRelatedDeviceResps);
        procBaseRespForApp.setProcId("procId");
        procBaseRespForApp.setCreateTime(new Date());
        procBaseRespForApp.setExpectedCompletedTime(new Date());
        procBaseRespForApp.setRefAlarmTime(new Date().getTime());
        procBaseRespForApps.add(procBaseRespForApp);
        procClearFailureService.downLoadClearFailureProcForApp(procBaseRespForApps);

    }

    @Test
    public void updateProcClearFailureStatusById() {
        procClearFailureService.updateProcClearFailureStatusById(new ProcClearFailure());
    }

    @Test
    public void queryExistsProcClearFailureForAlarmList() {
        procClearFailureService.queryExistsProcClearFailureForAlarmList(new ArrayList<>());
    }

    @Test
    public void updateProcClearFailureRemarkById() {
        procClearFailureService.updateProcClearFailureRemarkById(new ProcClearFailure());
    }

    @Test
    public void updateProcClearFailureIsDeletedByIds() {
        procClearFailureService.updateProcClearFailureIsDeletedByIds(new ProcBaseReq());
    }

    @Test
    public void queryClearFailureProcRelateDevice() {
        procClearFailureService.queryClearFailureProcRelateDevice(new ProcBaseReq());
    }

    @Test
    public void queryClearFailureListForApp() {
        procClearFailureService.queryClearFailureListForApp(new ProcBaseReq());
    }

    @Test
    public void updateProcClearAssignBatch() {
        procClearFailureService.updateProcClearAssignBatch(new ProcBaseReq(), new ArrayList<>());
    }

    @Test
    public void queryExistsProcClearForAlarmList() {
        procClearFailureService.queryExistsProcClearForAlarmList(null);
        List<String> alarmProcList = new ArrayList<>();
        alarmProcList.add("x");
        List<ProcClearFailure> clearFailureList = new ArrayList<>();
        when(procClearFailureDao.queryExistsProcClearForAlarmList(alarmProcList)).thenReturn(clearFailureList);
        procClearFailureService.queryExistsProcClearForAlarmList(alarmProcList);
    }

    @Test
    public void selectClearRelatedDeviceListInfo() {
        procClearFailureService.selectClearRelatedDeviceListInfo(new ProcRelatedDeviceListForDeviceIdsReq());
    }

    @Test
    public void queryExistsProcForUserList() {
        procClearFailureService.queryExistsProcForUserList(new ArrayList<>());
    }

    @Test
    public void queryProcRelateDeviceByProcIds() {
        procClearFailureService.queryProcRelateDeviceByProcIds(new ArrayList<>());
    }

    @Test
    public void queryCountListProcClearUnfinishedProc() {
        procClearFailureService.queryCountListProcClearUnfinishedProc(new QueryCondition<>());
    }

    @Test
    public void queryListProcClearUnfinishedProcByPage() {
        procClearFailureService.queryListProcClearUnfinishedProcByPage(new QueryCondition<>());
    }

    @Test
    public void queryCountListProcClearHisProc() {
        procClearFailureService.queryCountListProcClearHisProc(new QueryCondition<>());
    }

    @Test
    public void queryListProcClearHisProcByPage() {
        procClearFailureService.queryListProcClearHisProcByPage(new QueryCondition<>());
    }

    @Test
    public void queryListClearFailureGroupByDeviceType() {
        procClearFailureService.queryListClearFailureGroupByDeviceType();
    }

    @Test
    public void queryListClearFailureByStatus() {
        Result<Integer> integerResult = new Result<>();
        integerResult.setData(1);
        when(procBaseService.queryCountProcByStatus(any())).thenReturn(integerResult);
        when(procBaseService.queryCountListProcHisProc(any())).thenReturn(integerResult);
        i18nUtilsExpectation();
        procClearFailureService.queryListClearFailureByStatus();
    }

    @Test
    public void queryCountListProcHisProc() {
    }

    @Test
    public void setProcTypeToClearFailure() {
    }

    @Test
    public void checkProcParamsForClearFailure() {
        i18nUtilsExpectation();
        ProcessInfo processInfo = new ProcessInfo();
        ProcBase procBase = new ProcBase();
        procBase.setTitle("title");
        procBase.setProcType("procType");
        procBase.setRefAlarm("refAlarm");
        processInfo.setProcBase(procBase);
        List<ProcRelatedDevice> procRelatedDevices = new ArrayList<>();
        procRelatedDevices.add(new ProcRelatedDevice());
        processInfo.setProcRelatedDevices(procRelatedDevices);
        procClearFailureService.checkProcParamsForClearFailure(processInfo);
        procBase.setExpectedCompletedTime(new Date());
        procBase.setRemark("Remark");
        processInfo.setProcBase(procBase);
        when(procBaseService.queryExistsProcForAlarm(any(), any())).thenReturn(true);
        procClearFailureService.checkProcParamsForClearFailure(processInfo);
        when(procBaseService.queryExistsProcForAlarm(any(), any())).thenReturn(false);
        procClearFailureService.checkProcParamsForClearFailure(processInfo);
    }
}