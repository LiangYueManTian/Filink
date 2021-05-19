package com.fiberhome.filink.alarmcurrentserver.service.impl;


import com.fiberhome.filink.alarm_api.api.AlarmHistoryFeigm;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmCurrent;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmStatus;
import com.fiberhome.filink.alarmcurrentserver.stream.AlarmStreams;
import com.fiberhome.filink.bean.FilterCondition;
import com.fiberhome.filink.bean.PageCondition;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.RequestInfoUtils;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.mongodb.BasicDBObject;
import java.util.ArrayList;
import java.util.List;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

/**
 * Service测试类
 */
@RunWith(JMockit.class)
public class AlarmCurrentServiceImplTest {

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

    /**
     * stream
     */
    @Injectable
    private AlarmStreams alarmStreams;

    /**
     * code
     */
    @Mocked
    private I18nUtils i18nUtils;

    /**
     * list
     */
    private List<AlarmCurrent> list = new ArrayList<AlarmCurrent>();

    /**
     * 日志api
     */
    @Injectable
    private LogProcess logProcess;

    @Injectable
    private AlarmHistoryFeigm alarmHistoryFeigm;

    /**
     * 查询当前告警列表
     */
    @Test
    public void queryAlarmCurrentListTest() {
        Result result = alarmCurrentService.queryAlarmCurrentList(new QueryCondition<AlarmCurrent>());
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
        Result result1 = alarmCurrentService.queryAlarmCurrentList(queryCondition);
    }

    /**
     * 查询单个当前告警信息
     */
    @Test
    public void queryAlarmCurrentInfoByIdTest() {
        Result result = alarmCurrentService.queryAlarmCurrentInfoById(null);
        String alarmId = "1";
        new Expectations() {
            {
                mongoTemplate.findById((Query) any,AlarmCurrent.class);
                result = null;
            }
        };
        Result result1 = alarmCurrentService.queryAlarmCurrentInfoById(alarmId);
    }

    /**
     * 批量设置当前告警的告警确认状态
     */
    @Test
    public void updateAlarmConfirmStatusTest() {
        new Expectations() {
            {
                mongoTemplate.updateFirst((Query) any, (Update) any, AlarmCurrent.class);
                result = null;
            }
        };
        new Expectations() {
            {
                mongoTemplate.find((Query) any, AlarmCurrent.class);
                result = 1;
            }
        };
        new MockUp<RequestInfoUtils>() {
            @Mock
            String getUserId() {
                return "1";
            }
        };
        new MockUp<RequestInfoUtils>() {
            @Mock
            String getUserName() {
                return "admin";
            }
        };
        AlarmStatus alarmStatus = new AlarmStatus();
        List<AlarmCurrent> lists = new ArrayList<AlarmCurrent>();
        AlarmCurrent alarmCurrents = new AlarmCurrent();
        alarmCurrents.setId("1");
        lists.add(alarmCurrents);
        alarmStatus.setAlarmCurrents(lists);
        alarmStatus.setUserId("1");
        alarmStatus.setToken("123456");
        Result result = alarmCurrentService.updateAlarmConfirmStatus(lists);
    }

    @Test
    public void updateAlarmConfirmStatusTestOne() {
        new Expectations() {
            {
                mongoTemplate.find((Query) any, AlarmCurrent.class);
                result = null;
            }
        };
        new MockUp<RequestInfoUtils>() {
            @Mock
            String getUserId() {
                return "1";
            }
        };
        new MockUp<RequestInfoUtils>() {
            @Mock
            String getUserName() {
                return "admin";
            }
        };
        List<AlarmCurrent> lists = new ArrayList<AlarmCurrent>();
        AlarmCurrent alarmCurrents = new AlarmCurrent();
        alarmCurrents.setId("1");
        lists.add(alarmCurrents);
        Result result = alarmCurrentService.updateAlarmConfirmStatus(lists);
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
        new Expectations() {
            {
                mongoTemplate.find((Query) any, AlarmCurrent.class);
                result = 1;
            }
        };
        new MockUp<RequestInfoUtils>() {
            @Mock
            String getUserId() {
                return "1";
            }
        };
        new MockUp<RequestInfoUtils>() {
            @Mock
            String getUserName() {
                return "admin";
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
        Result result1 = alarmCurrentService.updateAlarmCleanStatus(lists);
    }

    /**
     * 查询各级别告警总数
     */
    @Test
    public void queryEveryAlarmCountTest() {
        new Expectations() {
            {
                AggregationResults<BasicDBObject> alarm_current = mongoTemplate.aggregate((Aggregation) any, "alarm_current", BasicDBObject.class);
                result = alarm_current;
            }
        };
        Result result = alarmCurrentService.queryEveryAlarmCount();
    }

    /**
     * 查询设备信息是否存在
     */
    @Test
    public void queryAlarmSourceTest() {

        new Expectations() {
            {
                List<AlarmCurrent> list = mongoTemplate.find((Query) any, AlarmCurrent.class);
                result = 1;
            }
        };
        List<String> list = new ArrayList<>();
        list.add("1");
        List<String> list2 = alarmCurrentService.queryAlarmSource(list);
        new Expectations() {
            {
                List<AlarmCurrent> list = mongoTemplate.find((Query) any, AlarmCurrent.class);
                result = null;
            }
        };
        List<String> list1 = alarmCurrentService.queryAlarmSource(list);
    }

    /**
     * 查询设备信息是否存在
     */
    @Test
    public void queryAreaTest() {
        new Expectations() {
            {
                List<AlarmCurrent> list = mongoTemplate.find((Query) any, AlarmCurrent.class);
                result = 1;
            }
        };
        List<String> list = new ArrayList<>();
        list.add("1");
        List<String> list1 = alarmCurrentService.queryArea(list);
        new Expectations() {
            {
                List<AlarmCurrent> list = mongoTemplate.find((Query) any, AlarmCurrent.class);
                result = null;
            }
        };
        List<String> list2 = alarmCurrentService.queryArea(list);
    }
}