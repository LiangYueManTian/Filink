package com.fiberhome.filink.alarmcurrentserver.controller;


import com.fiberhome.filink.alarmcurrentserver.bean.AlarmCurrent;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmStatus;
import com.fiberhome.filink.alarmcurrentserver.service.AlarmCurrentService;
import com.fiberhome.filink.alarmcurrentserver.stream.AlarmStreams;
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

/**
 * controller测试类
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

    /**
     * websocket
     */
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
        new MockUp<ResultUtils>() {
            @Mock
            Result warn(int resultCode, String msg) {
                Result result = new Result();
                result.setCode(1);
                result.setMsg("success");
                return result;
            }
        };
        new MockUp<I18nUtils>() {
            @Mock
            String getString(String key) {
                return "ALARM_ID_NULL";
            }
        };
        Result result = alarmCurrentController.queryAlarmCurrentInfoById("1");
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
        Result result1 = alarmCurrentController.queryAlarmCurrentInfoById(null);
    }


    /**
     * 批量设置当前告警的告警确认状态
     */
    @Test
    public void updateAlarmConfirmStatusTest() {
        List<AlarmCurrent> alarmCurrentList = new ArrayList<>();
        AlarmCurrent alarmCurrent = new AlarmCurrent();
        alarmCurrent.setId("1");
        alarmCurrentList.add(alarmCurrent);
        new Expectations() {
            {
                alarmCurrentService.updateAlarmConfirmStatus((List<AlarmCurrent>) any);
                result = ResultUtils.success();
            }
        };
        Result result = alarmCurrentController.updateAlarmConfirmStatus(alarmCurrentList);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    /**
     * 批量设置当前告警的告警清除状态
     */
    @Test
    public void updateAlarmCleanStatusTest() {
        List<AlarmCurrent> alarmCurrentList = new ArrayList<>();
        AlarmCurrent alarmCurrent = new AlarmCurrent();
        alarmCurrent.setId("1");
        alarmCurrentList.add(alarmCurrent);
        new Expectations() {
            {
                alarmCurrentService.updateAlarmCleanStatus((List<AlarmCurrent>) any);
                result = ResultUtils.success();
            }
        };
        Result result = alarmCurrentController.updateAlarmCleanStatus(alarmCurrentList);
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

    /**
     * 查询设备信息是否存在
     */
    @Test
    public void queryAlarmSourceTest() {
        new Expectations() {
            {
                alarmCurrentService.queryAlarmSource((List<String>) any);
            }
        };
        List<String> list = new ArrayList<>();
        list.add("1");
        alarmCurrentController.queryAlarmSource(list);
    }

    /**
     * 查询区域信息是否存在
     */
    @Test
    public void queryAreaTest() {
        new Expectations() {
            {
                alarmCurrentService.queryArea((List<String>) any);
            }
        };
        List<String> list = new ArrayList<>();
        list.add("1");
        alarmCurrentController.queryArea(list);
    }

    /**
     * websocket
     */
    @Test
    public void websocketTest() {
        alarmCurrentController.websocket();
    }

}