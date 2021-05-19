package com.fiberhome.filink.rfid.controller.fibercore;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.rfid.req.fibercore.CoreCoreInfoReq;
import com.fiberhome.filink.rfid.req.fibercore.InsertCoreCoreInfoReq;
import com.fiberhome.filink.rfid.req.fibercore.app.BatchOperationCoreCoreInfoReqForApp;
import com.fiberhome.filink.rfid.req.fibercore.app.QueryCoreCoreInfoReqForApp;
import com.fiberhome.filink.rfid.service.fibercore.CoreCoreInfoService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

/**
 * CoreCoreInfoControllerTest
 *
 * @author congcongsun2@wistronits.com
 * @since 2019/7/4
 */
@RunWith(MockitoJUnitRunner.class)
public class CoreCoreInfoControllerTest {
    /**
     * 被测试类
     */
    @InjectMocks
    private CoreCoreInfoController coreCoreInfoController;
    @Mock
    private CoreCoreInfoService coreCoreInfoService;

    /**
     * 返回结果封装
     */
    private Result resultResp = new Result();

    @Test
    public void queryCoreCoreInfo() {
        CoreCoreInfoReq coreCoreInfoReq = new CoreCoreInfoReq();
        when(coreCoreInfoService.queryCoreCoreInfo(coreCoreInfoReq)).thenReturn(resultResp);
        Assert.assertEquals(resultResp, coreCoreInfoController.queryCoreCoreInfo(coreCoreInfoReq));
    }

    @Test
    public void queryCoreCoreInfoNotInDevice() {
        CoreCoreInfoReq coreCoreInfoReq = new CoreCoreInfoReq();
        when(coreCoreInfoService.queryCoreCoreInfoNotInDevice(coreCoreInfoReq)).thenReturn(resultResp);
        Assert.assertEquals(resultResp, coreCoreInfoController.queryCoreCoreInfoNotInDevice(coreCoreInfoReq));
    }

    @Test
    public void saveCoreCoreInfo() {
        List<InsertCoreCoreInfoReq> insertCoreCoreInfoReqList = new ArrayList<>();
        when(coreCoreInfoService.saveCoreCoreInfo(insertCoreCoreInfoReqList)).thenReturn(resultResp);
        Assert.assertEquals(resultResp, coreCoreInfoController.saveCoreCoreInfo(insertCoreCoreInfoReqList));

    }

    @Test
    public void queryCoreCoreInfoForApp() {
        QueryCoreCoreInfoReqForApp queryCoreCoreInfoReqForApp = new QueryCoreCoreInfoReqForApp();
        when(coreCoreInfoService.queryCoreCoreInfoForApp(queryCoreCoreInfoReqForApp)).thenReturn(resultResp);
        Assert.assertEquals(resultResp, coreCoreInfoController.queryCoreCoreInfoForApp(queryCoreCoreInfoReqForApp));

    }

    @Test
    public void operationCoreCoreInfoReqForApp() {
        BatchOperationCoreCoreInfoReqForApp batchOperationCoreCoreInfoReqForApp = new BatchOperationCoreCoreInfoReqForApp();
        when(coreCoreInfoService.operationCoreCoreInfoReqForApp(batchOperationCoreCoreInfoReqForApp)).thenReturn(resultResp);
        Assert.assertEquals(resultResp, coreCoreInfoController.operationCoreCoreInfoReqForApp(batchOperationCoreCoreInfoReqForApp));
    }
}