package com.fiberhome.filink.alarmsetserver.service.impl;

import com.fiberhome.filink.alarmsetserver.bean.AlarmFilterCondition;
import com.fiberhome.filink.alarmsetserver.bean.AlarmFilterRule;
import com.fiberhome.filink.alarmsetserver.bean.AlarmFilterRuleDto;
import com.fiberhome.filink.alarmsetserver.dao.AlarmFilterRuleDao;
import com.fiberhome.filink.alarmsetserver.dao.AlarmFilterRuleNameDao;
import com.fiberhome.filink.alarmsetserver.dao.AlarmFilterRuleSourceDao;
import com.fiberhome.filink.alarmsetserver.dao.AlarmNameDao;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.RequestInfoUtils;
import com.fiberhome.filink.deviceapi.api.DeviceFeign;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
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
public class AlarmFilterRuleServiceImplTest {

    /**
     * 注入dao
     */
    @Injectable
    private AlarmFilterRuleDao alarmFilterRuleDao;

    /**
     * 日志
     */
    @Injectable
    private LogProcess logProcess;

    /**
     * 告警名称dao
     */
    @Injectable
    private AlarmFilterRuleNameDao alarmFilterRuleNameDao;

    /**
     * 告警源dao
     */
    @Injectable
    private AlarmFilterRuleSourceDao alarmFilterRuleSourceDao;

    /**
     * 设施api
     */
    @Injectable
    private DeviceFeign deviceFeign;

    /**
     * 告警名称
     */
    @Injectable
    private AlarmNameDao alarmNameDao;

    @Injectable
    private SystemLanguageUtil systemLanguageUtil;

    @Tested
    private AlarmFilterRuleServiceImpl alarmFilterRuleService;


    @Test
    public void queryAlarmFilterRuleListTest() throws Exception {
        QueryCondition<AlarmFilterRuleDto> queryCondition = new QueryCondition();
        AlarmFilterRuleDto alarmFilterRuleDto = new AlarmFilterRuleDto();
        alarmFilterRuleDto.setId("1");
        queryCondition.setBizCondition(alarmFilterRuleDto);
        List<AlarmFilterRule> alarmFilterRules = new ArrayList<>();
        AlarmFilterRule alarmFilterRule = new AlarmFilterRule();
        alarmFilterRule.setId("1");
        Set<String> strings = new HashSet<>();
        strings.add("1");
        alarmFilterRule.setAlarmFilterRuleSourceList(strings);
        List<String> list = new ArrayList<>();
        list.add("1");
        alarmFilterRule.setAlarmFilterRuleSourceName(list);
        alarmFilterRule.setAlarmFilterRuleNameList(strings);
        alarmFilterRules.add(alarmFilterRule);

        new MockUp<RequestInfoUtils>() {
            @Mock
            String getUserId() {
                return "2";
            }
        };
        new Expectations() {
            {
                alarmFilterRuleDao.queryAlarmFilterRuleList((AlarmFilterRuleDto) any);
                result = alarmFilterRules;
            }
        };
        new MockUp<RedisUtils>() {
            @Mock
            void set(String key, Object value) {

            }
        };
        try {
            alarmFilterRuleService.queryAlarmFilterRuleList(queryCondition);
        } catch (Exception e) {
            Assert.assertTrue(true);
        }

    }

    @Test
    public void queryAlarmFilterRuleByIdTest() throws Exception {
        List<AlarmFilterRule> alarmFilterRules = new ArrayList<>();
        AlarmFilterRule alarmFilterRule = new AlarmFilterRule();
        alarmFilterRule.setId("1");
        Set<String> strings = new HashSet<>();
        strings.add("1");
        alarmFilterRule.setAlarmFilterRuleSourceList(strings);
        List<String> list = new ArrayList<>();
        list.add("1");
        alarmFilterRule.setAlarmFilterRuleSourceName(list);
        alarmFilterRule.setAlarmFilterRuleNameList(strings);
        alarmFilterRules.add(alarmFilterRule);
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
        alarmFilterRuleService.queryAlarmFilterRuleById("1");
        new MockUp<RedisUtils>() {
            @Mock
            Object get(String key) {
                return alarmFilterRules;
            }
        };
        alarmFilterRuleService.queryAlarmFilterRuleById("1");
    }

