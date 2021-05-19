package com.fiberhome.filink.fdevice.service.statistics.impl;

import com.alibaba.fastjson.JSONObject;
import com.fiberhome.filink.bean.RequestInfoUtils;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.exportapi.bean.ExportRequestInfo;
import com.fiberhome.filink.exportapi.exception.FilinkExportDataTooLargeException;
import com.fiberhome.filink.exportapi.exception.FilinkExportNoDataException;
import com.fiberhome.filink.exportapi.exception.FilinkExportTaskNumTooBigException;
import com.fiberhome.filink.fdevice.bean.Sensor.SensorLimit;
import com.fiberhome.filink.fdevice.bean.devicelog.UnlockingStatistics;
import com.fiberhome.filink.fdevice.constant.device.DeviceConstant;
import com.fiberhome.filink.fdevice.constant.device.DeviceResultCode;
import com.fiberhome.filink.fdevice.dao.statistics.SensorLimitDao;
import com.fiberhome.filink.fdevice.dao.statistics.StatisticsDao;
import com.fiberhome.filink.fdevice.dao.statistics.UnlockingStatisticsDao;
import com.fiberhome.filink.fdevice.dto.*;
import com.fiberhome.filink.fdevice.export.DeployStatusCountExport;
import com.fiberhome.filink.fdevice.export.DeviceCountExport;
import com.fiberhome.filink.fdevice.export.DeviceInfoTopNumExport;
import com.fiberhome.filink.fdevice.export.DeviceStatusCountExport;
import com.fiberhome.filink.fdevice.service.device.DeviceInfoService;
import com.fiberhome.filink.fdevice.service.devicelog.DeviceLogService;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.constant.LogConstants;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import com.fiberhome.filink.userapi.api.UserFeign;
import com.fiberhome.filink.userapi.bean.Department;
import com.fiberhome.filink.userapi.bean.Role;
import com.fiberhome.filink.userapi.bean.RoleDeviceType;
import com.fiberhome.filink.userapi.bean.User;
import mockit.Expectations;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.when;

/**
 * @Author: zhaoliang
 * @Date: 2019/7/2 18:07
 * @Description: com.fiberhome.filink.fdevice.service.statistics.impl
 * @version: 1.0
 */
@RunWith(MockitoJUnitRunner.class)
public class StatisticsServiceImplTest {

    @InjectMocks
    private StatisticsServiceImpl statisticsService;

    @Mock
    private StatisticsDao statisticsDao;

    @Mock
    private SensorLimitDao sensorLimitDao;

    @Mock
    private UserFeign userFeign;

    @Mock
    private DeviceInfoService deviceInfoService;

    @Mock
    private MongoTemplate mongoTemplate;

    @Mock
    private UnlockingStatisticsDao unlockingStatisticsDao;

    @Mock
    private DeviceCountExport deviceCountExport;

    @Mock
    private DeviceStatusCountExport deviceStatusCountExport;

    @Mock
    private DeployStatusCountExport deployStatusCountExport;

    @Mock
    private DeviceInfoTopNumExport deviceInfoTopNumExport;

    @Mock
    private LogProcess logProcess;
    @Mock
    private SystemLanguageUtil systemLanguageUtil;
    @Mock
    private DeviceLogService deviceLogService;

    private void i18nUtilsExpectation() {
        new Expectations(I18nUtils.class) {
            {
                I18nUtils.getSystemString(anyString);
                result = "demo";
            }
        };
    }

    @Test
    public void queryDeviceCount() {
        i18nUtilsExpectation();
        DeviceParam deviceParam = new DeviceParam();
        Result result = statisticsService.queryDeviceCount(deviceParam);
        Assert.assertTrue(result.getCode() == DeviceResultCode.DEVICE_PARAM_ERROR);

        deviceParam.setAreaIds(new ArrayList<>());
        deviceParam.setDeviceTypes(new ArrayList<>());
        result = statisticsService.queryDeviceCount(deviceParam);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());

