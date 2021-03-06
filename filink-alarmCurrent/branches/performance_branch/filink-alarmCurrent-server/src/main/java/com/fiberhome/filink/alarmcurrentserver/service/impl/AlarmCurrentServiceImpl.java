package com.fiberhome.filink.alarmcurrentserver.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmCurrent;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmDepartment;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmFilter;
import com.fiberhome.filink.alarmcurrentserver.bean.GetMongoQueryData;
import com.fiberhome.filink.alarmcurrentserver.constant.AlarmCurrent18n;
import com.fiberhome.filink.alarmcurrentserver.constant.AppConstant;
import com.fiberhome.filink.alarmcurrentserver.constant.LogFunctionCodeConstant;
import com.fiberhome.filink.alarmcurrentserver.dao.AlarmCurrentDao;
import com.fiberhome.filink.alarmcurrentserver.enums.AlarmNameTypeEnum;
import com.fiberhome.filink.alarmcurrentserver.exception.FilinkAlarmCurrentException;
import com.fiberhome.filink.alarmcurrentserver.service.AlarmCurrentService;
import com.fiberhome.filink.alarmcurrentserver.service.AlarmQueryTemplateService;
import com.fiberhome.filink.alarmcurrentserver.utils.ListUtil;
import com.fiberhome.filink.alarmcurrentserver.utils.PageBeanHelper;
import com.fiberhome.filink.alarmhistoryapi.api.AlarmHistoryFeign;
import com.fiberhome.filink.alarmhistoryapi.bean.AlarmHistory;
import com.fiberhome.filink.alarmsetapi.api.AlarmSetFeign;
import com.fiberhome.filink.bean.FilterCondition;
import com.fiberhome.filink.bean.PageBean;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.RequestInfoUtils;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.bean.SortCondition;
import com.fiberhome.filink.deviceapi.api.DevicePicFeign;
import com.fiberhome.filink.deviceapi.bean.PicRelationInfo;
import com.fiberhome.filink.exportapi.utils.ExportApiUtils;
import com.fiberhome.filink.filinklockapi.bean.RemoveAlarm;
import com.fiberhome.filink.filinklockapi.feign.ControlFeign;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.constant.LogConstants;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.mongo.MongoQueryHelper;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import com.fiberhome.filink.userapi.api.DepartmentFeign;
import com.fiberhome.filink.userapi.api.UserFeign;
import com.fiberhome.filink.userapi.bean.Department;
import com.fiberhome.filink.userapi.bean.RoleDeviceType;
import com.fiberhome.filink.userapi.bean.User;
import com.fiberhome.filink.workflowbusinessapi.api.procbase.ProcBaseFeign;
import com.mongodb.WriteResult;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

/**
 * <p>
 * ???????????????????????????
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-22
 */
@Slf4j
@Service
public class AlarmCurrentServiceImpl extends ServiceImpl<AlarmCurrentDao, AlarmCurrent> implements AlarmCurrentService {

    /**
     * mongodb?????????
     */
    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * ??????api
     */
    @Autowired
    private LogProcess logProcess;

    /**
     * ????????????api
     */
    @Autowired
    private AlarmHistoryFeign alarmHistoryFeigm;

    /**
     * ??????Feign
     */
    @Autowired
    private DevicePicFeign devicePicFeign;

    /**
     * ????????????Feign
     */
    @Autowired
    private AlarmSetFeign alarmSetFeign;

    /**
     * ??????Feign
     */
    @Autowired
    private ProcBaseFeign procBaseFeign;

    /**
     * ??????????????????
     */
    @Autowired
    private AlarmQueryTemplateService alarmQueryTemplateService;

    /**
     * ??????fegin
     */
    @Autowired
    private UserFeign userFeign;

    /**
     * ??????fegin
     */
    @Autowired
    private DepartmentFeign departmentFeign;

    @Autowired
    private AlarmDisposeServiceImpl alarmDisposeService;

    @Autowired
    private SystemLanguageUtil systemLanguageUtil;

    @Autowired
    private ControlFeign controlFeign;

