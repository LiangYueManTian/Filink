package com.fiberhome.filink.rfid.service.impl.fibercore;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.deviceapi.api.DeviceFeign;
import com.fiberhome.filink.deviceapi.bean.DeviceInfoDto;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.exportapi.bean.ExportRequestInfo;
import com.fiberhome.filink.exportapi.exception.FilinkExportDataTooLargeException;
import com.fiberhome.filink.exportapi.exception.FilinkExportNoDataException;
import com.fiberhome.filink.exportapi.exception.FilinkExportTaskNumTooBigException;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.constant.LogConstants;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.rfid.bean.fibercore.JumpFiberInfo;
import com.fiberhome.filink.rfid.constant.AppConstant;
import com.fiberhome.filink.rfid.constant.RfIdResultCodeConstant;
import com.fiberhome.filink.rfid.dao.fibercore.JumpFiberInfoDao;
import com.fiberhome.filink.rfid.export.jumpfiber.JumpFiberExport;
import com.fiberhome.filink.rfid.req.fibercore.DeleteJumpFiberInfoReq;
import com.fiberhome.filink.rfid.req.fibercore.QueryJumpFiberInfoReq;
import com.fiberhome.filink.rfid.req.fibercore.app.BatchOperationJumpFiberInfoForApp;
import com.fiberhome.filink.rfid.req.fibercore.app.OperationJumpFiberInfoReqForApp;
import com.fiberhome.filink.rfid.req.fibercore.app.QueryJumpFiberInfoReqForApp;
import com.fiberhome.filink.rfid.resp.fibercore.JumpFiberInfoResp;
import com.fiberhome.filink.rfid.resp.fibercore.app.JumpFiberInfoRespForApp;
import com.fiberhome.filink.rfid.service.impl.UpdateOpticCableSectionStatus;
import com.fiberhome.filink.rfid.service.impl.UpdatePortStatusAsync;
import com.fiberhome.filink.rfid.service.rfid.RfidInfoService;
import com.fiberhome.filink.rfid.service.statistics.OdnFacilityResourcesStatisticsService;
import com.fiberhome.filink.rfid.service.template.TemplateService;
import com.fiberhome.filink.rfid.utils.RfidServerPermission;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import mockit.Expectations;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.BeanUtils;
import org.springframework.test.util.ReflectionTestUtils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

/**
 * JumpFiberInfoServiceImplTest
 *
 * @author chaofanrong@wistronits.com
 * @since 2019/7/9
 */
@RunWith(MockitoJUnitRunner.class)
public class JumpFiberInfoServiceImplTest {
    /**
     * 被测试类
     */
    @InjectMocks
    private JumpFiberInfoServiceImpl jumpFiberInfoService;
    @Mock
    private JumpFiberInfoDao jumpFiberInfoDao;
    /**
     * 远程调用日志服务
     */
    @Mock
    private LogProcess logProcess;
    @Mock
    private RfidInfoService rfidInfoService;
    /**
     * 远程调用SystemLanguage服务
     */
    @Mock
    private SystemLanguageUtil systemLanguageUtil;
    @Mock
    private OdnFacilityResourcesStatisticsService odnFacilityResourcesStatisticsService;

    /**
     * 智能标签业务权限rfidServerPermission
     */
    @Mock
    private RfidServerPermission rfidServerPermission;

    @Mock
    private TemplateService templateService;
    /**
     * 注入updatePortStatusAsync接口
     */
    @Mock
    private UpdatePortStatusAsync updatePortStatusAsync;

    /**
     * 注入updateOpticCableSectionStatusAsync接口
     */
    @Mock
    private UpdateOpticCableSectionStatus updateOpticCableSectionStatusAsync;

    /**
     * 远程调用设施服务
     */
    @Mock
    private DeviceFeign deviceFeign;

    /**
     * 注入跳接信息列表导出类
     */
    @Mock
    private JumpFiberExport jumpFiberExport;


    /**
     * 注入rfIdCode接口
     */
    @Mock
    private RfidInfoService rfIdInfoService;


    @Before
    public void setUp() throws Exception {
        ReflectionTestUtils.setField(jumpFiberInfoService, "exportServerName", "filink-rfid-server");
        ReflectionTestUtils.setField(jumpFiberInfoService, "maxExportDataSize", 10000);
        ReflectionTestUtils.setField(jumpFiberInfoService, "jumpFiberMaxNum", 64);
    }

