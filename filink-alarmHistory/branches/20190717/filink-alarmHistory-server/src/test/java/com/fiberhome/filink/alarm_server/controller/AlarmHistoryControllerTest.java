package com.fiberhome.filink.alarm_server.controller;


import com.fiberhome.filink.alarmhistoryserver.bean.AlarmHistory;
import com.fiberhome.filink.alarmhistoryserver.controller.AlarmHistoryController;
import com.fiberhome.filink.alarmhistoryserver.service.AlarmHistoryService;
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


/**
 * <p>
 *  controller测试类
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-22
 */
@RunWith(JMockit.class)
public class AlarmHistoryControllerTest {

    /**
     * controller测试
     */
    @Tested
    private AlarmHistoryController alarmHistoryController;

    /**
     * 历史告警Service
     */
    @Injectable
    private AlarmHistoryService alarmHistoryService;

    /**
     * 查询历史告警列表信息
     */
    @Test
    public void queryAlarmHistoryListTest() {
        new Expectations() {
            {
                alarmHistoryService.queryAlarmHistoryList((QueryCondition<AlarmHistory>) any);
                result = ResultUtils.success();
            }
        };
        Result result = alarmHistoryController.queryAlarmHistoryList(new QueryCondition<>());
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    /**
     * 查询单个历史告警的信息
     */
    @Test
    public void queryAlarmHistoryInfoByIdTest() {
        new Expectations() {
            {
                alarmHistoryService.queryAlarmHistoryInfoById((String) any);
                result = ResultUtils.success();
            }
        };
        Result result = alarmHistoryController.queryAlarmHistoryInfoById("1");
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }
}