    /**
     * ????????????????????????
     *
     * @param queryCondition ??????????????????
     * @return ??????????????????
     */
    @Override
    public PageBean queryAlarmCurrentList(QueryCondition<AlarmCurrent> queryCondition, User user, boolean needsAuth) {
        Query query = null;
        // ????????????
        boolean flags = true;
        if (needsAuth) {
            flags = addDataPermission(queryCondition, user, flags);
        }
        GetMongoQueryData queryResult = this.castToMongoQuery(queryCondition, query);
        if (null != queryResult.getResult()) {
            return new PageBean();
        }
        // ?????? ????????????
        query = queryResult.getQuery();
        // ????????????????????????
        List<AlarmCurrent> alarmCurrents = mongoTemplate.find(query, AlarmCurrent.class);
        // ??????????????????????????????
        long count = mongoTemplate.count(query, AlarmCurrent.class);
        //????????????????????????
        if (!flags) {
            alarmCurrents = new ArrayList<>();
            count = 0;
        }
        PageBean pageBean = PageBeanHelper.generatePageBean(alarmCurrents, queryCondition, count);
        return pageBean;
    }

    /**
     * ????????????????????????
     *
     * @return ????????????
     */
    @Override
    public User getUser() {
        String userId = RequestInfoUtils.getUserId();
        String token = RequestInfoUtils.getToken();
        // ??????????????????
        Object object = userFeign.queryCurrentUser(userId, token);
        return JSONArray.toJavaObject((JSON) JSONArray.toJSON(object), User.class);
    }

    /**
     * ????????????
     *
     * @param queryCondition ????????????
     * @param user           ????????????
     * @return ????????????
     */
    @Override
    public Integer getCount(QueryCondition queryCondition, User user) {
        boolean flags = true;
        flags = addDataPermission(queryCondition, user, flags);
        if (!flags) {
            return 0;
        }
        Query query = null;
        GetMongoQueryData queryResult = this.castToMongoQuery(queryCondition, query);
        query = queryResult.getQuery();
        return (int) mongoTemplate.count(query, AlarmCurrent.class);
    }

    /**
     * ????????????????????????
     *
     * @return ????????????
     */
    @Override
    public User getExportUser() {
        String userId = ExportApiUtils.getCurrentUserId();
        String token = ExportApiUtils.getCurrentUserToken();
        Object object = userFeign.queryCurrentUser(userId, token);
        return JSONArray.toJavaObject((JSON) JSONArray.toJSON(object), User.class);
    }

    /**
     * ??????????????????????????????
     *
     * @param queryCondition ????????????
     * @param user           ????????????
     * @return ????????????
     */
    public boolean addDataPermission(QueryCondition<AlarmCurrent> queryCondition, User user, boolean flags) {
        if (AppConstant.ONE.equals(user.getId())) {
            return true;
        }
        List<FilterCondition> filterConditions = new ArrayList<>();
        List<String> areaIds = getUserAreaIds(user);
        List<String> deviceTypes = getDeviceTypes(user);
        if (!ListUtil.isEmpty(areaIds)) {
            filterConditions.add(alarmQueryTemplateService.selectCondition(AppConstant.AREA_ID, "in", areaIds));
        } else {
            return false;
        }
        if (!ListUtil.isEmpty(deviceTypes)) {

            // step1-?????????????????????????????????  ????????????????????????????????????
            if (ListUtil.isEmpty(queryCondition.getFilterConditions())) {
                filterConditions.add(alarmQueryTemplateService.selectCondition(AppConstant.ALARM_SOURCE_TYPE_ID, "in", deviceTypes));
            } else {
                // ????????????????????????
                List<FilterCondition> conditions = queryCondition.getFilterConditions();
                List<FilterCondition> alarmSourceTypeId = conditions.stream().filter(filterCondition ->
                        filterCondition.getFilterField().equals(AlarmCurrent18n.ALARM_SOURCE_TYPE_ID)).collect(Collectors.toList());
                // ??????????????????????????????
                // ????????? ????????????????????????  ???????????????????????? ???????????????alarmSourceTypeId ??????????????????
                if (!ListUtil.isEmpty(alarmSourceTypeId)) {
                    // ?????????????????????????????????????????????
                    Object filterValue = alarmSourceTypeId.get(0).getFilterValue();
                    if (filterValue instanceof Collection) {
                        deviceTypes.retainAll((Collection<?>) filterValue);
                    } else if (filterValue instanceof String) {
                        List<String> arrayList = new ArrayList<>();
                        arrayList.add((String) filterValue);
                        deviceTypes.retainAll(arrayList);
                    }

                    // ??????deviceTypes???????????? ?????????false
                    if (ListUtil.isEmpty(deviceTypes)) {
                        flags = false;
                    } else {
                        filterConditions.add(alarmQueryTemplateService
                                .selectCondition(AppConstant.ALARM_SOURCE_TYPE_ID, "in", deviceTypes));
                    }
                } else {
                    // ????????????????????????alarmSourceTypeId ??????????????????
                    filterConditions.add(alarmQueryTemplateService.selectCondition(AppConstant.ALARM_SOURCE_TYPE_ID, "in", deviceTypes));
                }
            }
        } else {
            flags = false;
        }
        // ???????????????????????? ????????????????????????????????? ??????????????????????????????????????????????????????????????????????????????????????????????????????
        if (ListUtil.isEmpty(queryCondition.getFilterConditions())) {
            queryCondition.setFilterConditions(filterConditions);
        } else if (!ListUtil.isEmpty(filterConditions)) {
            queryCondition.getFilterConditions().addAll(filterConditions);
        }
        return flags;

    }