    @Test
    public void assemblyJumpFiberInfoResp(){
        List<DeviceInfoDto> deviceInfoDtoList = new ArrayList<>();
        DeviceInfoDto deviceInfoDto = new DeviceInfoDto();
        deviceInfoDto.setDeviceId("sfdgffhgfdgfsfdsf");
        deviceInfoDtoList.add(deviceInfoDto);

        Set<String> deviceIds = new HashSet<>();
        String[] deviceArray = new String[deviceIds.size()];
        deviceIds.toArray(deviceArray);
        when(deviceFeign.getDeviceByIds(any())).thenReturn(deviceInfoDtoList);

        List<JumpFiberInfoResp> jumpFiberInfoRespList = new ArrayList<>();
        JumpFiberInfoResp jumpFiberInfoResp = new JumpFiberInfoResp();
        jumpFiberInfoResp.setDeviceId("sfdgffhgfdgfsfdsf");
        jumpFiberInfoResp.setBoxSide(0);
        jumpFiberInfoResp.setFrameNo("1");
        jumpFiberInfoResp.setDiscSide(0);
        jumpFiberInfoResp.setDiscNo("1");
        jumpFiberInfoResp.setPortNo("1");

        jumpFiberInfoResp.setOppositeDeviceId("sfdgffhgfdgfsfdsf");
        jumpFiberInfoRespList.add(jumpFiberInfoResp);

        QueryJumpFiberInfoReq queryJumpFiberInfoReq = new QueryJumpFiberInfoReq();
        queryJumpFiberInfoReq.setDeviceId("sfdgffhgfdgfsfdsf");
        queryJumpFiberInfoReq.setBoxSide(0);
        queryJumpFiberInfoReq.setFrameNo("1");
        queryJumpFiberInfoReq.setDiscSide(0);
        queryJumpFiberInfoReq.setDiscNo("1");
        queryJumpFiberInfoReq.setPortNo("1");
        queryJumpFiberInfoReq.setOppositeDeviceId("sfdgffhgfdgfsfdsf");

        //组装本端及对端数据
        jumpFiberInfoRespList = JumpFiberInfoResp.assemblyJumpFiberInfoThisAndOpposite(jumpFiberInfoRespList,queryJumpFiberInfoReq);

        when(templateService.batchQueryPortInfo(any())).thenReturn(jumpFiberInfoRespList);

        when(templateService.queryPortIdByPortInfo(any())).thenReturn("fdsafdsfdsfdsaf");

        when(templateService.queryPortNumByPortId(any())).thenReturn("1A-1-1A");

        List<JumpFiberInfoResp> jumpFiberInfoRespList1 = new ArrayList<>();
        JumpFiberInfoResp jumpFiberInfoResp1 = new JumpFiberInfoResp();
        jumpFiberInfoResp1.setDeviceId("sfdgffhgfdgfsfdsf");
        jumpFiberInfoResp1.setBoxSide(0);
        jumpFiberInfoResp1.setFrameNo("1");
        jumpFiberInfoResp1.setDiscSide(0);
        jumpFiberInfoResp1.setDiscNo("1");
        jumpFiberInfoResp1.setPortNo("1");

        jumpFiberInfoResp1.setOppositeDeviceId("sfdgffhgfdgfsfdsf");
        jumpFiberInfoRespList1.add(jumpFiberInfoResp1);

        QueryJumpFiberInfoReq queryJumpFiberInfoReq1 = new QueryJumpFiberInfoReq();
        queryJumpFiberInfoReq1.setDeviceId("sfdgffhgfdgfsfdsf");
        queryJumpFiberInfoReq1.setBoxSide(0);
        queryJumpFiberInfoReq1.setFrameNo("1");
        queryJumpFiberInfoReq1.setDiscSide(0);
        queryJumpFiberInfoReq1.setDiscNo("1");
        queryJumpFiberInfoReq1.setPortNo("1");

        queryJumpFiberInfoReq1.setOppositeDeviceId("sfdgffhgfdgfsfdsf");
        List<JumpFiberInfoResp> jumpFiberInfoRespList3 = new ArrayList<>();
        JumpFiberInfoResp jumpFiberInfoResp3 = new JumpFiberInfoResp();
        jumpFiberInfoResp3.setDeviceId("sfdgffhgfdgfsfdsf");
        jumpFiberInfoResp3.setBoxSide(0);
        jumpFiberInfoResp3.setFrameNo("1");
        jumpFiberInfoResp3.setDiscSide(0);
        jumpFiberInfoResp3.setDiscNo("1");
        jumpFiberInfoResp3.setPortNo("1");

        jumpFiberInfoResp3.setOppositeDeviceId("sfdgffhgfdgfsfdsf");
        jumpFiberInfoRespList3.add(jumpFiberInfoResp3);

//        when(jumpFiberInfoService.assemblyJumpFiberInfoResp(jumpFiberInfoRespList1,queryJumpFiberInfoReq1)).thenReturn(jumpFiberInfoRespList3);
        jumpFiberInfoService.assemblyJumpFiberInfoResp(jumpFiberInfoRespList1,queryJumpFiberInfoReq1);
    }

