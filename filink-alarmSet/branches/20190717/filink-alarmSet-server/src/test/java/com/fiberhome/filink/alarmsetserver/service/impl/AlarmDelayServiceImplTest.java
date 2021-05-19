package com.fiberhome.filink.alarmsetserver.service.impl;


import com.fiberhome.filink.alarmsetserver.bean.AlarmDelay;
import com.fiberhome.filink.alarmsetserver.dao.AlarmDelayDao;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
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
 * 历史告警Service测试类
 */
@RunWith(JMockit.class)
public class AlarmDelayServiceImplTest {

    /**
     * 测试Service
     */
    @Tested
    private AlarmDelayServiceImpl alarmDelayService;

    /**
     * 注入的dao
     */
    @Injectable
    private AlarmDelayDao alarmDelayDao;

    /**
     * 日志
     */
    @Injectable
    private LogProcess logProcess;

    @Injectable
    private SystemLanguageUtil systemLanguageUtil;

    /**
     * 查询历史设置信息
     */
    @Test
    public void selectAlarmDelayTest() {
        AlarmDelay alarmDelay = new AlarmDelay();
        alarmDelay.setDelay(1);
        new Expectations() {
            {
                alarmDelayDao.selectDelay();
                result = alarmDelay;
            }
        };
        /*new Expectations() {
            {
                alarmDelayDao.selectDelay();
                result = null;
            }
        };
        new MockUp<I18nUtils>() {
            @Mock
            String getString(String key) {
                return "HISTORY_QUERY_FAILED";
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
        };*/
        new MockUp<RedisUtils>() {
            @Mock
            void set(String key, Object value) {
            }
        };
        Result result = alarmDelayService.selectAlarmDelay();
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    /**
     * 修改入库时间
     */
    @Test
    public void updateAlarmDelayTest() {
        new MockUp<I18nUtils>() {
            @Mock
            String getString(String key) {
                return "UPDATE_TIAM_SUCCESS";
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
                alarmDelayDao.updateDelay((AlarmDelay) any);
                result = 1;
            }
        };
        new MockUp<RedisUtils>() {
            @Mock
            void remove(String key) {
                return;
            }
        };
        Result result = alarmDelayService.updateAlarmDelay(new AlarmDelay());
    }

    @Test
    public void selectAlarmDelayTimeTest() {
        AlarmDelay alarmDelay = new AlarmDelay();
        alarmDelay.setId("1");
        new Expectations() {
            {
                alarmDelayDao.selectDelay();
                result = alarmDelay;
            }
        };
        alarmDelayService.selectAlarmDelayTime();
    }

}