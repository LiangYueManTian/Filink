package com.fiberhome.filink.alarmsetserver.service.impl;

import com.fiberhome.filink.alarmsetserver.bean.AlarmLevel;
import com.fiberhome.filink.alarmsetserver.dao.AlarmLevelDao;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import java.util.List;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mock;
import mockit.MockUp;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

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
        new MockUp<I18nUtils>() {
            @Mock
            String getString(String key) {
                return "ALARM_LEVEL_FAILED";
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
        Result result = alarmLevelService.queryAlarmLevelList();
    }

    /**
     * 修改告警级别
     */
    @Test
    public void updateAlarmLevelTest() {
        AlarmLevel alarmLevel = new AlarmLevel();
        alarmLevel.setId("2");
        alarmLevel.setAlarmLevelCode("1");
        alarmLevel.setAlarmLevelColor("1");
        alarmLevel.setAlarmLevelName("1");
        alarmLevel.setIsPlay(1);
        alarmLevel.setAlarmLevelSound("1");
        alarmLevel.setOrderField(1);
        alarmLevel.setPlayCount(1);
        new MockUp<I18nUtils>() {
            @Mock
            String getString(String key){
                return "SET_UP_SUCCESS";
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
                alarmLevelDao.updateById(alarmLevel);
                result = 1;
            }
        };
        alarmLevelService.updateAlarmLevel(alarmLevel);
    }

    /**
     * 查询单个告警级别信息
     */
    @Test
    public void queryAlarmLevelByIdTest() {
        new Expectations() {
            {
                AlarmLevel alarmLevel = alarmLevelDao.selectById("1");
            }
        };
        Result result = alarmLevelService.queryAlarmLevelById("1");
        new MockUp<I18nUtils>() {
            @Mock
            String getString(String key){
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
        new Expectations() {
            {
                alarmLevelDao.selectById(null);
                result = null;
            }
        };
        Result result1 = alarmLevelService.queryAlarmLevelById(null);
    }

    /**
     * 查询告警级别
     */
    @Test
    public void queryAlarmLevelTest() {
        new Expectations() {
            {
                List<AlarmLevel> alarmLevels = alarmLevelDao.selectAlarmLevel();
                result = ResultUtils.success(alarmLevels);
            }
        };
        new Expectations() {
            {
                List<AlarmLevel> alarmLevels = alarmLevelDao.selectAlarmLevel();
                result = null;
            }
        };
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
        Result result = alarmLevelService.queryAlarmLevel();
    }
}