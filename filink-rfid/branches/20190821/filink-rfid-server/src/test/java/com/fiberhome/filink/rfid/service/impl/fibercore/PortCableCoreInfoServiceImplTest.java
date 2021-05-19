package com.fiberhome.filink.rfid.service.impl.fibercore;

import com.fiberhome.filink.clientcommon.utils.Result;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.constant.LogConstants;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.rfid.dao.fibercore.PortCableCoreInfoDao;
import com.fiberhome.filink.rfid.req.fibercore.InsertPortCableCoreInfoReq;
import com.fiberhome.filink.rfid.req.fibercore.PortCableCoreInfoReq;
import com.fiberhome.filink.rfid.req.fibercore.UpdatePortCableCoreInfoReq;
import com.fiberhome.filink.rfid.req.fibercore.app.BatchOperationPortCableCoreInfoReqForApp;
import com.fiberhome.filink.rfid.req.fibercore.app.OperationPortCableCoreInfoReqForApp;
import com.fiberhome.filink.rfid.req.fibercore.app.QueryPortCableCoreInfoReqForApp;
import com.fiberhome.filink.rfid.service.impl.UpdateOpticCableSectionStatus;
import com.fiberhome.filink.rfid.service.impl.UpdatePortStatusAsync;
import com.fiberhome.filink.rfid.service.opticcable.OpticCableSectionInfoService;
import com.fiberhome.filink.rfid.service.template.TemplateService;
import com.fiberhome.filink.rfid.utils.RfidServerPermission;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
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
 * PortCableCoreInfoServiceImplTest
 *
 * @author congcongsun2@wistronits.com
 * @since 2019/7/4
 */
@RunWith(MockitoJUnitRunner.class)
public class PortCableCoreInfoServiceImplTest {
    /**
     * 被测试类
     */
    @InjectMocks
    private PortCableCoreInfoServiceImpl portCableCoreInfoService;
    @Mock
    private PortCableCoreInfoDao portCableCoreInfoDao;
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
     * 智能标签业务权限rfidServerPermission
     */
    @Mock
    private RfidServerPermission rfidServerPermission;

    @Mock
    private OpticCableSectionInfoService opticCableSectionInfoService;

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


    @Test
    public void getPortCableCoreInfo() {
        PortCableCoreInfoReq portCableCoreInfoReq = new PortCableCoreInfoReq();
        when(portCableCoreInfoDao.getPortCableCoreInfo(portCableCoreInfoReq)).thenReturn(new ArrayList<>());
        Assert.assertEquals(0, portCableCoreInfoService.getPortCableCoreInfo(portCableCoreInfoReq).getCode());
    }

    @Test
    public void getPortCableCoreInfoNotInDevice() {
        PortCableCoreInfoReq portCableCoreInfoReq = new PortCableCoreInfoReq();
        when(portCableCoreInfoDao.getPortCableCoreInfoNotInDevice(portCableCoreInfoReq)).thenReturn(new ArrayList<>());
        Assert.assertEquals(0, portCableCoreInfoService.getPortCableCoreInfoNotInDevice(portCableCoreInfoReq).getCode());

    }

