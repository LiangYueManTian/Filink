package com.fiberhome.filink.alarmhistoryserver.service.impl;


import com.fiberhome.filink.alarmhistoryserver.bean.AlarmHistory;
import com.fiberhome.filink.bean.FilterCondition;
import com.fiberhome.filink.bean.PageCondition;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import java.util.ArrayList;
import java.util.List;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

@RunWith(JMockit.class)
public class AlarmHistoryServiceImplTest {

    /**
     * controller测试
     */
    @Tested
    private AlarmHistoryServiceImpl alarmHistoryService;

    /**
     * 注入的dao
     */
    @Injectable
    private MongoTemplate mongoTemplate;

    @Mocked
    private I18nUtils i18nUtils;

    /**
     * 查询历史告警列表信息
     */
    @Test
    public void queryAlarmHistoryListTest() {
        Result result1 = alarmHistoryService.queryAlarmHistoryList(new QueryCondition<AlarmHistory>());
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
        Result result = alarmHistoryService.queryAlarmHistoryList(queryCondition);
    }

    /**
     * 查询单个历史告警的信息
     */
    @Test
    public void queryAlarmHistoryInfoByIdTest() {
        new Expectations() {
            {
                mongoTemplate.findById((Query) any, AlarmHistory.class);
            }
        };
        Result result = alarmHistoryService.queryAlarmHistoryInfoById("1");
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
                mongoTemplate.save((AlarmHistory) any);
            }
        };
        alarmHistoryService.insertAlarmHistory(alarmHistory);
    }
}