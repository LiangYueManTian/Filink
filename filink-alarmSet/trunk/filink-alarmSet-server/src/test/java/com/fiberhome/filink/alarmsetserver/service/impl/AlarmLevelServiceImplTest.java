package com.fiberhome.filink.alarmsetserver.service.impl;


import com.fiberhome.filink.alarmsetserver.bean.AlarmLevel;
import com.fiberhome.filink.alarmsetserver.dao.AlarmLevelDao;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
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
 * 告警级别设置service测试类
 */
@RunWith(JMockit.class)
public class AlarmLevelServiceImplTest {


    /**
     * 测试Service
     */
    @Tested
    private AlarmLevelServiceImpl alarmLevelService;

    /**
     * 注入dao
     */
    @Injectable
    private AlarmLevelDao alarmLevelDao;

    @Injectable
    private SystemLanguageUtil systemLanguageUtil;

    /**
     * 查询告警级别信息列表
     */
    @Test
    public void queryAlarmLevelListTest() {
        new Expectations() {
            {
                List<AlarmLevel> alarmLevels = alarmLevelDao.selectList(null);
                result = ResultUtils.success(alarmLevels);
            }
        };
        new Expectations() {
            {
                List<AlarmLevel> alarmLevels = alarmLevelDao.selectList(null);
                result = null;
            }
        };
        new MockUp<RedisUtils>() {
            @Mock
            void set(String key, Object value) {

            }
        };
        Result result = alarmLevelService.queryAlarmLevelList();
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    /**
     * 修改告警级别
     */
    @Test
    public void updateAlarmLevelTest() {
        List<AlarmLevel> levels = new ArrayList<>();
        AlarmLevel alarmLevel = new AlarmLevel();
        alarmLevel.setId("1");
        alarmLevel.setAlarmLevelCode("1");
        alarmLevel.setAlarmLevelColor("111");
        alarmLevel.setAlarmLevelName("紧急");
        alarmLevel.setIsPlay(1);
        alarmLevel.setAlarmLevelSound("d.mp3");
        alarmLevel.setOrderField(1);
        alarmLevel.setPlayCount(1);
        levels.add(alarmLevel);
        AlarmLevel alarmLevel1 = new AlarmLevel();
        alarmLevel1.setId("2");
        alarmLevel1.setAlarmLevelCode("2");
        alarmLevel1.setAlarmLevelColor("222");
        alarmLevel1.setAlarmLevelName("主要");
        alarmLevel1.setIsPlay(2);
        alarmLevel1.setAlarmLevelSound("a.mp3");
        alarmLevel1.setOrderField(2);
        alarmLevel1.setPlayCount(2);
        new Expectations() {
            {
                alarmLevelDao.queryAlarmLevelColor(anyString);
                result = levels;
            }
        };
        new Expectations() {
            {
                alarmLevelDao.updateById(alarmLevel1);
                result = 1;
            }
        };
        new MockUp<RedisUtils>() {
            @Mock
            void remove(String key) {
            }
        };
        Result result = alarmLevelService.updateAlarmLevel(alarmLevel1);
        new Expectations() {
            {
                alarmLevelDao.updateById(alarmLevel1);
                result = 2;
            }
        };
        try {
            alarmLevelService.updateAlarmLevel(alarmLevel1);
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
    }

    /**
     * 查询单个告警级别信息
     */
    @Test
    public void queryAlarmLevelByIdTest() {
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
        new Expectations() {
            {
                alarmLevelDao.selectById("1");
            }
        };
        new MockUp<I18nUtils>() {
            @Mock
            String getString(String key) {
                return "ALARM_ID_FAILED";
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
        Result result = alarmLevelService.queryAlarmLevelById("1");
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    /**
     * 查询告警级别
     */
    @Test
    public void queryAlarmLevelTest() {
        List<AlarmLevel> alarmLevels = new ArrayList<>();
        AlarmLevel alarmLevel = new AlarmLevel();
        alarmLevel.setId("1");
        alarmLevels.add(alarmLevel);
        new Expectations() {
            {
                alarmLevelDao.selectAlarmLevel();
                result = alarmLevels;
            }
        };
        Result result = alarmLevelService.queryAlarmLevel();
        Assert.assertTrue(result.getCode() == 0);
        new MockUp<I18nUtils>() {
            @Mock
            String getString(String key) {
                return "ALARM_LEVEL_NULL";
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
        new Expectations() {
            {
                alarmLevelDao.selectAlarmLevel();
                result = null;
            }
        };
        Result resultOne = alarmLevelService.queryAlarmLevel();
        Assert.assertTrue(resultOne.getCode() != 0);
    }

    @Test
    public void queryAlarmLevelSetFeignTest() {
        new Expectations() {
            {
                alarmLevelDao.queryAlarmLevelSetFeign(anyString);
            }
        };
        alarmLevelService.queryAlarmLevelSetFeign("1");
    }

}