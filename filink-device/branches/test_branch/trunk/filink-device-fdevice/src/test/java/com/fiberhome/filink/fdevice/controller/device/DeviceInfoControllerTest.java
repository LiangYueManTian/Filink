package com.fiberhome.filink.fdevice.controller.device;

import com.alibaba.fastjson.JSON;
import com.fiberhome.filink.bean.*;
import com.fiberhome.filink.exportapi.bean.ColumnInfo;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.fdevice.bean.device.DeviceInfo;
import com.fiberhome.filink.fdevice.constant.device.DeviceI18n;
import com.fiberhome.filink.fdevice.constant.device.DeviceResultCode;
import com.fiberhome.filink.fdevice.dto.*;
import com.fiberhome.filink.fdevice.exception.FiLinkDeviceException;
import com.fiberhome.filink.fdevice.service.device.impl.DeviceInfoServiceImpl;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import mockit.Expectations;
import org.apache.poi.ss.formula.functions.T;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

/**
 * @author WH1707069 create on 2019/1/12
 */
@RunWith(MockitoJUnitRunner.class)
public class DeviceInfoControllerTest {

    @InjectMocks
    private DeviceInfoController deviceInfoController;

    @Mock
    private DeviceInfoServiceImpl deviceInfoService;

    private Result result = new Result();

