package com.fiberhome.filink.rfid.controller.opticcable;

import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.exportapi.bean.ColumnInfo;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.rfid.req.opticcable.OpticCableSectionInfoReq;
import com.fiberhome.filink.rfid.req.opticcable.app.OperatorOpticCableSectionInfoReqForApp;
import com.fiberhome.filink.rfid.req.opticcable.app.OpticCableSectionInfoReqForApp;
import com.fiberhome.filink.rfid.service.opticcable.OpticCableSectionInfoService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 * 光缆段信息表 前端控制器测试
 *
 * @author congcongsun2@wistronits.com
 * @since 2019/6/28
 */
@RunWith(MockitoJUnitRunner.class)
public class OpticCableSectionInfoControllerTest {
    /**
     * 被测试类
     */
    @InjectMocks
    private OpticCableSectionInfoController opticCableSectionInfoController;
    /**
     * Mock OpticCableInfoService
     */
    @Mock
    private OpticCableSectionInfoService opticCableSectionInfoService;
    /**
     * 返回结果封装
     */
    private Result resultResp;
    /**
     * 返回工具类
     */
    private ResultUtils resultUtils = spy(ResultUtils.class);

    /**
     * 查询光缆段列表测试
     */
    @Test
    public void opticCableSectionById() {
        QueryCondition<OpticCableSectionInfoReq> queryCondition = new QueryCondition<>();
        when(opticCableSectionInfoService.selectOpticCableSection(queryCondition)).thenReturn(resultResp);
        Assert.assertEquals(resultResp, opticCableSectionInfoController.opticCableSectionById(queryCondition));
    }

    /**
     * 拓扑根据光缆id查询光缆段列表测试
     */
    @Test
    public void opticCableSectionByIdForTopology() {
        OpticCableSectionInfoReq opticCableSectionInfoReq = new OpticCableSectionInfoReq();
        when(opticCableSectionInfoService.opticCableSectionByIdForTopology(opticCableSectionInfoReq)).thenReturn(resultResp);
        Assert.assertEquals(resultResp, opticCableSectionInfoController.opticCableSectionByIdForTopology(opticCableSectionInfoReq));
    }

    /**
     * 查询光缆段列表选择器
     */
    @Test
    public void opticCableSectionByIdForPageSelection() {
        QueryCondition<OpticCableSectionInfoReq> queryCondition = new QueryCondition<>();
        when(opticCableSectionInfoService.selectOpticCableSection(queryCondition)).thenReturn(resultResp);
        Assert.assertEquals(resultResp, opticCableSectionInfoController.opticCableSectionByIdForPageSelection(queryCondition));
    }

    /**
     * 根据设施id查询光缆段列表
     */
    @Test
    public void selectOpticCableSectionByDeviceId() {
        String deviceId = "xx";
        when(opticCableSectionInfoService.selectOpticCableSectionByDeviceId(deviceId)).thenReturn(resultResp);
        Assert.assertEquals(resultResp, opticCableSectionInfoController.selectOpticCableSectionByDeviceId(deviceId));
    }

    /**
     * 通过光缆id查询所有设施信息
     */
    @Test
    public void queryDeviceInfoListByOpticCableId() {
        String opticCableId = "xx";
        when(opticCableSectionInfoService.queryDeviceInfoListByOpticCableId(opticCableId)).thenReturn(resultResp);
        Assert.assertEquals(resultResp, opticCableSectionInfoController.queryDeviceInfoListByOpticCableId(opticCableId));
    }

    /**
     * 通过光缆段id删除光缆段
     */
    @Test
    public void deleteOpticCableSectionByOpticCableSectionId() {
        String opticCableSectionId = "xx";
        when(opticCableSectionInfoService.deleteOpticCableSectionByOpticCableSectionId(opticCableSectionId)).thenReturn(resultResp);
        Assert.assertEquals(resultResp, opticCableSectionInfoController.deleteOpticCableSectionByOpticCableSectionId(opticCableSectionId));
    }

    /**
     * app请求光缆段基础信息
     */
    @Test
    public void queryOpticCableSectionListForApp() {
        OpticCableSectionInfoReqForApp opticCableSectionInfoReqForApp = new OpticCableSectionInfoReqForApp();
        when(opticCableSectionInfoService.queryOpticCableSectionListForApp(opticCableSectionInfoReqForApp)).thenReturn(resultResp);
        Assert.assertEquals(resultResp, opticCableSectionInfoController.queryOpticCableSectionListForApp(opticCableSectionInfoReqForApp));
    }

    /**
     * app请求光缆段基础信息
     */
    @Test
    public void uploadOpticCableSectionInfoForApp() {
        OperatorOpticCableSectionInfoReqForApp operatorOpticCableSectionInfoReqForApp = new OperatorOpticCableSectionInfoReqForApp();
        when(opticCableSectionInfoService.uploadOpticCableSectionInfoForApp(operatorOpticCableSectionInfoReqForApp)).thenReturn(resultResp);
        Assert.assertEquals(resultResp, opticCableSectionInfoController.uploadOpticCableSectionInfoForApp(operatorOpticCableSectionInfoReqForApp));
    }

    @Test
    public void exportOpticCableList() {
        ExportDto<OpticCableSectionInfoReq> exportDto = new ExportDto<>();
        Assert.assertEquals(150000, opticCableSectionInfoController.exportOpticCableList(exportDto).getCode());
        List<ColumnInfo> columnInfoList = new ArrayList<>();
        ColumnInfo columnInfo = new ColumnInfo();
        columnInfo.setColumnName("xx");
        columnInfoList.add(columnInfo);
        exportDto.setColumnInfoList(columnInfoList);
        exportDto.setExcelType(1);
        when(opticCableSectionInfoService.exportOpticCableSectionList(exportDto)).thenReturn(resultResp);
        Assert.assertEquals(resultResp, opticCableSectionInfoController.exportOpticCableList(exportDto));
    }

    @Test
    public void checkOpticCableSectionNameForApp() {
        OperatorOpticCableSectionInfoReqForApp operatorOpticCableSectionInfoReqForApp = new OperatorOpticCableSectionInfoReqForApp();
        Assert.assertEquals(150000, opticCableSectionInfoController.checkOpticCableSectionNameForApp(null).getCode());
        when(opticCableSectionInfoService.checkOpticCableSectionNameForApp(operatorOpticCableSectionInfoReqForApp)).thenReturn(true);
        Assert.assertEquals(150108, opticCableSectionInfoController.checkOpticCableSectionNameForApp(operatorOpticCableSectionInfoReqForApp).getCode());
        when(opticCableSectionInfoService.checkOpticCableSectionNameForApp(operatorOpticCableSectionInfoReqForApp)).thenReturn(false);
        Assert.assertEquals(0, opticCableSectionInfoController.checkOpticCableSectionNameForApp(operatorOpticCableSectionInfoReqForApp).getCode());
    }
}