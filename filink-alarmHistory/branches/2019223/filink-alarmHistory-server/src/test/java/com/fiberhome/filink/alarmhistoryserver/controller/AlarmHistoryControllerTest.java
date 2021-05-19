package com.fiberhome.filink.alarmhistoryserver.controller;

import com.fiberhome.filink.alarmhistoryserver.bean.AlarmHistory;
import com.fiberhome.filink.alarmhistoryserver.service.AlarmHistoryService;
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

import static org.junit.Assert.*;

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
        new MockUp<I18nUtils>() {
            @Mock
            String getString(String key) {
                return "ALARM_ID_NULL";
            }
        };
        new MockUp<ResultUtils>() {
            @Mock
            Result warn(int resultCode, String msg) {
                Result result = new Result();
                result.setCode(1);
                result.setMsg("success");
                return result;
            }
        };
        Result result = alarmHistoryController.queryAlarmHistoryInfoById("1");
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
        Result result1 = alarmHistoryController.queryAlarmHistoryInfoById(null);
    }

    /**
     * 添加历史告警信息
     */
    @Test
    public void insertAlarmHistoryTest() {
        AlarmHistory alarmHistory = new AlarmHistory();
        alarmHistory.setId("1");
        new Expectations() {
            {
                alarmHistoryService.insertAlarmHistory((AlarmHistory) any);
            }
        };
        alarmHistoryController.insertAlarmHistory(alarmHistory);
    }
}