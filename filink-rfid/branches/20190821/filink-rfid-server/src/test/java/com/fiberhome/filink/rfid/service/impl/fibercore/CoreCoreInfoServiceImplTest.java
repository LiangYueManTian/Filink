package com.fiberhome.filink.rfid.service.impl.fibercore;

import com.fiberhome.filink.clientcommon.utils.Result;
import com.fiberhome.filink.deviceapi.api.DeviceFeign;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.constant.LogConstants;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.rfid.dao.fibercore.CoreCoreInfoDao;
import com.fiberhome.filink.rfid.req.fibercore.CoreCoreInfoReq;
import com.fiberhome.filink.rfid.req.fibercore.InsertCoreCoreInfoReq;
import com.fiberhome.filink.rfid.req.fibercore.app.BatchOperationCoreCoreInfoReqForApp;
import com.fiberhome.filink.rfid.req.fibercore.app.OperationCoreCoreInfoReqForApp;
import com.fiberhome.filink.rfid.req.fibercore.app.QueryCoreCoreInfoReqForApp;
import com.fiberhome.filink.rfid.service.impl.UpdateOpticCableSectionStatus;
import com.fiberhome.filink.rfid.service.opticcable.OpticCableSectionInfoService;
import com.fiberhome.filink.rfid.utils.RfidServerPermission;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import com.fiberhome.filink.userapi.api.UserFeign;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * CoreCoreInfoServiceImplTest
 *
 * @author congcongsun2@wistronits.com
 * @since 2019/7/4
 */
@RunWith(MockitoJUnitRunner.class)
public class CoreCoreInfoServiceImplTest {


    @InjectMocks
    private CoreCoreInfoServiceImpl coreCoreInfoService;

    @Mock
    private CoreCoreInfoDao coreCoreInfoDao;
    /**
     * 远程调用日志服务
     */
    @Mock
    private LogProcess logProcess;
    /**
     * 远程调用SystemLanguage服务
     */
    @Mock
    private SystemLanguageUtil systemLanguageUtil;
    /**
     * 用户Feign
     */
    @Mock
    private UserFeign userFeign;
    /**
     * 设施Feign
     */
    @Mock
    private DeviceFeign deviceFeign;
    /**
     * 智能标签业务权限rfidServerPermission
     */
    @Mock
    private RfidServerPermission rfidServerPermission;

    @Mock
    private OpticCableSectionInfoService opticCableSectionInfoService;

    /**
     * 注入updateOpticCableSectionStatusAsync接口
     */
    @Mock
    private UpdateOpticCableSectionStatus updateOpticCableSectionStatusAsync;


    @Test
    public void queryCoreCoreInfo() {
        CoreCoreInfoReq coreCoreInfoReq = new CoreCoreInfoReq();
        when(coreCoreInfoDao.queryCoreCoreInfo(coreCoreInfoReq)).thenReturn(new ArrayList<>());
        Assert.assertEquals(0, coreCoreInfoService.queryCoreCoreInfo(coreCoreInfoReq).getCode());
    }

    @Test
    public void queryCoreCoreInfoNotInDevice() {
        CoreCoreInfoReq coreCoreInfoReq = new CoreCoreInfoReq();
        when(coreCoreInfoDao.queryCoreCoreInfoNotInDevice(coreCoreInfoReq)).thenReturn(new ArrayList<>());
        Assert.assertEquals(0, coreCoreInfoService.queryCoreCoreInfoNotInDevice(coreCoreInfoReq).getCode());
    }

