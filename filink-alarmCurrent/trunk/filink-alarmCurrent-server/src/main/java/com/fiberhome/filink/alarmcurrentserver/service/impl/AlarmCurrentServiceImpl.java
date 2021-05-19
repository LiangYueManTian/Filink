package com.fiberhome.filink.alarmcurrentserver.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmCurrent;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmDepartment;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmFilter;
import com.fiberhome.filink.alarmcurrentserver.bean.GetMongoQueryData;
import com.fiberhome.filink.alarmcurrentserver.constant.AlarmCurrent18n;
import com.fiberhome.filink.alarmcurrentserver.constant.AlarmCurrentConstants;
import com.fiberhome.filink.alarmcurrentserver.constant.AppConstant;
import com.fiberhome.filink.alarmcurrentserver.constant.AlarmCurrentResultCode;
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
import com.fiberhome.filink.bean.*;
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

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 当前告警服务实现类
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-22
 */
@Slf4j
@Service
public class AlarmCurrentServiceImpl extends ServiceImpl<AlarmCurrentDao, AlarmCurrent> implements AlarmCurrentService {

    /**
     * mongodb实现类
     */
    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 日志api
     */
    @Autowired
    private LogProcess logProcess;

    /**
     * 历史告警api
     */
    @Autowired
    private AlarmHistoryFeign alarmHistoryFeign;

    /**
     * 设施Feign
     */
    @Autowired
    private DevicePicFeign devicePicFeign;

    /**
     * 告警设置Feign
     */
    @Autowired
    private AlarmSetFeign alarmSetFeign;

    /**
     * 工单Feign
     */
    @Autowired
    private ProcBaseFeign procBaseFeign;

    /**
     * 当前告警模板
     */
    @Autowired
    private AlarmQueryTemplateService alarmQueryTemplateService;

    /**
     * 用户fegin
     */
    @Autowired
    private UserFeign userFeign;

    /**
     * 部门fegin
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
     * 查询当前告警列表
     *
     * @param queryCondition 查询条件封装
     * @return 当前告警信息
     */
    @Override
    public PageBean queryAlarmCurrentList(QueryCondition<AlarmCurrent> queryCondition, User user, boolean needsAuth) {
        Query query = null;
        // 数据权限
        boolean flags = true;
        if (needsAuth) {
            flags = addDataPermission(queryCondition, user, flags);
        }
        GetMongoQueryData queryResult = this.castToMongoQuery(queryCondition, query);
        if (null != queryResult.getResult()) {
            return new PageBean();
        }
        // 分页 排序条件
        query = queryResult.getQuery();
        // 查询当前告警列表
        List<AlarmCurrent> alarmCurrents = mongoTemplate.find(query, AlarmCurrent.class);
        // 查询当前告警的总条数
        long count = mongoTemplate.count(query, AlarmCurrent.class);
        //没有权限查询未空
        if (!flags) {
            alarmCurrents = new ArrayList<>();
            count = 0;
        }
        return PageBeanHelper.generatePageBean(alarmCurrents, queryCondition, count);
    }

    /**
     * 获取当前用户信息
     *
     * @return 用户信息
     */
    @Override
    public User getUser() {
        String userId = RequestInfoUtils.getUserId();
        String token = RequestInfoUtils.getToken();
        // 获取用户信息
        Object object = userFeign.queryCurrentUser(userId, token);
        return JSONArray.toJavaObject((JSON) JSONArray.toJSON(object), User.class);
    }

    /**
     * 获取数量
     *
     * @param queryCondition 条件信息
     * @param user           用户信息
     * @return 判断结果
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
     * 获取当前用户信息
     *
     * @return 用户信息
     */
    @Override
    public User getExportUser() {
        String userId = ExportApiUtils.getCurrentUserId();
        String token = ExportApiUtils.getCurrentUserToken();
        Object object = userFeign.queryCurrentUser(userId, token);
        return JSONArray.toJavaObject((JSON) JSONArray.toJSON(object), User.class);
    }

