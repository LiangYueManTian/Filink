package com.fiberhome.filink.alarmsetserver.controller;

import com.fiberhome.filink.alarmsetserver.bean.AlarmForwardCondition;
import com.fiberhome.filink.alarmsetserver.bean.AlarmForwardRule;
import com.fiberhome.filink.alarmsetserver.bean.AlarmForwardRuleDto;
import com.fiberhome.filink.alarmsetserver.service.AlarmForwardRuleService;
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
public class AlarmForwardRuleControllerTest {

    @Injectable
    private AlarmForwardRuleService alarmForwardRuleService;

    @Tested
    private AlarmForwardRuleController alarmForwardRuleController;

    /**
     * 查询告警远程通知列表信息
     *
     * @throws Exception
     */
    @Test
    public void queryAlarmForwardRuleList() throws Exception {
        new Expectations() {
            {
                alarmForwardRuleService.queryAlarmForwardRuleList((QueryCondition<AlarmForwardRuleDto>) any);
            }
        };
        Result result = alarmForwardRuleController.queryAlarmForwardRuleList(new QueryCondition<>());
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    /**
     * 根据id查询远程通知信息
     *
     * @throws Exception
     */
    @Test
    public void queryAlarmForwardId() throws Exception {
        new Expectations() {
            {
                alarmForwardRuleService.queryAlarmForwardId(anyString);
            }
        };
        Result result = alarmForwardRuleController.queryAlarmForwardId("1");
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
        Result result1 = alarmForwardRuleController.queryAlarmForwardId(null);
        Assert.assertTrue(result1.getCode() != 0);
    }

    /**
     * 新增告警远程通知信息
     *
     * @throws Exception
     */
    @Test
    public void addAlarmForwardRule() throws Exception {
        new Expectations() {
            {
                alarmForwardRuleService.addAlarmForwardRule((AlarmForwardRule) any);
            }
        };
        Result result = alarmForwardRuleController.addAlarmForwardRule(new AlarmForwardRule());
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
        Result result1 = alarmForwardRuleController.addAlarmForwardRule(null);
        Assert.assertTrue(result1.getCode() != 0);
    }

    /**
     * 删除告警远程通知信息
     *
     * @throws Exception
     */
    @Test
    public void batchDeleteAlarmForwardRule() throws Exception {
        new Expectations() {
            {
                alarmForwardRuleService.deleteAlarmForwardRule((String[]) any);
            }
        };
        String[] strings = new String[]{"1"};
        Result result = alarmForwardRuleController.batchDeleteAlarmForwardRule(strings);
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
        Result result1 = alarmForwardRuleController.batchDeleteAlarmForwardRule(null);
        Assert.assertTrue(result1.getCode() != 0);
    }

    /**
     * 修改告警远程通知信息
     *
     * @throws Exception
     */
    @Test
    public void updateAlarmForwardRule() throws Exception {
        new Expectations() {
            {
                alarmForwardRuleService.updateAlarmForwardRule((AlarmForwardRule) any);
            }
        };
        Result result = alarmForwardRuleController.updateAlarmForwardRule(new AlarmForwardRule());
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
        Result result1 = alarmForwardRuleController.updateAlarmForwardRule(null);
        Assert.assertTrue(result1.getCode() != 0);
    }

    /**
     * 修改告警远程通知状态信息
     *
     * @throws Exception
     */
    @Test
    public void batchUpdateAlarmForwardRuleStatus() throws Exception {
        new Expectations() {
            {
                alarmForwardRuleService.batchUpdateAlarmForwardRuleStatus((Integer) any, (String[]) any);
            }
        };
        String[] strings = new String[]{"1"};
        Result result = alarmForwardRuleController.batchUpdateAlarmForwardRuleStatus(1, strings);
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
        Result result1 = alarmForwardRuleController.batchUpdateAlarmForwardRuleStatus(null, null);
        Assert.assertTrue(result1.getCode() != 0);
    }

    /**
     * 修改告警远程通知推送类型信息
     *
     * @throws Exception
     */
    @Test
    public void batchUpdateAlarmForwardRulePushType() throws Exception {
        new Expectations() {
            {
                alarmForwardRuleService.batchUpdateAlarmForwardRulePushType((Integer) any, (String[]) any);
            }
        };
        String[] strings = new String[]{"1"};
        Result result = alarmForwardRuleController.batchUpdateAlarmForwardRulePushType(1, strings);
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
        Result result1 = alarmForwardRuleController.batchUpdateAlarmForwardRulePushType(null, null);
        Assert.assertTrue(result1.getCode() != 0);
    }

    /**
     * 查询当前告警远程通知规则
     *
     * @throws Exception
     */
    @Test
    public void queryAlarmForwardRuleFeign() throws Exception {
        List<AlarmForwardCondition> alarmForwardConditions = new ArrayList<>();
        AlarmForwardCondition alarmForwardCondition = new AlarmForwardCondition();
        alarmForwardCondition.setId("1");
        alarmForwardConditions.add(alarmForwardCondition);
        new Expectations() {
            {
                alarmForwardRuleService.queryAlarmForwardRuleFeign((List<AlarmForwardCondition>) any);
            }
        };
        alarmForwardRuleController.queryAlarmForwardRuleFeign(alarmForwardConditions);
        alarmForwardRuleController.queryAlarmForwardRuleFeign(null);
    }

}