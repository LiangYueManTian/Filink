package com.fiberhome.filink.fdevice.controller.statistics;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.exportapi.bean.ColumnInfo;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.fdevice.constant.device.DeviceResultCode;
import com.fiberhome.filink.fdevice.dto.*;
import com.fiberhome.filink.fdevice.service.statistics.StatisticsService;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import mockit.Expectations;
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
 * @Author: zhaoliang
 * @Date: 2019/7/2 17:36
 * @Description: com.fiberhome.filink.fdevice.controller.statistics
 * @version: 1.0
 */
@RunWith(MockitoJUnitRunner.class)
public class StatisticsControllerTest {

    @InjectMocks
    private StatisticsController statisticsController;

    @Mock
    private StatisticsService statisticsService;

    @Test
    public void queryDeviceCount() {
        DeviceParam deviceParam = new DeviceParam();
        when(statisticsService.queryDeviceCount(deviceParam)).thenReturn(new Result());
        Result result = statisticsController.queryDeviceCount(deviceParam);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void queryDeviceStatusCount() {
        DeviceParam deviceParam = new DeviceParam();
        when(statisticsService.queryDeviceStatusCount(deviceParam)).thenReturn(new Result());
        Result result = statisticsController.queryDeviceStatusCount(deviceParam);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void queryDeployStatusCount() {
        DeviceParam deviceParam = new DeviceParam();
        when(statisticsService.queryDeployStatusCount(deviceParam)).thenReturn(new Result());
        Result result = statisticsController.queryDeployStatusCount(deviceParam);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void queryDeviceTypeCount() {
        when(statisticsService.queryDeviceTypeCount()).thenReturn(new Result());
        Result result = statisticsController.queryDeviceTypeCount();
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void queryUserDeviceStatusCount() {
        DeviceParam deviceParam = new DeviceParam();
        when(statisticsService.queryUserDeviceStatusCount(deviceParam)).thenReturn(new Result());
        Result result = statisticsController.queryUserDeviceStatusCount(deviceParam);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void queryUserDeviceStatusCount1() {
        when(statisticsService.queryUserDeviceStatusCount(null)).thenReturn(new Result());
        Result result = statisticsController.queryUserDeviceStatusCount();
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void queryUserDeviceAndStatusCount() {
        when(statisticsService.queryUserDeviceAndStatusCount()).thenReturn(new Result());
        Result result = statisticsController.queryUserDeviceAndStatusCount();
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void queryUserUnlockingTopNum() {
        when(statisticsService.queryUserUnlockingTopNum()).thenReturn(new Result());
        Result result = statisticsController.queryUserUnlockingTopNum();
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void queryUnlockingTopNum() {
        DeviceLogTopNumReq deviceLogTopNumReq = new DeviceLogTopNumReq();
        when(statisticsService.queryUnlockingTopNum(deviceLogTopNumReq)).thenReturn(new Result());
        Result result = statisticsController.queryUnlockingTopNum(deviceLogTopNumReq);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void queryDeviceSensorTopNum() {
        SensorTopNumReq sensorTopNumReq = new SensorTopNumReq();
        when(statisticsService.queryDeviceSensorTopNum(sensorTopNumReq)).thenReturn(new Result());
        Result result = statisticsController.queryDeviceSensorTopNum(sensorTopNumReq);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void updateSensorLimit() {
        SensorInfo sensorInfo = new SensorInfo();
        when(statisticsService.updateSensorLimit(sensorInfo)).thenReturn(new Result());
        Result result = statisticsController.updateSensorLimit(sensorInfo);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void queryUnlockingTimesByDeviceId() {
        UnlockingReq unlockingReq = new UnlockingReq();
        when(statisticsService.queryUnlockingTimesByDeviceId(unlockingReq)).thenReturn(new Result());
        Result result = statisticsController.queryUnlockingTimesByDeviceId(unlockingReq);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    private void i18nUtilsExpectation() {
        new Expectations(I18nUtils.class) {
            {
                I18nUtils.getSystemString(anyString);
                result = "demo";
            }
        };
    }

    @Test
    public void exportDeviceCount() {
        i18nUtilsExpectation();
        ExportDto exportDto = new ExportDto();
        Result result = statisticsController.exportDeviceCount(exportDto);
        Assert.assertTrue(result.getCode() == DeviceResultCode.EXPORT_PARAM_NULL);

        List<ColumnInfo> columnInfoList = new ArrayList<>();
        columnInfoList.add(new ColumnInfo());
        exportDto.setColumnInfoList(columnInfoList);
        exportDto.setExcelType(0);
        when(statisticsService.exportDeviceCount(exportDto)).thenReturn(new Result());
        result = statisticsController.exportDeviceCount(exportDto);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void exportDeviceStatusCount() {
        i18nUtilsExpectation();
        ExportDto exportDto = new ExportDto();
        Result result = statisticsController.exportDeviceStatusCount(exportDto);
        Assert.assertTrue(result.getCode() == DeviceResultCode.EXPORT_PARAM_NULL);

        List<ColumnInfo> columnInfoList = new ArrayList<>();
        columnInfoList.add(new ColumnInfo());
        exportDto.setColumnInfoList(columnInfoList);
        exportDto.setExcelType(0);
        when(statisticsService.exportDeviceStatusCount(exportDto)).thenReturn(new Result());
        result = statisticsController.exportDeviceStatusCount(exportDto);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void exportDeployStatusCount() {
        i18nUtilsExpectation();
        ExportDto exportDto = new ExportDto();
        Result result = statisticsController.exportDeployStatusCount(exportDto);
        Assert.assertTrue(result.getCode() == DeviceResultCode.EXPORT_PARAM_NULL);

        List<ColumnInfo> columnInfoList = new ArrayList<>();
        columnInfoList.add(new ColumnInfo());
        exportDto.setColumnInfoList(columnInfoList);
        exportDto.setExcelType(0);
        when(statisticsService.exportDeployStatusCount(exportDto)).thenReturn(new Result());
        result = statisticsController.exportDeployStatusCount(exportDto);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void exportUnlockingTopNum() {
        i18nUtilsExpectation();
        ExportDto exportDto = new ExportDto();
        Result result = statisticsController.exportUnlockingTopNum(exportDto);
        Assert.assertTrue(result.getCode() == DeviceResultCode.EXPORT_PARAM_NULL);

        List<ColumnInfo> columnInfoList = new ArrayList<>();
        columnInfoList.add(new ColumnInfo());
        exportDto.setColumnInfoList(columnInfoList);
        exportDto.setExcelType(0);
        when(statisticsService.exportUnlockingTopNum(exportDto)).thenReturn(new Result());
        result = statisticsController.exportUnlockingTopNum(exportDto);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void exportDeviceSensorTopNum() {
        i18nUtilsExpectation();
        ExportDto exportDto = new ExportDto();
        Result result = statisticsController.exportDeviceSensorTopNum(exportDto);
        Assert.assertTrue(result.getCode() == DeviceResultCode.EXPORT_PARAM_NULL);

        List<ColumnInfo> columnInfoList = new ArrayList<>();
        columnInfoList.add(new ColumnInfo());
        exportDto.setColumnInfoList(columnInfoList);
        exportDto.setExcelType(0);
        when(statisticsService.exportDeviceSensorTopNum(exportDto)).thenReturn(new Result());
        result = statisticsController.exportDeviceSensorTopNum(exportDto);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }
}