    /**
     * 获取当前告警权限信息
     *
     * @param queryCondition 封装条件
     * @param user           用户信息
     * @return 判断结果
     */
    public boolean addDataPermission(QueryCondition<AlarmCurrent> queryCondition, User user, boolean flags) {
        if (AppConstant.ONE.equals(user.getId())) {
            return true;
        }
        List<String> areaIds = getUserAreaIds(user);
        List<String> deviceTypes = getDeviceTypes(user);
        //用户是否有权限
        if (ListUtil.isEmpty(areaIds) || ListUtil.isEmpty(deviceTypes)) {
            return false;
        } else {
            // 获取条件里的集合
            List<FilterCondition> conditions = queryCondition.getFilterConditions();
            // step1-判断传递的参数是否为空  如果为空，就添加权限条件，不为空做权限过滤
            if (ListUtil.isEmpty(conditions)) {
                List<FilterCondition> filterConditions = new ArrayList<>();
                filterConditions.add(alarmQueryTemplateService.selectCondition(AppConstant.AREA_ID, "in", areaIds));
                filterConditions.add(alarmQueryTemplateService.selectCondition(AppConstant.ALARM_SOURCE_TYPE_ID, "in", deviceTypes));
                queryCondition.setFilterConditions(filterConditions);
            } else {
                //校验权限
                boolean permission = getPermission(queryCondition, areaIds, deviceTypes, conditions);
                if (permission) {
                    return false;
                }
            }
        }
        return flags;
    }

    /**
     * 校验权限
     * @param queryCondition 封装条件
     * @param areaIds 区域
     * @param deviceTypes 设施类型
     * @param conditions 过滤条件
     * @return 权限
     */
    private boolean getPermission(QueryCondition<AlarmCurrent> queryCondition,
                                  List<String> areaIds, List<String> deviceTypes, List<FilterCondition> conditions) {
        //判断是否查询区域
        boolean containAreaId = true;
        //判断是否查询设施类型
        boolean containDeviceType = true;
        //遍历查询条件
        for (FilterCondition condition : conditions) {
            //匹配设施类型查询，存在取交集
            if (condition.getFilterField().equals(AlarmCurrent18n.ALARM_SOURCE_TYPE_ID)) {
                List<String> deviceTypeId = (List) condition.getFilterValue();
                deviceTypes.retainAll(deviceTypeId);
                if (ListUtil.isEmpty(deviceTypes)) {
                    //查询条件没有权限
                    return true;
                }
                condition.setFilterValue(deviceTypes);
                containDeviceType = false;
            }
            //匹配区域查询，存在取交集
            if (condition.getFilterField().equals(AlarmCurrent18n.AREA_ID)) {
                List<String> areaId = (List) condition.getFilterValue();
                areaIds.retainAll(areaId);
                if (ListUtil.isEmpty(areaIds)) {
                    //查询条件没有权限
                    return true;
                }
                containAreaId = false;
                condition.setFilterValue(areaIds);
            }
        }
        if (containAreaId) {
            //没有查询区域，添加权限
            conditions.add(alarmQueryTemplateService.selectCondition(AppConstant.AREA_ID, "in", areaIds));
        }
        if (containDeviceType) {
            //没有查询设施类型，添加权限
            conditions.add(alarmQueryTemplateService.selectCondition(AppConstant.ALARM_SOURCE_TYPE_ID, "in", deviceTypes));
        }
        queryCondition.setFilterConditions(conditions);
        return false;
    }

    /**
     * 获取用户区域信息
     *
     * @param user 用户信息
     * @return 用户区域信息
     */
    public List<String> getUserAreaIds(User user) {
        return user.getDepartment().getAreaIdList();
    }

    /**
     * 获取用户设施类型信息
     *
     * @param user 用户信息
     * @return 用户设施类型信息
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
     * 查询当前告警列表
     *
     * @param queryCondition 查询条件封装
     * @return 当前告警信息
     */
    @Override
    public Result queryAlarmCurrentPage(QueryCondition<AlarmCurrent> queryCondition) {
        Query query = new Query();
        //不为空的情况需要添加过滤条件到MongoQuery对象中
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
        //添加分页条件到MongoQuery条件
        MongoQueryHelper.withPage(query, queryCondition.getPageCondition());
        Query buildQuery = MongoQueryHelper.buildQuery(query, queryCondition);
        buildQuery.addCriteria(Criteria.where("alarmCode").ne("orderOutOfTime"));
        // 查询当前告警的总条数
        long count = mongoTemplate.count(buildQuery, AlarmCurrent.class);
        // 查询当前告警列表
        List<AlarmCurrent> alarmCurrents = mongoTemplate.find(buildQuery, AlarmCurrent.class);
        if (!flags) {
            alarmCurrents = new ArrayList<>();
            count = 0;
        }
        PageBean pageBean = PageBeanHelper.generatePageBean(alarmCurrents, queryCondition, count);
        return ResultUtils.pageSuccess(pageBean);
    }