    @Test
    public void addDeviceTest() {
        DeviceInfo deviceInfo = new DeviceInfo();
        when(deviceInfoService.addDevice(deviceInfo)).thenReturn(ResultUtils.success());
        Result result = deviceInfoController.addDevice(deviceInfo);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void addDeviceForPdaTest() {
        DeviceInfo deviceInfo = new DeviceInfo();
        when(deviceInfoService.addDeviceForPda(deviceInfo)).thenReturn(ResultUtils.success());
        Result result = deviceInfoController.addDeviceForPda(deviceInfo);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void checkDeviceNameTest() {
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setDeviceId("1");
        deviceInfo.setDeviceName("2");
        DeviceInfo deviceInfo1 = null;
        new Expectations(I18nUtils.class) {
            {
                I18nUtils.getSystemString(anyString);
                result = "demo";
            }
        };
        when(deviceInfoService.checkDeviceName("1", "2")).thenReturn(true);
        when(deviceInfoService.checkDeviceName("2", "2")).thenReturn(false);
        Result result = deviceInfoController.checkDeviceName(deviceInfo);
        Result result1 = deviceInfoController.checkDeviceName(deviceInfo1);
        deviceInfo.setDeviceId("2");
        Result result2 = deviceInfoController.checkDeviceName(deviceInfo);
        Assert.assertTrue(result.getCode() == DeviceResultCode.DEVICE_NAME_SAME);
        Assert.assertTrue(result1.getCode() == DeviceResultCode.DEVICE_PARAM_ERROR);
        Assert.assertTrue(result2.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void updateDeviceTest() {
        DeviceInfo deviceInfo = new DeviceInfo();
        when(deviceInfoService.updateDevice(deviceInfo)).thenReturn(new Result());
        Result result = deviceInfoController.updateDevice(deviceInfo);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void updateDeviceForPdaTest() {
        DeviceInfo deviceInfo = new DeviceInfo();
        when(deviceInfoService.updateDeviceForPda(deviceInfo)).thenReturn(new Result());
        Result result = deviceInfoController.updateDeviceForPda(deviceInfo);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void listDeviceTest() throws Exception {
        when(deviceInfoService.listDevice(any())).thenReturn(new Result());
        Result result = deviceInfoController.listDevice(new QueryCondition<>());
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void getDeviceByIdTest() throws Exception {
        new Expectations(RequestInfoUtils.class) {
            {
                RequestInfoUtils.getUserId();
                result = "1";
            }
        };
        when(deviceInfoService.getDeviceById(anyString(), anyString())).thenReturn(new Result());
        Result result = deviceInfoController.getDeviceById("1");
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void feignGetDeviceById() throws Exception {
        Result r = new Result();
        when(deviceInfoService.getDeviceById(anyString(), anyString())).thenReturn(r);
        DeviceInfoDto deviceInfoDto = deviceInfoController.feignGetDeviceById("1");
        Assert.assertEquals(deviceInfoDto, r.getData());
    }

    @Test
    public void queryDeviceAreaList() {
        when(deviceInfoService.queryDeviceAreaList()).thenReturn(new Result());
        Result result = deviceInfoController.queryDeviceAreaList();
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void deleteDeviceByIdsTest() {
        String[] str = {"!"};
        when(deviceInfoService.deleteDeviceByIds(any())).thenReturn(new Result());
        Result result = deviceInfoController.deleteDeviceByIds(str);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void deviceCanChangeDetailTest() {
        new Expectations(I18nUtils.class) {
            {
                I18nUtils.getSystemString(anyString);
                result = "demo";
            }
        };
        when(deviceInfoService.deviceCanChangeDetail("1")).thenReturn(true);
        when(deviceInfoService.deviceCanChangeDetail("2")).thenReturn(false);
        Result result = deviceInfoController.deviceCanChangeDetail("1");
        Result result1 = deviceInfoController.deviceCanChangeDetail("2");
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
        Assert.assertTrue(result1.getCode() == DeviceResultCode.DEVICE_CANNOT_UPDATE);
    }

    @Test
    public void addDevice() {
        when(deviceInfoService.addDevice(any())).thenReturn(ResultUtils.success());
        Result result = deviceInfoController.addDevice(new DeviceInfoDto());
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void deviceListByPage() {
        when(deviceInfoService.listDevice(any())).thenReturn(new Result());
        QueryCondition queryCondition = new QueryCondition();
        DeviceInfo deviceInfo = new DeviceInfo();
        queryCondition.setBizCondition(deviceInfo);
        Result result = deviceInfoController.deviceListByPage(queryCondition);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void queryCurrentDeviceCount() {
        Integer integer1 = new Integer(0);
        when(deviceInfoService.queryCurrentDeviceCount()).thenReturn(integer1);
        Integer integer = deviceInfoController.queryCurrentDeviceCount();
        Assert.assertEquals(integer, integer1);
    }

    @Test
    public void queryDeviceDtoByAreaIdForPageSelection() {
        new Expectations(I18nUtils.class) {
            {
                I18nUtils.getSystemString(anyString);
                result = "demo";
            }
        };
        List<DeviceInfoDto> deviceInfoDtoList = new ArrayList<>();
        when(deviceInfoService.queryDeviceDtoByAreaId("areaId")).thenReturn(deviceInfoDtoList);
        Result result = deviceInfoController.queryDeviceDtoByAreaIdForPageSelection("areaId");
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
        Assert.assertEquals(result.getData(), deviceInfoDtoList);
    }

    @Test
    public void queryDeviceDtoByAreaIdsForPageSelection() {
        new Expectations(I18nUtils.class) {
            {
                I18nUtils.getSystemString(anyString);
                result = "demo";
            }
        };
        List<DeviceInfoDto> deviceInfoDtoList = new ArrayList<>();
        List<String> areaIdList = new ArrayList<>();
        when(deviceInfoService.queryDeviceDtoByAreaIds(areaIdList)).thenReturn(deviceInfoDtoList);
        Result result = deviceInfoController.queryDeviceDtoByAreaIdsForPageSelection(areaIdList);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
        Assert.assertEquals(result.getData(), deviceInfoDtoList);
    }

    @Test
    public void queryDeviceDtoForPageSelection() {
        DeviceParam deviceParam = new DeviceParam();
        when(deviceInfoService.queryDeviceDtoForPageSelection(deviceParam)).thenReturn(new Result());
        Result result = deviceInfoController.queryDeviceDtoForPageSelection(deviceParam);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void queryDeviceDtoByParam() {
        DeviceParam deviceParam = new DeviceParam();
        List<DeviceInfoDto> deviceInfoDtoList = new ArrayList<>();
        when(deviceInfoService.queryDeviceDtoForPageSelection(deviceParam)).thenReturn(ResultUtils.success(deviceInfoDtoList));
        List<DeviceInfoDto> deviceInfoDtoList1 = deviceInfoController.queryDeviceDtoByParam(deviceParam);
        Assert.assertEquals(deviceInfoDtoList1, deviceInfoDtoList);
    }

    @Test
    public void queryDeviceByBean() throws Exception {
        new Expectations(I18nUtils.class) {
            {
                I18nUtils.getSystemString(anyString);
                result = "demo";
            }
        };
        //参数为空
        try {
            deviceInfoController.queryDeviceByBean(null);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(e instanceof FiLinkDeviceException);
            Assert.assertEquals(e.getMessage(), "demo");
        }
        //参数未赋值
        DeviceReq deviceReq = new DeviceReq();
        try {
            deviceInfoController.queryDeviceByBean(deviceReq);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(e instanceof FiLinkDeviceException);
            Assert.assertEquals(e.getMessage(), "demo");
        }
        //参数赋值
        deviceReq.setDeviceName("deviceName");
        when(deviceInfoService.queryDeviceByBean(deviceReq)).thenReturn(new Result());
        Result result = deviceInfoController.queryDeviceByBean(deviceReq);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void getDefaultParams() {
        new Expectations(I18nUtils.class) {
            {
                I18nUtils.getSystemString(anyString);
                result = "demo";
            }
        };
        //参数为空
        try {
            deviceInfoController.getDefaultParams(null);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(e instanceof FiLinkDeviceException);
            Assert.assertEquals(e.getMessage(), "demo");
        }
        //参数正常
        Map<String, String> map = new HashMap<>();
        when(deviceInfoService.getDefaultParamsByDeviceType("deviceId")).thenReturn(map);
        String defaultParams = deviceInfoController.getDefaultParams("deviceId");
        Assert.assertEquals(defaultParams, JSON.toJSONString(map));
    }

    @Test
    public void feignGetDeviceByIds() throws Exception {
        String[] deviceIds = {"deviceId1", "deviceId2"};
        List<DeviceInfoDto> dtoList = new ArrayList<>();
        when(deviceInfoService.getDeviceByIds(deviceIds)).thenThrow(Exception.class).thenReturn(dtoList);
        ;

        List<DeviceInfoDto> deviceInfoDtoList = deviceInfoController.feignGetDeviceByIds(deviceIds);
        Assert.assertEquals(deviceInfoDtoList, null);

        deviceInfoDtoList = deviceInfoController.feignGetDeviceByIds(deviceIds);
        Assert.assertEquals(deviceInfoDtoList, dtoList);
    }

    @Test
    public void getDeviceByIds() {
        String[] ids = {"deviceId1", "deviceId2"};
        when(deviceInfoService.getDeviceResultByIds(ids)).thenReturn(new Result());
        Result result = deviceInfoController.getDeviceByIds(ids);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void queryHomeDeviceById() {
        try {
            deviceInfoController.queryHomeDeviceById(null);
        } catch (Exception e) {
            Assert.assertTrue(e instanceof FiLinkDeviceException);
        }
        String id = "xx";
        when(deviceInfoService.queryHomeDeviceById(id)).thenReturn(result);
        Assert.assertEquals(result, deviceInfoController.queryHomeDeviceById(id));

    }

    @Test
    public void queryHomeDeviceByIds() {
        try {
            deviceInfoController.queryHomeDeviceByIds(null);
        } catch (Exception e) {
            Assert.assertTrue(e instanceof FiLinkDeviceException);
        }
        List<String> deviceIds = new ArrayList<>();
        deviceIds.add("xx");
        when(deviceInfoService.queryHomeDeviceByIds(deviceIds)).thenReturn(result);
        Assert.assertEquals(result, deviceInfoController.queryHomeDeviceByIds(deviceIds));
    }

    @Test
    public void updateHomeDeviceLimit() {
        Integer homeDeviceLimit = null;
        deviceInfoController.updateHomeDeviceLimit(homeDeviceLimit);
        homeDeviceLimit = 1;
        deviceInfoController.updateHomeDeviceLimit(homeDeviceLimit);
    }

    @Test
    public void queryHomeDeviceArea() {
        when(deviceInfoService.queryHomeDeviceArea()).thenReturn(result);
        Assert.assertEquals(result, deviceInfoController.queryHomeDeviceArea());
    }

    @Test
    public void refreshHomeDeviceAreaHuge() {
        List<String> areaIdList = new ArrayList<>();
        try {
            deviceInfoController.refreshHomeDeviceAreaHuge(areaIdList);
        } catch (Exception e) {
            Assert.assertTrue(e instanceof FiLinkDeviceException);
        }
        areaIdList.add("xx");
        when(deviceInfoService.refreshHomeDeviceAreaHuge(areaIdList)).thenReturn(result);
        Assert.assertEquals(result, deviceInfoController.refreshHomeDeviceAreaHuge(areaIdList));
    }

    @Test
    public void refreshHomeDeviceArea() {
        when(deviceInfoService.refreshHomeDeviceArea()).thenReturn(result);
        Assert.assertEquals(result, deviceInfoController.refreshHomeDeviceArea());
    }

    @Test
    public void exportDeviceList() {
        new Expectations(I18nUtils.class) {
            {
                I18nUtils.getSystemString(anyString);
                result = "demo";
            }
        };
        ExportDto exportDto = new ExportDto();
        Result result = deviceInfoController.exportDeviceList(exportDto);
        Assert.assertTrue(result.getCode() == DeviceResultCode.EXPORT_PARAM_NULL);

        QueryCondition queryCondition = new QueryCondition();
        queryCondition.setPageCondition(new PageCondition());
        queryCondition.setFilterConditions(new ArrayList<>());
        exportDto.setQueryCondition(queryCondition);
        List<ColumnInfo> columnInfoList = new ArrayList<>();
        columnInfoList.add(new ColumnInfo());
        exportDto.setColumnInfoList(columnInfoList);
        exportDto.setExcelType(0);
        when(deviceInfoService.exportDeviceList(exportDto)).thenReturn(new Result());
        result = deviceInfoController.exportDeviceList(exportDto);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());

    }

    @Test
    public void queryNearbyDeviceListForPda() {
        DeviceReqPda deviceReqPda = new DeviceReqPda();
        when(deviceInfoService.queryNearbyDeviceListForPda(deviceReqPda)).thenReturn(new Result());
        Result result = deviceInfoController.queryNearbyDeviceListForPda(deviceReqPda);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void updateDeviceStatus() throws Exception {
        DeviceInfoDto deviceInfoDto = new DeviceInfoDto();
        when(deviceInfoService.updateDeviceStatus(deviceInfoDto)).thenReturn(new Result());
        Result result = deviceInfoController.updateDeviceStatus(deviceInfoDto);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void updateDeviceListStatus() {
        UpdateDeviceStatusPda updateDeviceStatusPda = new UpdateDeviceStatusPda();
        when(deviceInfoService.updateDeviceListStatus(updateDeviceStatusPda)).thenReturn(new Result());
        Result result = deviceInfoController.updateDeviceListStatus(updateDeviceStatusPda);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void queryDeviceTypesByAreaIds() {
        new Expectations(I18nUtils.class) {
            {
                I18nUtils.getSystemString(anyString);
                result = "demo";
            }
        };
        List<String> areaIds = new ArrayList<>();
        when(deviceInfoService.queryDeviceTypesByAreaIds(areaIds)).thenReturn(new ArrayList<>());
        Result result = deviceInfoController.queryDeviceTypesByAreaIds(areaIds);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void queryDeviceInfoBaseByParam() {
        DeviceParam deviceParam = new DeviceParam();
        when(deviceInfoService.queryDeviceInfoBaseByParam(deviceParam)).thenReturn(new ArrayList<>());
        List<DeviceInfoBase> deviceInfoBases = deviceInfoController.queryDeviceInfoBaseByParam(deviceParam);
        Assert.assertTrue(deviceInfoBases.size() == 0);
    }
}