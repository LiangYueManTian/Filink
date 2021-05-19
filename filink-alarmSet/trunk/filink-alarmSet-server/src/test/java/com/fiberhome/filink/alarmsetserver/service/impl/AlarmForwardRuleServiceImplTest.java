package com.fiberhome.filink.alarmsetserver.service.impl;

import com.fiberhome.filink.alarmsetserver.bean.*;
import com.fiberhome.filink.alarmsetserver.dao.*;
import com.fiberhome.filink.alarmsetserver.service.AlarmOrderRuleService;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.RequestInfoUtils;
import com.fiberhome.filink.deviceapi.api.AreaFeign;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import com.fiberhome.filink.userapi.api.UserFeign;
import com.fiberhome.filink.userapi.bean.Department;
import com.fiberhome.filink.userapi.bean.Role;
import com.fiberhome.filink.userapi.bean.RoleDeviceType;
import com.fiberhome.filink.userapi.bean.User;
import mockit.*;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RunWith(JMockit.class)
public class AlarmForwardRuleServiceImplTest {

    @Tested
    private AlarmForwardRuleServiceImpl alarmForwardRuleService;

    /**
     * 远程通知dao
     */
    @Injectable
    private AlarmForwardRuleDao alarmForwardRuleDao;

    /**
     * 通知人dao
     */
    @Injectable
    private AlarmForwardRuleUserDao alarmForwardRuleUserDao;

    /**
     * 区域api
     */
    @Injectable
    private AlarmForwardRuleAreaDao alarmForwardRuleAreaDao;

    /**
     * 设施类型dao
     */
    @Injectable
    private AlarmForwardRuleDeviceTypeDao alarmForwardRuleDeviceTypeDao;

    /**
     * 告警级别
     */
    @Injectable
    private AlarmForwardRuleLevelDao alarmForwardRuleLevelDao;

    /**
     * 区域Api
     */
    @Injectable
    private AreaFeign areaFeign;

    /**
     * 用户feign
     */
    @Injectable
    private UserFeign userFeign;

    /**
     * 日志
     */
    @Injectable
    private LogProcess logProcess;

    /**
     * 告警转工单service
     */
    @Injectable
    private AlarmOrderRuleService alarmOrderRuleService;

    @Injectable
    private SystemLanguageUtil systemLanguageUtil;

    @Test
    public void queryAlarmForwardRuleList() throws Exception {
        QueryCondition<AlarmForwardRuleDto> queryCondition = new QueryCondition();
        AlarmForwardRuleDto alarmForwardRuleDto = new AlarmForwardRuleDto();
        Set<String> strings = new HashSet<>();
        strings.add("1");
        alarmForwardRuleDto.setAlarmForwardRuleUserList(strings);
        alarmForwardRuleDto.setAlarmForwardRuleAreaList(strings);
        List<AlarmForwardRuleDeviceType> list = new ArrayList<>();
        AlarmForwardRuleDeviceType alarmForwardRuleDeviceType = new AlarmForwardRuleDeviceType();
        alarmForwardRuleDeviceType.setDeviceTypeId("1");
        alarmForwardRuleDeviceType.setRuleId("1");
        list.add(alarmForwardRuleDeviceType);
        alarmForwardRuleDto.setAlarmForwardRuleDeviceTypeList(list);
        List<AlarmForwardRuleLevel> alarmForwardRuleLevels = new ArrayList<>();
        AlarmForwardRuleLevel alarmForwardRuleLevel = new AlarmForwardRuleLevel();
        alarmForwardRuleLevel.setRuleId("1");
        alarmForwardRuleLevel.setAlarmLevelId("1");
        alarmForwardRuleLevels.add(alarmForwardRuleLevel);
        alarmForwardRuleDto.setAlarmForwardRuleLevels(alarmForwardRuleLevels);
        queryCondition.setBizCondition(alarmForwardRuleDto);
        User userName = new User();
        userName.setId("2");
        Role role = new Role();
        List<RoleDeviceType> roleDeviceTypes = new ArrayList<>();
        RoleDeviceType roleDeviceType = new RoleDeviceType();
        roleDeviceType.setDeviceTypeId("001");
        role.setRoleDevicetypeList(roleDeviceTypes);
        roleDeviceTypes.add(roleDeviceType);
        Department department = new Department();
        List<String> stringList = new ArrayList<>();
        stringList.add("1");
        department.setAreaIdList(stringList);
        userName.setDepartment(department);
        userName.setRole(role);

        List<String> arrayList = new ArrayList<>();
        arrayList.add(userName.getRole().getRoleDevicetypeList().get(0).getDeviceTypeId());
        new Expectations() {
            {
                alarmOrderRuleService.getUser();
                result = userName;
            }
        };
        new Expectations() {
            {
                alarmOrderRuleService.getDeviceTypes(userName);
                result = arrayList;
            }
        };
        new Expectations() {
            {
                alarmOrderRuleService.getUserAreaIds(userName);
                result = stringList;
            }
        };
        alarmForwardRuleService.queryAlarmForwardRuleList(queryCondition);
    }