    /**
     * ????????????????????????
     *
     * @param user ????????????
     * @return ??????????????????
     */
    public List<String> getUserAreaIds(User user) {
        return user.getDepartment().getAreaIdList();
    }

    /**
     * ??????????????????????????????
     *
     * @param user ????????????
     * @return ????????????????????????
     */
    public List<String> getDeviceTypes(User user) {
        List<String> deviceTypes = new ArrayList<>();
        List<RoleDeviceType> roleDeviceTypes = user.getRole().getRoleDevicetypeList();
        for (RoleDeviceType roleDeviceType : roleDeviceTypes) {
            deviceTypes.add(roleDeviceType.getDeviceTypeId());
        }
        return deviceTypes;
    }

    /**
     * ????????????????????????
     *
     * @param queryCondition ??????????????????
     * @return ??????????????????
     */
    @Override
    public Result queryAlarmCurrentPage(QueryCondition<AlarmCurrent> queryCondition) {
        Query query = new Query();
        //?????????????????????????????????????????????MongoQuery?????????
        boolean flags = true;
        User user = getUser();
        flags = addDataPermission(queryCondition, user, flags);
        if (null != queryCondition.getFilterConditions()) {
            List<FilterCondition> collect = queryCondition.getFilterConditions().stream().filter(u ->
                    null != u.getFilterValue() && !"[]".equals(u.getFilterValue().toString())).collect(Collectors.toList());
            MongoQueryHelper.convertFilterConditions(collect);
            queryCondition.setFilterConditions(collect);
        }
        selectCondition(queryCondition);
        //?????????????????????MongoQuery??????
        MongoQueryHelper.withPage(query, queryCondition.getPageCondition());
        Query buildQuery = MongoQueryHelper.buildQuery(query, queryCondition);
        buildQuery.addCriteria(Criteria.where("alarmCode").ne("orderOutOfTime"));
        // ??????????????????????????????
        long count = mongoTemplate.count(buildQuery, AlarmCurrent.class);
        // ????????????????????????
        List<AlarmCurrent> alarmCurrents = mongoTemplate.find(buildQuery, AlarmCurrent.class);
        if (!flags) {
            alarmCurrents = new ArrayList<>();
            count = 0;
        }
        PageBean pageBean = PageBeanHelper.generatePageBean(alarmCurrents, queryCondition, count);
        return ResultUtils.pageSuccess(pageBean);
    }