    /**
     * 查询符合条件对象的信息
     *
     * @param queryCondition 条件信息
     * @param query          查询对象
     * @return 查询条件信息
     */
    public GetMongoQueryData castToMongoQuery(QueryCondition<AlarmCurrent> queryCondition, Query query) {
        query = new Query();
        GetMongoQueryData queryResult = new GetMongoQueryData();
        Result result = null;

        //判断filterCondition对象是否为空
        if (null != queryCondition.getFilterConditions()) {
            //不为空的情况需要添加过滤条件到MongoQuery对象中
            List<FilterCondition> collect = queryCondition.getFilterConditions().stream().filter(u ->
                    null != u.getFilterValue() && !"[]".equals(u.getFilterValue().toString())).collect(Collectors.toList());
            MongoQueryHelper.convertFilterConditions(collect);
            queryCondition.setFilterConditions(collect);
        }
        // 排序
        selectCondition(queryCondition);
        //判断pageCondition对象是否为空
        if (null != queryCondition.getPageCondition()) {
            //添加分页条件到MonogoQuery条件
            MongoQueryHelper.withPage(query, queryCondition.getPageCondition());
        } else {
            //返回pageCondition对象不能为空
            result = ResultUtils.warn(ResultCode.FAIL, I18nUtils.getSystemString(AppConstant.PAGE_CONDITION_NULL));
            queryResult.setQuery(query);
            queryResult.setResult(result);
            return queryResult;
        }
        query = MongoQueryHelper.buildQuery(query, queryCondition);
        queryResult.setQuery(query);
        queryResult.setResult(null);
        return queryResult;
    }

    /**
     * 默认排序
     *
     * @param queryCondition 条件信息
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
     * 查询当前告警信息
     *
     * @param list 告警id
     * @return 当前告警信息
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
     * 查询单个当前告警信息
     *
     * @param alarmId 当前告警ID
     * @return 当前告警信息
     */
    @Override
    public Result queryAlarmCurrentInfoById(String alarmId) {
        AlarmCurrent alarmCurrent = mongoTemplate.findById(alarmId, AlarmCurrent.class);
        return ResultUtils.success(alarmCurrent);
    }

    /**
     * 批量修改当前告警备注信息
     *
     * @param alarmCurrents 当前告警信息
     * @return 判断结果
     */
    @Override
    public Result batchUpdateAlarmRemark(List<AlarmCurrent> alarmCurrents) {
        try {
            for (AlarmCurrent alarmCurrent : alarmCurrents) {
                checkName(alarmCurrent);
                if (!alarmCurrent.checkMark()) {
                    return ResultUtils.success(AlarmCurrentResultCode.INCORRECT_EXPRESSION,
                            I18nUtils.getSystemString(AppConstant.INCORRECT_EXPRESSION));
                }
                // 查询当前告警ID
                Query query = new Query(Criteria.where(AppConstant.ALARM_ID).is(alarmCurrent.getId()));
                // 修改备注
                Update update = new Update().set("remark", alarmCurrent.getRemark());
                mongoTemplate.updateFirst(query, update, AlarmCurrent.class);
                updateLog(alarmCurrent.getId(), AppConstant.ALARM_LOG_ONE);
            }
            return ResultUtils.success(ResultCode.SUCCESS,
                    I18nUtils.getSystemString(AppConstant.MODIFY_NOTE_SUCCESS));
        } catch (Exception e) {
            log.error("update alarm current remark error: {}", e);
            throw new FilinkAlarmCurrentException(I18nUtils.getSystemString(AppConstant.MODIFY_NOTE_FAILURE));
        }

    }

