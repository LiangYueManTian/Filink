package com.fiberhome.filink.fdevice.service.devicelog.impl;

import com.fiberhome.filink.bean.*;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.exportapi.bean.ExportRequestInfo;
import com.fiberhome.filink.exportapi.exception.FilinkExportDataTooLargeException;
import com.fiberhome.filink.exportapi.exception.FilinkExportNoDataException;
import com.fiberhome.filink.exportapi.exception.FilinkExportTaskNumTooBigException;
import com.fiberhome.filink.fdevice.bean.area.AreaInfo;
import com.fiberhome.filink.fdevice.bean.devicelog.DeviceLog;
import com.fiberhome.filink.fdevice.dao.statistics.UnlockingStatisticsDao;
import com.fiberhome.filink.fdevice.dto.DeviceInfoDto;
import com.fiberhome.filink.fdevice.dto.DeviceParam;
import com.fiberhome.filink.fdevice.export.DeviceLogExport;
import com.fiberhome.filink.fdevice.service.device.DeviceInfoService;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.constant.LogConstants;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import com.fiberhome.filink.userapi.api.UserFeign;
import com.fiberhome.filink.userapi.bean.User;
import mockit.Expectations;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

/**
 * DeviceLogServiceImplTest
 *
 * @author congcongsun2@wistronits.com
 * @since 2019/7/8
 */
@RunWith(MockitoJUnitRunner.class)
public class DeviceLogServiceImplTest {

    @InjectMocks
    private DeviceLogServiceImpl deviceLogService;

    @Mock
    private DeviceInfoService deviceInfoService;

    @Mock
    private MongoTemplate mongoTemplate;

    @Mock
    private UnlockingStatisticsDao unlockingStatisticsDao;
    /**
     * 设施日志导出类
     */
    @Mock
    private DeviceLogExport deviceLogExport;
    /**
     * userFeign
     */
    @Mock
    private UserFeign userFeign;

    @Mock
    private LogProcess logProcess;

    @Mock
    private SystemLanguageUtil systemLanguageUtil;
    /**
     * 服务名
     */
    private static String SERVER_NAME = "filink-device-server";


    private QueryCondition queryCondition = new QueryCondition();

    private List<FilterCondition> filterConditions = new ArrayList<>();

    @Before
    public void setUp() {
        ReflectionTestUtils.setField(deviceLogService, "maxExportDataSize", 10000);
        SortCondition sortCondition = new SortCondition();
        sortCondition.setSortField("1");
        queryCondition.setSortCondition(sortCondition);

        List<String> filterValue = new ArrayList<>();
        FilterCondition filterCondition = new FilterCondition();
        filterCondition.setOperator("gt");
        filterCondition.setFilterField("areaId");
        filterValue.add("beijing");
        filterCondition.setFilterValue(filterValue);
        filterConditions.add(filterCondition);
        FilterCondition filterCondition1 = new FilterCondition();
        filterCondition1.setOperator("gt");
        filterCondition1.setFilterField("deviceType");
        filterValue.clear();
        filterValue.add("030");
        filterCondition1.setFilterValue(filterValue);
        filterConditions.add(filterCondition1);
        queryCondition.setFilterConditions(filterConditions);

        PageCondition pageCondition = new PageCondition();
        pageCondition.setPageNum(1);
        pageCondition.setPageSize(6);
        pageCondition.setBeginNum(0);
        queryCondition.setPageCondition(pageCondition);
    }

    @Test
    public void deviceLogListByPage() {
        DeviceParam deviceParam = new DeviceParam();
        when(deviceInfoService.getUserAuth(any())).thenReturn(deviceParam);
        User user = new User();
        user.setId("1");
        when(userFeign.queryCurrentUser(anyString(), anyString())).thenReturn(user);
        deviceLogService.deviceLogListByPage(queryCondition, user, true);
        user.setId("2");
        deviceLogService.deviceLogListByPage(queryCondition, user, true);
        addDataPermission();
        deviceLogService.deviceLogListByPage(queryCondition, user, true);
    }

    @Test
    public void deviceLogCount() {
        User user = new User();
        user.setId("1");
        addDataPermission();
        deviceLogService.deviceLogCount(queryCondition, user);

    }

    @Test
    public void generateQuery() {
        deviceLogService.generateQuery(queryCondition);
    }

    @Test
    public void deviceLogListByPageForPda() {
        addDataPermission();
        deviceLogService.deviceLogListByPageForPda(queryCondition);


    }