    /**
     * ?????????????????????????????????
     *
     * @param queryCondition ????????????
     * @param query          ????????????
     * @return ??????????????????
     */
    public GetMongoQueryData castToMongoQuery(QueryCondition<AlarmCurrent> queryCondition, Query query) {
        query = new Query();
        GetMongoQueryData queryResult = new GetMongoQueryData();
        Result result = null;

        //??????filterCondition??????????????????
        if (null != queryCondition.getFilterConditions()) {
            //?????????????????????????????????????????????MongoQuery?????????
            List<FilterCondition> collect = queryCondition.getFilterConditions().stream().filter(u ->
                    null != u.getFilterValue() && !"[]".equals(u.getFilterValue().toString())).collect(Collectors.toList());
            MongoQueryHelper.convertFilterConditions(collect);
            queryCondition.setFilterConditions(collect);
        }
        // ??????
        selectCondition(queryCondition);
        //??????pageCondition??????????????????
        if (null != queryCondition.getPageCondition()) {
            //?????????????????????MonogoQuery??????
            MongoQueryHelper.withPage(query, queryCondition.getPageCondition());
        } else {
            //??????pageCondition??????????????????
            result = ResultUtils.warn(ResultCode.FAIL, I18nUtils.getSystemString(AppConstant.PAGE_CONDITION_NULL));
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
     * ????????????
     *
     * @param queryCondition ????????????
     */
    private void selectCondition(QueryCondition queryCondition) {
        if (null == queryCondition.getSortCondition() || StringUtils.isEmpty(queryCondition.getSortCondition().getSortRule())) {
            SortCondition sortCondition = new SortCondition();
            sortCondition.setSortField("alarm_near_time");
            sortCondition.setSortRule("desc");
            queryCondition.setSortCondition(sortCondition);
        }
    }

    /**
     * ????????????????????????
     *
     * @param list ??????id
     * @return ??????????????????
     */
    @Override
    public List<AlarmCurrent> queryAlarmCurrentByIdsFeign(List<String> list) {
        List<AlarmCurrent> alarmCurrentList = new ArrayList<>();
        list.forEach((String alarmId) -> {
            AlarmCurrent alarmCurrent = mongoTemplate.findById(alarmId, AlarmCurrent.class);
            alarmCurrentList.add(alarmCurrent);
        });
        return alarmCurrentList;
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
        return ResultUtils.success(alarmCurrent);
    }

    /**
     * ????????????????????????????????????
     *
     * @param alarmCurrents ??????????????????
     * @return ????????????
     */
    @Override
    public Result batchUpdateAlarmRemark(List<AlarmCurrent> alarmCurrents) {
        try {
            for (AlarmCurrent alarmCurrent : alarmCurrents) {
                checkName(alarmCurrent);
                if (!alarmCurrent.checkMark()) {
                    return ResultUtils.success(LogFunctionCodeConstant.INCORRECT_EXPRESSION,
                            I18nUtils.getSystemString(AppConstant.INCORRECT_EXPRESSION));
                }
                // ??????????????????ID
                Query query = new Query(Criteria.where(AppConstant.ALARM_ID).is(alarmCurrent.getId()));
                // ????????????
                Update update = new Update().set("remark", alarmCurrent.getRemark());
                mongoTemplate.updateFirst(query, update, AlarmCurrent.class);
                updateLog(alarmCurrent.getId(), AppConstant.ALARM_LOG_ONE);
            }
            return ResultUtils.success(ResultCode.SUCCESS,
                    I18nUtils.getSystemString(AppConstant.MODIFY_NOTE_SUCCESS));
        } catch (Exception e) {
            log.error("?????????????????????{}", e);
            throw new FilinkAlarmCurrentException(I18nUtils.getSystemString(AppConstant.MODIFY_NOTE_FAILURE));
        }

    }

    /**
     * ?????????????????????????????????????????????
     *
     * @param alarmCurrents ????????????????????????
     * @return ????????????
     */
    @Override
    public Result batchUpdateAlarmConfirmStatus(List<AlarmCurrent> alarmCurrents) {
        try {
            List<AlarmCurrent> list = selectStatus(alarmCurrents, AppConstant.ALARM_CONFIRM_STATUS, 2);
            // ????????????????????????????????????
            if (ListUtil.isEmpty(list)) {
                return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getSystemString(AppConstant.STATUS_CONFIRMED));
            }
            for (AlarmCurrent alarmCurrent : alarmCurrents) {
                checkName(alarmCurrent);
                long time = System.currentTimeMillis();
                // ??????????????????
                String userId = RequestInfoUtils.getUserId();
                String userName = RequestInfoUtils.getUserName();
                Query query = new Query(Criteria.where(AppConstant.ALARM_ID).is(alarmCurrent.getId()).
                        and(AppConstant.ALARM_CONFIRM_STATUS).in(LogFunctionCodeConstant.ALARM_STATUS_TWO));
                // ?????????????????????
                Update update = new Update().set(AppConstant.ALARM_CONFIRM_STATUS, LogFunctionCodeConstant.ALARM_STATUS_ONE)
                        .set(AppConstant.ALARM_CONFIRM_ID, userId).set(AppConstant.ALARM_CONFIRM_NAME, userName)
                        .set(AppConstant.ALARM_CONFIRM_TIME, time);
                mongoTemplate.updateFirst(query, update, AlarmCurrent.class);
                updateLog(alarmCurrent.getId(), AppConstant.ALARM_LOG_TWO);
            }
            return ResultUtils.success(ResultCode.SUCCESS,
                    I18nUtils.getSystemString(AppConstant.ALARM_CONFIRM_SUCCESS));
        } catch (Exception e) {
            log.error("?????????????????????{}", e);
            throw new FilinkAlarmCurrentException(I18nUtils.getSystemString(AppConstant.ALARM_CONFIRM_FAILED));
        }
    }

    /**
     * ?????????????????????????????????????????????
     *
     * @param alarmCurrents ????????????????????????
     * @return ????????????
     */
    @Override
    public Result batchUpdateAlarmCleanStatus(List<AlarmCurrent> alarmCurrents) {
        try {
            // ????????????????????????????????????
            log.info(System.currentTimeMillis() + "");
            List<AlarmCurrent> list
                    = selectStatus(alarmCurrents, AppConstant.ALARM_CLEAN_STATUS, LogFunctionCodeConstant.ALARM_STATUS_THREE);
            // ????????????????????????????????????
            if (ListUtil.isEmpty(list)) {
                return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getSystemString(AppConstant.STATUS_REMOVE));
            }
            List<RemoveAlarm> removeAlarms = new ArrayList<>();
            for (AlarmCurrent alarmCurrent : alarmCurrents) {
                checkName(alarmCurrent);
                // ??????????????????
                long string = System.currentTimeMillis();
                // ??????????????????
                String userId = RequestInfoUtils.getUserId();
                String userName = RequestInfoUtils.getUserName();
                Query queryOne = new Query(new Criteria(AppConstant.ALARM_ID).is(alarmCurrent.getId()).
                        and(AppConstant.ALARM_CLEAN_STATUS).in(LogFunctionCodeConstant.ALARM_STATUS_THREE));
                // ??????????????????
                Update update = new Update().set(AppConstant.ALARM_CLEAN_STATUS, LogFunctionCodeConstant.ALARM_STATUS_ONE)
                        .set(AppConstant.ALARM_CLEAN_ID, userId).set(AppConstant.ALARM_CLEAN_NAME, userName)
                        .set(AppConstant.ALARM_CLEAN_TIME, string);
                mongoTemplate.updateFirst(queryOne, update, AlarmCurrent.class);
                // ????????????
                updateLog(alarmCurrent.getId(), AppConstant.ALARM_LOG_THREE);
                // ?????????????????????????????????
                Query queryTwo = new Query(Criteria.where(AppConstant.ALARM_ID).is(alarmCurrent.getId()));
                AlarmCurrent alarmCurrentOne = mongoTemplate.findOne(queryTwo, AlarmCurrent.class);
                // ????????????????????????
                RemoveAlarm removeAlarm = new RemoveAlarm();
                List<String> strings = new ArrayList<>();
                strings.add(AlarmNameTypeEnum.getDesc(alarmCurrentOne.getAlarmName()));
                removeAlarm.setAlarmType(strings);
                removeAlarm.setControlId(alarmCurrentOne.getControlId());
                removeAlarms.add(removeAlarm);
                // ???????????????????????????id????????????
                Query buery = new Query(Criteria.where(AppConstant.ALARM_CLEAN_STATUS).is(LogFunctionCodeConstant.ALARM_STATUS_THREE)
                        .and(AppConstant.CONTROLID).is(alarmCurrentOne.getControlId()));
                AlarmCurrent alarmCurrentList = mongoTemplate.findOne(buery, AlarmCurrent.class);
                if (ObjectUtils.isEmpty(alarmCurrentList)) {
                    alarmDisposeService.selectEquipmentId(alarmCurrentOne.getControlId());
                }
                // ????????????????????????
                Integer setTime = alarmSetFeign.queryAlarmHistorySetFeign();
                long time = setTime * 60 * 60;
                RedisUtils.set(AppConstant.ALARM_MONGODB_PRE + alarmCurrentOne.getId(), alarmCurrentOne.getId(), time);
            }
            controlFeign.removeAlarm(removeAlarms);
            return ResultUtils.warn(ResultCode.SUCCESS,
                    I18nUtils.getSystemString(AppConstant.ALARM_REMOVE_SUCCESS));
        } catch (Exception e) {
            log.error("?????????????????????{}", e);
            throw new FilinkAlarmCurrentException(I18nUtils.getSystemString(AppConstant.ALARM_REMOVE_FAILED));
        }
    }

