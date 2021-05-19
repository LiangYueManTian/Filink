package com.fiberhome.filink.alarmsetserver.controller;

import com.fiberhome.filink.alarmsetserver.bean.AlarmDelay;
import com.fiberhome.filink.alarmsetserver.bean.AlarmLevel;
import com.fiberhome.filink.alarmsetserver.bean.AlarmName;
import com.fiberhome.filink.alarmsetserver.service.AlarmDelayService;
import com.fiberhome.filink.alarmsetserver.service.AlarmLevelService;
import com.fiberhome.filink.alarmsetserver.service.AlarmNameService;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMockit.class)
public class AlarmSetControllerTest {

    /**
     * 测试Controller
     */
    @Tested
    private AlarmSetController alarmSetController;

    /**
     * 调用告警级别service
     */
    @Injectable
    private AlarmLevelService alarmLevelService;

    /**
     * 调用告警设置service
     */
    @Injectable
    private AlarmNameService alarmNameService;

    /**
     * 调用历史告警service
     */
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
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
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
        alarmSetController.queryAlarmLevelById(null);
    }

    @Test
    public void queryAlarmLevelSetFeign() {
        alarmSetController.queryAlarmLevelSetFeign("1");
    }

    /**
     * 查询当前告警设置信息列表
     */
    @Test
    public void queryAlarmCurrentSetListTest() {
        QueryCondition queryCondition = new QueryCondition();
        new Expectations() {
            {
                alarmNameService.queryAlarmNameList(queryCondition);
                result = ResultUtils.success();
            }
        };
        Result result = alarmSetController.queryAlarmNameList(queryCondition);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    /**
     * 查询单个告警设置信息
     */
    @Test
    public void queryAlarmCurrentSetByIdTest() {
        new Expectations() {
            {
                alarmNameService.queryAlarmCurrentSetById((String[]) any);
                result = ResultUtils.success();
            }
        };
        String[] strings = new String[]{"1"};
        Result result = alarmSetController.queryAlarmCurrentSetById(strings);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
        alarmSetController.queryAlarmCurrentSetById(null);
    }

    @Test
    public void queryCurrentAlarmSetFeign() {
        alarmSetController.queryCurrentAlarmSetFeign("1");
    }

    @Test
    public void queryCurrentAlarmSetByNameFeign() {
        alarmSetController.queryCurrentAlarmSetByNameFeign("1");
        alarmSetController.queryCurrentAlarmSetByNameFeign(null);
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
        alarmSetController.updateAlarmCurrentSet(new AlarmName());
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

    @Test
    public void queryAlarmHistorySetFeign() {
        alarmSetController.queryAlarmHistorySetFeign();
    }

    @Test
    public void queryAlarmNamePage() {
        alarmSetController.queryAlarmNamePage(new QueryCondition());
    }

    /**
     * 修改延时入库时间
     */
    @Test
    public void updateAlarmDelayTest() {
        AlarmDelay alarmDelay = new AlarmDelay();
        alarmDelay.setDelay(3);
        new Expectations() {
            {
                alarmDelayService.updateAlarmDelay((AlarmDelay) any);
                result = ResultUtils.success();
            }
        };
        Result result = alarmSetController.updateAlarmDelay(alarmDelay);
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
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }
}