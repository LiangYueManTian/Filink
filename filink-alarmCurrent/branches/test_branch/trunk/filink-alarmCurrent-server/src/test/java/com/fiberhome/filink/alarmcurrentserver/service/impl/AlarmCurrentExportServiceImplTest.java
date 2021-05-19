package com.fiberhome.filink.alarmcurrentserver.service.impl;

import com.fiberhome.filink.alarmcurrentserver.bean.AlarmCurrent;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmSourceLevelParameter;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmStatisticsGroupInfo;
import com.fiberhome.filink.alarmcurrentserver.constant.AppConstant;
import com.fiberhome.filink.alarmcurrentserver.constant.LogFunctionCodeConstant;
import com.fiberhome.filink.alarmcurrentserver.service.AlarmQueryTemplateService;
import com.fiberhome.filink.alarmcurrentserver.utils.AlarmCurrentExport;
import com.fiberhome.filink.alarmcurrentserver.utils.AlarmTopExport;
import com.fiberhome.filink.alarmhistoryapi.api.AlarmHistoryFeign;
import com.fiberhome.filink.alarmsetapi.api.AlarmSetFeign;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.deviceapi.api.DevicePicFeign;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.exportapi.exception.FilinkExportDataTooLargeException;
import com.fiberhome.filink.exportapi.exception.FilinkExportNoDataException;
import com.fiberhome.filink.exportapi.exception.FilinkExportTaskNumTooBigException;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import com.fiberhome.filink.userapi.api.DepartmentFeign;
import com.fiberhome.filink.userapi.api.UserFeign;
import com.fiberhome.filink.userapi.bean.Department;
import com.fiberhome.filink.userapi.bean.Role;
import com.fiberhome.filink.userapi.bean.RoleDeviceType;
import com.fiberhome.filink.userapi.bean.User;
import com.fiberhome.filink.workflowbusinessapi.api.procbase.ProcBaseFeign;
import java.util.ArrayList;
import java.util.List;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Query;

@RunWith(JMockit.class)
public class AlarmCurrentExportServiceImplTest {

    /**
     * 当前告警导出
     */
    @Injectable
    private AlarmCurrentExport alarmCurrentExport;

    @Tested
    private AlarmCurrentExportServiceImpl alarmCurrentExportService;

    /**
     * 日志api
     */
    @Injectable
    private LogProcess logProcess;

    @Injectable
    private SystemLanguageUtil systemLanguageUtil;

    @Injectable
    private AlarmCurrentServiceImpl alarmCurrentService;

    @Injectable
    private MongoTemplate mongoTemplate;

    @Injectable
    private AlarmStatisticsServiceImpl alarmStatisticsService;

    @Injectable
    private AlarmTopExport alarmTopExport;

    @Injectable
    private AlarmHistoryFeign alarmHistoryFeigm;

    @Injectable
    private DevicePicFeign devicePicFeign;

    @Injectable
    private AlarmSetFeign alarmSetFeign;

    @Injectable
    private ProcBaseFeign procBaseFeign;

    @Injectable
    private AlarmQueryTemplateService alarmQueryTemplateService;

    @Injectable
    private UserFeign userFeign;

    @Injectable
    private DepartmentFeign departmentFeign;

    @Injectable
    private AlarmDisposeServiceImpl alarmDisposeService;

    @Injectable
    private Integer maxExportDataSize = 1000;