    /**
     * ??????????????????????????????
     *
     * @param alarmCurrents ??????????????????
     * @param alarmStatus   ??????????????????
     * @param alarmStatusId ????????????
     * @return ????????????????????????
     */
    protected List<AlarmCurrent> selectStatus(List<AlarmCurrent> alarmCurrents, String alarmStatus,
                                              int alarmStatusId) {
        Set<String> strings = new HashSet<>();
        // ??????????????????id
        alarmCurrents.forEach(alarmCurrent -> strings.add(alarmCurrent.getId()));
        // ???????????????????????????????????????
        Query query = new Query(Criteria.where(AppConstant.ALARM_ID).in(strings).and(alarmStatus).is(alarmStatusId));
        return mongoTemplate.find(query, AlarmCurrent.class);
    }

    /**
     * ?????????????????????????????????
     *
     * @return ????????????????????????
     */
    @Override
    public Result queryEveryAlarmCount(String alarmLevel) {
        Query query = new Query();
        User user = getUser();
        if (!user.getId().equals(AppConstant.ONE)) {
            List<String> deviceTypes = getDeviceTypes(user);
            List<String> areaIds = getUserAreaIds(user);
            if (ListUtil.isEmpty(areaIds) || ListUtil.isEmpty(deviceTypes)) {
                query = new Query(Criteria.where(AppConstant.ALARM_CLEAN_STATUS).is(LogFunctionCodeConstant.ALARM_STATUS_THREE)
                        .and(AppConstant.ALARM_FIXED_LEVEL).is(alarmLevel)
                        .and(AppConstant.ALARM_SOURCE_TYPE_ID).in((Object) null).and(AppConstant.AREA_ID).in((Object) null));
            } else {
                query = new Query(Criteria.where(AppConstant.ALARM_CLEAN_STATUS).is(LogFunctionCodeConstant.ALARM_STATUS_THREE)
                        .and(AppConstant.ALARM_FIXED_LEVEL).is(alarmLevel)
                        .and(AppConstant.ALARM_SOURCE_TYPE_ID).in(deviceTypes).and(AppConstant.AREA_ID).in(areaIds));
            }
        } else {
            query = new Query(Criteria.where(AppConstant.ALARM_CLEAN_STATUS).is(LogFunctionCodeConstant.ALARM_STATUS_THREE)
                    .and(AppConstant.ALARM_FIXED_LEVEL).is(alarmLevel));
        }
        long count = mongoTemplate.count(query, AlarmCurrent.class);
        return ResultUtils.success(count);
    }

