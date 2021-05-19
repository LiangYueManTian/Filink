package com.fiberhome.filink.alarmcurrentserver.utils;

import com.fiberhome.filink.alarmcurrentserver.bean.AlarmCurrent;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmQueryTemplate;
import com.fiberhome.filink.alarmcurrentserver.bean.GetMongoQueryData;
import com.fiberhome.filink.alarmcurrentserver.service.impl.AlarmCurrentServiceImpl;
import com.fiberhome.filink.alarmcurrentserver.service.impl.AlarmQueryTemplateServiceImpl;
import com.fiberhome.filink.bean.FilterCondition;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.exportapi.job.AbstractExport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 导出方法
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-22
 */
@Component
public class AlarmCurrentExport extends AbstractExport {

    /**
     * mongodb方法
     */
    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 当前告警service
     */
    @Autowired
    private AlarmCurrentServiceImpl alarmCurrentService;

    @Autowired
    private AlarmQueryTemplateServiceImpl alarmQueryTemplateService;

    /**
     * 查询导出信息
     *
     * @param queryCondition 条件
     * @return 导出数据
     */
    @Override
    protected List queryData(QueryCondition queryCondition) {
        List<FilterCondition> filterConditions = queryCondition.getFilterConditions();
        boolean flag = filterConditions.stream().anyMatch(u -> u.getFilterField().equals("templateId"));
        if (flag) {
            queryCondition = templateQuery(queryCondition, filterConditions);
        }
        Query query = this.conditionToQuery(queryCondition);
        List<AlarmCurrent> alarmCurrentList = mongoTemplate.find(query, AlarmCurrent.class);
        return alarmCurrentList;
    }

    private QueryCondition templateQuery(QueryCondition queryCondition, List<FilterCondition> filterConditions) {
        String templateId = filterConditions.get(0).getFilterValue().toString();
        Result result = alarmQueryTemplateService.queryAlarmTemplateById(templateId);

        AlarmQueryTemplate alarmQueryTemplate = (AlarmQueryTemplate) result.getData();
        if (alarmQueryTemplate == null) {
            return null;
        }
        // 地址
        filterConditions = new ArrayList<>();
        //获取新的模板数据
        filterConditions = alarmQueryTemplateService.copy1QueryTemplateById(filterConditions, alarmQueryTemplate);
        filterConditions = alarmQueryTemplateService.copy2QueryTemplateById(filterConditions, alarmQueryTemplate);

        //将时间条件放入查询条件
        alarmQueryTemplateService.setAlarmConditionTime(queryCondition, alarmQueryTemplate, filterConditions);
        return queryCondition;
    }

    /**
     * 查询count
     *
     * @param queryCondition
     * @return count信息
     */
    @Override
    protected Integer queryCount(QueryCondition queryCondition) {
        List<FilterCondition> filterConditions = queryCondition.getFilterConditions();
        boolean flag = filterConditions.stream().anyMatch(u -> u.getFilterField().equals("templateId"));
        if (flag) {
            queryCondition = templateQuery(queryCondition, filterConditions);
        }

        Query query = this.conditionToQuery(queryCondition);
        long count = mongoTemplate.count(query, AlarmCurrent.class);
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
        GetMongoQueryData queryResult = alarmCurrentService.castToMongoQuery(condition, query);
        query = queryResult.getQuery();
        return query;
    }
}