    @Test
    public void checkJumpFiberUsed(){
        List<JumpFiberInfoResp> jumpFiberInfoRespList = new ArrayList<>();
        JumpFiberInfoResp jumpFiberInfoResp = new JumpFiberInfoResp();
        jumpFiberInfoResp.setDeviceId("sfdgffhgfdgfsfdsf");
        jumpFiberInfoResp.setBoxSide(0);
        jumpFiberInfoResp.setFrameNo("1");
        jumpFiberInfoResp.setDiscSide(0);
        jumpFiberInfoResp.setDiscNo("1");
        jumpFiberInfoResp.setPortNo("1");
        jumpFiberInfoRespList.add(jumpFiberInfoResp);
        List<String> deviceIds = new ArrayList<>();
        QueryJumpFiberInfoReq queryJumpFiberInfoReq = new QueryJumpFiberInfoReq();
        queryJumpFiberInfoReq.setDeviceIds(deviceIds);
        when(jumpFiberInfoDao.queryJumpFiberInfoByPortInfo(any())).thenReturn(jumpFiberInfoRespList);

        BatchOperationJumpFiberInfoForApp batchOperationJumpFiberInfoForApp = new BatchOperationJumpFiberInfoForApp();
        List<OperationJumpFiberInfoReqForApp> operationJumpFiberInfoReqForAppList = new ArrayList<>();
        OperationJumpFiberInfoReqForApp operationJumpFiberInfoReqForApp = new OperationJumpFiberInfoReqForApp();
        operationJumpFiberInfoReqForApp.setJumpFiberId("khjglkjkhfkj");
        operationJumpFiberInfoReqForApp.setDeviceId("fdsfsqrewqreqw");
        operationJumpFiberInfoReqForApp.setBoxSide(0);
        operationJumpFiberInfoReqForApp.setFrameNo("1");
        operationJumpFiberInfoReqForApp.setDiscSide(0);
        operationJumpFiberInfoReqForApp.setDiscNo("1");
        operationJumpFiberInfoReqForApp.setPortNo("1");

        operationJumpFiberInfoReqForApp.setOppositeDeviceId("rewqrewtertrewre");
        operationJumpFiberInfoReqForApp.setOppositeBoxSide(0);
        operationJumpFiberInfoReqForApp.setOppositeFrameNo("1");
        operationJumpFiberInfoReqForApp.setOppositeDiscSide(0);
        operationJumpFiberInfoReqForApp.setOppositeDiscNo("1");
        operationJumpFiberInfoReqForApp.setOppositePortNo("2");

        operationJumpFiberInfoReqForAppList.add(operationJumpFiberInfoReqForApp);

        batchOperationJumpFiberInfoForApp.setOperationJumpFiberInfoReqForAppList(operationJumpFiberInfoReqForAppList);
        Assert.assertEquals(false,jumpFiberInfoService.checkJumpFiberUsed(batchOperationJumpFiberInfoForApp));

    }

    @Test
    public void queryJumpFiberInfoByPortInfo() {
        when(jumpFiberInfoDao.queryJumpFiberInfoByPortInfo(any())).thenReturn(new ArrayList<>());
        assemblyJumpFiberInfoResp();
        Assert.assertEquals(0, jumpFiberInfoService.queryJumpFiberInfoByPortInfo(any()).getCode());

    }


