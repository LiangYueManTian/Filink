package com.fiberhome.filink.alarmhistoryserver.service.impl;

import com.fiberhome.filink.alarmhistoryserver.bean.AlarmHistory;
import com.fiberhome.filink.bean.FilterCondition;
import com.fiberhome.filink.bean.PageBean;
import com.fiberhome.filink.bean.PageCondition;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.deviceapi.api.DevicePicFeign;
import com.fiberhome.filink.deviceapi.bean.PicRelationInfo;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

@RunWith(JMockit.class)
public class AlarmHistoryServiceImplTest {
    /**
     * controller测试
     */
    @Tested
    private AlarmHistoryServiceImpl alarmHistoryService;

    /**
     * 注入的dao
     */
    @Injectable
    private MongoTemplate mongoTemplate;

    @Mocked
    private I18nUtils i18nUtils;

    @Injectable
    private LogProcess logProcess;

    @Injectable
    private DevicePicFeign devicePicFeign;

    @Injectable
    private ProcBaseFeign procBaseFeign;

    @Injectable
    private UserFeign userFeign;

    @Injectable
    private DepartmentFeign departmentFeign;

    @Injectable
    private SystemLanguageUtil systemLanguageUtil;

    /**
     * 查询历史告警列表信息
     */
    @Test
    public void queryAlarmHistoryListTest() {
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
        List<FilterCondition> filterConditionList = new ArrayList<FilterCondition>();
        FilterCondition filterCondition = new FilterCondition();
        filterCondition.setFilterField("id");
        filterCondition.setFilterValue("1");
        filterCondition.setOperator("like");
        filterConditionList.add(filterCondition);
        queryCondition.setFilterConditions(filterConditionList);
        User user = alarmHistoryService.getUser();
        PageBean pageBean = alarmHistoryService.queryAlarmHistoryList(queryCondition, user, true);
    }

    /**
     * 查询单个历史告警的信息
     */
    @Test
    public void queryAlarmHistoryInfoByIdTest() {
        new Expectations() {
            {
                mongoTemplate.findById((Query) any, AlarmHistory.class);
            }
        };
        Result result = alarmHistoryService.queryAlarmHistoryInfoById("1");
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
//        Result resultOne = alarmHistoryService.queryAlarmHistoryInfoById(null);
    }

    /**
     * 查询单个历史告警的信息
     */
    @Test
    public void queryAlarmHistoryByIdFeignTest() {
        new Expectations() {
            {
                mongoTemplate.find((Query) any, AlarmHistory.class);
            }
        };
        alarmHistoryService.queryAlarmHistoryById("1");
        alarmHistoryService.queryAlarmHistoryById(null);
    }

    /**
     * 根据设施id查询告警信息
     */
    @Test
    public void queryAlarmHistoryDeviceIdTest() {
        AlarmHistory alarmHistory = new AlarmHistory();
        alarmHistory.setId("1");
        new Expectations() {
            {
                mongoTemplate.find((Query) any, AlarmHistory.class);
                result = alarmHistory;
            }
        };
        Result resultOne = new Result();
        List<PicRelationInfo> picRelationInfoList = new ArrayList<>();
        PicRelationInfo picRelationInfo = new PicRelationInfo();
        picRelationInfo.setDeviceId("1");
        picRelationInfo.setDevicePicId("1");
        picRelationInfo.setResourceId("1");
        picRelationInfoList.add(picRelationInfo);
        resultOne.setData(picRelationInfoList);
        new Expectations() {
            {
                devicePicFeign.getPicUrlByAlarmIds((List<String>) any);
                result = ResultUtils.success(picRelationInfoList);
            }
        };
        Map map = new HashMap();
        map.put("1", "1");
        new Expectations() {
            {
                procBaseFeign.queryExistsProcForAlarmList((List<String>) any);
                result = map;
            }
        };
        Result result = alarmHistoryService.queryAlarmHistoryDeviceId("1");
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
        alarmHistoryService.queryAlarmHistoryDeviceId(null);
    }

    /**
     * 根据id查询历史告警信息
     */
    @Test
    public void queryAlarmHistoryByIds() {
        AlarmHistory alarmHistory = new AlarmHistory();
        alarmHistory.setId("1");
        new Expectations() {
            {
                mongoTemplate.find((Query) any, AlarmHistory.class);
                result = alarmHistory;
            }
        };
        List<String> list = new ArrayList<>();
        list.add("1");
        alarmHistoryService.queryAlarmHistoryByIds(list);
    }

    @Test
    public void queryDepartmentHistory() {
        AlarmHistory alarmHistory = new AlarmHistory();
        alarmHistory.setId("1");
        alarmHistory.setResponsibleDepartmentId("1");
        new Expectations() {
            {
                mongoTemplate.find((Query) any, AlarmHistory.class);
                result = alarmHistory;
            }
        };
        List<Department> departments = new ArrayList<>();
        Department department = new Department();
        department.setId("1");
        department.setDeptName("1");
        departments.add(department);
        new Expectations(){
            {
                departmentFeign.queryDepartmentFeignById((List<String>) any);
                result = departments;
            }
        };
        List<String> list = new ArrayList<>();
        list.add("1");
        alarmHistoryService.queryDepartmentHistory(list);
    }

    @Test
    public void batchUpdateAlarmRemark() {
        List<AlarmHistory> alarmHistoryList = new ArrayList<>();
        AlarmHistory alarmHistory1 = new AlarmHistory();
        alarmHistory1.setId(null);
        alarmHistoryList.add(alarmHistory1);
        try {
            alarmHistoryService.batchUpdateAlarmRemark(alarmHistoryList);
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
        List<AlarmHistory> alarmHistories = new ArrayList<>();
        AlarmHistory alarmHistory = new AlarmHistory();
        alarmHistory.setId("1");
        alarmHistories.add(alarmHistory);
        alarmHistoryService.batchUpdateAlarmRemark(alarmHistories);
    }

    @Test
    public void insertAlarmHistoryFeign() {
        AlarmHistory alarmHistory = new AlarmHistory();
        alarmHistory.setId("1");
        alarmHistoryService.insertAlarmHistoryFeign(alarmHistory);
    }

    @Test
    public void deleteAlarmHistory() {
        List<AlarmHistory> alarmHistories = new ArrayList<>();
        AlarmHistory alarmHistory = new AlarmHistory();
        alarmHistory.setId("1");
        alarmHistories.add(alarmHistory);
        alarmHistoryService.insertAlarmHistoryList(alarmHistories);
    }

    @Test
    public void insertAlarmHistoryList() {
        List<String> deviceIds = new ArrayList<>();
        deviceIds.add("1");
        alarmHistoryService.deleteAlarmHistory(deviceIds);
    }
}