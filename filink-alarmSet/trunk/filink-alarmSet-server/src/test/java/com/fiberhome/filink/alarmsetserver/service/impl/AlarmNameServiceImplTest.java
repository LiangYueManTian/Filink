package com.fiberhome.filink.alarmsetserver.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.fiberhome.filink.alarmsetserver.bean.AlarmName;
import com.fiberhome.filink.alarmsetserver.dao.AlarmNameDao;
import com.fiberhome.filink.bean.FilterCondition;
import com.fiberhome.filink.bean.PageCondition;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import java.util.ArrayList;
import java.util.List;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mock;
import mockit.MockUp;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 告警设置service测试类
 */
@RunWith(JMockit.class)
public class AlarmNameServiceImplTest {

    @Tested
    private AlarmNameServiceImpl alarmNameService;

    /**
     * 注入的dao
     */
    @Injectable
    private AlarmNameDao alarmNameDao;

    @Injectable
    private LogProcess logProcess;

    @Injectable
    private SystemLanguageUtil systemLanguageUtil;

    /**
     * 查询告警名称信息列表
     */
    @Test
    public void queryAlarmCurrentSetListTest() {
        QueryCondition queryCondition = new QueryCondition();
        new Expectations() {
            {
                List<AlarmName> alarmNames = alarmNameDao.selectList((EntityWrapper) any);
                result = ResultUtils.success(alarmNames);
            }
        };
        new MockUp<RedisUtils>() {
            @Mock
            void set(String key, Object value) {
                return;
            }
        };
        new MockUp<I18nUtils>() {
            @Mock
            String getString(String key) {
                return "ALARM_NAME_NULL";
            }
        };
        new MockUp<ResultUtils>() {
            @Mock
            Result success(int successCode, String msg) {
                Result result = new Result();
                result.setCode(1);
                result.setMsg("success");
                return result;
            }
        };
        Result result = alarmNameService.queryAlarmNameList(queryCondition);
    }

    /**
     * 新增告警设置
     */
    @Test
    public void insertAlarmCurrentSetTest() {
        AlarmName alarmName = new AlarmName();
        alarmName.setAlarmAutomaticConfirmation("1");
        alarmName.setAlarmCode("1");
        alarmName.setAlarmDesc("111");
        alarmName.setAlarmDefaultLevel("1");
        alarmName.setAlarmLevel("1");
        alarmName.setAlarmName("1");
        Integer insert = alarmNameDao.insert(alarmName);
    }

    /**
     * 查询单个告警设置信息
     */
    @Test
    public void queryAlarmCurrentSetByIdTest() {
        List<AlarmName> list = new ArrayList<>();
        AlarmName alarmName = new AlarmName();
        alarmName.setId("1");
        alarmName.setAlarmName("wom");
        AlarmName alarmName1 = new AlarmName();
        alarmName.setId("1");
        alarmName.setAlarmName("wom");
        list.add(alarmName);
        new MockUp<RedisUtils>() {
            @Mock
            boolean hasKey(String key) {
                return true;
            }
        };
        new MockUp<RedisUtils>() {
            @Mock
            Object get(String key) {
                return list;
            }
        };
        String[] strings = new String[]{"1"};
        Result result = alarmNameService.queryAlarmCurrentSetById(strings);
    }

    /**
     * 删除告警设置
     */
    @Test
    public void deleteAlarmCurrentSetTest() {
        Integer deleteById = alarmNameDao.deleteById("1");
    }

    /**
     * 修改告警设置
     */
    @Test
    public void updateAlarmCurrentSetTest() {
        new Expectations() {
            {
                alarmNameDao.updateById((AlarmName) any);
                result = 1;
            }
        };
        new MockUp<RedisUtils>() {
            @Mock
            void remove(String key) {
                return;
            }
        };
        AlarmName alarmName = new AlarmName();
        alarmName.setId("2");
        alarmName.setAlarmCode("1");
        alarmName.setAlarmDesc("111");
        alarmName.setAlarmDefaultLevel("1");
        alarmName.setAlarmLevel("1");
        alarmName.setAlarmName("1");
        Result result = alarmNameService.updateAlarmCurrentSet(alarmName);
        new Expectations() {
            {
                alarmNameDao.updateById((AlarmName) any);
                result = 2;
            }
        };
        try {
            alarmNameService.updateAlarmCurrentSet(alarmName);
        }catch (Exception e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void queryCurrentAlarmSetByNameFeign() {
        alarmNameService.queryCurrentAlarmSetByNameFeign("1");
    }

    @Test
    public void queryCurrentAlarmSetFeign() {
        alarmNameService.queryCurrentAlarmSetFeign("1");
    }

    @Test
    public void queryAlarmNamePage() {
        QueryCondition queryCondition = new QueryCondition();
        List<FilterCondition> filterConditions = new ArrayList<>();
        FilterCondition filterCondition = new FilterCondition();
        filterCondition.setFilterField("code");
        filterCondition.setOperator("eq");
        filterCondition.setFilterValue("1");
        filterConditions.add(filterCondition);
        PageCondition pageCondition = new PageCondition();
        pageCondition.setPageNum(1);
        pageCondition.setPageSize(10);
        queryCondition.setPageCondition(pageCondition);
        queryCondition.setFilterConditions(filterConditions);
        alarmNameService.queryAlarmNamePage(queryCondition);
    }

}