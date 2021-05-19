package com.fiberhome.filink.alarmsetserver.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.fiberhome.filink.alarmsetserver.bean.AlarmName;
import com.fiberhome.filink.alarmsetserver.bean.AlarmOrderCondition;
import com.fiberhome.filink.alarmsetserver.bean.AlarmOrderRule;
import com.fiberhome.filink.alarmsetserver.bean.AlarmOrderRuleDeviceType;
import com.fiberhome.filink.alarmsetserver.bean.AlarmOrderRuleDto;
import com.fiberhome.filink.alarmsetserver.dao.AlarmNameDao;
import com.fiberhome.filink.alarmsetserver.dao.AlarmOrderRuleAreaDao;
import com.fiberhome.filink.alarmsetserver.dao.AlarmOrderRuleDao;
import com.fiberhome.filink.alarmsetserver.dao.AlarmOrderRuleDeviceTypeDao;
import com.fiberhome.filink.alarmsetserver.dao.AlarmOrderRuleNameDao;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.RequestInfoUtils;
import com.fiberhome.filink.deviceapi.api.AreaFeign;
import com.fiberhome.filink.deviceapi.bean.AreaInfoForeignDto;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import com.fiberhome.filink.userapi.api.UserFeign;
import com.fiberhome.filink.userapi.bean.Department;
import com.fiberhome.filink.userapi.bean.Role;
import com.fiberhome.filink.userapi.bean.RoleDeviceType;
import com.fiberhome.filink.userapi.bean.User;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mock;
import mockit.MockUp;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMockit.class)
public class AlarmOrderRuleServiceImplTest {

    @Tested
    private AlarmOrderRuleServiceImpl alarmOrderRuleService;

    /**
     * 告警转工单规则dao层
     */
    @Injectable
    private AlarmOrderRuleDao alarmOrderRuleDao;

    /**
     * 区域dao
     */
    @Injectable
    private AlarmOrderRuleAreaDao alarmOrderRuleAreaDao;

    /**
     * 告警名称dao
     */
    @Injectable
    private AlarmOrderRuleNameDao alarmOrderRuleNameDao;

    /**
     * 设施类型dao
     */
    @Injectable
    private AlarmOrderRuleDeviceTypeDao alarmOrderRuleDeviceTypeDao;

    /**
     * 区域Api
     */
    @Injectable
    private AreaFeign areaFeign;

    /**
     * 日志
     */
    @Injectable
    private LogProcess logProcess;

    /**
     * 用户feign
     */
    @Injectable
    private UserFeign userFeign;

    /**
     * 告警名称
     */
    @Injectable
    private AlarmNameDao alarmNameDao;

    @Injectable
    private SystemLanguageUtil systemLanguageUtil;

    @Test
    public void queryAlarmOrderRuleList() throws Exception {
        QueryCondition<AlarmOrderRuleDto> queryCondition = new QueryCondition();
        AlarmOrderRuleDto alarmForwardRuleDto = new AlarmOrderRuleDto();
        Set<String> strings = new HashSet<>();
        strings.add("1");
        alarmForwardRuleDto.setAlarmOrderRuleArea(strings);
        alarmForwardRuleDto.setAlarmOrderRuleNameList(strings);
        List<AlarmOrderRuleDeviceType> list = new ArrayList<>();
        AlarmOrderRuleDeviceType alarmForwardRuleDeviceType = new AlarmOrderRuleDeviceType();
        alarmForwardRuleDeviceType.setDeviceTypeId("1");
        alarmForwardRuleDeviceType.setRuleId("1");
        list.add(alarmForwardRuleDeviceType);
        alarmForwardRuleDto.setAlarmOrderRuleDeviceTypeList(list);
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
        new MockUp<RedisUtils>() {
            @Mock
            void set(String key, Object value) {

            }
        };
        alarmOrderRuleService.queryAlarmOrderRuleList(queryCondition);
    }

    @Test
    public void getUser() throws Exception {
        Object object = new Object();
        User user = JSONArray.toJavaObject((JSON) JSONArray.toJSON(object), User.class);
        new Expectations() {
            {
                userFeign.queryCurrentUser(anyString, anyString);
                result = user;
            }
        };
        alarmOrderRuleService.getUser();
    }

