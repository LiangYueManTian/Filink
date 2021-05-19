package com.fiberhome.filink.alarmcurrentserver.service.impl;

import com.fiberhome.filink.alarmcurrentserver.bean.AlarmCurrent;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmStatus;
import com.fiberhome.filink.alarmcurrentserver.service.AlarmQueryTemplateService;
import com.fiberhome.filink.alarmhistoryapi.api.AlarmHistoryFeign;
import com.fiberhome.filink.alarmhistoryapi.bean.AlarmHistory;
import com.fiberhome.filink.alarmsetapi.api.AlarmSetFeign;
import com.fiberhome.filink.bean.FilterCondition;
import com.fiberhome.filink.bean.PageBean;
import com.fiberhome.filink.bean.PageCondition;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.RequestInfoUtils;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.deviceapi.api.DevicePicFeign;
import com.fiberhome.filink.deviceapi.bean.PicRelationInfo;
import com.fiberhome.filink.filinklockapi.feign.ControlFeign;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import com.fiberhome.filink.userapi.api.DepartmentFeign;
import com.fiberhome.filink.userapi.api.UserFeign;
import com.fiberhome.filink.userapi.bean.Department;
import com.fiberhome.filink.userapi.bean.Role;
import com.fiberhome.filink.userapi.bean.RoleDeviceType;
import com.fiberhome.filink.userapi.bean.User;
import com.fiberhome.filink.workflowbusinessapi.api.procbase.ProcBaseFeign;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mock;
import mockit.MockUp;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;


@RunWith(JMockit.class)
public class AlarmCurrentServiceImplTest {

    /**
     * 测试Service
     */
    @Tested
    private AlarmCurrentServiceImpl alarmCurrentService;

    /**
     * mongodb实现类
     */
    @Injectable
    private MongoTemplate mongoTemplate;

    /**
     * 日志api
     */
    @Injectable
    private LogProcess logProcess;

    /**
     * 历史告警api
     */
    @Injectable
    private AlarmHistoryFeign alarmHistoryFeigm;

    /**
     * 设施Feign
     */
    @Injectable
    private DevicePicFeign devicePicFeign;

    /**
     * 告警设置Feign
     */
    @Injectable
    private AlarmSetFeign alarmSetFeign;

    /**
     * 工单Feign
     */
    @Injectable
    private ProcBaseFeign procBaseFeign;

    /**
     * 当前告警模板
     */
    @Injectable
    private AlarmQueryTemplateService alarmQueryTemplateService;

    /**
     * 用户fegin
     */
    @Injectable
    private UserFeign userFeign;

    /**
     * 部门fegin
     */
    @Injectable
    private DepartmentFeign departmentFeign;

    @Injectable
    private AlarmDisposeServiceImpl alarmDisposeService;

    @Injectable
    private SystemLanguageUtil systemLanguageUtil;

    @Injectable
    private ControlFeign controlFeign;

