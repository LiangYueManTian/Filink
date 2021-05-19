package com.fiberhome.filink.alarmsetserver.controller;

import com.fiberhome.filink.alarmsetserver.bean.AlarmFilterCondition;
import com.fiberhome.filink.alarmsetserver.bean.AlarmFilterRule;
import com.fiberhome.filink.alarmsetserver.service.AlarmFilterRuleService;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.deviceapi.api.DeviceFeign;
import java.util.ArrayList;
import java.util.List;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMockit.class)
public class AlarmFilterRuleControllerTest {

    @Tested
    private AlarmFilterRuleController alarmFilterRuleController;

    @Injectable
    private AlarmFilterRuleService alarmFilterRuleService;

    @Injectable
    private DeviceFeign deviceFeign;

    /**
     * 查询告警过滤列表信息
     */
    @Test
    public void queryAlarmFilterRuleListTest() {
        new Expectations() {
            {
                alarmFilterRuleService.queryAlarmFilterRuleList((QueryCondition) any);
            }
        };
        Result result = alarmFilterRuleController.queryAlarmFilterRuleList(new QueryCondition<>());
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    /**
     * 查询告警过滤信息
     */
    @Test
    public void queryAlarmFilterRuleByIdTest() {
        new Expectations() {
            {
                alarmFilterRuleService.queryAlarmFilterRuleById((String) any);
            }
        };
        Result result = alarmFilterRuleController.queryAlarmFilterRuleById("1");
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
        alarmFilterRuleController.queryAlarmFilterRuleById(null);
    }

    /**
     * 新增告警过滤信息
     */
    @Test
    public void addAlarmFilterRuleTest() {
        new Expectations() {
            {
                alarmFilterRuleService.addAlarmFilterRule((AlarmFilterRule) any);
            }
        };
        Result result = alarmFilterRuleController.addAlarmFilterRule(new AlarmFilterRule());
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
        alarmFilterRuleController.addAlarmFilterRule(null);
    }

    /**
     * 删除告警过滤信息
     */
    @Test
    public void batchDeleteAlarmFilterRuleTest() {
        new Expectations() {
            {
                alarmFilterRuleService.batchDeleteAlarmFilterRule((String[]) any);
            }
        };
        String[] strings = new String[]{"1","3"};
        Result result = alarmFilterRuleController.batchDeleteAlarmFilterRule(strings);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
        alarmFilterRuleController.batchDeleteAlarmFilterRule(null);
    }

    /**
     * 修改告警过滤信息
     */
    @Test
    public void updateAlarmFilterRuleTest() {
        new Expectations() {
            {
                alarmFilterRuleService.updateAlarmFilterRule((AlarmFilterRule) any);
            }
        };
        Result result = alarmFilterRuleController.updateAlarmFilterRule(new AlarmFilterRule());
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
        alarmFilterRuleController.updateAlarmFilterRule(null);
    }

    /**
     * 修改告警过滤状态信息
     */
    @Test
    public void batchUpdateAlarmFilterRuleStatusTest() {
        new Expectations() {
            {
                alarmFilterRuleService.batchUpdateAlarmFilterRuleStatus((Integer) any, (String[]) any);
            }
        };
        String[] strings = new String[]{"1"};
        Result result = alarmFilterRuleController.batchUpdateAlarmFilterRuleStatus(1, strings);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
        alarmFilterRuleController.batchUpdateAlarmFilterRuleStatus(1, null);
    }

    /**
     * 修改告警过滤存库信息
     */
    @Test
    public void batchUpdateAlarmFilterRuleStoredTest() {
        new Expectations() {
            {
                alarmFilterRuleService.batchUpdateAlarmFilterRuleStored((Integer) any, (String[]) any);
            }
        };
        String[] strings = new String[]{"1"};
        Result result = alarmFilterRuleController.batchUpdateAlarmFilterRuleStored(1, strings);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
        alarmFilterRuleController.batchUpdateAlarmFilterRuleStored(1, null);
    }

    /**
     * 查询当前告警信息是否过被过滤
     */
    @Test
    public void queryAlarmIsIncludedFeignTest() {
        List<AlarmFilterCondition> alarmFilterConditions = new ArrayList<>();
        AlarmFilterCondition alarmFilterCondition = new AlarmFilterCondition();
        alarmFilterCondition.setId("1");
        alarmFilterConditions.add(alarmFilterCondition);
        new Expectations() {
            {
                alarmFilterRuleService.queryAlarmIsIncludedFeign((List<AlarmFilterCondition>) any);
            }
        };
        alarmFilterRuleController.queryAlarmIsIncludedFeign(alarmFilterConditions);
        alarmFilterRuleController.queryAlarmIsIncludedFeign(null);
    }

}