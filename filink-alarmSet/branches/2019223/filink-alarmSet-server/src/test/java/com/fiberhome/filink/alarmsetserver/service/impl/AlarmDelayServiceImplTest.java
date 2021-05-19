package com.fiberhome.filink.alarmsetserver.service.impl;

import com.fiberhome.filink.alarmsetserver.bean.AlarmDelay;
import com.fiberhome.filink.alarmsetserver.bean.AlarmDelayDto;
import com.fiberhome.filink.alarmsetserver.dao.AlarmDelayDao;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mock;
import mockit.MockUp;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;


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
     * 查询历史设置信息
     */
    @Test
    public void selectAlarmDelayTest() {
        AlarmDelay alarmDelay = new AlarmDelay();
        alarmDelay.setDelay(1);
        new Expectations() {
            {
                AlarmDelay alarmDelays = alarmDelayDao.selectDelay();
                result = alarmDelay;
            }
        };
        new Expectations() {
            {
                AlarmDelay alarmDelays = alarmDelayDao.selectDelay();
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
        };
        Result result = alarmDelayService.selectAlarmDelay();
    }

    /**
     * 修改入库时间
     */
    @Test
    public void updateAlarmDelayTest() {
        AlarmDelayDto alarmDelayDto = new AlarmDelayDto();
        AlarmDelay alarmDelay = new AlarmDelay();
        alarmDelay.setDelay(3);
        alarmDelayDto.setAlarmDelay(alarmDelay);
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
        Result result = alarmDelayService.updateAlarmDelay(alarmDelayDto);
    }

}