    @Test
    public void savePortCableCoreInfo() {
        java.util.List<InsertPortCableCoreInfoReq> insertPortCableCoreInfoReqList = new ArrayList<>();
        List<UpdatePortCableCoreInfoReq> updatePortCableCoreInfoReqList = new ArrayList<>();
        Assert.assertEquals(150000, portCableCoreInfoService.savePortCableCoreInfo(insertPortCableCoreInfoReqList, updatePortCableCoreInfoReqList).getCode());

        InsertPortCableCoreInfoReq insertPortCableCoreInfoReq = new InsertPortCableCoreInfoReq();
        insertPortCableCoreInfoReqList.add(insertPortCableCoreInfoReq);
        Assert.assertEquals(150000, portCableCoreInfoService.savePortCableCoreInfo(insertPortCableCoreInfoReqList, updatePortCableCoreInfoReqList).getCode());

        insertPortCableCoreInfoReq.setResourceDeviceId("zz");
        insertPortCableCoreInfoReq.setResourceBoxSide(2);
        insertPortCableCoreInfoReq.setResourceFrameNo("xx");
        insertPortCableCoreInfoReq.setOppositeResource("cc");
        insertPortCableCoreInfoReq.setResourceDiscSide(1);
//        insertPortCableCoreInfoReq.setPortNo("1");
//        insertPortCableCoreInfoReq.setOppositeCableCoreNo("1");
//        insertPortCableCoreInfoReqList.remove(0);
        insertPortCableCoreInfoReqList.add(insertPortCableCoreInfoReq);
        when(rfidServerPermission.getPermissionsInfoFoRfidServer(any(), any())).thenReturn(true);
        Assert.assertEquals(1500105, portCableCoreInfoService.savePortCableCoreInfo(insertPortCableCoreInfoReqList, updatePortCableCoreInfoReqList).getCode());

        when(rfidServerPermission.getPermissionsInfoFoRfidServer(any(), any())).thenReturn(false);
        when(portCableCoreInfoDao.deletePortCoreInfoByResourceAndDevice(any())).thenReturn(1);

        //更新端口业务状态
        updatePortStatusAsync.updatePortBindingState(insertPortCableCoreInfoReqList,updatePortCableCoreInfoReqList);

        //批量更新端口成端状态
        updatePortStatusAsync.batchUpdatePortCableState(insertPortCableCoreInfoReqList,updatePortCableCoreInfoReqList);

        //更新光缆段使用状态
        updateOpticCableSectionStatusAsync.updateOpticCableSectionStateByPortCableCore(insertPortCableCoreInfoReqList,updatePortCableCoreInfoReqList);

        insertPortCableCoreInfoReqList.remove(0);
        InsertPortCableCoreInfoReq insertPortCableCoreInfoReq1 = new InsertPortCableCoreInfoReq();
        insertPortCableCoreInfoReq1.setResourceDeviceId("zz");
        insertPortCableCoreInfoReq1.setResourceBoxSide(2);
        insertPortCableCoreInfoReq1.setResourceFrameNo("xx");
        insertPortCableCoreInfoReq1.setOppositeResource("cc");
        insertPortCableCoreInfoReq1.setResourceDiscSide(1);
        insertPortCableCoreInfoReq1.setPortNo("1");
        insertPortCableCoreInfoReq1.setOppositeCableCoreNo("1");
        insertPortCableCoreInfoReqList.add(insertPortCableCoreInfoReq1);

        saveOperatorLog();
        Assert.assertEquals(0, portCableCoreInfoService.savePortCableCoreInfo(insertPortCableCoreInfoReqList, updatePortCableCoreInfoReqList).getCode());

//        insertPortCableCoreInfoReqList.add(insertPortCableCoreInfoReq);
//        saveOperatorLog();
//        Assert.assertEquals(0, portCableCoreInfoService.savePortCableCoreInfo(insertPortCableCoreInfoReqList, updatePortCableCoreInfoReqList).getCode());


    }

    @Test
    public void queryPortCableCoreInfoForApp() {
        QueryPortCableCoreInfoReqForApp queryPortCableCoreInfoReqForApp = new QueryPortCableCoreInfoReqForApp();
        when(portCableCoreInfoDao.queryPortCableCoreInfoForApp(queryPortCableCoreInfoReqForApp)).thenReturn(new ArrayList<>());
        Assert.assertEquals(0, portCableCoreInfoService.queryPortCableCoreInfoForApp(queryPortCableCoreInfoReqForApp).getCode());

    }

    @Test
    public void operationPortCableCoreInfoForApp() {
        BatchOperationPortCableCoreInfoReqForApp batchOperationPortCableCoreInfoReqForApp = new BatchOperationPortCableCoreInfoReqForApp();
        Assert.assertEquals(150000, portCableCoreInfoService.operationPortCableCoreInfoForApp(batchOperationPortCableCoreInfoReqForApp).getCode());

        //日志
        saveOperatorLog();

        OperationPortCableCoreInfoReqForApp operationPortCableCoreInfoReqForApp = new OperationPortCableCoreInfoReqForApp();
        operationPortCableCoreInfoReqForApp.setUploadType("2");
        operationPortCableCoreInfoReqForApp.setOppositeCableCoreNo("cc");
        List<OperationPortCableCoreInfoReqForApp> operationPortCableCoreInfoReqForAppList = new ArrayList<>();
        operationPortCableCoreInfoReqForAppList.add(operationPortCableCoreInfoReqForApp);
        batchOperationPortCableCoreInfoReqForApp.setOperationPortCableCoreInfoReqForAppList(operationPortCableCoreInfoReqForAppList);
        batchOperationPortCableCoreInfoReqForApp.setUploadType("0");

        updateOpticCableSectionStatusAsync.updateOpticCableSectionStateByPortCableCoreForApp(batchOperationPortCableCoreInfoReqForApp);

        Assert.assertEquals(0, portCableCoreInfoService.operationPortCableCoreInfoForApp(batchOperationPortCableCoreInfoReqForApp).getCode());

        batchOperationPortCableCoreInfoReqForApp.setUploadType("1");
        Assert.assertEquals(0, portCableCoreInfoService.operationPortCableCoreInfoForApp(batchOperationPortCableCoreInfoReqForApp).getCode());

        batchOperationPortCableCoreInfoReqForApp.setUploadType("2");
        Assert.assertEquals(0, portCableCoreInfoService.operationPortCableCoreInfoForApp(batchOperationPortCableCoreInfoReqForApp).getCode());

        batchOperationPortCableCoreInfoReqForApp.setUploadType("3");
        Assert.assertEquals(150000, portCableCoreInfoService.operationPortCableCoreInfoForApp(batchOperationPortCableCoreInfoReqForApp).getCode());


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