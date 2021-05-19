package com.fiberhome.filink.rfid.controller.fibercore;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.exportapi.bean.ColumnInfo;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.rfid.req.fibercore.DeleteJumpFiberInfoReq;
import com.fiberhome.filink.rfid.req.fibercore.QueryJumpFiberInfoReq;
import com.fiberhome.filink.rfid.req.fibercore.app.BatchOperationJumpFiberInfoForApp;
import com.fiberhome.filink.rfid.req.fibercore.app.QueryJumpFiberInfoReqForApp;
import com.fiberhome.filink.rfid.service.fibercore.JumpFiberInfoService;
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
 * JumpFiberInfoControllerTest
 *
 * @author congcongsun2@wistronits.com
 * @since 2019/7/4
 */
@RunWith(MockitoJUnitRunner.class)
public class JumpFiberInfoControllerTest {
    /**
     * 被测试类
     */
    @InjectMocks
    private JumpFiberInfoController jumpFiberInfoController;
    @Mock
    private JumpFiberInfoService jumpFiberInfoService;

    /**
     * 返回结果封装
     */
    private Result resultResp = new Result();

    @Test
    public void queryJumpFiberInfoByPortInfo() {
        QueryJumpFiberInfoReq queryJumpFiberInfoReq = new QueryJumpFiberInfoReq();
        when(jumpFiberInfoService.queryJumpFiberInfoByPortInfo(queryJumpFiberInfoReq)).thenReturn(resultResp);
        Assert.assertEquals(resultResp, jumpFiberInfoController.queryJumpFiberInfoByPortInfo(queryJumpFiberInfoReq));
    }

    @Test
    public void deleteJumpFiberInfoById() {
        DeleteJumpFiberInfoReq deleteJumpFiberInfoReq = new DeleteJumpFiberInfoReq();
        when(jumpFiberInfoService.deleteJumpFiberInfoById(deleteJumpFiberInfoReq)).thenReturn(resultResp);
        Assert.assertEquals(resultResp, jumpFiberInfoController.deleteJumpFiberInfoById(deleteJumpFiberInfoReq));
    }

    @Test
    public void exportJumpFiberList() {
        ExportDto<QueryJumpFiberInfoReq> exportDto = new ExportDto<>();
        Assert.assertEquals(150000, jumpFiberInfoController.exportJumpFiberList(exportDto).getCode());
        List<ColumnInfo> columnInfoList = new ArrayList<>();
        ColumnInfo columnInfo = new ColumnInfo();
        columnInfo.setColumnName("Cc");
        columnInfo.setPropertyName("Zz");
        columnInfoList.add(columnInfo);
        exportDto.setColumnInfoList(columnInfoList);
        exportDto.setExcelType(1);
        when(jumpFiberInfoService.exportJumpFiberList(exportDto)).thenReturn(resultResp);
        Assert.assertEquals(resultResp, jumpFiberInfoController.exportJumpFiberList(exportDto));
    }

    @Test
    public void queryJumpFiberInfoByPortInfoForApp() {
        QueryJumpFiberInfoReqForApp queryJumpFiberInfoReqForApp = new QueryJumpFiberInfoReqForApp();
        when(jumpFiberInfoService.queryJumpFiberInfoByPortInfoForApp(queryJumpFiberInfoReqForApp)).thenReturn(resultResp);
        Assert.assertEquals(resultResp, jumpFiberInfoController.queryJumpFiberInfoByPortInfoForApp(queryJumpFiberInfoReqForApp));

    }

    @Test
    public void operationJumpFiberInfoReqForApp() {

        BatchOperationJumpFiberInfoForApp batchOperationJumpFiberInfoForApp = new BatchOperationJumpFiberInfoForApp();
        when(jumpFiberInfoService.operationJumpFiberInfoReqForApp(batchOperationJumpFiberInfoForApp)).thenReturn(resultResp);
        Assert.assertEquals(resultResp, jumpFiberInfoController.operationJumpFiberInfoReqForApp(batchOperationJumpFiberInfoForApp));

    }
}