    @Test
    public void deleteJumpFiberInfoById() {
        DeleteJumpFiberInfoReq deleteJumpFiberInfoReq = new DeleteJumpFiberInfoReq();
        Set<String> jumpFiberIdList = new HashSet<>();
        jumpFiberIdList.add("dfsafdsafd");
        deleteJumpFiberInfoReq.setJumpFiberIdList(jumpFiberIdList);
        QueryJumpFiberInfoReq queryJumpFiberInfoReq = new QueryJumpFiberInfoReq();
        BeanUtils.copyProperties(deleteJumpFiberInfoReq,queryJumpFiberInfoReq);
        queryJumpFiberInfoReq.setJumpFiberIdList(deleteJumpFiberInfoReq.getJumpFiberIdList());

        List<JumpFiberInfoResp> jumpFiberInfoRespList = new ArrayList<>();
        when(jumpFiberInfoDao.queryJumpFiberInfoByPortInfo(any())).thenReturn(jumpFiberInfoRespList);
        Assert.assertEquals(150506, jumpFiberInfoService.deleteJumpFiberInfoById(deleteJumpFiberInfoReq).getCode());

        JumpFiberInfoResp jumpFiberInfoResp = new JumpFiberInfoResp();
        jumpFiberInfoResp.setPortNo("1");
        jumpFiberInfoResp.setDiscNo("2");
        jumpFiberInfoRespList.add(jumpFiberInfoResp);
        when(jumpFiberInfoDao.queryJumpFiberInfoByPortInfo(any())).thenReturn(jumpFiberInfoRespList);
        when(rfidInfoService.deleteRfidInfo(any())).thenReturn(new Result());
        when(jumpFiberInfoDao.deleteJumpFiberInfoById(any(),any(),any())).thenReturn(1);
        saveOperatorLog();
        Assert.assertEquals(0, jumpFiberInfoService.deleteJumpFiberInfoById(deleteJumpFiberInfoReq).getCode());
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
    public void exportJumpFiberList() {
        new Expectations(MessageFormat.class) {
            {
                MessageFormat.format(anyString,any);
                result = "demo";
            }
        };
        i18nUtilsExpectation();
        saveOperatorLog();
        ExportDto exportDto = new ExportDto();
        when(jumpFiberExport.insertTask(exportDto, "filink-rfid-server", null))
                .thenThrow(FilinkExportNoDataException.class)
                .thenThrow(FilinkExportDataTooLargeException.class)
                .thenThrow(FilinkExportTaskNumTooBigException.class)
                .thenThrow(Exception.class)
                .thenReturn(new ExportRequestInfo());
        Result result = jumpFiberInfoService.exportJumpFiberList(exportDto);
        Assert.assertTrue(result.getCode() == RfIdResultCodeConstant.EXPORT_NO_DATA);

        result = jumpFiberInfoService.exportJumpFiberList(exportDto);
        Assert.assertTrue(result.getCode() == RfIdResultCodeConstant.EXPORT_DATA_TOO_LARGE);

        result = jumpFiberInfoService.exportJumpFiberList(exportDto);
        Assert.assertTrue(result.getCode() == RfIdResultCodeConstant.EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS);

        result = jumpFiberInfoService.exportJumpFiberList(exportDto);
        Assert.assertTrue(result.getCode() == RfIdResultCodeConstant.FAILED_TO_CREATE_EXPORT_TASK);

        when(logProcess.generateAddLogToCallParam(LogConstants.LOG_TYPE_OPERATE)).thenReturn(new AddLogBean());
        result = jumpFiberInfoService.exportJumpFiberList(exportDto);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }


    @Test
    public void queryJumpFiberInfoByPortInfoForApp() {
        QueryJumpFiberInfoReqForApp queryJumpFiberInfoReqForApp = new QueryJumpFiberInfoReqForApp();
        List<JumpFiberInfoRespForApp> jumpFiberInfoRespForAppList = new ArrayList<>();
        JumpFiberInfoRespForApp jumpFiberInfoRespForApp = new JumpFiberInfoRespForApp();
        jumpFiberInfoRespForApp.setDeviceId("sfdgffhgfdgfsfdsf");
        jumpFiberInfoRespForApp.setBoxSide(0);
        jumpFiberInfoRespForApp.setFrameNo("1");
        jumpFiberInfoRespForApp.setDiscSide(0);
        jumpFiberInfoRespForApp.setDiscNo("1");
        jumpFiberInfoRespForApp.setPortNo("1");
        jumpFiberInfoRespForAppList.add(jumpFiberInfoRespForApp);
        when(jumpFiberInfoDao.queryJumpFiberInfoByPortInfoForApp(any())).thenReturn(jumpFiberInfoRespForAppList);

        assemblyJumpFiberInfoThisAndOppositeForApp();

        Result result = jumpFiberInfoService.queryJumpFiberInfoByPortInfoForApp(queryJumpFiberInfoReqForApp);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());

    }