    /**
     * 查询当前告警列表
     */
    @Test
    public void queryAlarmCurrentListTest() {
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
        new Expectations() {
            {
                userFeign.queryCurrentUser(anyString, anyString);
                result = userName;
            }
        };
        QueryCondition queryCondition = new QueryCondition();
        PageCondition pageCondition = new PageCondition();
        pageCondition.setPageNum(1);
        pageCondition.setPageSize(10);
        queryCondition.setPageCondition(pageCondition);
        List<FilterCondition> filterConditionList = new ArrayList<>();
        FilterCondition filterCondition = new FilterCondition();
        filterCondition.setFilterField("alarmSourceTypeId");
        filterCondition.setFilterValue("001");
        filterCondition.setOperator("like");
        filterConditionList.add(filterCondition);
        queryCondition.setFilterConditions(filterConditionList);
        User user = alarmCurrentService.getUser();
        PageBean result = alarmCurrentService.queryAlarmCurrentList(queryCondition, user, true);
//        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    /**
     * 查询单个当前告警信息
     */
    @Test
    public void queryAlarmCurrentInfoByIdTest() {
        Result result = alarmCurrentService.queryAlarmCurrentInfoById(null);
        String alarmId = "1";
        new Expectations() {
            {
                mongoTemplate.findById((Query) any, AlarmCurrent.class);
                result = null;
            }
        };
        Result resultOne = alarmCurrentService.queryAlarmCurrentInfoById(alarmId);
    }

    /**
     * 批量修改当前告警备注信息
     */
    @Test
    public void updateAlarmRemarkTest() {
        List<AlarmCurrent> list = new ArrayList<>();
        Result resultOne = alarmCurrentService.batchUpdateAlarmRemark(list);
        new Expectations() {
            {
                mongoTemplate.updateFirst((Query) any, (Update) any, AlarmCurrent.class);
                result = null;
            }
        };
        List<AlarmCurrent> lists = new ArrayList<AlarmCurrent>();
        AlarmCurrent alarmCurrents = new AlarmCurrent();
        alarmCurrents.setId("1");
        alarmCurrents.setAreaName("code");
        alarmCurrents.setAlarmCode("code");
        alarmCurrents.setAlarmBeginTime(111L);
        alarmCurrents.setAlarmSourceTypeId("1");
        alarmCurrents.setAlarmSourceType("001");;
        alarmCurrents.setAlarmObject("11");
        alarmCurrents.setAreaId("1");
        alarmCurrents.setResponsibleDepartmentId("1");
        alarmCurrents.setResponsibleDepartment("1");
        alarmCurrents.setAlarmNearTime(0L);
        alarmCurrents.setAlarmSystemNearTime(0L);
        alarmCurrents.setAlarmName("1");
        alarmCurrents.setControlId("1");
        alarmCurrents.setAlarmHappenCount(1);
        alarmCurrents.setAlarmType(1);
        alarmCurrents.setAlarmContinousTime(0);
        alarmCurrents.setDoorNumber("1");
        alarmCurrents.setDoorName("1");
        alarmCurrents.setAlarmCleanStatus(1);
        alarmCurrents.setAlarmConfirmStatus(1);
        alarmCurrents.setAlarmFixedLevel("1");
        alarmCurrents.setIsOrder(true);
        alarmCurrents.setAlarmProcessing("1");
        alarmCurrents.setExtraMsg("1");
        alarmCurrents.setIsPicture(true);
        alarmCurrents.setAlarmConfirmPeopleNickname("1");
        alarmCurrents.setAlarmConfirmPeopleId("1");
        alarmCurrents.setAlarmCleanPeopleNickname("1");
        alarmCurrents.setAlarmCleanPeopleId("1");
        alarmCurrents.setPrompt("1");
        alarmCurrents.setTrapOid("1");
        alarmCurrents.setAlarmContent("1");
        alarmCurrents.setAlarmCleanType(1);
        alarmCurrents.setAddress("1");
        alarmCurrents.setAlarmCleanTime(0L);
        alarmCurrents.setAlarmConfirmTime(0L);
        alarmCurrents.setAlarmNameId("1");
        alarmCurrents.setAlarmSystemTime(0L);
        alarmCurrents.setRemark("11");
        lists.add(alarmCurrents);
        Result result = alarmCurrentService.batchUpdateAlarmRemark(lists);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    /**
     * 批量设置当前告警的告警确认状态
     */
    @Test
    public void updateAlarmConfirmStatusTest() {
        AlarmStatus alarmStatus = new AlarmStatus();
        List<AlarmCurrent> lists = new ArrayList<AlarmCurrent>();
        AlarmCurrent alarmCurrents = new AlarmCurrent();
        alarmCurrents.setId("1");
        lists.add(alarmCurrents);
        alarmStatus.setAlarmCurrents(lists);
        alarmStatus.setUserId("1");
        alarmStatus.setToken("123456");
        new Expectations() {
            {
                mongoTemplate.find((Query) any, AlarmCurrent.class);
                result = 1;
            }
        };
        try {
            alarmCurrentService.batchUpdateAlarmConfirmStatus(lists);
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
        new Expectations() {
            {
                mongoTemplate.updateFirst((Query) any, (Update) any, AlarmCurrent.class);
                result = null;
            }
        };
        new Expectations() {
            {
                mongoTemplate.find((Query) any, AlarmCurrent.class);
                result = 1;
            }
        };
        new MockUp<RequestInfoUtils>() {
            @Mock
            String getUserId() {
                return "1";
            }
        };
        new MockUp<RequestInfoUtils>() {
            @Mock
            String getUserName() {
                return "admin";
            }
        };
        Result result = alarmCurrentService.batchUpdateAlarmConfirmStatus(lists);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());

    }

    /**
     * 批量设置当前告警的告警清除状态
     */
    @Test
    public void updateAlarmCleanStatusTest() {
        AlarmStatus alarmStatus = new AlarmStatus();
        List<AlarmCurrent> lists = new ArrayList<AlarmCurrent>();
        AlarmCurrent alarmCurrents = new AlarmCurrent();
        alarmCurrents.setId("1");
        alarmCurrents.setControlId("code");
        alarmCurrents.setAlarmCode("gg");
        alarmCurrents.setAlarmName("cc");
        lists.add(alarmCurrents);
        alarmStatus.setAlarmCurrents(lists);
        alarmStatus.setUserId("1");
        alarmStatus.setToken("admin");
        new Expectations() {
            {
                mongoTemplate.updateFirst((Query) any, (Update) any, AlarmCurrent.class);
                result = null;
            }
        };
        new Expectations() {
            {
                mongoTemplate.find((Query) any, AlarmCurrent.class);
                result = 1;
            }
        };
        new Expectations() {
            {
                mongoTemplate.findOne((Query) any, AlarmCurrent.class);
                result = alarmCurrents;
            }
        };

        new MockUp<RequestInfoUtils>() {
            @Mock
            String getUserId() {
                return "1";
            }
        };
        new MockUp<RequestInfoUtils>() {
            @Mock
            String getUserName() {
                return "admin";
            }
        };
        new MockUp<RedisUtils>() {
            @Mock
            void set(String key, Object value, long time) {
            }
        };

        Result resultOne = alarmCurrentService.batchUpdateAlarmCleanStatus(lists);
        Assert.assertTrue(resultOne.getCode() == ResultUtils.success().getCode());
        new Expectations() {
            {
                mongoTemplate.find((Query) any, AlarmCurrent.class);
                result = null;
            }
        };
        try {
            alarmCurrentService.batchUpdateAlarmConfirmStatus(lists);
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
    }

    /**
     * 查询各级别告警总数
     */
    @Test
    public void queryEveryAlarmCountTest() {
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
                userFeign.queryCurrentUser(anyString, anyString);
                result = userName;
            }
        };
        new Expectations() {
            {
                mongoTemplate.count((Query) any, AlarmCurrent.class);
            }
        };
        Result result = alarmCurrentService.queryEveryAlarmCount("1");
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
        User user = new User();
        user.setId("2");
        department.setAreaIdList(strings);
        user.setDepartment(department);
        user.setRole(role);
        new Expectations() {
            {
                userFeign.queryCurrentUser(anyString, anyString);
                result = user;
            }
        };
        alarmCurrentService.queryEveryAlarmCount("1");
    }

    @Test
    public void queryEveryAlarmCountTests() {
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
        new Expectations() {
            {
                userFeign.queryCurrentUser(anyString, anyString);
                result = userName;
            }
        };
        Result result = alarmCurrentService.queryEveryAlarmCount("1");
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    /**
     * 查询设备信息是否存在
     */
    @Test
    public void queryAlarmSourceTest() {
        new Expectations() {
            {
                List<AlarmCurrent> list = mongoTemplate.find((Query) any, AlarmCurrent.class);
                result = 1;
            }
        };
        List<String> list = new ArrayList<>();
        list.add("1");
        List<String> listTwo = alarmCurrentService.queryAlarmSourceForFeign(list);
        new Expectations() {
            {
                List<AlarmCurrent> list = mongoTemplate.find((Query) any, AlarmCurrent.class);
                result = null;
            }
        };
        List<String> listOne = alarmCurrentService.queryAlarmSourceForFeign(list);
    }

    /**
     * 查询设备信息是否存在
     */
    @Test
    public void queryAreaTest() {
        new Expectations() {
            {
                List<AlarmCurrent> list = mongoTemplate.find((Query) any, AlarmCurrent.class);
                result = 1;
            }
        };
        List<String> list = new ArrayList<>();
        list.add("1");
        List<String> listOne = alarmCurrentService.queryAreaForFeign(list);
        new Expectations() {
            {
                List<AlarmCurrent> list = mongoTemplate.find((Query) any, AlarmCurrent.class);
                result = null;
            }
        };
        List<String> listTwo = alarmCurrentService.queryAreaForFeign(list);
    }

    /**
     * 查询单位id信息
     */
    @Test
    public void queryDepartmentIdTest() {
        List<AlarmCurrent> alarmCurrentList = new ArrayList<>();
        AlarmCurrent alarmCurrent = new AlarmCurrent();
        alarmCurrent.setId("1");
        alarmCurrent.setResponsibleDepartment("qq");
        alarmCurrent.setResponsibleDepartmentId("gg");
        alarmCurrentList.add(alarmCurrent);
        new Expectations() {
            {
                mongoTemplate.find((Query) any, AlarmCurrent.class);
                result = alarmCurrentList;
            }
        };
        List<Department> departments = new ArrayList<>();
        Department department = new Department();
        department.setId("1");
        department.setDeptName("name");
        departments.add(department);
        new Expectations() {
            {
                departmentFeign.queryDepartmentFeignById((List<String>) any);
                result = departments;
            }
        };
        List<String> list = new ArrayList<>();
        list.add("1");
        Result result = alarmCurrentService.queryDepartmentId(list);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    /**
     * 查询告警类型
     */
    @Test
    public void queryIsStatusTest() {
        List<AlarmCurrent> alarmCurrentList = new ArrayList<>();
        AlarmCurrent alarmCurrent = new AlarmCurrent();
        alarmCurrent.setResponsibleDepartment("qq");
        alarmCurrent.setResponsibleDepartmentId("gg");
        alarmCurrentList.add(alarmCurrent);
        new Expectations() {
            {
                List<AlarmCurrent> list = mongoTemplate.find((Query) any, AlarmCurrent.class);
                result = alarmCurrentList;
            }
        };
        Result result = alarmCurrentService.queryIsStatus("1");
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    /**
     * 查询告警类型
     */
    @Test
    public void queryIsStatusTestOne() {
        List<AlarmHistory> alarmHistoryList = new ArrayList<>();
        AlarmHistory alarmHistory = new AlarmHistory();
        alarmHistory.setId("1");
        alarmHistoryList.add(alarmHistory);
        new Expectations() {
            {
                List<AlarmHistory> alarmHistories = alarmHistoryFeigm.queryAlarmHistoryByIdFeign(anyString);
                result = alarmHistoryList;
            }
        };
        Result result = alarmCurrentService.queryIsStatus("1");
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    /**
     * 查询当前告警信息
     */
    @Test
    public void queryAlarmCurrentByIdsFeignTest() {
        new Expectations() {
            {
                AlarmCurrent alarmCurrent = mongoTemplate.findById((Query) any, AlarmCurrent.class);
            }
        };
        List<String> list = new ArrayList<>();
        list.add("1");
        alarmCurrentService.queryAlarmCurrentByIdsFeign(list);
    }

    /**
     * 设施id查询告警信息
     */
    @Test
    public void queryAlarmDeviceIdTest() {
        List<AlarmCurrent> alarmCurrentList = new ArrayList<>();
        AlarmCurrent alarmCurrent = new AlarmCurrent();
        alarmCurrent.setId("1");
        alarmCurrent.setAlarmFixedLevel("1");
        alarmCurrent.setAlarmNearTime(111111L);
        alarmCurrent.setAlarmSource("1");
        alarmCurrentList.add(alarmCurrent);

        Result resultOne = new Result();
        List<PicRelationInfo> picRelationInfoList = new ArrayList<>();
        PicRelationInfo picRelationInfo = new PicRelationInfo();
        picRelationInfo.setDeviceId("1");
        picRelationInfo.setDevicePicId("1");
        picRelationInfo.setResourceId("1");
        picRelationInfoList.add(picRelationInfo);
        resultOne.setData(picRelationInfoList);

        Map map = new HashMap();
        map.put("1", "1");
        new Expectations() {
            {
                List<AlarmCurrent> alarmCurrents = mongoTemplate.find((Query) any, AlarmCurrent.class);
                result = alarmCurrentList;
            }
        };
        new Expectations() {
            {
                procBaseFeign.queryExistsProcForAlarmList((List<String>) any);
                result = map;
            }
        };
        new Expectations() {
            {
                devicePicFeign.getPicUrlByAlarmIds((List<String>) any);
                result = ResultUtils.success(picRelationInfoList);
            }
        };
        Result result = alarmCurrentService.queryAlarmDeviceId("1");
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    /**
     * 查询告警门信息
     */
    @Test
    public void queryAlarmDoorTest() {
        AlarmCurrent alarmCurrent = new AlarmCurrent();
        alarmCurrent.setId("1");
        new Expectations() {
            {
                AlarmCurrent alarmCurrents = mongoTemplate.findOne((Query) any, AlarmCurrent.class);
                result = alarmCurrent;
            }
        };
        List<String> list = new ArrayList<>();
        list.add("1");
        alarmCurrentService.queryAlarmDoor(list);
    }

    /**
     * 删除设施相关信息
     */
    @Test
    public void deleteAlarmsTest() {
        new Expectations() {
            {
                mongoTemplate.remove((Query) any, AlarmCurrent.class);
            }
        };
        List<String> list = new ArrayList<>();
        list.add("1");
        Result result = alarmCurrentService.deleteAlarms(list);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    /**
     * 根据单位id查询告警信息
     */
    @Test
    public void queryAlarmDepartmentFeignTest() {
        AlarmCurrent alarmCurrent = new AlarmCurrent();
        alarmCurrent.setId("1");
        new Expectations() {
            {
                mongoTemplate.findOne((Query) any, AlarmCurrent.class);
                result = alarmCurrent;
            }
        };
        new Expectations() {
            {
                mongoTemplate.findOne((Query) any, AlarmCurrent.class);
                result = null;
            }
        };
        List<String> list = new ArrayList<>();
        list.add("1");
        alarmCurrentService.queryAlarmDepartmentFeign(list);
    }

    /**
     * 告警设施id查询c当前告警ode信息
     */
    @Test
    public void queryAlarmDeviceIdCodeTest() {
        new Expectations() {
            {
                mongoTemplate.find((Query) any, AlarmCurrent.class);
            }
        };
        alarmCurrentService.queryAlarmDeviceIdCode("1");
    }

    @Test
    public void queryAlarmCurrentPageTest() {
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
        new Expectations() {
            {
                userFeign.queryCurrentUser(anyString, anyString);
                result = userName;
            }
        };
        QueryCondition queryCondition = new QueryCondition();
        PageCondition pageCondition = new PageCondition();
        pageCondition.setPageNum(1);
        pageCondition.setPageSize(10);
        queryCondition.setPageCondition(pageCondition);
        List<FilterCondition> filterConditionList = new ArrayList<>();
        FilterCondition filterCondition = new FilterCondition();
        filterCondition.setFilterField("alarmSourceTypeId");
        filterCondition.setFilterValue("001");
        filterCondition.setOperator("like");
        filterConditionList.add(filterCondition);
        queryCondition.setFilterConditions(filterConditionList);
        Result result = alarmCurrentService.queryAlarmCurrentPage(queryCondition);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void getCount() {
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
        new Expectations() {
            {
                mongoTemplate.count((Query) any, AlarmCurrent.class);
            }
        };
        alarmCurrentService.getCount(new QueryCondition(), userName);
    }

    @Test
    public void getExportUser() {
        alarmCurrentService.getExportUser();
    }
}