    /**
     * ??????id??????????????????
     *
     * @param deviceId ??????id
     * @return ????????????
     */
    @Override
    public Result queryAlarmDeviceId(String deviceId) {
        List<String> list = new ArrayList<>();
        Query query = new Query();
        Sort sort = new Sort(Sort.Direction.ASC, AppConstant.ALARM_FIXED_LEVEL).
                and(new Sort(Sort.Direction.DESC, "alarm_near_time"));
        Criteria criteria = Criteria.where(AppConstant.ALARM_SOURCE).is(deviceId);
        query.with(sort).addCriteria(criteria).limit(5);
        List<AlarmCurrent> alarmCurrents = mongoTemplate.find(query, AlarmCurrent.class);
        alarmCurrents.forEach((AlarmCurrent alarmCurrent) -> {
            list.add(alarmCurrent.getId());
        });
        if (!ListUtil.isEmpty(list)) {
            // ??????????????????
            getPicUrlByAlarmIds(alarmCurrents, list);
            // ????????????
            getProcBase(alarmCurrents, list);
        }
        return ResultUtils.success(alarmCurrents);
    }

    /**
     * ??????????????????
     *
     * @param alarmCurrents ????????????
     * @param list          ??????id
     */
    private void getPicUrlByAlarmIds(List<AlarmCurrent> alarmCurrents, List<String> list) {
        Result picUrlByAlarmIds = devicePicFeign.getPicUrlByAlarmIds(list);
        if (ObjectUtils.isEmpty(picUrlByAlarmIds)) {
            return;
        }
        List<Object> picRelationInfos = (List<Object>) picUrlByAlarmIds.getData();
        alarmCurrents.forEach(alarmCurrent -> {
            if (ListUtil.isEmpty(picRelationInfos)) {
                alarmCurrent.setIsPicture(false);
            } else {
                picRelationInfos.forEach(picRelationInfo -> {
                    PicRelationInfo picRelationInfoOne =
                            JSONArray.toJavaObject((JSON) JSONArray.toJSON(picRelationInfo), PicRelationInfo.class);
                    if (picRelationInfoOne.getResourceId().equals(alarmCurrent.getId())) {
                        if (StringUtils.isEmpty(picRelationInfoOne.getResourceId())) {
                            alarmCurrent.setIsPicture(false);
                        } else {
                            alarmCurrent.setIsPicture(true);
                        }
                    }
                });
            }
        });
    }