        deviceParam.getAreaIds().add("areaId001");
        deviceParam.getDeviceTypes().add("001");
        deviceParam.getDeviceTypes().add("030");
        List<DeviceNumDto> deviceNumDtoList = new ArrayList<>();
        deviceNumDtoList.add(new DeviceNumDto());
        when(statisticsDao.queryDeviceCount(deviceParam)).thenReturn(deviceNumDtoList);
        result = statisticsService.queryDeviceCount(deviceParam);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());

    }

    @Test
    public void queryDeviceStatusCount() {
        i18nUtilsExpectation();
        DeviceParam deviceParam = new DeviceParam();
        Result result = statisticsService.queryDeviceStatusCount(deviceParam);
        Assert.assertTrue(result.getCode() == DeviceResultCode.DEVICE_PARAM_ERROR);

        deviceParam.setAreaIds(new ArrayList<>());
        deviceParam.setDeviceType("001");
        result = statisticsService.queryDeviceStatusCount(deviceParam);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());

        deviceParam.getAreaIds().add("areaId001");
        List<DeviceNumDto> deviceNumDtoList = new ArrayList<>();
        deviceNumDtoList.add(new DeviceNumDto());
        when(statisticsDao.queryDeviceStatusCount(deviceParam)).thenReturn(deviceNumDtoList);
        result = statisticsService.queryDeviceStatusCount(deviceParam);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void queryDeployStatusCount() {
        i18nUtilsExpectation();
        DeviceParam deviceParam = new DeviceParam();
        Result result = statisticsService.queryDeployStatusCount(deviceParam);
        Assert.assertTrue(result.getCode() == DeviceResultCode.DEVICE_PARAM_ERROR);

        deviceParam.setAreaIds(new ArrayList<>());
        deviceParam.setDeviceType("001");
        result = statisticsService.queryDeployStatusCount(deviceParam);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());

        deviceParam.getAreaIds().add("areaId001");
        List<DeviceNumDto> deviceNumDtoList = new ArrayList<>();
        deviceNumDtoList.add(new DeviceNumDto());
        when(statisticsDao.queryDeployStatusCount(deviceParam)).thenReturn(deviceNumDtoList);
        result = statisticsService.queryDeployStatusCount(deviceParam);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    private User createUser() {
        User user = new User();
        user.setId(DeviceConstant.ADMIN);
        user.setDepartment(new Department());
        user.getDepartment().setAreaIdList(new ArrayList<>());
        user.setRole(new Role());
        user.getRole().setRoleDevicetypeList(new ArrayList<>());

        return user;
    }

    private User fillUser(User user) {
        user.getDepartment().getAreaIdList().add("areaId001");
        RoleDeviceType roleDeviceType = new RoleDeviceType();
        roleDeviceType.setDeviceTypeId("type001");
        user.getRole().getRoleDevicetypeList().add(roleDeviceType);
        return user;
    }

    private User mockGettingUser() {
        new Expectations(RequestInfoUtils.class) {
            {
                RequestInfoUtils.getUserId();
                result = "userId";
            }

            {
                RequestInfoUtils.getToken();
                result = "token";
            }
        };
        User user = createUser();
        fillUser(user);
//        user.setId("userId");
        when(userFeign.queryCurrentUser("userId", "token")).thenReturn(JSONObject.toJSON(user));
        return user;
    }

    @Test
    public void queryDeviceTypeCount() {
        User user = mockGettingUser();
        List<DeviceNumDto> deviceNumDtoList = new ArrayList<>();
        DeviceNumDto deviceNumDto = new DeviceNumDto();
        deviceNumDto.setDeviceType("type001");
        deviceNumDtoList.add(deviceNumDto);
        when(statisticsDao.queryDeviceTypeCount(anyList())).thenReturn(deviceNumDtoList);

        Result result = statisticsService.queryDeviceTypeCount();
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());

        user.setId("userId");
        when(userFeign.queryCurrentUser("userId", "token")).thenReturn(JSONObject.toJSON(user));
        result = statisticsService.queryDeviceTypeCount();
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void queryUserDeviceStatusCount() {
        User user = mockGettingUser();
        DeviceParam deviceParam = new DeviceParam();

        Result result = statisticsService.queryUserDeviceStatusCount(deviceParam);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());

        user.setId("userId");
        when(userFeign.queryCurrentUser("userId", "token")).thenReturn(JSONObject.toJSON(user));
        result = statisticsService.queryUserDeviceStatusCount(null);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());

        user.getDepartment().setAreaIdList(null);
        when(userFeign.queryCurrentUser("userId", "token")).thenReturn(JSONObject.toJSON(user));
        result = statisticsService.queryUserDeviceStatusCount(null);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void queryUserDeviceAndStatusCount() {
        User user = mockGettingUser();
        user.setId("userId");
        when(userFeign.queryCurrentUser("userId", "token")).thenReturn(JSONObject.toJSON(user));
        Result result = statisticsService.queryUserDeviceAndStatusCount();
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());

        user.getDepartment().setAreaIdList(null);
        when(userFeign.queryCurrentUser("userId", "token")).thenReturn(JSONObject.toJSON(user));
        result = statisticsService.queryUserDeviceAndStatusCount();
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void queryUnlockingTimesByDeviceId() {
        i18nUtilsExpectation();
        UnlockingReq unlockingReq = new UnlockingReq();
        unlockingReq.setDeviceId("deviceId001");
        unlockingReq.setStartDate("20190520");
        Result result = statisticsService.queryUnlockingTimesByDeviceId(unlockingReq);
        Assert.assertTrue(result.getCode() == DeviceResultCode.DEVICE_PARAM_ERROR);

        LocalDate nowDate = LocalDateTime.now().toLocalDate();
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMdd");
        unlockingReq.setEndDate(nowDate.format(df));
        List<UnlockingStatistics> unlockingStatisticsList = new ArrayList<>();
        when(unlockingStatisticsDao.queryUnlockingTimesByDeviceId(unlockingReq)).thenReturn(unlockingStatisticsList);
        Long startTime = Timestamp.valueOf(LocalDateTime.of(nowDate, LocalTime.MIN)).getTime();
        Long endTime = Timestamp.valueOf(LocalDateTime.of(nowDate, LocalTime.MAX)).getTime();
        when(deviceLogService.queryUnlockingCountByDeviceId("deviceId001", startTime, endTime))
                .thenReturn(1);
        result = statisticsService.queryUnlockingTimesByDeviceId(unlockingReq);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void exportDeviceCount() {
        i18nUtilsExpectation();
        ExportDto exportDto = new ExportDto();
        when(systemLanguageUtil.querySystemLanguage()).thenReturn("ZH");
        when(deviceCountExport.insertTask(exportDto, "filink-device-server", "demo"))
                .thenThrow(FilinkExportNoDataException.class)
                .thenThrow(FilinkExportDataTooLargeException.class)
                .thenThrow(FilinkExportTaskNumTooBigException.class)
                .thenThrow(Exception.class)
                .thenReturn(new ExportRequestInfo());
        Result result = statisticsService.exportDeviceCount(exportDto);
        Assert.assertTrue(result.getCode() == DeviceResultCode.EXPORT_NO_DATA);

        result = statisticsService.exportDeviceCount(exportDto);
        Assert.assertTrue(result.getCode() == DeviceResultCode.EXPORT_DATA_TOO_LARGE);

        result = statisticsService.exportDeviceCount(exportDto);
        Assert.assertTrue(result.getCode() == DeviceResultCode.EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS);

        result = statisticsService.exportDeviceCount(exportDto);
        Assert.assertTrue(result.getCode() == DeviceResultCode.FAILED_TO_CREATE_EXPORT_TASK);

        when(logProcess.generateAddLogToCallParam(LogConstants.LOG_TYPE_OPERATE)).thenReturn(new AddLogBean());
        result = statisticsService.exportDeviceCount(exportDto);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void exportDeviceStatusCount() {
        i18nUtilsExpectation();
        ExportDto exportDto = new ExportDto();
        when(deviceStatusCountExport.insertTask(exportDto, "filink-device-server", "demo"))
                .thenThrow(FilinkExportNoDataException.class);
        Result result = statisticsService.exportDeviceStatusCount(exportDto);
        Assert.assertTrue(result.getCode() == DeviceResultCode.EXPORT_NO_DATA);
    }

    @Test
    public void exportDeployStatusCount() {
        i18nUtilsExpectation();
        ExportDto exportDto = new ExportDto();
        when(deployStatusCountExport.insertTask(exportDto, "filink-device-server", "demo"))
                .thenThrow(FilinkExportNoDataException.class);
        Result result = statisticsService.exportDeployStatusCount(exportDto);
        Assert.assertTrue(result.getCode() == DeviceResultCode.EXPORT_NO_DATA);
    }

    @Test
    public void exportUnlockingTopNum() {
        i18nUtilsExpectation();
        ExportDto exportDto = new ExportDto();
        when(deviceInfoTopNumExport.insertTask(exportDto, "filink-device-server", "demo"))
                .thenThrow(FilinkExportNoDataException.class);
        Result result = statisticsService.exportUnlockingTopNum(exportDto);
        Assert.assertTrue(result.getCode() == DeviceResultCode.EXPORT_NO_DATA);
    }

    @Test
    public void exportDeviceSensorTopNum() {
        i18nUtilsExpectation();
        ExportDto exportDto = new ExportDto();
        when(deviceInfoTopNumExport.insertTask(exportDto, "filink-device-server", "demo"))
                .thenThrow(FilinkExportNoDataException.class);
        Result result = statisticsService.exportDeviceSensorTopNum(exportDto);
        Assert.assertTrue(result.getCode() == DeviceResultCode.EXPORT_NO_DATA);
    }

    @Test
    public void queryUnlockingTopNum() {
        i18nUtilsExpectation();
        DeviceLogTopNumReq deviceLogTopNumReq = new DeviceLogTopNumReq();
        deviceLogTopNumReq.setAreaIdList(new ArrayList<>());
        deviceLogTopNumReq.setDeviceType("001");
        deviceLogTopNumReq.setTopTotal(10);
        Result result = statisticsService.queryUnlockingTopNum(deviceLogTopNumReq);
        Assert.assertTrue(result.getCode() == DeviceResultCode.DEVICE_PARAM_ERROR);

        deviceLogTopNumReq.setStartDate("20190620");
        LocalDate nowDate = LocalDateTime.now().toLocalDate();
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMdd");
        deviceLogTopNumReq.setEndDate(nowDate.format(df));
        result = statisticsService.queryUnlockingTopNum(deviceLogTopNumReq);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());

        deviceLogTopNumReq.getAreaIdList().add("areaId001");
        result = statisticsService.queryUnlockingTopNum(deviceLogTopNumReq);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void queryUserUnlockingTopNum() {
        User user = createUser();
        fillUser(user);
        when(userFeign.queryCurrentUser(any(), any())).thenReturn(JSONObject.toJSON(user));
        Assert.assertEquals(0, statisticsService.queryUserUnlockingTopNum().getCode());


    }

    @Test
    public void queryDeviceSensorTopNum() {
        SensorTopNumReq sensorTopNumReq = new SensorTopNumReq();
        sensorTopNumReq.setSensorType("type");
        List<String> areaIdList = new ArrayList<>();
        areaIdList.add("wuhan");
        sensorTopNumReq.setAreaIdList(areaIdList);
        sensorTopNumReq.setDeviceType("001");
        Assert.assertEquals(130201, statisticsService.queryDeviceSensorTopNum(sensorTopNumReq).getCode());
        sensorTopNumReq.setTopTotal(0);
        Assert.assertEquals(0, statisticsService.queryDeviceSensorTopNum(sensorTopNumReq).getCode());
        sensorTopNumReq.setTopTotal(1);
        when(sensorLimitDao.queryDeviceSensorTopNum(any())).thenReturn(new ArrayList<>());
        Assert.assertEquals(0, statisticsService.queryDeviceSensorTopNum(sensorTopNumReq).getCode());

    }

    @Test
    public void updateSensorLimit() {
        new Expectations() {
            {
                sensorLimitDao.insert(any());
            }

            {
                sensorLimitDao.updateById(any());
            }
        };
        SensorInfo sensorInfo = new SensorInfo();
        sensorInfo.setDeviceId("001");
        sensorInfo.setTemperature(1);
        sensorInfo.setHumidity(2);
        Assert.assertEquals(130201, statisticsService.updateSensorLimit(sensorInfo).getCode());
        sensorInfo.setCurrentTime(new Date().getTime());
        when(sensorLimitDao.selectById(any())).thenReturn(null);
        Assert.assertEquals(0, statisticsService.updateSensorLimit(sensorInfo).getCode());
        SensorLimit sensorLimit = new SensorLimit();
        sensorLimit.setDeviceId("001");
        sensorLimit.setTemperatureMax(0);
        sensorLimit.setTemperatureMin(2);
        sensorLimit.setTemperatureMin(3);
        sensorLimit.setHumidityMax(1);
        when(sensorLimitDao.selectById(any())).thenReturn(sensorLimit);
        when(sensorLimitDao.selectById(any())).thenReturn(sensorLimit);
        Assert.assertEquals(0, statisticsService.updateSensorLimit(sensorInfo).getCode());

    }
}