    @Test
    public void saveCoreCoreInfo() {
        Assert.assertEquals(150000, coreCoreInfoService.saveCoreCoreInfo(null).getCode());
        List<InsertCoreCoreInfoReq> insertCoreCoreInfoReqList = new ArrayList<>();
        InsertCoreCoreInfoReq insertCoreCoreInfoReq = new InsertCoreCoreInfoReq();
        insertCoreCoreInfoReqList.add(insertCoreCoreInfoReq);
        Assert.assertEquals(150000, coreCoreInfoService.saveCoreCoreInfo(insertCoreCoreInfoReqList).getCode());

        insertCoreCoreInfoReq.setIntermediateNodeDeviceId("Xx");
        insertCoreCoreInfoReq.setOppositeResource("zz");
        insertCoreCoreInfoReq.setResource("cc");
        insertCoreCoreInfoReq.setOppositeCableCoreNo("1");
        insertCoreCoreInfoReq.setCableCoreNo("1");
//        insertCoreCoreInfoReqList.remove(0);
        insertCoreCoreInfoReqList.add(insertCoreCoreInfoReq);

        when(rfidServerPermission.getPermissionsInfoFoRfidServer(any(), any())).thenReturn(false);
//        Assert.assertEquals(0, coreCoreInfoService.saveCoreCoreInfo(insertCoreCoreInfoReqList).getCode());

//        insertCoreCoreInfoReqList.add(insertCoreCoreInfoReq);

        when(logProcess.generateAddLogToCallParam(LogConstants.LOG_TYPE_OPERATE)).thenReturn(new AddLogBean());
//        Assert.assertEquals(0, coreCoreInfoService.saveCoreCoreInfo(insertCoreCoreInfoReqList).getCode());

        saveOperatorLog();
        Assert.assertEquals(0, coreCoreInfoService.saveCoreCoreInfo(insertCoreCoreInfoReqList).getCode());

        when(rfidServerPermission.getPermissionsInfoFoRfidServer(any(), any())).thenReturn(true);
        Assert.assertEquals(1500105, coreCoreInfoService.saveCoreCoreInfo(insertCoreCoreInfoReqList).getCode());

        List<InsertCoreCoreInfoReq> insertCoreCoreInfoReqList1 = new ArrayList<>();
        InsertCoreCoreInfoReq insertCoreCoreInfoReq1 = new InsertCoreCoreInfoReq();
        insertCoreCoreInfoReq1.setIntermediateNodeDeviceId("Xx");
        insertCoreCoreInfoReq1.setOppositeResource("zz");
        insertCoreCoreInfoReq1.setResource("cc");
        insertCoreCoreInfoReqList1.add(insertCoreCoreInfoReq1);

        when(rfidServerPermission.getPermissionsInfoFoRfidServer(any(), any())).thenReturn(false);

        when(logProcess.generateAddLogToCallParam(LogConstants.LOG_TYPE_OPERATE)).thenReturn(new AddLogBean());
        saveOperatorLog();
        Assert.assertEquals(0, coreCoreInfoService.saveCoreCoreInfo(insertCoreCoreInfoReqList1).getCode());

    }

    @Test
    public void queryCoreCoreInfoForApp() {
        QueryCoreCoreInfoReqForApp queryCoreCoreInfoReqForApp = new QueryCoreCoreInfoReqForApp();
        when(coreCoreInfoDao.queryCoreCoreInfoForApp(queryCoreCoreInfoReqForApp)).thenReturn(new ArrayList<>());
        Assert.assertEquals(0, coreCoreInfoService.queryCoreCoreInfoForApp(queryCoreCoreInfoReqForApp).getCode());
    }

    @Test
    public void operationCoreCoreInfoReqForApp() {
        BatchOperationCoreCoreInfoReqForApp batchOperationCoreCoreInfoReqForApp = new BatchOperationCoreCoreInfoReqForApp();
        Assert.assertEquals(150000, coreCoreInfoService.operationCoreCoreInfoReqForApp(batchOperationCoreCoreInfoReqForApp).getCode());

        OperationCoreCoreInfoReqForApp operationCoreCoreInfoReqForApp = new OperationCoreCoreInfoReqForApp();
        operationCoreCoreInfoReqForApp.setCoreCoreId("xx");
        operationCoreCoreInfoReqForApp.setResource("cc");
        operationCoreCoreInfoReqForApp.setOppositeResource("zz");
        operationCoreCoreInfoReqForApp.setOppositeCableCoreNo("1");
        operationCoreCoreInfoReqForApp.setCableCoreNo("1");
        List<OperationCoreCoreInfoReqForApp> operationCoreCoreInfoReqForAppList = new ArrayList<>();
        operationCoreCoreInfoReqForAppList.add(operationCoreCoreInfoReqForApp);
        saveOperatorLog();
        batchOperationCoreCoreInfoReqForApp.setOperationCoreCoreInfoReqForAppList(operationCoreCoreInfoReqForAppList);

        batchOperationCoreCoreInfoReqForApp.setUploadType("0");
        Assert.assertEquals(0, coreCoreInfoService.operationCoreCoreInfoReqForApp(batchOperationCoreCoreInfoReqForApp).getCode());

        batchOperationCoreCoreInfoReqForApp.setUploadType("1");
        Assert.assertEquals(0, coreCoreInfoService.operationCoreCoreInfoReqForApp(batchOperationCoreCoreInfoReqForApp).getCode());

        batchOperationCoreCoreInfoReqForApp.setUploadType("2");
        Assert.assertEquals(0, coreCoreInfoService.operationCoreCoreInfoReqForApp(batchOperationCoreCoreInfoReqForApp).getCode());

        batchOperationCoreCoreInfoReqForApp.setUploadType("3");
        Assert.assertEquals(150000, coreCoreInfoService.operationCoreCoreInfoReqForApp(batchOperationCoreCoreInfoReqForApp).getCode());

    }


    @Test
    public void saveOperatorLogForApp() {
    }

    /**
     * 日志
     */
    public void saveOperatorLog() {
        AddLogBean addLogBean = new AddLogBean();
        addLogBean.setTableName("ww");
        when(logProcess.generateAddLogToCallParam(LogConstants.LOG_TYPE_OPERATE)).thenReturn(addLogBean);
        when(systemLanguageUtil.querySystemLanguage()).thenReturn("CN");
        when(logProcess.addOperateLogBatchInfoToCall(any(), any())).thenReturn(new Result());
    }
}