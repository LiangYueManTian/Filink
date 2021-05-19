package com.fiberhome.filink.rfid.controller.rfid;


import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.rfid.req.rfid.InsertRfidInfoReq;
import com.fiberhome.filink.rfid.req.rfid.QueryRfidInfoReq;
import com.fiberhome.filink.rfid.service.rfid.RfidInfoService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

/**
 * RfidInfoControllerTest
 *
 * @author congcongsun2@wistronits.com
 * @since 2019/7/5
 */
@RunWith(MockitoJUnitRunner.class)
public class RfidInfoControllerTest {

    @InjectMocks
    private RfidInfoController rfidInfoController;
    @Mock
    private RfidInfoService rfidInfoService;
    /**
     * 返回结果封装
     */
    private Result resultResp = new Result();

    @Test
    public void queryRfIdInfo() {
        QueryRfidInfoReq queryRfidInfoReq = new QueryRfidInfoReq();
        when(rfidInfoService.queryRfidInfo(queryRfidInfoReq)).thenReturn(resultResp);
        Assert.assertEquals(0, rfidInfoController.queryRfIdInfo(queryRfidInfoReq).getCode());
    }

    @Test
    public void addRfIdInfo() {
        List<InsertRfidInfoReq> insertRfidInfoReqList = new ArrayList<>();
        when(rfidInfoService.addRfidInfo(insertRfidInfoReqList)).thenReturn(resultResp);
        Assert.assertEquals(0, rfidInfoController.addRfIdInfo(insertRfidInfoReqList).getCode());

    }

    @Test
    public void deleteRfIdInfo() {
        Set<String> rfIdCodeList = new HashSet<>();
        when(rfidInfoService.deleteRfidInfo(rfIdCodeList)).thenReturn(resultResp);
        Assert.assertEquals(0, rfidInfoController.deleteRfIdInfo(rfIdCodeList).getCode());
    }

    @Test
    public void checkRfidCodeListIsExist() {
        Set<String> rfIdCodeList = new HashSet<>();
        when(rfidInfoService.checkRfidCodeListIsExist(rfIdCodeList)).thenReturn(true);
        Assert.assertTrue(rfidInfoController.checkRfidCodeListIsExist(rfIdCodeList));
    }

    @Test
    public void deleteRfidInfoByDeviceId() {
        when(rfidInfoService.deleteRfidInfoByDeviceId(anyString())).thenReturn(0);
        Assert.assertEquals(0,rfidInfoController.deleteRfidInfoByDeviceId("dfsadfdsa"));
    }
}