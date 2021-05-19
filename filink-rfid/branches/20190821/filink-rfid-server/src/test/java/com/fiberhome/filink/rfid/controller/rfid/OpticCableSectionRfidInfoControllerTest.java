package com.fiberhome.filink.rfid.controller.rfid;

import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.rfid.req.rfid.DeleteOpticCableSectionRfidInfoReq;
import com.fiberhome.filink.rfid.req.rfid.OpticCableSectionRfidInfoReq;
import com.fiberhome.filink.rfid.req.rfid.UpdateOpticCableSectionRfidInfoReq;
import com.fiberhome.filink.rfid.req.rfid.app.OpticCableSectionRfidInfoReqApp;
import com.fiberhome.filink.rfid.req.rfid.app.UploadOpticCableSectionRfidInfoReqApp;
import com.fiberhome.filink.rfid.service.rfid.OpticCableSectionRfidInfoService;
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
 * OpticCableSectionRfidInfoControllerTest
 *
 * @author congcongsun2@wistronits.com
 * @since 2019/7/5
 */
@RunWith(MockitoJUnitRunner.class)
public class OpticCableSectionRfidInfoControllerTest {

    @InjectMocks
    private OpticCableSectionRfidInfoController opticCableSectionRfidInfoController;
    @Mock
    private OpticCableSectionRfidInfoService opticCableSectionRfidInfoService;
    /**
     * 返回结果封装
     */
    private Result resultResp = new Result();

    @Test
    public void opticCableSectionRfidInfoById() {
        QueryCondition<OpticCableSectionRfidInfoReq> queryCondition = new QueryCondition<>();
        when(opticCableSectionRfidInfoService.opticCableSectionRfidInfoById(queryCondition)).thenReturn(resultResp);
        Assert.assertEquals(0,opticCableSectionRfidInfoController.opticCableSectionRfidInfoById(queryCondition).getCode());
    }

    @Test
    public void queryOpticCableSectionRfidInfoByOpticCableSectionId() {
        String opticCableSectionId = "zz";
        when(opticCableSectionRfidInfoService.queryOpticCableSectionRfidInfoByOpticCableSectionId(opticCableSectionId)).thenReturn(resultResp);
        Assert.assertEquals(0,opticCableSectionRfidInfoController.queryOpticCableSectionRfidInfoByOpticCableSectionId(opticCableSectionId).getCode());
    }

    @Test
    public void queryOpticCableSectionRfidInfoByOpticCableId() {
        String opticCableId = "zz";
        when(opticCableSectionRfidInfoService.queryOpticCableSectionRfidInfoByOpticCableId(opticCableId)).thenReturn(resultResp);
        Assert.assertEquals(0,opticCableSectionRfidInfoController.queryOpticCableSectionRfidInfoByOpticCableId(opticCableId).getCode());

    }

    @Test
    public void queryOpticCableSectionRfidInfo() {
        OpticCableSectionRfidInfoReqApp opticCableSectionRfidInfoReqApp = new OpticCableSectionRfidInfoReqApp();
        when(opticCableSectionRfidInfoService.queryOpticCableSectionRfidInfo(opticCableSectionRfidInfoReqApp)).thenReturn(resultResp);
        Assert.assertEquals(0,opticCableSectionRfidInfoController.queryOpticCableSectionRfidInfo(opticCableSectionRfidInfoReqApp).getCode());

    }

    @Test
    public void uploadOpticCableSectionRfidInfo() {
        UploadOpticCableSectionRfidInfoReqApp uploadOpticCableSectionRfidInfoReqApp = new UploadOpticCableSectionRfidInfoReqApp();
        when(opticCableSectionRfidInfoService.uploadOpticCableSectionRfidInfo(uploadOpticCableSectionRfidInfoReqApp)).thenReturn(resultResp);
        Assert.assertEquals(0,opticCableSectionRfidInfoController.uploadOpticCableSectionRfidInfo(uploadOpticCableSectionRfidInfoReqApp).getCode());

    }

    @Test
    public void updateOpticCableSectionRfidInfoPositionById() {
        List<UpdateOpticCableSectionRfidInfoReq> updateOpticCableSectionRfidInfoReqList = new ArrayList<>();
        when(opticCableSectionRfidInfoService.updateOpticCableSectionRfidInfoPositionById(updateOpticCableSectionRfidInfoReqList)).thenReturn(resultResp);
        Assert.assertEquals(0,opticCableSectionRfidInfoController.updateOpticCableSectionRfidInfoPositionById(updateOpticCableSectionRfidInfoReqList).getCode());

    }

    @Test
    public void deleteOpticCableSectionRfidInfoById() {
        DeleteOpticCableSectionRfidInfoReq deleteOpticCableSectionRfidInfoReq = new DeleteOpticCableSectionRfidInfoReq();
        when(opticCableSectionRfidInfoService.deleteOpticCableSectionRfidInfoById(deleteOpticCableSectionRfidInfoReq)).thenReturn(resultResp);
        Assert.assertEquals(0,opticCableSectionRfidInfoController.deleteOpticCableSectionRfidInfoById(deleteOpticCableSectionRfidInfoReq).getCode());

    }
}