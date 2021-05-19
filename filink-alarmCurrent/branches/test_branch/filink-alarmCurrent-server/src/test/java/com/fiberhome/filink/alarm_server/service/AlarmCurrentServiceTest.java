package com.fiberhome.filink.alarm_server.service;


import com.fiberhome.filink.alarmcurrentserver.bean.AlarmCurrent;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmStatus;
import com.fiberhome.filink.alarmcurrentserver.service.impl.AlarmCurrentServiceImpl;
import com.fiberhome.filink.alarmcurrentserver.stream.AlarmStreams;
import com.fiberhome.filink.bean.FilterCondition;
import com.fiberhome.filink.bean.PageCondition;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.user_api.api.UserFeign;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapreduce.GroupBy;
import org.springframework.data.mongodb.core.mapreduce.GroupByResults;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * AlarmCurrentService测试类
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-22
 */
@RunWith(JMockit.class)
public class AlarmCurrentServiceTest {

    /**
     * 测试Service
     */
    @Tested
    private AlarmCurrentServiceImpl alarmCurrentService;

    /**
     * 注入的dao
     */
    @Injectable
    private MongoTemplate mongoTemplate;

    @Injectable
    private AlarmStreams alarmStreams;

    @Mocked
    private I18nUtils i18nUtils;

    @Injectable
    private UserFeign userFeign;
    /**
     * list
     */
    private List<AlarmCurrent> list = new ArrayList<AlarmCurrent>();

    /**
     * 查询当前告警列表
     */
    @Test
    public void queryAlarmCurrentListTest() {
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
        Result result = alarmCurrentService.queryAlarmCurrentList(queryCondition);
    }

    /**
     * 查询单个当前告警信息
     */
    @Test
    public void queryAlarmCurrentInfoByIdTest() {
        String alarmId = "1";
        new Expectations() {
            {
                mongoTemplate.findById((Query) any,AlarmCurrent.class);
                result = null;
            }
        };
        Result result = alarmCurrentService.queryAlarmCurrentInfoById(alarmId);
    }

    /**
     * 批量修改当前告警备注信息
     */
    @Test
    public void updateAlarmRemarkTest() {
        new Expectations() {
            {
                mongoTemplate.updateFirst((Query) any, (Update) any, AlarmCurrent.class);
                result = null;
            }
        };
        List<AlarmCurrent> lists = new ArrayList<AlarmCurrent>();
        AlarmCurrent alarmCurrents = new AlarmCurrent();
        alarmCurrents.setId("1");
        alarmCurrents.setRemark("11");
        lists.add(alarmCurrents);
        Result result = alarmCurrentService.updateAlarmRemark(lists);
    }

    /**
     * 批量设置当前告警的告警清除状态
     */
    @Test
    public void updateAlarmConfirmStatusTest() {
        new Expectations() {
            {
                mongoTemplate.updateFirst((Query) any, (Update) any, AlarmCurrent.class);
                result = null;
            }
        };
        AlarmStatus alarmStatus = new AlarmStatus();
        List<AlarmCurrent> lists = new ArrayList<AlarmCurrent>();
        AlarmCurrent alarmCurrents = new AlarmCurrent();
        alarmCurrents.setId("1");
        lists.add(alarmCurrents);
        alarmStatus.setAlarmCurrents(lists);
        alarmStatus.setUserId("1");
        alarmStatus.setToken("admin");
        Result result1 = alarmCurrentService.updateAlarmConfirmStatus(alarmStatus);
    }

    /**
     * 批量设置当前告警的告警清除状态
     */
    @Test
    public void updateAlarmCleanStatusTest() {
        new Expectations() {
            {
                mongoTemplate.updateFirst((Query) any, (Update) any, AlarmCurrent.class);
                result = null;
            }
        };
        AlarmStatus alarmStatus = new AlarmStatus();
        List<AlarmCurrent> lists = new ArrayList<AlarmCurrent>();
        AlarmCurrent alarmCurrents = new AlarmCurrent();
        alarmCurrents.setId("1");
        lists.add(alarmCurrents);
        alarmStatus.setAlarmCurrents(lists);
        alarmStatus.setUserId("1");
        alarmStatus.setToken("admin");
        Result result1 = alarmCurrentService.updateAlarmCleanStatus(alarmStatus);
    }

    /**
     * 查询各级别告警总数
     */
    @Test
    public void queryEveryAlarmCountTest() {
        new Expectations() {
            {
                GroupByResults<AlarmCurrent> alarm_current = mongoTemplate.group("alarm_current", (GroupBy) any, AlarmCurrent.class);
                result = alarm_current;
            }
        };
        Result result = alarmCurrentService.queryEveryAlarmCount();
    }

}