    /**
     * ??????????????????
     *
     * @param alarmCurrents ????????????
     * @param list          ??????id
     */
    private void getProcBase(List<AlarmCurrent> alarmCurrents, List<String> list) {
        // ????????????
        Map<String, Object> map = procBaseFeign.queryExistsProcForAlarmList(list);
        if (ObjectUtils.isEmpty(map)) {
            return;
        }
        for (String s : map.keySet()) {
            String string = (String) map.get(s);
            alarmCurrents.forEach(alarmCurrent -> {
                if (alarmCurrent.getId().equals(s)) {
                    if (string.equals(AppConstant.LING)) {
                        alarmCurrent.setIsOrder(false);
                    } else {
                        alarmCurrent.setIsOrder(true);
                    }
                }
            });
        }
    }

    /**
     * ????????????id??????c????????????ode??????
     *
     * @param deviceId ??????id
     * @return ????????????code??????
     */
    @Override
    public List<AlarmCurrent> queryAlarmDeviceIdCode(String deviceId) {
        Query query = new Query(Criteria.where(AppConstant.ALARM_SOURCE).is(deviceId));
        return mongoTemplate.find(query, AlarmCurrent.class);
    }

    /**
     * ??????????????????????????????
     *
     * @param alarmSources ??????id
     * @return ????????????
     */
    @Override
    public List<String> queryAlarmSourceForFeign(List<String> alarmSources) {
        List<String> list = new ArrayList<>();
        for (String alarmSource : alarmSources) {
            Query query = new Query(Criteria.where(AppConstant.ALARM_SOURCE).is(alarmSource));
            List<AlarmCurrent> alarmCurrent = mongoTemplate.find(query, AlarmCurrent.class);
            if (!ListUtil.isEmpty(alarmCurrent)) {
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
    public List<String> queryAreaForFeign(List<String> areaIds) {
        List<String> list = new ArrayList<>();
        for (String areaId : areaIds) {
            Query query = new Query(Criteria.where("areaId").is(areaId));
            List<AlarmCurrent> alarmCurrent = mongoTemplate.find(query, AlarmCurrent.class);
            if (!ListUtil.isEmpty(alarmCurrent)) {
                list.add(areaId);
                break;
            }
        }
        return list;
    }

    /**
     * ????????????id??????
     *
     * @param alarmIds ??????id
     * @return ??????id??????
     */
    @Override
    public Result queryDepartmentId(List<String> alarmIds) {
        List<AlarmDepartment> list = new ArrayList<>();
        // ??????????????????
        Query query = new Query(Criteria.where(AppConstant.ALARM_ID).in(alarmIds));
        List<AlarmCurrent> alarmCurrents = mongoTemplate.find(query, AlarmCurrent.class);
        List<AlarmCurrent> alarmCurrentList = alarmCurrents.stream().filter(alarmCurrent ->
                alarmCurrent.getResponsibleDepartment() != null).collect(Collectors.toList());
        alarmCurrentList.forEach(alarmCurrent -> {
            String responsibleDepartmentId = alarmCurrent.getResponsibleDepartmentId();
            String[] strings = responsibleDepartmentId.split(",");
            List<String> asList = Arrays.asList(strings);
            List<Department> departments = departmentFeign.queryDepartmentFeignById(asList);
            departments.forEach(department -> {
                AlarmDepartment alarmDepartment = new AlarmDepartment();
                alarmDepartment.setResponsibleDepartmentId(department.getId());
                alarmDepartment.setResponsibleDepartment(department.getDeptName());
                list.add(alarmDepartment);
            });
        });
        return ResultUtils.success(list);
    }

    /**
     * ??????????????????
     *
     * @param id ??????id
     * @return ????????????
     */
    @Override
    public Result queryIsStatus(String id) {
        Map<String, Object> map = new HashMap<>();
        Query query = new Query(Criteria.where(AppConstant.ALARM_ID).is(id));
        // ????????????????????????
        List<AlarmCurrent> alarmCurrentList = mongoTemplate.find(query, AlarmCurrent.class);
        if (!ListUtil.isEmpty(alarmCurrentList)) {
            map.put("status", "5");
            return ResultUtils.success(map);
        }
        // ????????????????????????
        List<AlarmHistory> alarmHistories = alarmHistoryFeigm.queryAlarmHistoryByIdFeign(id);
        if (!ListUtil.isEmpty(alarmHistories)) {
            map.put("status", "6");
            return ResultUtils.success(map);
        }
        return ResultUtils.success(ResultCode.FAIL,
                I18nUtils.getSystemString(AppConstant.ALARM_WORK_FAILED));
    }

    /**
     * ?????????????????????
     *
     * @param ids ??????id
     * @return ???????????????
     */
    @Override
    public List<AlarmCurrent> queryAlarmDoor(List<String> ids) {
        List<AlarmCurrent> list = new ArrayList<>();
        // ?????????????????????
        ids.forEach(id -> {
            Query query = new Query(Criteria.where(AppConstant.ALARM_ID).is(id));
            AlarmCurrent alarmCurrent = mongoTemplate.findOne(query, AlarmCurrent.class);
            if (alarmCurrent == null) {
                return;
            }
            list.add(alarmCurrent);
        });
        return list;
    }

    /**
     * ????????????????????????
     *
     * @param deviceIds ??????id
     * @return ????????????
     */
    @Override
    public Result deleteAlarms(List<String> deviceIds) {
        // ??????????????????
        Query query = new Query(Criteria.where(AppConstant.ALARM_SOURCE).in(deviceIds));
        WriteResult remove = mongoTemplate.remove(query, AlarmCurrent.class);
        if (remove.getN() == 0) {
            ResultUtils.warn(LogFunctionCodeConstant.DELETE_CURRENT_FALL, I18nUtils.getSystemString(AppConstant.DELETE_CURRENT_FALL));
        }
        // ??????????????????
        int writeResult = alarmHistoryFeigm.deleteAlarmHistoryFeign(deviceIds);
        if (writeResult == 0) {
            ResultUtils.warn(LogFunctionCodeConstant.DELETE_HISTORY_FALL, I18nUtils.getSystemString(AppConstant.DELETE_HISTORY_FALL));
        }
        // ??????????????????
        WriteResult result = mongoTemplate.remove(query, AlarmFilter.class);
        if (result.getN() == 0) {
            ResultUtils.warn(LogFunctionCodeConstant.DELETE_FILTER_FALL, I18nUtils.getSystemString(AppConstant.DELETE_FILTER_FALL));
        }
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(AppConstant.DELETE_ALARMS_SUCCESS));
    }

    /**
     * ????????????id??????????????????
     *
     * @param departmentIds ??????id
     * @return ????????????
     */
    @Override
    public boolean queryAlarmDepartmentFeign(List<String> departmentIds) {
        boolean flag = true;
        for (String departmentId : departmentIds) {
            Query query = new Query(Criteria.where(AppConstant.DEPARTMENTID).is(departmentId));
            AlarmCurrent alarmCurrent = mongoTemplate.findOne(query, AlarmCurrent.class);
            if (alarmCurrent == null) {
                flag = false;
                break;
            }
        }
        return flag;
    }

    /**
     * ??????????????????
     *
     * @param id    ????????????id
     * @param model ????????????
     */
    private void updateLog(String id, String model) {
        if (StringUtils.isEmpty(id)) {
            return;
        }
        AlarmCurrent alarmCurrent = mongoTemplate.findOne(
                new Query(Criteria.where(AppConstant.ALARM_ID).is(id)), AlarmCurrent.class);
        systemLanguageUtil.querySystemLanguage();
        // ??????????????????
        String logType = LogConstants.LOG_TYPE_OPERATE;
        AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
        addLogBean.setDataId(AppConstant.ALARM_ID);
        addLogBean.setDataName(AppConstant.ALARM_NAMES);
        addLogBean.setFunctionCode(model);
        // ??????????????????
        addLogBean.setOptObjId(id);
        addLogBean.setOptObj(alarmCurrent.getAlarmName());
        // ???????????????
        addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_UPDATE);
        // ??????????????????
        logProcess.addOperateLogInfoToCall(addLogBean, LogConstants.ADD_LOG_LOCAL_FILE);
    }

    /**
     * ????????????
     *
     * @param alarmCurrent ??????????????????
     */
    private void checkName(AlarmCurrent alarmCurrent) {
        if (alarmCurrent.getId() == null) {
            throw new FilinkAlarmCurrentException(I18nUtils.getSystemString(AppConstant.ALARM_ID_NULL));
        }
    }
}