    @Test
    public void exportAlarmList() throws Exception {
        ExportDto exportDto = new ExportDto<>();
        Result result = alarmCurrentExportService.exportAlarmList(exportDto);
        Assert.assertTrue(result.getCode() == ResultCode.SUCCESS);
        new Expectations() {
            {
                alarmCurrentExport.insertTask((ExportDto) any, anyString, anyString);
                result = new FilinkExportNoDataException();
            }
        };
        Result result2 = alarmCurrentExportService.exportAlarmList(exportDto);
        Assert.assertTrue(result2.getCode() == LogFunctionCodeConstant.EXPORT_NO_DATA);
        new Expectations() {
            {
                I18nUtils.getSystemString(AppConstant.EXPORT_DATA_TOO_LARGE);
                result = "导出数据{0}条，超过最大限制{1}条";
            }

            {
                alarmCurrentExport.insertTask((ExportDto) any, anyString, anyString);
                result = new FilinkExportDataTooLargeException("1000");
            }
        };
        Result result1 = alarmCurrentExportService.exportAlarmList(exportDto);
        Assert.assertTrue(result1.getCode() == LogFunctionCodeConstant.EXPORT_DATA_TOO_LARGE);
        new Expectations() {
            {
                alarmCurrentExport.insertTask((ExportDto) any, anyString, anyString);
                result = new FilinkExportTaskNumTooBigException();
            }
        };
        Result result3 = alarmCurrentExportService.exportAlarmList(exportDto);
        Assert.assertTrue(result3.getCode() == LogFunctionCodeConstant.EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS);
        new Expectations() {
            {
                alarmCurrentExport.insertTask((ExportDto) any, anyString, anyString);
                result = new NullPointerException();
            }
        };
        Result result4 = alarmCurrentExportService.exportAlarmList(exportDto);
        Assert.assertTrue(result4.getCode() == LogFunctionCodeConstant.FAILED_TO_CREATE_EXPORT_TASK);
    }

