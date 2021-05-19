package com.fiberhome.filink.fdevice.controller.devicelog;

import com.fiberhome.filink.bean.PageBean;
import com.fiberhome.filink.bean.PageCondition;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.exportapi.bean.ColumnInfo;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.fdevice.bean.devicelog.DeviceLog;
import com.fiberhome.filink.fdevice.dto.DeviceLogReqtForPda;
import com.fiberhome.filink.fdevice.service.devicelog.DeviceLogService;
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
 * DeviceLogControllerTest
 *
 * @author congcongsun2@wistronits.com
 * @since 2019/7/6
 */
@RunWith(MockitoJUnitRunner.class)
public class DeviceLogControllerTest {
    /**
     * 自动注入service对象
     */
    @InjectMocks
    private DeviceLogController deviceLogController;

    @Mock
    private DeviceLogService deviceLogService;


    private Result result = new Result();

    @Test
    public void deviceLogListByPage() {
        QueryCondition queryCondition = new QueryCondition();
        Assert.assertEquals(130301, deviceLogController.deviceLogListByPage(queryCondition).getCode());
        PageCondition pageCondition = new PageCondition();
        pageCondition.setPageSize(2);
        pageCondition.setPageNum(1);
        queryCondition.setPageCondition(pageCondition);
        PageBean pageBean = new PageBean();
        pageBean.setPageNum(1);
        pageBean.setSize(2);
        pageBean.setTotalCount(2);
        pageBean.setTotalPage(1);
        pageBean.setData("ok");
        when(deviceLogService.deviceLogListByPage(queryCondition, null, true)).thenReturn(pageBean);
        Assert.assertEquals(0, deviceLogController.deviceLogListByPage(queryCondition).getCode());

    }

    @Test
    public void deviceLogListByPageForPda() {
        DeviceLogReqtForPda deviceLogReqtForPda = new DeviceLogReqtForPda();
        QueryCondition queryCondition = new QueryCondition();
        Assert.assertEquals(130301, deviceLogController.deviceLogListByPageForPda(deviceLogReqtForPda).getCode());
        PageCondition pageCondition = new PageCondition();
        pageCondition.setPageSize(2);
        pageCondition.setPageNum(1);
        deviceLogReqtForPda.setPageCondition(pageCondition);
        List<String> list = new ArrayList<>();
        list.add("xx");
        deviceLogReqtForPda.setAreaId(list);
        deviceLogReqtForPda.setDeviceId(list);
        deviceLogReqtForPda.setDeviceType(list);
        deviceLogReqtForPda.setLogType(list);
        deviceLogReqtForPda.setType(list);
        deviceLogReqtForPda.setEndTime(1L);
        deviceLogReqtForPda.setStartTime(0L);
        when(deviceLogService.deviceLogListByPageForPda(any())).thenReturn(new PageBean());
        Assert.assertEquals(0, deviceLogController.deviceLogListByPageForPda(deviceLogReqtForPda).getCode());
    }


    @Test
    public void saveDeviceLog() throws Exception {
        DeviceLog deviceLog = new DeviceLog();
        when(deviceLogService.saveDeviceLog(deviceLog)).thenReturn(result);
        Assert.assertEquals(result, deviceLogController.saveDeviceLog(deviceLog));
    }

    @Test
    public void deleteDeviceLogByDeviceIds() {
        List<String> deviceLogList = new ArrayList<>();
        when(deviceLogService.deleteDeviceLogByDeviceIds(deviceLogList)).thenReturn(result);
        Assert.assertEquals(result, deviceLogController.deleteDeviceLogByDeviceIds(deviceLogList));

    }

    @Test
    public void exportDeviceLogList() {
        ExportDto exportDto = new ExportDto();
        exportDto.setExcelType(1);
        List<ColumnInfo> columnInfoList = new ArrayList<>();
        ColumnInfo columnInfo = new ColumnInfo();
        columnInfo.setPropertyName("cc");
        columnInfoList.add(columnInfo);
        exportDto.setColumnInfoList(columnInfoList);
        when(deviceLogService.exportDeviceLogList(any())).thenReturn(result);
        Assert.assertEquals(result, deviceLogController.exportDeviceLogList(exportDto));
    }

    @Test
    public void queryRecentDeviceLogTime() {
        String deviceId = "id";
        when(deviceLogService.queryRecentDeviceLogTime(any())).thenReturn(result);
        Assert.assertEquals(result, deviceLogController.queryRecentDeviceLogTime(deviceId));
    }
}