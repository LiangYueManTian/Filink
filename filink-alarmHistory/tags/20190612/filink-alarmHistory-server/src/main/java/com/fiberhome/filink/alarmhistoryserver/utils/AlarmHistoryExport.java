package com.fiberhome.filink.alarmhistoryserver.utils;

import com.fiberhome.filink.alarmhistoryserver.bean.AlarmHistory;
import com.fiberhome.filink.alarmhistoryserver.bean.GetMongoQueryData;
import com.fiberhome.filink.alarmhistoryserver.service.impl.AlarmHistoryServiceImpl;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.exportapi.job.AbstractExport;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

/**
 * <p>
 * 导出方法
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-22
 */
@Component
public class AlarmHistoryExport extends AbstractExport {

    /**
     * mongodb方法
     */
    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 历史告警service
     */
    @Autowired
    private AlarmHistoryServiceImpl alarmHistoryService;

    /**
     * 查询导出信息
     *
     * @param queryCondition 条件
     * @return 导出数据
     */
    @Override
    protected List queryData(QueryCondition queryCondition) {
        Query query = this.conditionToQuery(queryCondition);
        // 查询当前告警列表
        List<AlarmHistory> alarmHistoryList = mongoTemplate.find(query, AlarmHistory.class);
        return alarmHistoryList;
    }

    /**
     * 查询count
     *
     * @param queryCondition
     * @return count信息
     */
    @Override
    protected Integer queryCount(QueryCondition queryCondition) {
        Query query = this.conditionToQuery(queryCondition);
        long count = mongoTemplate.count(query, AlarmHistory.class);
        return (int) count;
    }

    /**
     * 条件对象信息
     *
     * @param condition 条件
     * @return 条件信息
     */
    private Query conditionToQuery(QueryCondition condition) {
        Query query = null;
        //将查询条件创建成query对象
        GetMongoQueryData queryResult = alarmHistoryService.castToMongoQuery(condition, query);
        query = queryResult.getQuery();
        return query;
    }
}
