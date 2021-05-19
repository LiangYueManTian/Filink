package com.fiberhome.filink.alarmsetserver.service.impl;


import com.fiberhome.filink.alarmsetserver.bean.AlarmDelay;
import com.fiberhome.filink.alarmsetserver.constant.AppConstant;
import com.fiberhome.filink.alarmsetserver.dao.AlarmDelayDao;
import com.fiberhome.filink.alarmsetserver.exception.FilinkAlarmDelayException;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import mockit.*;
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
        AlarmDelay alarmDelay = new AlarmDelay();
        new Expectations() {
            {
                alarmDelayDao.updateDelay((AlarmDelay) any);
                result = 0;
            }
        };
        try {
            alarmDelayService.updateAlarmDelay(alarmDelay);
        } catch (Exception e) {
            Assert.assertSame(e.getClass(), FilinkAlarmDelayException.class);
        }
        alarmDelay.setId("1");
        alarmDelay.setDelayName("test");
        new Expectations(RedisUtils.class, I18nUtils.class) {
            {
                alarmDelayDao.updateDelay((AlarmDelay) any);
                result = 1;

                RedisUtils.remove(anyString);

                I18nUtils.getSystemString(AppConstant.UPDATE_TIAM_SUCCESS);
                result = "成功";
            }
        };
        Result result = alarmDelayService.updateAlarmDelay(alarmDelay);
        Assert.assertEquals(result.getCode(), 0);
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
        Integer result = alarmDelayService.selectAlarmDelayTime();
        Assert.assertEquals(result, alarmDelay.getDelay());
        new Expectations() {
            {
                alarmDelayDao.selectDelay();
                result = null;
            }
        };
        result = alarmDelayService.selectAlarmDelayTime();
        Assert.assertSame(result, 0);
    }

}