    @Test
    public void assemblyJumpFiberInfoThisAndOppositeForApp() {
        List<JumpFiberInfoRespForApp> jumpFiberInfoRespForAppList= new ArrayList<>();
        JumpFiberInfoRespForApp jumpFiberInfoRespForApp = new JumpFiberInfoRespForApp();
        jumpFiberInfoRespForApp.setDeviceId("sfdgffhgfdgfsfdsf");
        jumpFiberInfoRespForApp.setBoxSide(0);
        jumpFiberInfoRespForApp.setFrameNo("1");
        jumpFiberInfoRespForApp.setDiscSide(0);
        jumpFiberInfoRespForApp.setDiscNo("1");
        jumpFiberInfoRespForApp.setPortNo("1");
        jumpFiberInfoRespForAppList.add(jumpFiberInfoRespForApp);

        QueryJumpFiberInfoReqForApp queryJumpFiberInfoReqForApp = new QueryJumpFiberInfoReqForApp();
        queryJumpFiberInfoReqForApp.setDeviceId("sfdgffhgfdgfsfdsf");
        queryJumpFiberInfoReqForApp.setBoxSide(0);
        queryJumpFiberInfoReqForApp.setFrameNo("1");
        queryJumpFiberInfoReqForApp.setDiscSide(0);
        queryJumpFiberInfoReqForApp.setDiscNo("1");
        queryJumpFiberInfoReqForApp.setPortNo("1");
        jumpFiberInfoService.assemblyJumpFiberInfoThisAndOppositeForApp(jumpFiberInfoRespForAppList,queryJumpFiberInfoReqForApp);
    }

    @Test
    public void operationJumpFiberInfoReqForApp(){

        when(logProcess.generateAddLogToCallParam(LogConstants.LOG_TYPE_OPERATE)).thenReturn(new AddLogBean());

        BatchOperationJumpFiberInfoForApp batchOperationJumpFiberInfoForApp = new BatchOperationJumpFiberInfoForApp();
        batchOperationJumpFiberInfoForApp.setUploadType(AppConstant.OPERATOR_TYPE_SAVE);
        List<OperationJumpFiberInfoReqForApp> operationJumpFiberInfoReqForAppList = new ArrayList<>();
        OperationJumpFiberInfoReqForApp operationJumpFiberInfoReqForApp = new OperationJumpFiberInfoReqForApp();
        operationJumpFiberInfoReqForApp.setJumpFiberId("khjglkjkhfkj");
        operationJumpFiberInfoReqForApp.setDeviceId("fdsfsqrewqreqw");
        operationJumpFiberInfoReqForApp.setBoxSide(0);
        operationJumpFiberInfoReqForApp.setFrameNo("1");
        operationJumpFiberInfoReqForApp.setDiscSide(0);
        operationJumpFiberInfoReqForApp.setDiscNo("1");
        operationJumpFiberInfoReqForApp.setPortNo("1");

        operationJumpFiberInfoReqForApp.setOppositeDeviceId("rewqrewtertrewre");
        operationJumpFiberInfoReqForApp.setOppositeBoxSide(0);
        operationJumpFiberInfoReqForApp.setOppositeFrameNo("1");
        operationJumpFiberInfoReqForApp.setOppositeDiscSide(0);
        operationJumpFiberInfoReqForApp.setOppositeDiscNo("1");
        operationJumpFiberInfoReqForApp.setOppositePortNo("2");

        operationJumpFiberInfoReqForAppList.add(operationJumpFiberInfoReqForApp);

        batchOperationJumpFiberInfoForApp.setOperationJumpFiberInfoReqForAppList(operationJumpFiberInfoReqForAppList);
        Result result = jumpFiberInfoService.operationJumpFiberInfoReqForApp(batchOperationJumpFiberInfoForApp);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());