    @Test
    public void saveDeviceLog() {
        new Expectations() {
            {
                mongoTemplate.save(anyString);
            }
        };
        DeviceLog deviceLog = new DeviceLog();
        deviceLog.setDeviceType("1");
        DeviceInfoDto deviceInfoDto = new DeviceInfoDto();
        deviceInfoDto.setDeviceId("id");
        deviceInfoDto.setDeviceType("Type");
        deviceInfoDto.setDeviceName("name");
        deviceInfoDto.setDeviceCode("1");
        AreaInfo areaInfo = new AreaInfo();
        areaInfo.setAreaName("wuhan");
        areaInfo.setAreaId("wuhan");
        deviceInfoDto.setAreaInfo(areaInfo);

        Result result = new Result();
        result.setData(deviceInfoDto);
        when(deviceInfoService.getDeviceById(anyString(), any())).thenReturn(result);
        try {
            deviceLogService.saveDeviceLog(deviceLog);
        } catch (Exception e) {

        }
    }

    @Test
    public void deleteDeviceLogByDeviceIds() {
        i18nUtilsExpectation();
        Assert.assertEquals(130201, deviceLogService.deleteDeviceLogByDeviceIds(null).getCode());
        List<String> deviceIdList = new ArrayList<>();
        deviceIdList.add("sx");
        Assert.assertEquals(0, deviceLogService.deleteDeviceLogByDeviceIds(deviceIdList).getCode());
    }

    @Test
    public void exportDeviceLogList() {
        ExportDto exportDto = new ExportDto();
        i18nUtilsExpectation();
        when(systemLanguageUtil.querySystemLanguage()).thenReturn("ZH");
        when(logProcess.generateAddLogToCallParam(LogConstants.LOG_TYPE_OPERATE)).thenReturn(new AddLogBean());
        when(deviceLogExport.insertTask(exportDto, "filink-device-server", "xx"))
                .thenThrow(FilinkExportNoDataException.class)
                .thenThrow(FilinkExportDataTooLargeException.class)
                .thenThrow(FilinkExportTaskNumTooBigException.class)
                .thenThrow(Exception.class)
                .thenReturn(new ExportRequestInfo());

        Result result = deviceLogService.exportDeviceLogList(exportDto);
        Assert.assertEquals(130306, result.getCode());

        result = deviceLogService.exportDeviceLogList(exportDto);
        Assert.assertEquals(130305, result.getCode());

        result = deviceLogService.exportDeviceLogList(exportDto);
        Assert.assertEquals(130307, result.getCode());

        result = deviceLogService.exportDeviceLogList(exportDto);
        Assert.assertEquals(130304, result.getCode());


        result = deviceLogService.exportDeviceLogList(exportDto);
        Assert.assertEquals(0, result.getCode());
    }

    @Test
    public void queryRecentDeviceLogTime() {
        List<DeviceLog> deviceLogList = new ArrayList<>();
        i18nUtilsExpectation();
        Assert.assertEquals(130308, deviceLogService.queryRecentDeviceLogTime("xx").getCode());

    }

    @Test
    public void synchronizeUnlockingStatistics() {
        when(unlockingStatisticsDao.queryMaxStatisticsDate()).thenReturn("20190701");
        try {
            deviceLogService.synchronizeUnlockingStatistics();
        } catch (Exception e) {

        }


    }

    @Test
    public void queryUnlockingCountByDeviceId() {
        deviceLogService.queryUnlockingCountByDeviceId("xx", 1L, 2L);

    }


    private void addDataPermission() {
        DeviceParam deviceParam = new DeviceParam();
        List<String> areaIds = new ArrayList<>();
        areaIds.add("wuhan");
        deviceParam.setAreaIds(areaIds);
        List<String> deviceTypes = new ArrayList<>();
        deviceTypes.add("001");
        deviceParam.setDeviceType("1");
        deviceParam.setDeviceTypes(deviceTypes);
        when(deviceInfoService.getUserAuth(any())).thenReturn(deviceParam);
        User user = new User();
        user.setId("1");
        when(userFeign.queryCurrentUser(any(), any())).thenReturn(user);
    }

    private void i18nUtilsExpectation() {
        new Expectations(I18nUtils.class) {
            {
                I18nUtils.getSystemString(anyString);
                result = "xx";
            }
        };
    }
}