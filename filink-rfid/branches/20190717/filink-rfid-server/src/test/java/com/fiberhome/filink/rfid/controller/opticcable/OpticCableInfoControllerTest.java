package com.fiberhome.filink.rfid.controller.opticcable;

import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.exportapi.bean.ColumnInfo;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.rfid.bean.opticcable.OpticCableInfo;
import com.fiberhome.filink.rfid.req.opticcable.InsertOpticCableInfoReq;
import com.fiberhome.filink.rfid.req.opticcable.OpticCableInfoReq;
import com.fiberhome.filink.rfid.req.opticcable.UpdateOpticCableInfoReq;
import com.fiberhome.filink.rfid.service.opticcable.OpticCableInfoService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 * 光缆信息表 前端控制器测试
 *
 * @author congcongsun2@wistronits.com
 * @since 2019/6/28
 */
@RunWith(MockitoJUnitRunner.class)
public class OpticCableInfoControllerTest {
    /**
     * 被测试类
     */
    @InjectMocks
    private OpticCableInfoController opticCableInfoController;
    /**
     * Mock OpticCableInfoService
     */
    @Mock
    private OpticCableInfoService opticCableInfoService;
    /**
     * 返回结果封装
     */
    private Result resultResp;
    /**
     * 返回工具类
     */
    private ResultUtils resultUtils = spy(ResultUtils.class);

    @Test
    public void opticCableListByPage() {
        QueryCondition<OpticCableInfoReq> queryCondition = new QueryCondition<>();
        when(opticCableInfoService.opticCableListByPage(queryCondition)).thenReturn(resultResp);
        Assert.assertEquals(resultResp, opticCableInfoController.opticCableListByPage(queryCondition));
    }

    @Test
    public void opticCableListByPageForPageSelection() {
        QueryCondition<OpticCableInfoReq> queryCondition = new QueryCondition<>();
        when(opticCableInfoService.opticCableListByPage(queryCondition)).thenReturn(resultResp);
        Assert.assertEquals(resultResp, opticCableInfoController.opticCableListByPageForPageSelection(queryCondition));
    }

    @Test
    public void addOpticCable() {
        InsertOpticCableInfoReq insertOpticCableInfoReq = new InsertOpticCableInfoReq();
        when(opticCableInfoService.addOpticCable(insertOpticCableInfoReq)).thenReturn(resultResp);
        Assert.assertEquals(resultResp, opticCableInfoController.addOpticCable(insertOpticCableInfoReq));
    }

    @Test
    public void queryOpticCableById() {
        String id = "xx";
        when(opticCableInfoService.queryOpticCableById(id)).thenReturn(resultResp);
        Assert.assertEquals(resultResp, opticCableInfoController.queryOpticCableById(id));
    }

    @Test
    public void updateOpticCableById() {
        UpdateOpticCableInfoReq updateOpticCableInfoReq = new UpdateOpticCableInfoReq();
        when(opticCableInfoService.updateOpticCableById(updateOpticCableInfoReq)).thenReturn(resultResp);
        Assert.assertEquals(resultResp, opticCableInfoController.updateOpticCableById(updateOpticCableInfoReq));
    }

    @Test
    public void deleteOpticCableById() {
        String id = "xx";
        when(opticCableInfoService.deleteOpticCableById(id)).thenReturn(resultResp);
        Assert.assertEquals(resultResp, opticCableInfoController.deleteOpticCableById(id));
    }

    @Test
    public void checkOpticCableName() {
        String id = "xx";

        OpticCableInfo opticCableInfo = new OpticCableInfo();
        Result<Object> result = new Result<>(150107);
        result.setMsg("optic cable param error");
        opticCableInfo.setOpticCableId(id);
        opticCableInfo.setOpticCableName(id);

        Assert.assertEquals(150107, opticCableInfoController.checkOpticCableName(null).getCode());

        when(opticCableInfoService.checkOpticCableName(id, id)).thenReturn(true);
        Assert.assertEquals(150108, opticCableInfoController.checkOpticCableName(opticCableInfo).getCode());

        String name="zz";
        when(opticCableInfoService.checkOpticCableName(id, name)).thenReturn(false);
        opticCableInfo.setOpticCableName(name);
        Assert.assertEquals(0, opticCableInfoController.checkOpticCableName(opticCableInfo).getCode());
    }

    @Test
    public void getOpticCableListForApp() {
        when(opticCableInfoService.getOpticCableListForApp()).thenReturn(resultResp);
        Assert.assertEquals(resultResp, opticCableInfoController.getOpticCableListForApp());
    }

    @Test
    public void exportOpticCableList() {
        ExportDto<OpticCableInfoReq> exportDto = new ExportDto<>();
        when(opticCableInfoService.exportOpticCableList(exportDto)).thenReturn(resultResp);
        Assert.assertEquals(150000, opticCableInfoController.exportOpticCableList(exportDto).getCode());

        List<ColumnInfo> columnInfoList = new ArrayList<>();
        ColumnInfo columnInfo = new ColumnInfo();
        columnInfo.setColumnName("xx");
        columnInfoList.add(columnInfo);
        exportDto.setColumnInfoList(columnInfoList);
        exportDto.setExcelType(1);
        Assert.assertEquals(resultResp, opticCableInfoController.exportOpticCableList(exportDto));
    }
}