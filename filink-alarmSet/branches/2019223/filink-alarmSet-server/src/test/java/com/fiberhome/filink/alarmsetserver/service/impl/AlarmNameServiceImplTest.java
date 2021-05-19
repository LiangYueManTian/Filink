package com.fiberhome.filink.alarmsetserver.service.impl;


import com.fiberhome.filink.alarmsetserver.bean.AlarmName;
import com.fiberhome.filink.alarmsetserver.dao.AlarmNameDao;
import com.fiberhome.filink.bean.QueryCondition;
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
public class AlarmNameServiceImplTest {
    /**
     * 测试Service
     */
    @Tested
    private AlarmNameServiceImpl alarmNameService;

    /**
     * 注入的dao
     */
    @Injectable
    private AlarmNameDao alarmNameDao;

    /**
     * 查询告警名称信息列表
     */
    @Test
    public void queryAlarmCurrentSetListTest() {
        new Expectations() {
            {
                List<AlarmName> alarmNames = alarmNameDao.selectList(null);
                result = ResultUtils.success(alarmNames);
            }
        };
        Result result = alarmNameService.queryAlarmNameList(new QueryCondition());
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
        alarmName.setAlarmDefaultLevel("紧急");
        alarmName.setAlarmLevel("紧急");
        alarmName.setAlarmName("震动异常");
        Integer insert = alarmNameDao.insert(alarmName);
    }

    /**
     * 查询单个告警设置信息
     */
    @Test
    public void queryAlarmCurrentSetByIdTest() {
        Result result1 = alarmNameService.queryAlarmCurrentSetById(null);
        new Expectations() {
            {
                AlarmName alarmName = alarmNameDao.selectById("1");
                result = null;
            }
        };
        new MockUp<I18nUtils>() {
            @Mock
            String getString(String key) {
                return "SINGLE_ALARM_FAILED";
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
        Result result = alarmNameService.queryAlarmCurrentSetById("1");
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
        new MockUp<I18nUtils>() {
            @Mock
            String getString(String key) {
                return "UPDATE_ALARM_SUCCESS";
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
        AlarmName alarmName = new AlarmName();
        alarmName.setId("2");
        alarmName.setAlarmCode("1");
        alarmName.setAlarmDesc("111");
        alarmName.setAlarmDefaultLevel("紧急");
        alarmName.setAlarmLevel("紧急");
        alarmName.setAlarmName("震动异常");
        Result result = alarmNameService.updateAlarmCurrentSet(alarmName);
    }
}