        batchOperationJumpFiberInfoForApp.setUploadType(AppConstant.OPERATOR_TYPE_UPDATE);
        result = jumpFiberInfoService.operationJumpFiberInfoReqForApp(batchOperationJumpFiberInfoForApp);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());

        batchOperationJumpFiberInfoForApp.setUploadType(AppConstant.OPERATOR_TYPE_DELETE);
        result = jumpFiberInfoService.operationJumpFiberInfoReqForApp(batchOperationJumpFiberInfoForApp);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());

        List<OperationJumpFiberInfoReqForApp> operationJumpFiberInfoReqForAppList1 = new ArrayList<>();
        OperationJumpFiberInfoReqForApp operationJumpFiberInfoReqForApp1 = new OperationJumpFiberInfoReqForApp();
        operationJumpFiberInfoReqForApp1.setRfidCode("fdsfeqrewerewqres");
        operationJumpFiberInfoReqForApp1.setRfidStatus("0");
        operationJumpFiberInfoReqForApp1.setMarkType("0");

        operationJumpFiberInfoReqForApp1.setOppositeRfidCode("rewqrewtertrewre");
        operationJumpFiberInfoReqForApp1.setOppositeRfidStatus("0");
        operationJumpFiberInfoReqForApp1.setOppositeMarkType("0");

        operationJumpFiberInfoReqForAppList1.add(operationJumpFiberInfoReqForApp1);

        batchOperationJumpFiberInfoForApp.setUploadType(AppConstant.OPERATOR_TYPE_UPDATE_RFID_CODE);
        batchOperationJumpFiberInfoForApp.setOperationJumpFiberInfoReqForAppList(operationJumpFiberInfoReqForAppList1);

        List<JumpFiberInfo> jumpFiberInfoList = new ArrayList<>();
        JumpFiberInfo jumpFiberInfo = new JumpFiberInfo();
        jumpFiberInfo.setRfidCode("wreqrewrqew");
        jumpFiberInfo.setDeviceId("hfghgfdhgfh");
        jumpFiberInfoList.add(jumpFiberInfo);
        when(jumpFiberInfoDao.getJumpFiberInfoByRfidCode(anyString())).thenReturn(jumpFiberInfoList);

        result = jumpFiberInfoService.operationJumpFiberInfoReqForApp(batchOperationJumpFiberInfoForApp);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void exchangeJumpFiberInfoThisAndOppositeForApp(){
        JumpFiberInfoRespForApp jumpFiberInfoRespForApp = new JumpFiberInfoRespForApp();
        JumpFiberInfoRespForApp jumpFiberInfoRespForAppTemp = new JumpFiberInfoRespForApp();
        jumpFiberInfoRespForApp.setRfidCode("dfsafdsafdsadf");
        jumpFiberInfoRespForApp.setDeviceId("dfsafdsafdsadf");
        jumpFiberInfoRespForApp.setBoxSide(0);
        jumpFiberInfoRespForApp.setFrameNo("1");
        jumpFiberInfoRespForApp.setDiscSide(0);
        jumpFiberInfoRespForApp.setDiscNo("1");
        jumpFiberInfoRespForApp.setPortNo("1");
        jumpFiberInfoRespForApp.setRemark("remark");

        jumpFiberInfoRespForApp.setOppositeRfidCode("dfsafdsafdsadf");
        jumpFiberInfoRespForApp.setOppositeDeviceId("dfsafdsafdsadf");
        jumpFiberInfoRespForApp.setOppositeBoxSide(0);
        jumpFiberInfoRespForApp.setOppositeFrameNo("1");
        jumpFiberInfoRespForApp.setOppositeDiscSide(0);
        jumpFiberInfoRespForApp.setOppositeDiscNo("1");
        jumpFiberInfoRespForApp.setOppositePortNo("1");
        jumpFiberInfoRespForApp.setOppositeRemark("remark");

        jumpFiberInfoService.exchangeJumpFiberInfoThisAndOppositeForApp(jumpFiberInfoRespForApp,jumpFiberInfoRespForAppTemp);
    }

    /**
     * 日志
     */
    public void saveOperatorLog() {
        AddLogBean addLogBean = new AddLogBean();
        addLogBean.setTableName("ww");
        when(logProcess.generateAddLogToCallParam(LogConstants.LOG_TYPE_OPERATE)).thenReturn(addLogBean);
        when(systemLanguageUtil.querySystemLanguage()).thenReturn("CN");
        when(logProcess.addOperateLogBatchInfoToCall(any(), any())).thenReturn(new com.fiberhome.filink.clientcommon.utils.Result());
    }

}