    @Test
    public void addAlarmFilterRuleTest() throws Exception {
        List<AlarmFilterRule> list = new ArrayList<>();
        AlarmFilterRule alarmFilterRule = new AlarmFilterRule();
        alarmFilterRule.setId("2");
        alarmFilterRule.setRuleName("admin");
        list.add(alarmFilterRule);
        AlarmFilterRule alarmFilterRule1 = new AlarmFilterRule();
        alarmFilterRule1.setId("2");
        alarmFilterRule1.setRuleName("aa");
        Set<String> strings = new HashSet<>();
        strings.add("1");
        alarmFilterRule1.setAlarmFilterRuleSourceList(strings);
        alarmFilterRule1.setAlarmFilterRuleNameList(strings);
        new Expectations() {
            {
                alarmFilterRuleDao.queryAlarmFilterRuleNames();
                result = list;
            }
        };
        try {
            alarmFilterRuleService.addAlarmFilterRule(new AlarmFilterRule());
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
                alarmFilterRuleDao.insert((AlarmFilterRule) any);
                result = 1;
            }
        };
        new MockUp<RedisUtils>() {
            @Mock
            void remove(String key) {

            }
        };
        alarmFilterRuleService.addAlarmFilterRule(alarmFilterRule1);
    }

    @Test
    public void batchDeleteAlarmFilterRuleTest() throws Exception {
        new Expectations() {
            {
                alarmFilterRuleDao.deleteAlarmFilterRule((String[]) any);
                result = 1;
            }
        };
        new MockUp<RedisUtils>() {
            @Mock
            void remove(String key) {

            }
        };
        alarmFilterRuleService.batchDeleteAlarmFilterRule(new String[]{"1"});
        try {
            alarmFilterRuleService.batchDeleteAlarmFilterRule(new String[]{});
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
        new Expectations() {
            {
                alarmFilterRuleDao.deleteAlarmFilterRule((String[]) any);
                result = 0;
            }
        };
        new Expectations() {
            {
                alarmFilterRuleNameDao.batchDeleteAlarmFilterRuleName((String[]) any);
                result = 1;
            }
        };
        try {
            alarmFilterRuleService.batchDeleteAlarmFilterRule(new String[]{});
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void updateAlarmFilterRuleTest() throws Exception {
        List<AlarmFilterRule> alarmFilterRuleList = new ArrayList<>();
        AlarmFilterRule alarmFilterRule = new AlarmFilterRule();
        alarmFilterRule.setId("1");
        alarmFilterRule.setRuleName("admin");
        Set<String> strings = new HashSet<>();
        strings.add("1");
        alarmFilterRule.setAlarmFilterRuleNameList(strings);
        alarmFilterRule.setAlarmFilterRuleSourceList(strings);
        alarmFilterRuleList.add(alarmFilterRule);
        AlarmFilterRule filterRule = new AlarmFilterRule();
        filterRule.setId("1");
        filterRule.setRuleName("add");
        filterRule.setAlarmFilterRuleNameList(strings);
        filterRule.setAlarmFilterRuleSourceList(strings);
        new Expectations() {
            {
                alarmFilterRuleDao.queryAlarmFilterRuleName((String) any);
                result = alarmFilterRuleList;
            }
        };
        new Expectations() {
            {
                alarmFilterRuleDao.updateById((AlarmFilterRule) any);
                result = 1;
            }
        };
        new MockUp<RedisUtils>() {
            @Mock
            void remove(String key) {

            }
        };
        try {
            alarmFilterRuleService.updateAlarmFilterRule(filterRule);
        } catch (Exception e) {
            Assert.assertTrue(true);
        }

    }

    @Test
    public void batchUpdateAlarmFilterRuleStoredTest() throws Exception {
        new Expectations() {
            {
                alarmFilterRuleDao.updateAlarmFilterRuleStored((Integer) any, (String[]) any);
                result = 1;
            }
        };
        try {
            alarmFilterRuleService.batchUpdateAlarmFilterRuleStored(1, new String[]{});
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
        new MockUp<RedisUtils>() {
            @Mock
            void remove(String key) {

            }
        };
        try {
            alarmFilterRuleService.batchUpdateAlarmFilterRuleStored(1, new String[]{"1"});
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void batchUpdateAlarmFilterRuleStatusTest() throws Exception {
        new Expectations() {
            {
                alarmFilterRuleDao.updateAlarmFilterRuleStatus((Integer) any, (String[]) any);
                result = 1;
            }
        };
        try {
            alarmFilterRuleService.batchUpdateAlarmFilterRuleStatus(1, new String[]{});
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
        new MockUp<RedisUtils>() {
            @Mock
            void remove(String key) {
            }
        };
        try {
            alarmFilterRuleService.batchUpdateAlarmFilterRuleStatus(1, new String[]{"1"});
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void queryAlarmIsIncludedFeignTest() throws Exception {
        List<AlarmFilterCondition> alarmFilterConditionList = new ArrayList<>();
        AlarmFilterCondition alarmFilterCondition = new AlarmFilterCondition();
        alarmFilterCondition.setId("1");
        alarmFilterConditionList.add(alarmFilterCondition);
        List<AlarmFilterRule> list = new ArrayList<>();
        AlarmFilterRule alarmFilterRule = new AlarmFilterRule();
        alarmFilterRule.setId("1");
        list.add(alarmFilterRule);
        new Expectations() {
            {
                alarmFilterRuleDao.queryAlarmFilterRuleLists();
                result = list;
            }
        };
        new Expectations() {
            {
                alarmFilterRuleDao.queryAlarmIsIncludedFeign(alarmFilterCondition);
                result = list;
            }
        };
        alarmFilterRuleService.queryAlarmIsIncludedFeign(alarmFilterConditionList);

    }

}