    /**
     * 批量设置当前告警的告警确认状态
     *
     * @param alarmCurrents 当前告警用户信息
     * @return 判断结果
     */
    @Override
    public Result batchUpdateAlarmConfirmStatus(List<AlarmCurrent> alarmCurrents) {
        try {
            List<AlarmCurrent> list = selectStatus(alarmCurrents, AppConstant.ALARM_CONFIRM_STATUS, 2);
            // 判断是否有未确认状态信息
            if (ListUtil.isEmpty(list)) {
                return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getSystemString(AppConstant.STATUS_CONFIRMED));
            }
            for (AlarmCurrent alarmCurrent : alarmCurrents) {
                checkName(alarmCurrent);
                long time = System.currentTimeMillis();
                // 获取用户信息
                String userId = RequestInfoUtils.getUserId();
                String userName = RequestInfoUtils.getUserName();
                Query query = new Query(Criteria.where(AppConstant.ALARM_ID).is(alarmCurrent.getId()).
                        and(AppConstant.ALARM_CONFIRM_STATUS).in(AlarmCurrentConstants.ALARM_STATUS_TWO));
                // 修改要确定状态
                Update update = new Update().set(AppConstant.ALARM_CONFIRM_STATUS, AlarmCurrentConstants.ALARM_STATUS_ONE)
                        .set(AppConstant.ALARM_CONFIRM_ID, userId).set(AppConstant.ALARM_CONFIRM_NAME, userName)
                        .set(AppConstant.ALARM_CONFIRM_TIME, time);
                mongoTemplate.updateFirst(query, update, AlarmCurrent.class);
                updateLog(alarmCurrent.getId(), AppConstant.ALARM_LOG_TWO);
            }
            return ResultUtils.success(ResultCode.SUCCESS,
                    I18nUtils.getSystemString(AppConstant.ALARM_CONFIRM_SUCCESS));
        } catch (Exception e) {
            log.error("update alarm confirm error: {}", e);
            throw new FilinkAlarmCurrentException(I18nUtils.getSystemString(AppConstant.ALARM_CONFIRM_FAILED));
        }
    }

    /**
     * 批量设置当前告警的告警清除状态
     *
     * @param alarmCurrents 当前告警用户信息
     * @return 判断结果
     */
    @Override
    public Result batchUpdateAlarmCleanStatus(List<AlarmCurrent> alarmCurrents) {
        try {
            // 获取未清除的当前告警信息
            List<AlarmCurrent> list
                    = selectStatus(alarmCurrents, AppConstant.ALARM_CLEAN_STATUS, AlarmCurrentConstants.ALARM_STATUS_THREE);
            // 判断是否有未清除状态信息
            if (ListUtil.isEmpty(list)) {
                return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getSystemString(AppConstant.STATUS_REMOVE));
            }
            List<RemoveAlarm> removeAlarms = new ArrayList<>();
            for (AlarmCurrent alarmCurrent : alarmCurrents) {
                checkName(alarmCurrent);
                // 获取当前时间
                long string = System.currentTimeMillis();
                // 获取用户信息
                String userId = RequestInfoUtils.getUserId();
                String userName = RequestInfoUtils.getUserName();
                Query queryOne = new Query(new Criteria(AppConstant.ALARM_ID).is(alarmCurrent.getId()).
                        and(AppConstant.ALARM_CLEAN_STATUS).in(AlarmCurrentConstants.ALARM_STATUS_THREE));
                // 修改清除状态
                Update update = new Update().set(AppConstant.ALARM_CLEAN_STATUS, AlarmCurrentConstants.ALARM_STATUS_ONE)
                        .set(AppConstant.ALARM_CLEAN_ID, userId).set(AppConstant.ALARM_CLEAN_NAME, userName)
                        .set(AppConstant.ALARM_CLEAN_TIME, string);
                mongoTemplate.updateFirst(queryOne, update, AlarmCurrent.class);
                // 记录日志
                updateLog(alarmCurrent.getId(), AppConstant.ALARM_LOG_THREE);
                // 查询修改后当前告警信息
                Query queryTwo = new Query(Criteria.where(AppConstant.ALARM_ID).is(alarmCurrent.getId()));
                AlarmCurrent alarmCurrentOne = mongoTemplate.findOne(queryTwo, AlarmCurrent.class);
                // 获取清除告警信息
                String controlId = alarmCurrentOne.getControlId();
                if (!StringUtils.isEmpty(controlId)) {
                    RemoveAlarm removeAlarm = new RemoveAlarm();
                    List<String> strings = new ArrayList<>();
                    strings.add(AlarmNameTypeEnum.getDesc(alarmCurrentOne.getAlarmName()));
                    removeAlarm.setAlarmType(strings);
                    removeAlarm.setControlId(controlId);
                    removeAlarms.add(removeAlarm);
                    // 查询清除告警的主控id是否存在
                    Query query = new Query(Criteria.where(AppConstant.ALARM_CLEAN_STATUS).is(AlarmCurrentConstants.ALARM_STATUS_THREE)
                            .and(AppConstant.CONTROLID).is(controlId));
                    AlarmCurrent alarmCurrentList = mongoTemplate.findOne(query, AlarmCurrent.class);
                    if (ObjectUtils.isEmpty(alarmCurrentList)) {
                        alarmDisposeService.selectEquipmentId(controlId);
                    }
                }
                // 查询历史设置时间
                Integer setTime = alarmSetFeign.queryAlarmHistorySetFeign();
                if (setTime == null) {
                    setTime = 1;
                }
                long time = setTime * 60 * 60;
                RedisUtils.set(AppConstant.ALARM_MONGODB_PRE + alarmCurrentOne.getId(), alarmCurrentOne.getId(), time);
            }
            controlFeign.removeAlarm(removeAlarms);
            return ResultUtils.warn(ResultCode.SUCCESS,
                    I18nUtils.getSystemString(AppConstant.ALARM_REMOVE_SUCCESS));
        } catch (Exception e) {
            log.error("user clean alarm error", e);
            throw new FilinkAlarmCurrentException(I18nUtils.getSystemString(AppConstant.ALARM_REMOVE_FAILED));
        }
    }

    /**
     * 获取当前告警状态信息
     *
     * @param alarmCurrents 当前告警信息
     * @param alarmStatus   状态字段信息
     * @param alarmStatusId 状态信息
     * @return 当前告警状态信息
     */
    protected List<AlarmCurrent> selectStatus(List<AlarmCurrent> alarmCurrents, String alarmStatus,
                                              int alarmStatusId) {
        Set<String> strings = new HashSet<>();
        // 获取当前告警id
        alarmCurrents.forEach(alarmCurrent -> strings.add(alarmCurrent.getId()));
        // 查询当前告警符合状态的数据
        Query query = new Query(Criteria.where(AppConstant.ALARM_ID).in(strings).and(alarmStatus).is(alarmStatusId));
        return mongoTemplate.find(query, AlarmCurrent.class);
    }

    /**
     * 查询告警各级别告警总数
     *
     * @return 告警级别告警信息
     */
    @Override
    public Result queryEveryAlarmCount(String alarmLevel) {
        Query query;
        User user = getUser();
        //用户信息为空
        if (user == null) {
            return ResultUtils.success(AppConstant.LING);
        }
        if (!user.getId().equals(AppConstant.ONE)) {
            List<String> deviceTypes = getDeviceTypes(user);
            List<String> areaIds = getUserAreaIds(user);
            if (ListUtil.isEmpty(areaIds) || ListUtil.isEmpty(deviceTypes)) {
                query = new Query(Criteria.where(AppConstant.ALARM_CLEAN_STATUS).is(AlarmCurrentConstants.ALARM_STATUS_THREE)
                        .and(AppConstant.ALARM_FIXED_LEVEL).is(alarmLevel)
                        .and(AppConstant.ALARM_SOURCE_TYPE_ID).in((Object) null).and(AppConstant.AREA_ID).in((Object) null));
            } else {
                query = new Query(Criteria.where(AppConstant.ALARM_CLEAN_STATUS).is(AlarmCurrentConstants.ALARM_STATUS_THREE)
                        .and(AppConstant.ALARM_FIXED_LEVEL).is(alarmLevel)
                        .and(AppConstant.ALARM_SOURCE_TYPE_ID).in(deviceTypes).and(AppConstant.AREA_ID).in(areaIds));
            }
        } else {
            query = new Query(Criteria.where(AppConstant.ALARM_CLEAN_STATUS).is(AlarmCurrentConstants.ALARM_STATUS_THREE)
                    .and(AppConstant.ALARM_FIXED_LEVEL).is(alarmLevel));
        }
        long count = mongoTemplate.count(query, AlarmCurrent.class);
        return ResultUtils.success(count);
    }

    /**
     * 设施id查询告警信息
     *
     * @param deviceId 设施id
     * @return 判断结果
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
            // 查询图片关系
            getPicUrlByAlarmIds(alarmCurrents, list);
            // 工单信息
            getProcBase(alarmCurrents, list);
        }
        return ResultUtils.success(alarmCurrents);
    }

    /**
     * 查询图片关系
     *
     * @param alarmCurrents 告警信息
     * @param list          告警id
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
     * 查询工单信息
     *
     * @param alarmCurrents 告警信息
     * @param list          告警id
     */
    private void getProcBase(List<AlarmCurrent> alarmCurrents, List<String> list) {
        // 查询工单
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
     * 告警设施id查询c当前告警ode信息
     *
     * @param deviceId 设施id
     * @return 当前告警code信息
     */
    @Override
    public List<AlarmCurrent> queryAlarmDeviceIdCode(String deviceId) {
        Query query = new Query(Criteria.where(AppConstant.ALARM_SOURCE).is(deviceId));
        return mongoTemplate.find(query, AlarmCurrent.class);
    }

    /**
     * 查询设备信息是否存在
     *
     * @param alarmSources 设备id
     * @return 判断结果
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
     * 查询区域信息是否存在
     *
     * @param areaIds 区域ID
     * @return 判断结果
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
     * 查询单位id信息
     *
     * @param alarmIds 告警id
     * @return 单位id信息
     */
    @Override
    public Result queryDepartmentId(List<String> alarmIds) {
        List<AlarmDepartment> list = new ArrayList<>();
        // 查询单位信息
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
     * 查询告警类型
     *
     * @param id 告警id
     * @return 判断结果
     */
    @Override
    public Result queryIsStatus(String id) {
        Map<String, Object> map = new HashMap<>();
        Query query = new Query(Criteria.where(AppConstant.ALARM_ID).is(id));
        // 查询当前告警信息
        List<AlarmCurrent> alarmCurrentList = mongoTemplate.find(query, AlarmCurrent.class);
        if (!ListUtil.isEmpty(alarmCurrentList)) {
            map.put("status", "5");
            return ResultUtils.success(map);
        }
        // 查询历史告警信息
        List<AlarmHistory> alarmHistories = alarmHistoryFeign.queryAlarmHistoryByIdFeign(id);
        if (!ListUtil.isEmpty(alarmHistories)) {
            map.put("status", "6");
            return ResultUtils.success(map);
        }
        return ResultUtils.success(ResultCode.FAIL,
                I18nUtils.getSystemString(AppConstant.ALARM_WORK_FAILED));
    }

    /**
     * 查询告警门信息
     *
     * @param ids 告警id
     * @return 告警门信息
     */
    @Override
    public List<AlarmCurrent> queryAlarmDoor(List<String> ids) {
        List<AlarmCurrent> list = new ArrayList<>();
        // 查询告警门信息
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
     * 删除设施相关信息
     *
     * @param deviceIds 设施id
     * @return 判断结果
     */
    @Override
    public Result deleteAlarms(List<String> deviceIds) {
        String devices = String.join(",", deviceIds);
        log.info("delete device alarm: start delete alarm, device ids: {}", devices);
        // 删除当前告警
        Query query = new Query(Criteria.where(AppConstant.ALARM_SOURCE).in(deviceIds));
        WriteResult remove = mongoTemplate.remove(query, AlarmCurrent.class);
        log.info("delete device alarm: delete alarm current success, count:{}", remove.getN());
        // 删除历史告警
        Integer writeResult = alarmHistoryFeign.deleteAlarmHistoryFeign(deviceIds);
        if (writeResult == null) {
            log.warn("delete device alarm: delete alarm history failed, device ids: {}", devices);
            return null;
        }
        // 删除告警过滤
        WriteResult result = mongoTemplate.remove(query, AlarmFilter.class);
        log.info("delete device alarm: delete alarm filter success, count:{}", result.getN());
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(AppConstant.DELETE_ALARMS_SUCCESS));
    }

    /**
     * 根据单位id查询告警信息
     *
     * @param departmentIds 单位id
     * @return 判断结果
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
     * 修改日志记录
     *
     * @param id    当前告警id
     * @param model 告警魔板
     */
    private void updateLog(String id, String model) {
        if (StringUtils.isEmpty(id)) {
            return;
        }
        AlarmCurrent alarmCurrent = mongoTemplate.findOne(
                new Query(Criteria.where(AppConstant.ALARM_ID).is(id)), AlarmCurrent.class);
        systemLanguageUtil.querySystemLanguage();
        // 获取日志类型
        String logType = LogConstants.LOG_TYPE_OPERATE;
        AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
        addLogBean.setDataId(AppConstant.ALARM_ID);
        addLogBean.setDataName(AppConstant.ALARM_NAMES);
        addLogBean.setFunctionCode(model);
        // 获取操作对象
        addLogBean.setOptObjId(id);
        addLogBean.setOptObj(alarmCurrent.getAlarmName());
        // 操作为新增
        addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_UPDATE);
        // 新增操作日志
        logProcess.addOperateLogInfoToCall(addLogBean, LogConstants.ADD_LOG_LOCAL_FILE);
    }

    /**
     * 检验参数
     *
     * @param alarmCurrent 当前告警信息
     */
    private void checkName(AlarmCurrent alarmCurrent) {
        if (alarmCurrent.getId() == null) {
            throw new FilinkAlarmCurrentException(I18nUtils.getSystemString(AppConstant.ALARM_ID_NULL));
        }
    }
}