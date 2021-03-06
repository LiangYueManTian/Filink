package com.fiberhome.filink.alarmcurrentserver.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.fiberhome.filink.alarm_api.api.AlarmHistoryFeigm;
import com.fiberhome.filink.alarm_api.bean.AlarmHistory;
import com.fiberhome.filink.alarmcurrentserver.bean.Alarm18n;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmCurrent;
import com.fiberhome.filink.alarmcurrentserver.bean.GetMongoQueryData;
import com.fiberhome.filink.alarmcurrentserver.dao.AlarmCurrentDao;
import com.fiberhome.filink.alarmcurrentserver.exception.FilinkAlarmCurrentException;
import com.fiberhome.filink.alarmcurrentserver.service.AlarmCurrentService;
import com.fiberhome.filink.alarmcurrentserver.utils.PageBeanHelper;
import com.fiberhome.filink.bean.PageBean;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.RequestInfoUtils;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.bean.SortCondition;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.logapi.utils.LogConstants;
import com.fiberhome.filink.mongo.MongoQueryHelper;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.server_common.utils.UUIDUtil;
import com.mongodb.BasicDBObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * ???????????????????????????
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-22
 */
@Service
public class AlarmCurrentServiceImpl extends ServiceImpl<AlarmCurrentDao, AlarmCurrent> implements AlarmCurrentService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private LogProcess logProcess;

    @Autowired
    private AlarmHistoryFeigm alarmHistoryFeigm;

    /**
     * ????????????????????????
     *
     * @return ??????????????????
     */
    @Override
    public Result queryAlarmCurrentList(QueryCondition<AlarmCurrent> queryCondition) {
        Query query = null;
        GetMongoQueryData queryResult = this.castToMongoQuery(queryCondition, query);
        if (null != queryResult.getResult()) {
            return queryResult.getResult();
        }
        // ?????? ????????????
        query = queryResult.getQuery();
        // ????????????????????????
        List<AlarmCurrent> alarmCurrents = mongoTemplate.find(query, AlarmCurrent.class);
        // ??????????????????????????????
        long count = mongoTemplate.count(query, AlarmCurrent.class);
        PageBean pageBean = PageBeanHelper.generatePageBean(alarmCurrents, queryCondition, count);
        return ResultUtils.pageSuccess(pageBean);
    }

    /**
     * ??????id??????????????????
     *
     * @param deviceId ??????id
     * @return ????????????
     */
    @Override
    public Result queryAlarmDeviceId(String deviceId) {
        Query query = new Query();
        Sort sort = new Sort(Sort.Direction.ASC, "alarm_fixed_level").
                and(new Sort(Sort.Direction.DESC, "alarm_near_time"));
        Criteria criteria = Criteria.where("alarm_source").is(deviceId);
        query.with(sort).addCriteria(criteria).limit(5);
        List<AlarmCurrent> alarmCurrents = mongoTemplate.find(query, AlarmCurrent.class);
        return ResultUtils.success(alarmCurrents);
    }

    /**
     * ?????????????????????????????????
     *
     * @param queryCondition ????????????
     * @param query          ????????????
     * @return ??????????????????
     */
    private GetMongoQueryData castToMongoQuery(QueryCondition<AlarmCurrent> queryCondition, Query query) {
        query = new Query();
        GetMongoQueryData queryResult = new GetMongoQueryData();
        Result result = null;

        //??????filterCondition??????????????????
        if (null != queryCondition.getFilterConditions()) {
            //?????????????????????????????????????????????MongoQuery?????????
            MongoQueryHelper.convertFilterConditions(queryCondition.getFilterConditions());
        }
        // ??????
        if (null == queryCondition.getSortCondition() || StringUtils.isEmpty(queryCondition.getSortCondition().getSortRule())) {
            SortCondition sortCondition = new SortCondition();
            sortCondition.setSortField("alarm_begin_time");
            sortCondition.setSortRule("desc");
            queryCondition.setSortCondition(sortCondition);
        }
        //??????pageCondition??????????????????
        if (null != queryCondition.getPageCondition()) {
            //?????????????????????MonogoQuery??????
            MongoQueryHelper.withPage(query, queryCondition.getPageCondition());
        } else {
            //??????pageCondition??????????????????
            result = ResultUtils.warn(ResultCode.FAIL,
                    I18nUtils.getString(Alarm18n.PAGE_CONDITION_NULL));
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
     * ??????????????????????????????
     *
     * @param alarmId ????????????ID
     * @return ??????????????????
     */
    @Override
    public Result queryAlarmCurrentInfoById(String alarmId) {
        AlarmCurrent alarmCurrent = mongoTemplate.findById(alarmId, AlarmCurrent.class);
        if (alarmCurrent == null) {
            return ResultUtils.warn(ResultCode.FAIL,
                    I18nUtils.getString(Alarm18n.SINGLE_ALARM_FAILED));
        }
        return ResultUtils.success(alarmCurrent);
    }

    /**
     * ?????????????????????????????????????????????
     *
     * @param alarmCurrents ??????????????????
     * @return ????????????
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result updateAlarmConfirmStatus(List<AlarmCurrent> alarmCurrents) {
        try {
            Set<String> strings = new HashSet<>();
            // ??????????????????id
            alarmCurrents.forEach(alarmCurrent -> strings.add(alarmCurrent.getId()));
            // ??????????????????????????????????????????
            Query query = new Query(new Criteria().andOperator(
                    Criteria.where(Alarm18n.ALARM_ID).in(strings),
                    Criteria.where(Alarm18n.ALARM_CONFIRM_STATUS).is(2)));
            List<AlarmCurrent> list = mongoTemplate.find(query, AlarmCurrent.class);
            // ????????????????????????????????????
            if (list == null || list.size() == 0) {
                return ResultUtils.warn(ResultCode.FAIL,
                        I18nUtils.getString(Alarm18n.STATUS_CONFIRMED));
            }
            for (AlarmCurrent alarmCurrent : alarmCurrents) {
                checkName(alarmCurrent);
                // ????????????
                Date date = new Date();
                long time = date.getTime();
                // ??????????????????
                String userId = RequestInfoUtils.getUserId();
                String userName = RequestInfoUtils.getUserName();
                Query query1 = new Query(Criteria.where(Alarm18n.ALARM_ID).is(alarmCurrent.getId()).
                        andOperator(Criteria.where(Alarm18n.ALARM_CONFIRM_STATUS).in(2)));
                // ?????????????????????
                Update update = new Update().set(Alarm18n.ALARM_CONFIRM_STATUS, 1)
                        .set(Alarm18n.ALARM_CONFIRM_ID, userId).set(Alarm18n.ALARM_CONFIRM_NAME, userName)
                        .set(Alarm18n.ALARM_CONFIRM_TIME, time);
                mongoTemplate.updateFirst(query1, update, AlarmCurrent.class);
                updateLog(alarmCurrent.getId(), Alarm18n.ALARM_LOG_TWO);
            }
            return ResultUtils.success(ResultCode.SUCCESS,
                    I18nUtils.getString(Alarm18n.ALARM_CONFIRM_SUCCESS));
        } catch (Exception e) {
            e.printStackTrace();
            throw new FilinkAlarmCurrentException(I18nUtils.getString(Alarm18n.ALARM_CONFIRM_FAILED));
        }
    }

    /**
     * ?????????????????????????????????????????????
     *
     * @param alarmCurrents ????????????????????????
     * @return ????????????
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result updateAlarmCleanStatus(List<AlarmCurrent> alarmCurrents) {
        try {
            Set<String> strings = new HashSet<>();
            // ??????????????????id
            alarmCurrents.forEach(alarmCurrent -> strings.add(alarmCurrent.getId()));
            // ??????????????????????????????????????????
            Query query = new Query(new Criteria().andOperator(
                    Criteria.where(Alarm18n.ALARM_ID).in(strings),
                    Criteria.where(Alarm18n.ALARM_CLEAN_STATUS).is(3)));
            List<AlarmCurrent> list = mongoTemplate.find(query, AlarmCurrent.class);
            // ????????????????????????????????????
            if (list == null || list.size() == 0) {
                return ResultUtils.warn(ResultCode.FAIL,
                        I18nUtils.getString(Alarm18n.STATUS_CONFIRMED));
            }
            for (AlarmCurrent alarmCurrent : alarmCurrents) {
                checkName(alarmCurrent);
                // ????????????
                Date parse = new Date();
                long time = parse.getTime();
                // ??????????????????ID
                String userId = RequestInfoUtils.getUserId();
                String userName = RequestInfoUtils.getUserName();
                Query query1 = new Query(Criteria.where(Alarm18n.ALARM_ID).is(alarmCurrent.getId()));
                // ??????????????????
                Update update = new Update().set(Alarm18n.ALARM_CLEAN_STATUS, 1)
                        .set(Alarm18n.ALARM_CLEAN_ID, userId).set(Alarm18n.ALARM_CLEAN_NAME, userName)
                        .set(Alarm18n.ALARM_CLEAN_TIME, time);
                mongoTemplate.updateFirst(query1, update, AlarmCurrent.class);
                updateLog(alarmCurrent.getId(), Alarm18n.ALARM_LOG_THREE);
                // ?????????????????????????????????
                AlarmCurrent alarmCurrent1 = mongoTemplate.findOne(query1, AlarmCurrent.class);
                AlarmHistory alarmHistory = new AlarmHistory();
                // ??????????????????????????????????????????
                BeanUtils.copyProperties(alarmCurrent1, alarmHistory);
                // ??????id ????????????
                alarmHistory.setId(null);
                String id = UUIDUtil.getInstance().UUID32();
                alarmHistory.setId(id);
                // ????????????????????????
                alarmHistoryFeigm.insertAlarmHistory(alarmHistory);
                // ????????????????????????
                mongoTemplate.remove(query1, AlarmCurrent.class);
            }
            return ResultUtils.success(ResultCode.SUCCESS,
                    I18nUtils.getString(Alarm18n.ALARM_REMOVE_SUCCESS));
        } catch (Exception e) {
            e.printStackTrace();
            throw new FilinkAlarmCurrentException(I18nUtils.getString(Alarm18n.ALARM_REMOVE_FAILED));
        }
    }

    /**
     * ???????????????????????????
     *
     * @return ??????????????????
     */
    @Override
    public Result queryEveryAlarmCount() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where(Alarm18n.ALARM_CLEAN_STATUS).is(3)),
                Aggregation.group(Alarm18n.ALARM_FIXED_LEVEL).count().as(Alarm18n.COUNT),
                Aggregation.project(Alarm18n.COUNT).and(Alarm18n.ALARM_FIXED_LEVEL).previousOperation());
        AggregationResults<BasicDBObject> res = mongoTemplate.aggregate
                (aggregation, "alarm_current", BasicDBObject.class);
        Iterator<BasicDBObject> iterator = res.iterator();
        return ResultUtils.success(iterator);
    }

    /**
     * ??????????????????????????????
     *
     * @param alarmSources ??????id
     * @return ????????????
     */
    @Override
    public List<String> queryAlarmSource(List<String> alarmSources) {
        List<String> list = new ArrayList<String>();
        for (String alarmSource : alarmSources) {
            Query query = new Query(Criteria.where("alarmSource").is(alarmSource));
            List<AlarmCurrent> alarmCurrent = mongoTemplate.find(query, AlarmCurrent.class);
            if (alarmCurrent != null && alarmCurrent.size() > 0) {
                list.add(alarmSource);
                break;
            }
        }
        return list;
    }

    /**
     * ??????????????????????????????
     *
     * @param areaIds ??????ID
     * @return ????????????
     */
    @Override
    public List<String> queryArea(List<String> areaIds) {
        List<String> list = new ArrayList<String>();
        for (String areaId : areaIds) {
            Query query = new Query(Criteria.where("areaId").is(areaId));
            List<AlarmCurrent> alarmCurrent = mongoTemplate.find(query, AlarmCurrent.class);
            if (alarmCurrent != null && alarmCurrent.size() > 0) {
                list.add(areaId);
                break;
            }
        }
        return list;
    }

    /**
     * ????????????
     *
     * @param alarmCurrent ??????????????????
     */
    private void checkName(AlarmCurrent alarmCurrent) {
        if (alarmCurrent.getId() == null) {
            throw new FilinkAlarmCurrentException(I18nUtils.getString(Alarm18n.ALARM_ID_NULL));
        }
    }

    /**
     * ??????????????????
     *
     * @param id    ????????????id
     * @param model ????????????
     */
    private void updateLog(String id, String model) {
        if (StringUtils.isEmpty(id)){
            return;
        }
        AlarmCurrent alarmCurrent = mongoTemplate.findOne(new Query(Criteria.where(Alarm18n.ALARM_ID).is(id)), AlarmCurrent.class);
        // ??????????????????
        String logType = LogConstants.LOG_TYPE_OPERATE;
        AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
        addLogBean.setDataId(Alarm18n.ALARM_ID);
        addLogBean.setDataName("alarmName");
        addLogBean.setFunctionCode(model);
        // ??????????????????
        addLogBean.setOptObjId(id);
        addLogBean.setOptObj(alarmCurrent.getAlarmName());
        // ???????????????
        addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_UPDATE);
        // ??????????????????
        logProcess.addOperateLogInfoToCall(addLogBean, LogConstants.ADD_LOG_LOCAL_FILE);
    }

}
