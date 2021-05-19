package com.fiberhome.filink.alarmsetserver.controller;

import com.fiberhome.filink.alarmsetserver.bean.AlarmOrderCondition;
import com.fiberhome.filink.alarmsetserver.bean.AlarmOrderRule;
import com.fiberhome.filink.alarmsetserver.bean.AlarmOrderRuleDto;
import com.fiberhome.filink.alarmsetserver.service.AlarmOrderRuleService;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import java.util.ArrayList;
import java.util.List;
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
public class AlarmOrderRuleControllerTest {

    @Tested
    private AlarmOrderRuleController alarmOrderRuleController;

    @Injectable
    private AlarmOrderRuleService alarmOrderRuleService;

    /**
     * 查询告警转工单列表信息
     *
     * @throws Exception
     */
    @Test
    public void queryAlarmOrderRuleList() throws Exception {
        new Expectations() {
            {
                alarmOrderRuleService.queryAlarmOrderRuleList((QueryCondition<AlarmOrderRuleDto>) any);
            }
        };
        Result result = alarmOrderRuleController.queryAlarmOrderRuleList(new QueryCondition<>());
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    /**
     * 查询告警转工单信息
     *
     * @throws Exception
     */
    @Test
    public void queryAlarmOrderRule() throws Exception {
        new Expectations() {
            {
                alarmOrderRuleService.queryAlarmOrderRule(anyString);
            }
        };
        Result result = alarmOrderRuleController.queryAlarmOrderRule("1");
        Assert.assertTrue(result.getCode() == 0);
        new MockUp<I18nUtils>() {
            @Mock
            String getString(String key) {
                return "INCORRECT_PARAMETER";
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
        Result result1 = alarmOrderRuleController.queryAlarmOrderRule(null);
        Assert.assertTrue(result1.getCode() != 0);
    }

    /**
     * 添加告警转工单信息
     *
     * @throws Exception
     */
    @Test
    public void addAlarmOrderRule() throws Exception {
        new Expectations() {
            {
                alarmOrderRuleService.addAlarmOrderRule((AlarmOrderRule) any);
            }
        };
        Result result = alarmOrderRuleController.addAlarmOrderRule(new AlarmOrderRule());
        Assert.assertTrue(result.getCode() == 0);
        new MockUp<I18nUtils>() {
            @Mock
            String getString(String key) {
                return "INCORRECT_PARAMETER";
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
        Result result1 = alarmOrderRuleController.addAlarmOrderRule(null);
        Assert.assertTrue(result1.getCode() != 0);
    }

    /**
     * 删除告警转工单信息
     *
     * @throws Exception
     */
    @Test
    public void batchDeleteAlarmOrderRule() throws Exception {
        new Expectations() {
            {
                alarmOrderRuleService.deleteAlarmOrderRule((String[]) any);
            }
        };
        String[] strings = new String[]{"1"};
        Result result = alarmOrderRuleController.batchDeleteAlarmOrderRule(strings);
        Assert.assertTrue(result.getCode() == 0);
        new MockUp<I18nUtils>() {
            @Mock
            String getString(String key) {
                return "INCORRECT_PARAMETER";
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
        Result result1 = alarmOrderRuleController.batchDeleteAlarmOrderRule(null);
        Assert.assertTrue(result1.getCode() != 0);
    }

    @Test
    public void updateAlarmOrderRule() throws Exception {
        AlarmOrderRule alarmOrderRule = new AlarmOrderRule();
        alarmOrderRule.setCompletionTime(5);
        Result result = alarmOrderRuleController.updateAlarmOrderRule(alarmOrderRule);
        Assert.assertTrue(result.getCode() == 0);
    }

    @Test
    public void batchUpdateAlarmOrderRuleStatus() throws Exception {
        Integer status = 1;
        String[] idArray = new String[]{"1"};
        alarmOrderRuleController.batchUpdateAlarmOrderRuleStatus(status, idArray);
        alarmOrderRuleController.batchUpdateAlarmOrderRuleStatus(status, null);
    }

    @Test
    public void queryAlarmOrderRuleFeign() throws Exception {
        List<AlarmOrderCondition> list = new ArrayList<>();
        AlarmOrderCondition alarmOrderCondition = new AlarmOrderCondition();
        list.add(alarmOrderCondition);
        alarmOrderRuleController.queryAlarmOrderRuleFeign(list);
        alarmOrderRuleController.queryAlarmOrderRuleFeign(null);

    }

}