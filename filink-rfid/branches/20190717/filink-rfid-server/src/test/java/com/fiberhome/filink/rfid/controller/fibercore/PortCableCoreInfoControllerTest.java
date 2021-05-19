package com.fiberhome.filink.rfid.controller.fibercore;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.rfid.req.fibercore.InsertPortCableCoreInfoReq;
import com.fiberhome.filink.rfid.req.fibercore.PortCableCoreInfoReq;
import com.fiberhome.filink.rfid.req.fibercore.SavePortCableCoreInfoReq;
import com.fiberhome.filink.rfid.req.fibercore.UpdatePortCableCoreInfoReq;
import com.fiberhome.filink.rfid.req.fibercore.app.BatchOperationPortCableCoreInfoReqForApp;
import com.fiberhome.filink.rfid.req.fibercore.app.QueryPortCableCoreInfoReqForApp;
import com.fiberhome.filink.rfid.service.fibercore.PortCableCoreInfoService;
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
 * PortCableCoreInfoControllerTest
 *
 * @author congcongsun2@wistronits.com
 * @since 2019/7/4
 */
@RunWith(MockitoJUnitRunner.class)
public class PortCableCoreInfoControllerTest {
    /**
     * 被测试类
     */
    @InjectMocks
    private PortCableCoreInfoController portCableCoreInfoController;
    @Mock
    private PortCableCoreInfoService portCableCoreInfoService;
    /**
     * 返回结果封装
     */
    private Result resultResp = new Result();

    @Test
    public void getPortCableCoreInfo() {
        PortCableCoreInfoReq portCableCoreInfoReq = new PortCableCoreInfoReq();
        when(portCableCoreInfoService.getPortCableCoreInfo(portCableCoreInfoReq)).thenReturn(resultResp);
        Assert.assertEquals(resultResp, portCableCoreInfoController.getPortCableCoreInfo(portCableCoreInfoReq));


    }

    @Test
    public void getPortCableCoreInfoNotInDevice() {
        PortCableCoreInfoReq portCableCoreInfoReq = new PortCableCoreInfoReq();
        when(portCableCoreInfoService.getPortCableCoreInfoNotInDevice(portCableCoreInfoReq)).thenReturn(resultResp);
        Assert.assertEquals(resultResp, portCableCoreInfoController.getPortCableCoreInfoNotInDevice(portCableCoreInfoReq));
    }

    @Test
    public void savePortCableCoreInfo() {
        SavePortCableCoreInfoReq savePortCableCoreInfoReq = new SavePortCableCoreInfoReq();
        List<InsertPortCableCoreInfoReq> insertPortCableCoreInfoReqList = new ArrayList<>();
        List<UpdatePortCableCoreInfoReq> updatePortCableCoreInfoReqList = new ArrayList<>();
        savePortCableCoreInfoReq.setInsertPortCableCoreInfoReqList(insertPortCableCoreInfoReqList);
        savePortCableCoreInfoReq.setUpdatePortCableCoreInfoReqList(updatePortCableCoreInfoReqList);
        when(portCableCoreInfoService.savePortCableCoreInfo(savePortCableCoreInfoReq.getInsertPortCableCoreInfoReqList(), savePortCableCoreInfoReq.getUpdatePortCableCoreInfoReqList())).thenReturn(resultResp);
        Assert.assertEquals(resultResp, portCableCoreInfoController.savePortCableCoreInfo(savePortCableCoreInfoReq));

    }

    @Test
    public void queryPortCableCoreInfoForApp() {
        QueryPortCableCoreInfoReqForApp queryPortCableCoreInfoReqForApp = new QueryPortCableCoreInfoReqForApp();
        when(portCableCoreInfoService.queryPortCableCoreInfoForApp(queryPortCableCoreInfoReqForApp)).thenReturn(resultResp);
        Assert.assertEquals(resultResp, portCableCoreInfoController.queryPortCableCoreInfoForApp(queryPortCableCoreInfoReqForApp));

    }

    @Test
    public void operationPortCableCoreInfoForApp() {
        BatchOperationPortCableCoreInfoReqForApp batchOperationPortCableCoreInfoReqForApp = new BatchOperationPortCableCoreInfoReqForApp();
        when(portCableCoreInfoService.operationPortCableCoreInfoForApp(batchOperationPortCableCoreInfoReqForApp)).thenReturn(resultResp);
        Assert.assertEquals(resultResp, portCableCoreInfoController.operationPortCableCoreInfoForApp(batchOperationPortCableCoreInfoReqForApp));
    }
}