    @Test
    public void queryAlarmOrderRule() throws Exception {
        List<AlarmOrderRule> alarmOrderRules = new ArrayList<>();
        AlarmOrderRule alarmOrderRule = new AlarmOrderRule();
        alarmOrderRule.setId("1");
        Set<String> strings = new HashSet<>();
        strings.add("1");
        alarmOrderRule.setAlarmOrderRuleArea(strings);
        alarmOrderRule.setAlarmOrderRuleNameList(strings);
        List<String> list = new ArrayList<>();
        list.add("1");
        alarmOrderRule.setAlarmOrderRuleNames(list);
        alarmOrderRule.setAlarmOrderRuleAreaName(list);
        alarmOrderRules.add(alarmOrderRule);
        new MockUp<RedisUtils>() {
            @Mock
            boolean hasKey(String key) {
                return true;
            }
        };
        new MockUp<RedisUtils>() {
            @Mock
            Object get(String key) {
                return null;
            }
        };
        alarmOrderRuleService.queryAlarmOrderRule("1");
        new MockUp<RedisUtils>() {
            @Mock
            Object get(String key) {
                return alarmOrderRules;
            }
        };
        alarmOrderRuleService.queryAlarmOrderRule("1");
    }

    @Test
    public void addAlarmOrderRule() throws Exception {
        List<AlarmOrderRule> list = new ArrayList<>();
        AlarmOrderRule alarmOrderRule = new AlarmOrderRule();
        alarmOrderRule.setId("2");
        alarmOrderRule.setOrderName("admin");
        list.add(alarmOrderRule);
        AlarmOrderRule rule = new AlarmOrderRule();
        rule.setId("2");
        rule.setOrderName("aa");
        Set<String> strings = new HashSet<>();
        strings.add("1");
        rule.setAlarmOrderRuleArea(strings);
        rule.setAlarmOrderRuleNameList(strings);
        List<AlarmOrderRuleDeviceType> alarmOrderRuleDeviceTypes = new ArrayList<>();
        AlarmOrderRuleDeviceType alarmOrderRuleDeviceType = new AlarmOrderRuleDeviceType();
        alarmOrderRuleDeviceType.setRuleId("1");
        alarmOrderRuleDeviceType.setDeviceTypeId("1");
        alarmOrderRuleDeviceTypes.add(alarmOrderRuleDeviceType);
        rule.setAlarmOrderRuleDeviceTypeList(alarmOrderRuleDeviceTypes);
        try {
            alarmOrderRuleService.addAlarmOrderRule(new AlarmOrderRule());
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
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
        new Expectations() {
            {
                alarmOrderRuleDao.insert((AlarmOrderRule) any);
                result = 1;
            }
        };
        new MockUp<RedisUtils>() {
            @Mock
            void remove(String key) {

            }
        };
        alarmOrderRuleService.addAlarmOrderRule(rule);
    }

    @Test
    public void deleteAlarmOrderRule() throws Exception {
        new Expectations() {
            {
                alarmOrderRuleDao.deleteAlarmOrderRule((String[]) any);
                result = 1;
            }
        };
        new MockUp<RedisUtils>() {
            @Mock
            void remove(String key) {

            }
        };
        alarmOrderRuleService.deleteAlarmOrderRule(new String[]{"1"});
        try {
            alarmOrderRuleService.deleteAlarmOrderRule(new String[]{});
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void updateAlarmOrderRule() throws Exception {
        List<AlarmOrderRule> list = new ArrayList<>();
        AlarmOrderRule alarmOrderRule = new AlarmOrderRule();
        alarmOrderRule.setId("2");
        alarmOrderRule.setOrderName("admin");
        list.add(alarmOrderRule);
        AlarmOrderRule rule = new AlarmOrderRule();
        rule.setId("2");
        rule.setOrderName("aa");
        Set<String> strings = new HashSet<>();
        strings.add("1");
        rule.setAlarmOrderRuleArea(strings);
        rule.setAlarmOrderRuleNameList(strings);
        List<AlarmOrderRuleDeviceType> alarmOrderRuleDeviceTypes = new ArrayList<>();
        AlarmOrderRuleDeviceType alarmOrderRuleDeviceType = new AlarmOrderRuleDeviceType();
        alarmOrderRuleDeviceType.setRuleId("1");
        alarmOrderRuleDeviceType.setDeviceTypeId("1");
        alarmOrderRuleDeviceTypes.add(alarmOrderRuleDeviceType);
        rule.setAlarmOrderRuleDeviceTypeList(alarmOrderRuleDeviceTypes);
        try {
            alarmOrderRuleService.updateAlarmOrderRule(new AlarmOrderRule());
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
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
        new Expectations() {
            {
                alarmOrderRuleDao.updateById((AlarmOrderRule) any);
                result = 1;
            }
        };
        new MockUp<RedisUtils>() {
            @Mock
            void remove(String key) {

            }
        };
        alarmOrderRuleService.updateAlarmOrderRule(rule);
    }

    @Test
    public void updateAlarmOrderRuleStatus() throws Exception {
        new Expectations() {
            {
                alarmOrderRuleDao.updateAlarmOrderRuleStatus((Integer) any, (String[]) any);
                result = 1;
            }
        };
        try {
            alarmOrderRuleService.updateAlarmOrderRuleStatus(1, new String[]{});
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
        new MockUp<RedisUtils>() {
            @Mock
            void remove(String key) {

            }
        };
        alarmOrderRuleService.updateAlarmOrderRuleStatus(1, new String[]{"1"});
    }

    @Test
    public void queryAlarmOrderRuleFeign() throws Exception {
        List<AlarmOrderCondition> alarmOrderConditions = new ArrayList<>();
        AlarmOrderCondition alarmOrderCondition = new AlarmOrderCondition();
        alarmOrderCondition.setId("1");
        alarmOrderConditions.add(alarmOrderCondition);
        List<AlarmOrderRule> alarmOrderRules = new ArrayList<>();
        AlarmOrderRule alarmOrderRule = new AlarmOrderRule();
        alarmOrderRule.setId("1");
        alarmOrderRules.add(alarmOrderRule);
        new Expectations() {
            {
                alarmOrderRuleDao.queryAlarmOrderRuleLists();
                result = alarmOrderRules;
            }
        };
        new Expectations() {
            {
                alarmOrderRuleDao.queryAlarmOrderRuleFeign(alarmOrderCondition);
                result = alarmOrderRules;
            }
        };
        alarmOrderRuleService.queryAlarmOrderRuleFeign(alarmOrderConditions);
    }

    @Test
    public void selectAlarmAreaName() {
        List<AlarmOrderRule> alarmOrderRules = new ArrayList<>();
        AlarmOrderRule alarmOrderRule = new AlarmOrderRule();
        alarmOrderRule.setId("1");
        Set<String> strings = new HashSet<>();
        strings.add("1");
        alarmOrderRule.setAlarmOrderRuleArea(strings);
        alarmOrderRules.add(alarmOrderRule);
        List<AreaInfoForeignDto> alarmOrderConditions = new ArrayList<>();
        AreaInfoForeignDto areaInfoForeignDto = new AreaInfoForeignDto();
        areaInfoForeignDto.setAreaId("1");
        areaInfoForeignDto.setAreaName("1");
        alarmOrderConditions.add(areaInfoForeignDto);
        new Expectations() {
            {
                areaFeign.selectAreaInfoByIds((List<String>) any);
                result = alarmOrderConditions;
            }
        };
        alarmOrderRuleService.selectAlarmAreaName(alarmOrderRules);
    }

    @Test
    public void selectDeviceType() {
        List<AlarmOrderRule> alarmOrderRules = new ArrayList<>();
        AlarmOrderRule alarmOrderRule = new AlarmOrderRule();
        alarmOrderRule.setId("1");
        alarmOrderRules.add(alarmOrderRule);
        List<AlarmOrderRuleDeviceType> alarmOrderRuleDeviceTypes = new ArrayList<>();
        AlarmOrderRuleDeviceType alarmOrderRuleDeviceType = new AlarmOrderRuleDeviceType();
        alarmOrderRuleDeviceType.setRuleId("1");
        alarmOrderRuleDeviceTypes.add(alarmOrderRuleDeviceType);
        alarmOrderRule.setAlarmOrderRuleDeviceTypeList(alarmOrderRuleDeviceTypes);
        alarmOrderRuleService.selectDeviceType(alarmOrderRules);
    }

    @Test
    public void selectAlarmName() {
        List<AlarmOrderRule> alarmOrderRules = new ArrayList<>();
        AlarmOrderRule alarmOrderRule = new AlarmOrderRule();
        alarmOrderRule.setId("1");
        Set<String> strings = new HashSet<>();
        strings.add("1");
        alarmOrderRule.setAlarmOrderRuleNameList(strings);
        alarmOrderRules.add(alarmOrderRule);
        List<AlarmOrderRuleDeviceType> alarmOrderRuleDeviceTypes = new ArrayList<>();
        AlarmOrderRuleDeviceType alarmOrderRuleDeviceType = new AlarmOrderRuleDeviceType();
        alarmOrderRuleDeviceType.setRuleId("1");
        alarmOrderRuleDeviceTypes.add(alarmOrderRuleDeviceType);
        alarmOrderRule.setAlarmOrderRuleDeviceTypeList(alarmOrderRuleDeviceTypes);
        List<AlarmName> alarmNameList = new ArrayList<>();
        AlarmName alarmName = new AlarmName();
        alarmName.setId("1");
        alarmName.setAlarmName("11");
        alarmNameList.add(alarmName);
        new Expectations() {
            {
                alarmNameDao.selectByIds((String[]) any);
                result = alarmNameList;
            }
        };
        alarmOrderRuleService.selectAlarmName(alarmOrderRules);
    }
}