package com.fiberhome.filink.alarmhistoryserver.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.fiberhome.filink.alarmhistoryserver.bean.AlarmHistory;
import com.fiberhome.filink.alarmhistoryserver.bean.AlarmHistory18n;
import com.fiberhome.filink.alarmhistoryserver.bean.GetMongoQueryData;
import com.fiberhome.filink.alarmhistoryserver.dao.AlarmHistoryDao;
import com.fiberhome.filink.alarmhistoryserver.service.AlarmHistoryService;
import com.fiberhome.filink.alarmhistoryserver.utils.PageBeanHelper;
import com.fiberhome.filink.bean.PageBean;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.bean.SortCondition;
import com.fiberhome.filink.mongo.MongoQueryHelper;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  历史告警服务实现类
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-22
 */
@Service
public class AlarmHistoryServiceImpl extends ServiceImpl<AlarmHistoryDao, AlarmHistory> implements AlarmHistoryService {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 查询历史告警列表信息
     *
     * @param queryCondition 条件
     * @return 历史告警列表信息
     */
    @Override
    public Result queryAlarmHistoryList(QueryCondition<AlarmHistory> queryCondition) {
        Query query = null;
        GetMongoQueryData queryResult = this.castToMongoQuery(queryCondition, query);
        if (null != queryResult.getResult()) {
            return queryResult.getResult();
        }
        query = queryResult.getQuery();
        // 查询历史告警列表信息
        List<AlarmHistory> alarmCurrents = mongoTemplate.find(query, AlarmHistory.class);
        // 查询总条数
        long count = mongoTemplate.count(query, AlarmHistory.class);
        PageBean pageBean = PageBeanHelper.generatePageBean(alarmCurrents, queryCondition, count);
        return ResultUtils.pageSuccess(pageBean);
    }

    /**
     * 查询符合条件对象的信息
     *
     * @param queryCondition 条件信息
     * @param query 查询对象
     * @return 查询条件信息
     */
    private GetMongoQueryData castToMongoQuery(QueryCondition<AlarmHistory> queryCondition, Query query) {
        query = new Query();
        GetMongoQueryData queryResult = new GetMongoQueryData();
        Result result = null;

        //判断filterCondition对象是否为空
        if (null != queryCondition.getFilterConditions()) {
            //不为空的情况需要添加过滤条件到MongoQuery对象中
            MongoQueryHelper.convertFilterConditions(queryCondition.getFilterConditions());
        }
        // 排序
        if (null == queryCondition.getSortCondition() || StringUtils.isEmpty(queryCondition.getSortCondition().getSortRule())) {
            SortCondition sortCondition = new SortCondition();
            sortCondition.setSortField("alarm_begin_time");
            sortCondition.setSortRule("desc");
            queryCondition.setSortCondition(sortCondition);
        }
        //判断pageCondition对象是否为空
        if (null != queryCondition.getPageCondition()) {
            //添加分页条件到MonogoQuery条件
            MongoQueryHelper.withPage(query, queryCondition.getPageCondition());
        } else {
            //返回pageCondition对象不能为空
            result = ResultUtils.success(ResultCode.FAIL,
                    I18nUtils.getString(AlarmHistory18n.PAGE_CONDITION_NULL));
            queryResult.setQuery(query);
            queryResult.setResult(result);
            return queryResult;
        }
        query = MongoQueryHelper.buildQuery(query, queryCondition);
        queryResult.setQuery(query);
        queryResult.setResult(result);
        return queryResult;
    }

    /**
     * 查询单个历史告警的信息
     *
     * @param alarmId 告警id
     * @return 查询结果
     */
    @Override
    public Result queryAlarmHistoryInfoById(String alarmId) {
        AlarmHistory alarmHistory = mongoTemplate.findById(alarmId, AlarmHistory.class);
        if (alarmHistory == null) {
            return ResultUtils.success(ResultCode.FAIL,
                    I18nUtils.getString(AlarmHistory18n.ALARM_SINGLE_FAILURE));
        }
        return ResultUtils.success(alarmHistory);
    }

    /**
     * 添加历史告警信息
     *
     * @param alarmHistory 历史告警信息
     * @return 判断结果
     */
    @Override
    public Result insertAlarmHistory(AlarmHistory alarmHistory) {
        mongoTemplate.save(alarmHistory);
        return ResultUtils.success(ResultCode.SUCCESS);
    }

}
