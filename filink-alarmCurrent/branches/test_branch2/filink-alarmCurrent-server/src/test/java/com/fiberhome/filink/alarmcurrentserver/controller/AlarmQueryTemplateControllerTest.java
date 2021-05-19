package com.fiberhome.filink.alarmcurrentserver.controller;

import com.fiberhome.filink.alarmcurrentserver.bean.AlarmQueryTemplate;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmQueryTemplateDto;
import com.fiberhome.filink.alarmcurrentserver.service.AlarmQueryTemplateService;
import com.fiberhome.filink.bean.FilterCondition;
import com.fiberhome.filink.bean.PageCondition;
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
public class AlarmQueryTemplateControllerTest {

    @Tested
    private AlarmQueryTemplateController alarmQueryTemplateController;

    @Injectable
    private AlarmQueryTemplateService alarmQueryTemplateService;

    /**
     * 查询告警模板列表信息
     */
    @Test
    public void queryAlarmTemplateListTest() {
        new Expectations() {
            {
                alarmQueryTemplateService.queryAlarmTemplateList((QueryCondition<AlarmQueryTemplateDto>) any);
            }
        };
        Result result = alarmQueryTemplateController.queryAlarmTemplateList(new QueryCondition<AlarmQueryTemplateDto>());
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    /**
     * 删除告警模板信息
     */
    @Test
    public void batchDeleteAlarmTemplateTest() {
        new Expectations() {
            {
                alarmQueryTemplateService.batchDeleteAlarmTemplate((String[]) any);
            }
        };
        String[] strings = new String[]{"1", "2"};
        Result result = alarmQueryTemplateController.batchDeleteAlarmTemplate(strings);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
        new MockUp<ResultUtils>() {
            @Mock
            Result warn(int resultCode, String msg) {
                Result resultOne = new Result();
                resultOne.setCode(1);
                resultOne.setMsg("success");
                return resultOne;
            }
        };
        new MockUp<I18nUtils>() {
            @Mock
            String getString(String key) {
                return "INCORRECT_PARAMETER";
            }
        };
        Result resultOne = alarmQueryTemplateController.batchDeleteAlarmTemplate(null);
    }

    /**
     * 查询告警模板信息
     */
    @Test
    public void queryAlarmTemplateByIdTest() {
        new Expectations() {
            {
                alarmQueryTemplateService.queryAlarmTemplateById(anyString);
            }
        };
        Result result = alarmQueryTemplateController.queryAlarmTemplateById("1");
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
        new MockUp<ResultUtils>() {
            @Mock
            Result warn(int resultCode, String msg) {
                Result resultOne = new Result();
                resultOne.setCode(1);
                resultOne.setMsg("success");
                return resultOne;
            }
        };
        new MockUp<I18nUtils>() {
            @Mock
            String getString(String key) {
                return "INCORRECT_PARAMETER";
            }
        };
        Result resultOne = alarmQueryTemplateController.queryAlarmTemplateById(null);
    }

    /**
     * 查询告警模板信息
     */
    @Test
    public void queryAlarmQueryTemplateByIdTest() {
        new Expectations() {
            {
                alarmQueryTemplateService.queryAlarmQueryTemplateById(anyString, (QueryCondition) any);
            }
        };
        QueryCondition queryCondition = new QueryCondition();
        PageCondition pageCondition = new PageCondition();
        pageCondition.setPageNum(1);
        pageCondition.setPageSize(10);
        queryCondition.setPageCondition(pageCondition);
        List<FilterCondition> filterConditionList = new ArrayList<FilterCondition>();
        FilterCondition filterCondition = new FilterCondition();
        filterCondition.setFilterField("id");
        filterCondition.setFilterValue("1");
        filterCondition.setOperator("like");
        filterConditionList.add(filterCondition);
        queryCondition.setFilterConditions(filterConditionList);
        Result result = alarmQueryTemplateController.queryAlarmQueryTemplateById("1", queryCondition);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
        new MockUp<ResultUtils>() {
            @Mock
            Result warn(int resultCode, String msg) {
                Result resultOne = new Result();
                resultOne.setCode(1);
                resultOne.setMsg("success");
                return resultOne;
            }
        };
        new MockUp<I18nUtils>() {
            @Mock
            String getString(String key) {
                return "INCORRECT_PARAMETER";
            }
        };
        Result resultOne = alarmQueryTemplateController.queryAlarmQueryTemplateById(null, queryCondition);
    }

    /**
     * 修改告警模板信息
     */
    @Test
    public void updateAlarmTemplateTest() {
        new Expectations() {
            {
                alarmQueryTemplateService.updateAlarmTemplate((AlarmQueryTemplate) any);
            }
        };
        AlarmQueryTemplate alarmQueryTemplate = new AlarmQueryTemplate();
        Result result = alarmQueryTemplateController.updateAlarmTemplate(alarmQueryTemplate);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    /**
     * 新增告警模板
     */
    @Test
    public void addAlarmTemplateTest() {
        new Expectations() {
            {
                alarmQueryTemplateService.addAlarmTemplate((AlarmQueryTemplate) any);
            }
        };
        AlarmQueryTemplate alarmQueryTemplate = new AlarmQueryTemplate();
        Result result = alarmQueryTemplateController.addAlarmTemplate(alarmQueryTemplate);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
        new MockUp<ResultUtils>() {
            @Mock
            Result warn(int resultCode, String msg) {
                Result resultOne = new Result();
                resultOne.setCode(1);
                resultOne.setMsg("success");
                return resultOne;
            }
        };
        new MockUp<I18nUtils>() {
            @Mock
            String getString(String key) {
                return "INCORRECT_PARAMETER";
            }
        };
        Result resultOne = alarmQueryTemplateController.addAlarmTemplate(null);
    }
}