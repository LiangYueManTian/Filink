package com.fiberhome.filink.alarm_server.controller;


import com.fiberhome.filink.alarmcurrentserver.bean.AlarmCurrent;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmStatus;
import com.fiberhome.filink.alarmcurrentserver.controller.AlarmCurrentController;
import com.fiberhome.filink.alarmcurrentserver.service.AlarmCurrentService;
import com.fiberhome.filink.alarmcurrentserver.stream.AlarmStreams;
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

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * AlarmCurrentController测试类
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-22
 */
@RunWith(JMockit.class)
public class AlarmCurrentControllerTest {

    /**
     * 测试对象
     */
    @Tested
    private AlarmCurrentController alarmCurrentController;

    /**
     * 当前告警Service
     */
    @Injectable
    private AlarmCurrentService alarmCurrentService;

    @Injectable
    private AlarmStreams alarmStreams;

    /**
     * 实体类
     */
    private AlarmCurrent alarmCurrent;

    /**
     * list
     */
    private List<AlarmCurrent> list = new ArrayList<AlarmCurrent>();

    /**
     * 查询当前告警列表信息
     */
    @Test
    public void queryAlarmCurrentListTest() {
        new Expectations() {
            {
                alarmCurrentService.queryAlarmCurrentList((QueryCondition) any);
                result = ResultUtils.success();
            }
        };
        Result result = alarmCurrentController.queryAlarmCurrentList(new QueryCondition());
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    /**
     * 查询单个当前告警的详细信息
     */
    @Test
    public void queryAlarmCurrentInfoByIdTest() {
        new Expectations() {
            {
                alarmCurrentService.queryAlarmCurrentInfoById((String) any);
                result = ResultUtils.success();
            }
        };
        Result result = alarmCurrentController.queryAlarmCurrentInfoById("1");
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    /**
     * 批量修改当前告警备注信息
     */
    @Test
    public void updateAlarmRemarkTest() {
        new Expectations() {
            {
                alarmCurrentService.updateAlarmRemark((List<AlarmCurrent>) any);
                result = ResultUtils.success();
            }
        };
        Result result = alarmCurrentController.updateAlarmRemark(list);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    /**
     * 批量设置当前告警的告警确认状态
     */
    @Test
    public void updateAlarmConfirmStatusTest() {
        new Expectations() {
            {
                alarmCurrentService.updateAlarmConfirmStatus((AlarmStatus) any);
                result = ResultUtils.success();
            }
        };
        Result result = alarmCurrentController.updateAlarmConfirmStatus(new AlarmStatus());
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    /**
     * 批量设置当前告警的告警清除状态
     */
    @Test
    public void updateAlarmCleanStatusTest() {
        new Expectations() {
            {
                alarmCurrentService.updateAlarmCleanStatus((AlarmStatus) any);
                result = ResultUtils.success();
            }
        };
        Result result = alarmCurrentController.updateAlarmCleanStatus(new AlarmStatus());
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    /**
     * 查询各级别告警总数
     */
    @Test
    public void queryEveryAlarmCountTest() {
        new Expectations() {
            {
                alarmCurrentService.queryEveryAlarmCount();
                result = ResultUtils.success();
            }
        };
        Result result = alarmCurrentController.queryEveryAlarmCount();
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

}
