package com.fiberhome.filink.alarm_server.service;


import com.fiberhome.filink.alarmhistoryserver.bean.AlarmHistory;
import com.fiberhome.filink.alarmhistoryserver.service.AlarmHistoryService;
import com.fiberhome.filink.alarmhistoryserver.service.impl.AlarmHistoryServiceImpl;
import com.fiberhome.filink.bean.FilterCondition;
import com.fiberhome.filink.bean.PageCondition;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  service测试类
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-22
 */
@RunWith(JMockit.class)
public class AlarmHistoryServiceTest {

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
                result = null;
            }
        };
        String alarmIds = "1";
        Result result = alarmHistoryService.queryAlarmHistoryInfoById(alarmIds);
    }
}
