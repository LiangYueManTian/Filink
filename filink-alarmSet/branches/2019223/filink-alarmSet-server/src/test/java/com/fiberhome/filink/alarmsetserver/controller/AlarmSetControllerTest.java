package com.fiberhome.filink.alarmsetserver.controller;

import com.fiberhome.filink.alarmsetserver.bean.AlarmDelay;
import com.fiberhome.filink.alarmsetserver.bean.AlarmDelayDto;
import com.fiberhome.filink.alarmsetserver.bean.AlarmLevel;
import com.fiberhome.filink.alarmsetserver.bean.AlarmName;
import com.fiberhome.filink.alarmsetserver.service.AlarmDelayService;
import com.fiberhome.filink.alarmsetserver.service.AlarmLevelService;
import com.fiberhome.filink.alarmsetserver.service.AlarmNameService;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.server_common.utils.I18nUtils;
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
public class AlarmSetControllerTest {
    @Tested
    private AlarmSetController alarmSetController;

    @Injectable
    private AlarmLevelService alarmLevelService;

    @Injectable
    private AlarmNameService alarmNameService;

    @Injectable
    private AlarmDelayService alarmDelayService;

    /**
     * 查询全量告警级别信息
     */
    @Test
    public void queryAlarmLevelListTest() {
        new Expectations() {
            {
                alarmLevelService.queryAlarmLevelList();
                result = ResultUtils.success();
            }
        };
        Result result = alarmSetController.queryAlarmLevelList();
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    /**
     * 修改告警级别
     */
    @Test
    public void updateAlarmColorAndSoundTest() {
        AlarmLevel alarmLevel = new AlarmLevel();
        alarmLevel.setId("1");
        alarmLevel.setPlayCount(2);
        new Expectations() {
            {
                alarmLevelService.updateAlarmLevel(alarmLevel);
                result = ResultUtils.success();
            }
        };
        Result result = alarmSetController.updateAlarmLevel(alarmLevel);
        new MockUp<I18nUtils>() {
            @Mock
            String getString(String key) {
                return "ALARM_LEVEL_ID_NULL";
            }
        };
        new MockUp<ResultUtils>() {
            @Mock
            Result success(int successCode, String msg) {
                Result result1 = new Result();
                result1.setCode(1);
                result1.setMsg("success");
                return result1;
            }
        };
        Result result1 = alarmSetController.updateAlarmLevel(alarmLevel);
    }

    /**
     * 查询单个告警设置信息
     */
    @Test
    public void queryAlarmLevelByIdTest() {
        new Expectations() {
            {
                alarmLevelService.queryAlarmLevelById((String) any);
                result = ResultUtils.success();
            }
        };
        Result result = alarmSetController.queryAlarmLevelById("1");
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    /**
     * 查询当前告警设置信息列表
     */
    @Test
    public void queryAlarmCurrentSetListTest() {
        new Expectations() {
            {
                alarmNameService.queryAlarmNameList((QueryCondition) any);
                result = ResultUtils.success();
            }
        };
        Result result = alarmSetController.queryAlarmNameList(new QueryCondition());
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    /**
     * 查询单个告警设置信息
     */
    @Test
    public void queryAlarmCurrentSetByIdTest() {
        new Expectations() {
            {
                alarmNameService.queryAlarmCurrentSetById((String) any);
                result = ResultUtils.success();
            }
        };
        Result result = alarmSetController.queryAlarmCurrentSetById("1");
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    /**
     * 修改告警设置信息
     */
    @Test
    public void updateAlarmCurrentSetTest() {
        AlarmName alarmName = new AlarmName();
        alarmName.setId("1");
        new Expectations() {
            {
                alarmNameService.updateAlarmCurrentSet((AlarmName) any);
                result = ResultUtils.success();
            }
        };
        Result result = alarmSetController.updateAlarmCurrentSet(alarmName);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
        new MockUp<I18nUtils>() {
            @Mock
            String getString(String key) {
                return "ALARM_NAME_ID_NULL";
            }
        };
        new MockUp<ResultUtils>() {
            @Mock
            Result success(int successCode, String msg) {
                Result result1 = new Result();
                result1.setCode(1);
                result1.setMsg("success");
                return result1;
            }
        };
        Result result1 = alarmSetController.updateAlarmCurrentSet(new AlarmName());
    }

    /**
     * 查询历史设置信息
     */
    @Test
    public void selectAlarmDelayTest() {
        new Expectations() {
            {
                alarmDelayService.selectAlarmDelay();
                result = ResultUtils.success();
            }
        };
        Result result = alarmSetController.selectAlarmDelay();
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    /**
     * 修改延时入库时间
     */
    @Test
    public void updateAlarmDelayTest() {
        AlarmDelayDto alarmDelayDto = new AlarmDelayDto();
        AlarmDelay alarmDelay = new AlarmDelay();
        alarmDelay.setDelay(3);
        alarmDelayDto.setAlarmDelay(alarmDelay);
        new Expectations() {
            {
                alarmDelayService.updateAlarmDelay((AlarmDelayDto) any);
                result = ResultUtils.success();
            }
        };
        Result result = alarmSetController.updateAlarmDelay(alarmDelayDto);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    /**
     * 查询告警级别
     */
    @Test
    public void queryAlarmLevelTest() {
        new Expectations() {
            {
                alarmLevelService.queryAlarmLevel();
            }
        };
        Result result = alarmSetController.queryAlarmLevel();
    }

    /**
     * 查询颜色信息
     */
    @Test
    public void queryAlarmColorTest() {
        alarmSetController.queryAlarmColor();
    }
}