    @Test
    public void exportDeviceTop() throws Exception {
        ExportDto exportDto = new ExportDto();
        try {
            alarmCurrentExportService.exportDeviceTop(exportDto);
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void queryAlarmObjectCount() throws Exception {
        User userName = new User();
        userName.setId("1");
        new Expectations() {
            {
                alarmCurrentService.getUser();
                result = userName;
            }
        };
        new Expectations() {
            {
                mongoTemplate.count((Query) any, AlarmCurrent.class);
            }
        };
        alarmCurrentExportService.queryAlarmObjectCount("1");
    }

    @Test
    public void queryAlarmObjectCountTest() throws Exception {
        User userName = new User();
        userName.setId("2");
        Role role = new Role();
        List<RoleDeviceType> list = new ArrayList<>();
        RoleDeviceType roleDeviceType = new RoleDeviceType();
        roleDeviceType.setDeviceTypeId("001");
        role.setRoleDevicetypeList(list);
        list.add(roleDeviceType);
        Department department = new Department();
        List<String> strings = new ArrayList<>();
        strings.add("1");
        department.setAreaIdList(strings);
        userName.setDepartment(department);
        userName.setRole(role);

        List<String> stringList = new ArrayList<>();
        stringList.add(userName.getRole().getRoleDevicetypeList().get(0).getDeviceTypeId());
        new Expectations() {
            {
                alarmCurrentService.getUser();
                result = userName;
            }
        };
        new Expectations() {
            {
                mongoTemplate.count((Query) any, AlarmCurrent.class);
            }
        };
        alarmCurrentExportService.queryAlarmObjectCount("1");
        new Expectations() {
            {
                alarmCurrentService.getDeviceTypes(userName);
                result = stringList;
            }
        };
        new Expectations() {
            {
                alarmCurrentService.getUserAreaIds(userName);
                result = strings;
            }
        };
        alarmCurrentExportService.queryAlarmObjectCount("001");
    }

    @Test
    public void queryAlarmObjectCountHonePage() throws Exception {
        AlarmSourceLevelParameter alarmSourceLevelParameter = new AlarmSourceLevelParameter();
        alarmSourceLevelParameter.setAlarmLevel("1");
        alarmSourceLevelParameter.setId("1");
        alarmSourceLevelParameter.setDeviceId("1");
        User user = new User();
        user.setId("1");
        List<AlarmStatisticsGroupInfo> alarmStatisticsGroupInfos = new ArrayList<>();
        AlarmStatisticsGroupInfo alarmStatisticsGroupInfo = new AlarmStatisticsGroupInfo();
        alarmStatisticsGroupInfo.setGroupArea("001");
        alarmStatisticsGroupInfo.setGroupLevel("1");
        alarmStatisticsGroupInfo.setGroupNum(1);
        alarmStatisticsGroupInfos.add(alarmStatisticsGroupInfo);

        new Expectations() {
            {
                alarmCurrentService.getUser();
                result = user;
            }
        };
        new Expectations() {
            {
                AggregationResults<AlarmStatisticsGroupInfo> results =
                        mongoTemplate.aggregate((Aggregation) any, AppConstant.ALARM_CURRENT, AlarmStatisticsGroupInfo.class);
                result = results;
            }
        };
        alarmCurrentExportService.queryAlarmObjectCountHonePage(alarmSourceLevelParameter);
        User userName = new User();
        userName.setId("2");
        Role role = new Role();
        List<RoleDeviceType> list = new ArrayList<>();
        RoleDeviceType roleDeviceType = new RoleDeviceType();
        roleDeviceType.setDeviceTypeId("001");
        role.setRoleDevicetypeList(list);
        list.add(roleDeviceType);
        Department department = new Department();
        List<String> strings = new ArrayList<>();
        strings.add("1");
        department.setAreaIdList(strings);
        userName.setDepartment(department);
        userName.setRole(role);
        List<String> stringList = new ArrayList<>();
        stringList.add(userName.getRole().getRoleDevicetypeList().get(0).getDeviceTypeId());
        new Expectations() {
            {
                alarmCurrentService.getUser();
                result = userName;
            }
        };
        alarmCurrentExportService.queryAlarmObjectCountHonePage(alarmSourceLevelParameter);
        new Expectations() {
            {
                alarmCurrentService.getDeviceTypes(userName);
                result = stringList;
            }
        };
        new Expectations() {
            {
                alarmCurrentService.getUserAreaIds(userName);
                result = strings;
            }
        };
        alarmCurrentExportService.queryAlarmObjectCountHonePage(alarmSourceLevelParameter);
    }

    /**
     * 首页告警级别更多信息
     *
     * @throws Exception
     */
    @Test
    public void queryAlarmDeviceIdHonePage() throws Exception {
        AlarmSourceLevelParameter alarmSourceLevelParameter = new AlarmSourceLevelParameter();
        alarmSourceLevelParameter.setAlarmLevel("1");
        alarmSourceLevelParameter.setId("1");
        alarmSourceLevelParameter.setDeviceId("1");
        User user = new User();
        user.setId("1");
        new Expectations() {
            {
                alarmCurrentService.getUser();
                result = user;
            }
        };
        new Expectations() {
            {
                AggregationResults<AlarmStatisticsGroupInfo> results =
                        mongoTemplate.aggregate((Aggregation) any, AppConstant.ALARM_CURRENT, AlarmStatisticsGroupInfo.class);
                result = results;
            }
        };
        alarmCurrentExportService.queryAlarmDeviceIdHonePage(new AlarmSourceLevelParameter());
    }

    /**
     * 首页告警告警id查询设施类型
     *
     * @throws Exception
     */
    @Test
    public void queryAlarmIdCountHonePage() throws Exception {
        AlarmSourceLevelParameter alarmSourceLevelParameter = new AlarmSourceLevelParameter();
        alarmSourceLevelParameter.setAlarmLevel("1");
        alarmSourceLevelParameter.setId("1");
        alarmSourceLevelParameter.setDeviceId("1");
        User user = new User();
        user.setId("1");
        new Expectations() {
            {
                alarmCurrentService.getUser();
                result = user;
            }
        };
        new Expectations() {
            {
                AggregationResults<AlarmStatisticsGroupInfo> results =
                        mongoTemplate.aggregate((Aggregation) any, AppConstant.ALARM_CURRENT, AlarmStatisticsGroupInfo.class);
                result = results;
            }
        };
        alarmCurrentExportService.queryAlarmIdCountHonePage(alarmSourceLevelParameter);
        User userName = new User();
        userName.setId("2");
        Role role = new Role();
        List<RoleDeviceType> list = new ArrayList<>();
        RoleDeviceType roleDeviceType = new RoleDeviceType();
        roleDeviceType.setDeviceTypeId("001");
        role.setRoleDevicetypeList(list);
        list.add(roleDeviceType);
        Department department = new Department();
        List<String> strings = new ArrayList<>();
        strings.add("1");
        department.setAreaIdList(strings);
        userName.setDepartment(department);
        userName.setRole(role);
        List<String> stringList = new ArrayList<>();
        stringList.add(userName.getRole().getRoleDevicetypeList().get(0).getDeviceTypeId());
        new Expectations() {
            {
                alarmCurrentService.getUser();
                result = userName;
            }
        };
        alarmCurrentExportService.queryAlarmIdCountHonePage(alarmSourceLevelParameter);
        new Expectations() {
            {
                alarmCurrentService.getDeviceTypes(userName);
                result = stringList;
            }
        };
        new Expectations() {
            {
                alarmCurrentService.getUserAreaIds(userName);
                result = strings;
            }
        };
        alarmCurrentExportService.queryAlarmIdCountHonePage(alarmSourceLevelParameter);
    }

    /**
     * 首页告警告警id查询告警级别
     *
     * @throws Exception
     */
    @Test
    public void queryAlarmIdHonePage() throws Exception {
        AlarmSourceLevelParameter alarmSourceLevelParameter = new AlarmSourceLevelParameter();
        alarmSourceLevelParameter.setAlarmLevel("1");
        alarmSourceLevelParameter.setId("1");
        alarmSourceLevelParameter.setDeviceId("1");
        User user = new User();
        user.setId("1");
        new Expectations() {
            {
                alarmCurrentService.getUser();
                result = user;
            }
        };
        new Expectations() {
            {
                AggregationResults<AlarmStatisticsGroupInfo> results =
                        mongoTemplate.aggregate((Aggregation) any, AppConstant.ALARM_CURRENT, AlarmStatisticsGroupInfo.class);
                result = results;
            }
        };
        alarmCurrentExportService.queryAlarmIdHonePage(new AlarmSourceLevelParameter());
    }

    @Test
    public void getAlarmStatisticsBySourceDto() {
        List<AlarmStatisticsGroupInfo> list = new ArrayList<>();
        AlarmStatisticsGroupInfo alarmStatisticsGroupInfo = new AlarmStatisticsGroupInfo();
        AlarmStatisticsGroupInfo alarmStatisticsGroupInfoOne = new AlarmStatisticsGroupInfo();
        AlarmStatisticsGroupInfo alarmStatisticsGroupInfoTwo = new AlarmStatisticsGroupInfo();
        AlarmStatisticsGroupInfo alarmStatisticsGroupInfoThree = new AlarmStatisticsGroupInfo();
        AlarmStatisticsGroupInfo alarmStatisticsGroupInfoFilter = new AlarmStatisticsGroupInfo();
        alarmStatisticsGroupInfo.setGroupLevel("001");
        alarmStatisticsGroupInfoOne.setGroupLevel("030");
        alarmStatisticsGroupInfoTwo.setGroupLevel("060");
        alarmStatisticsGroupInfoThree.setGroupLevel("090");
        alarmStatisticsGroupInfoFilter.setGroupLevel("150");
        list.add(alarmStatisticsGroupInfo);
        list.add(alarmStatisticsGroupInfoOne);
        list.add(alarmStatisticsGroupInfoTwo);
        list.add(alarmStatisticsGroupInfoThree);
        list.add(alarmStatisticsGroupInfoFilter);
        alarmCurrentExportService.getAlarmStatisticsBySourceDto(list);
    }

}