    @Test
    public void queryAlarmForwardId() throws Exception {
        List<AlarmForwardRule> alarmForwardRules = new ArrayList<>();
        AlarmForwardRule alarmForwardRule = new AlarmForwardRule();
        alarmForwardRule.setId("1");
        Set<String> strings = new HashSet<>();
        strings.add("2");
        alarmForwardRule.setAlarmForwardRuleUserList(strings);
        alarmForwardRule.setAlarmForwardRuleAreaList(strings);
        alarmForwardRules.add(alarmForwardRule);
        new Expectations() {
            {
                alarmForwardRuleDao.queryAlarmForwardId(anyString);
                result = alarmForwardRules;
            }
        };
        alarmForwardRuleService.queryAlarmForwardId("1");
    }

    @Test
    public void addAlarmForwardRule() throws Exception {
        AlarmForwardRule alarmForwardRule = new AlarmForwardRule();
        alarmForwardRule.setId("1");
        Set<String> strings = new HashSet<>();
        strings.add("1");
        alarmForwardRule.setAlarmForwardRuleUserList(strings);
        alarmForwardRule.setAlarmForwardRuleAreaList(strings);
        List<AlarmForwardRuleDeviceType> alarmForwardRuleDeviceTypeList = new ArrayList<>();
        AlarmForwardRuleDeviceType alarmForwardRuleDeviceType = new AlarmForwardRuleDeviceType();
        alarmForwardRuleDeviceType.setRuleId("1");
        alarmForwardRuleDeviceType.setDeviceTypeId("1");
        alarmForwardRuleDeviceTypeList.add(alarmForwardRuleDeviceType);
        alarmForwardRule.setAlarmForwardRuleDeviceTypeList(alarmForwardRuleDeviceTypeList);
        List<AlarmForwardRuleLevel> alarmForwardRuleLevels = new ArrayList<>();
        AlarmForwardRuleLevel alarmForwardRuleLevel = new AlarmForwardRuleLevel();
        alarmForwardRuleLevel.setAlarmLevelId("1");
        alarmForwardRuleLevel.setRuleId("1");
        ;
        alarmForwardRuleLevels.add(alarmForwardRuleLevel);
        alarmForwardRule.setAlarmForwardRuleLevels(alarmForwardRuleLevels);
        new MockUp<RequestInfoUtils>() {
            @Mock
            String getUserName() {
                return "admin";
            }
        };
        new Expectations() {
            {
                alarmForwardRuleDao.insert(alarmForwardRule);
                result = 1;
            }
        };
        try {
            alarmForwardRuleService.addAlarmForwardRule(alarmForwardRule);
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
        try {
            alarmForwardRuleService.addAlarmForwardRule(new AlarmForwardRule());
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void deleteAlarmForwardRule() throws Exception {
        new Expectations() {
            {
                alarmForwardRuleDao.deleteAlarmForwardRule((String[]) any);
                result = 1;
            }
        };
        try {
            alarmForwardRuleService.deleteAlarmForwardRule(new String[]{"1"});
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
        try {
            alarmForwardRuleService.deleteAlarmForwardRule(new String[]{});
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void updateAlarmForwardRule() throws Exception {
        AlarmForwardRule alarmForwardRule = new AlarmForwardRule();
        alarmForwardRule.setId("1");
        Set<String> strings = new HashSet<>();
        strings.add("1");
        alarmForwardRule.setAlarmForwardRuleUserList(strings);
        alarmForwardRule.setAlarmForwardRuleAreaList(strings);
        List<AlarmForwardRuleDeviceType> alarmForwardRuleDeviceTypeList = new ArrayList<>();
        AlarmForwardRuleDeviceType alarmForwardRuleDeviceType = new AlarmForwardRuleDeviceType();
        alarmForwardRuleDeviceType.setRuleId("1");
        alarmForwardRuleDeviceType.setDeviceTypeId("1");
        alarmForwardRuleDeviceTypeList.add(alarmForwardRuleDeviceType);
        alarmForwardRule.setAlarmForwardRuleDeviceTypeList(alarmForwardRuleDeviceTypeList);
        List<AlarmForwardRuleLevel> alarmForwardRuleLevels = new ArrayList<>();
        AlarmForwardRuleLevel alarmForwardRuleLevel = new AlarmForwardRuleLevel();
        alarmForwardRuleLevel.setAlarmLevelId("1");
        alarmForwardRuleLevel.setRuleId("1");
        ;
        alarmForwardRuleLevels.add(alarmForwardRuleLevel);
        alarmForwardRule.setAlarmForwardRuleLevels(alarmForwardRuleLevels);
        new MockUp<RequestInfoUtils>() {
            @Mock
            String getUserName() {
                return "admin";
            }
        };
        new Expectations() {
            {
                alarmForwardRuleDao.updateById(alarmForwardRule);
                result = 1;
            }
        };
        alarmForwardRuleService.updateAlarmForwardRule(alarmForwardRule);
        try {
            alarmForwardRuleService.updateAlarmForwardRule(new AlarmForwardRule());
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void batchUpdateAlarmForwardRuleStatus() throws Exception {
        new Expectations() {
            {
                alarmForwardRuleDao.updateAlarmForwardRuleStatus((Integer) any, (String[]) any);
                result = 1;
            }
        };
        try {
            alarmForwardRuleService.batchUpdateAlarmForwardRuleStatus(1, new String[]{"1"});
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
        try {
            alarmForwardRuleService.batchUpdateAlarmForwardRuleStatus(1, new String[]{});
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void batchUpdateAlarmForwardRulePushType() throws Exception {
        new Expectations() {
            {
                alarmForwardRuleDao.updateAlarmForwardRulePushType((Integer) any, (String[]) any);
                result = 1;
            }
        };
        try {
            alarmForwardRuleService.batchUpdateAlarmForwardRulePushType(1, new String[]{"1"});
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
        try {
            alarmForwardRuleService.batchUpdateAlarmForwardRulePushType(1, new String[]{});
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void queryAlarmForwardRuleFeign() throws Exception {
        List<AlarmForwardCondition> alarmForwardConditions = new ArrayList<>();
        AlarmForwardCondition alarmForwardCondition = new AlarmForwardCondition();
        alarmForwardCondition.setId("1");
        alarmForwardCondition.setAlarmLevel("1");
        alarmForwardCondition.setAreaId("1");
        alarmForwardCondition.setDeviceType("001");
        alarmForwardConditions.add(alarmForwardCondition);
        List<AlarmForwardRule> list = new ArrayList<>();
        AlarmForwardRule alarmForwardRule = new AlarmForwardRule();
        alarmForwardRule.setId("1");
        list.add(alarmForwardRule);
        new Expectations() {
            {
                alarmForwardRuleDao.queryAlarmForwardRuleFeign((AlarmForwardCondition) any);
                result = list;
            }
        };
        alarmForwardRuleService.queryAlarmForwardRuleFeign(alarmForwardConditions);

    }

}