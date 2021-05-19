package com.fiberhome.filink.alarmcurrentserver.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmCurrent;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmDepartment;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmFilter;
import com.fiberhome.filink.alarmcurrentserver.bean.GetMongoQueryData;
import com.fiberhome.filink.alarmcurrentserver.constant.AppConstant;
import com.fiberhome.filink.alarmcurrentserver.constant.LogFunctionCodeConstant;
import com.fiberhome.filink.alarmcurrentserver.dao.AlarmCurrentDao;
import com.fiberhome.filink.alarmcurrentserver.exception.FilinkAlarmCurrentException;
import com.fiberhome.filink.alarmcurrentserver.service.AlarmCurrentService;
import com.fiberhome.filink.alarmcurrentserver.service.AlarmQueryTemplateService;
import com.fiberhome.filink.alarmcurrentserver.utils.ListUtil;
import com.fiberhome.filink.alarmcurrentserver.utils.PageBeanHelper;
import com.fiberhome.filink.alarmhistoryapi.api.AlarmHistoryFeign;
import com.fiberhome.filink.alarmhistoryapi.bean.AlarmHistory;
import com.fiberhome.filink.alarmsetapi.api.AlarmSetFeign;
import com.fiberhome.filink.bean.FilterCondition;
import com.fiberhome.filink.bean.NineteenUUIDUtils;
import com.fiberhome.filink.bean.PageBean;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.RequestInfoUtils;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.bean.SortCondition;
import com.fiberhome.filink.deviceapi.api.DevicePicFeign;
import com.fiberhome.filink.deviceapi.bean.PicRelationInfo;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.constant.LogConstants;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.mongo.MongoQueryHelper;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.server_common.utils.I18nUtils;
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
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.PostConstruct;

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

    private static boolean flags = true;

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
    private AlarmHistoryFeign alarmHistoryFeigm;

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

    /**
     * 区域相关信息
     */
    List<String[]> areaList = new ArrayList();
    /**
     * 告警类型
     */
    List<String[]> alarmList = new ArrayList();
    /**
     * 部门信息
     */
    List<String[]> departmentList = new ArrayList();
    /**
     * 设施信息
     */
    List<String[]> deviceList = new ArrayList();
    /**
     * 告警时间
     */
    List<Long[]> timeList = new ArrayList();
    /**
     * 门信息
     */
    List<String[]> doorList = new ArrayList();

    /**
     * 查询当前告警列表
     *
     * @param queryCondition 查询条件封装
     * @return 当前告警信息
     */
    @Override
    public Result queryAlarmCurrentList(QueryCondition<AlarmCurrent> queryCondition) {
        Query query = null;
        // 数据权限
        User user = getUser();
        flags = true;
        addDataPermission(queryCondition, user);
        GetMongoQueryData queryResult = this.castToMongoQuery(queryCondition, query);
        if (null != queryResult.getResult()) {
            return queryResult.getResult();
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
        PageBean pageBean = PageBeanHelper.generatePageBean(alarmCurrents, queryCondition, count);
        return ResultUtils.pageSuccess(pageBean);
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
     * 获取当前告警权限信息
     *
     * @param queryCondition 封装条件
     * @param user           用户信息
     * @return 判断结果
     */
    private void addDataPermission(QueryCondition<AlarmCurrent> queryCondition, User user) {
        if (AppConstant.YI.equals(user.getId())) {
            return;
        }
        List<FilterCondition> filterConditions = new ArrayList<>();

        List<String> areaIds = getUserAreaIds(user);
        List<String> deviceTypes = getDeviceTypes(user);
        if (areaIds != null) {
            filterConditions.add(alarmQueryTemplateService.selectCondition("areaId", "in", areaIds));
        }
        if (!ListUtil.isEmpty(deviceTypes)) {

            // step1-判断传递的参数是否为空  如果为空，就添加一个条件
            if (ListUtil.isEmpty(queryCondition.getFilterConditions())) {
                filterConditions.add(alarmQueryTemplateService.selectCondition("alarm_source_type_id", "in", deviceTypes));
            } else {
                // 如果不为空才走下一步
                // 不为空 获取集合中的数据  如果条件中的集合 字段包含了alarmSourceTypeId 才进行下一步
                boolean bool = false;
                for (FilterCondition condition : queryCondition.getFilterConditions()) {
                    // 如果field等于alarmSourceTypeId
                    if (condition.getFilterField().equals("alarmSourceTypeId")) {
                        bool = true;
                        break;
                    }
                }


                // 条件符合  构造List<FilterCondition>集合
                List<FilterCondition> alarmSourceTypeId = new ArrayList<>();
                if (bool) {
                    // 获取条件里的集合
                    List<FilterCondition> conditions = queryCondition.getFilterConditions();
                    // 循环集合 判断条件中的字段是否等于alarmSourceTypeId 如果等于就添加进List<FilterCondition>集合
                    for (FilterCondition filterCondition : conditions) {
                        if (filterCondition.getFilterField().equals("alarmSourceTypeId")) {
                            alarmSourceTypeId.add(filterCondition);
                        }
                    }

                    if (!ListUtil.isEmpty(alarmSourceTypeId)) {
                        // 获取集合中第一个元素的值取交集
                        Object filterValue = alarmSourceTypeId.get(0).getFilterValue();
                        if (filterValue instanceof Collection) {
                            deviceTypes.retainAll((Collection<?>) filterValue);
                        } else if (filterValue instanceof String) {
                            ArrayList<String> arrayList = new ArrayList<>();
                            arrayList.add((String) filterValue);
                            deviceTypes.retainAll(arrayList);
                        }
                    }

                    // 如果deviceTypes交集为空 开关为false
                    if (ListUtil.isEmpty(deviceTypes)) {
                        flags = false;
                    } else {
                        filterConditions.add(alarmQueryTemplateService.selectCondition("alarm_source_type_id", "in", deviceTypes));
                    }

                } else {
                    // 如果传入条件没有alarmSourceTypeId 就增加此条件
                    filterConditions.add(alarmQueryTemplateService.selectCondition("alarm_source_type_id", "in", deviceTypes));
                }

            }
        } else {
            flags = false;
        }

        // 如果传入条件为空 就添加进自己构造的条件 如果不为空并且自己构造的条件不为空，就把自己构造的条件添加进原始条件
        if (ListUtil.isEmpty(queryCondition.getFilterConditions())) {
            queryCondition.setFilterConditions(filterConditions);
        } else if (!ListUtil.isEmpty(filterConditions)) {
//            queryCondition.getFilterConditions().stream().filter(u->u.getFilterField().equals())
            queryCondition.getFilterConditions().addAll(filterConditions);
        }

    }

    /**
     * 获取用户区域信息
     *
     * @param user 用户信息
     * @return 用户区域信息
     */
    private static List<String> getUserAreaIds(User user) {
        return user.getDepartment().getAreaIdList();
    }

    /**
     * 获取用户设施类型信息
     *
     * @param user 用户信息
     * @return 用户设施类型信息
     */
    private static List<String> getDeviceTypes(User user) {
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
        flags = true;
        User user = getUser();
        addDataPermission(queryCondition, user);
        if (null != queryCondition.getFilterConditions()) {
            List<FilterCondition> collect = queryCondition.getFilterConditions().stream().filter(u ->
                    !"[]".equals(u.getFilterValue().toString())).collect(Collectors.toList());
            MongoQueryHelper.convertFilterConditions(collect);
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
            result = ResultUtils.warn(ResultCode.FAIL, I18nUtils.getString(AppConstant.PAGE_CONDITION_NULL));
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
     * 默认排序
     *
     * @param queryCondition 条件信息
     */
    private void selectCondition(QueryCondition queryCondition) {
        if (null == queryCondition.getSortCondition() || StringUtils.isEmpty(queryCondition.getSortCondition().getSortRule())) {
            SortCondition sortCondition = new SortCondition();
            sortCondition.setSortField("alarm_begin_time");
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
                    return ResultUtils.success(LogFunctionCodeConstant.INCORRECT_EXPRESSION,
                            I18nUtils.getString(AppConstant.INCORRECT_EXPRESSION));
                }
                // 查询当前告警ID
                Query query = new Query(Criteria.where(AppConstant.ALARM_ID).is(alarmCurrent.getId()));
                // 修改备注
                Update update = new Update().set("remark", alarmCurrent.getRemark());
                mongoTemplate.updateFirst(query, update, AlarmCurrent.class);
                updateLog(alarmCurrent.getId(), AppConstant.ALARM_LOG_ONE);
            }
            return ResultUtils.success(ResultCode.SUCCESS,
                    I18nUtils.getString(AppConstant.MODIFY_NOTE_SUCCESS));
        } catch (Exception e) {
            e.printStackTrace();
            throw new FilinkAlarmCurrentException(I18nUtils.getString(AppConstant.MODIFY_NOTE_FAILURE));
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
                return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getString(AppConstant.STATUS_CONFIRMED));
            }
            for (AlarmCurrent alarmCurrent : alarmCurrents) {
                checkName(alarmCurrent);
                long string = System.currentTimeMillis();
                // 获取用户信息
                String userId = RequestInfoUtils.getUserId();
                String userName = RequestInfoUtils.getUserName();
                Query query = new Query(Criteria.where(AppConstant.ALARM_ID).is(alarmCurrent.getId()).
                        andOperator(Criteria.where(AppConstant.ALARM_CONFIRM_STATUS).in(2)));
                // 修改要确定状态
                Update update = new Update().set(AppConstant.ALARM_CONFIRM_STATUS, 1)
                        .set(AppConstant.ALARM_CONFIRM_ID, userId).set(AppConstant.ALARM_CONFIRM_NAME, userName)
                        .set(AppConstant.ALARM_CONFIRM_TIME, string);
                mongoTemplate.updateFirst(query, update, AlarmCurrent.class);
                updateLog(alarmCurrent.getId(), AppConstant.ALARM_LOG_TWO);
            }
            return ResultUtils.success(ResultCode.SUCCESS,
                    I18nUtils.getString(AppConstant.ALARM_CONFIRM_SUCCESS));
        } catch (Exception e) {
            e.printStackTrace();
            throw new FilinkAlarmCurrentException(I18nUtils.getString(AppConstant.ALARM_CONFIRM_FAILED));
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
            List<AlarmCurrent> list = selectStatus(alarmCurrents, AppConstant.ALARM_CLEAN_STATUS, 3);
            // 判断是否有未清除状态信息
            if (ListUtil.isEmpty(list)) {
                return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getString(AppConstant.STATUS_CONFIRMEDS));
            }
            for (AlarmCurrent alarmCurrent : alarmCurrents) {
                checkName(alarmCurrent);
                // 获取当前时间
                long string = System.currentTimeMillis();
                // 获取用户信息
                String userId = RequestInfoUtils.getUserId();
                String userName = RequestInfoUtils.getUserName();
                Query queryOne = new Query(new Criteria(AppConstant.ALARM_ID).is(alarmCurrent.getId()).
                        andOperator(Criteria.where(AppConstant.ALARM_CLEAN_STATUS).in(3)));
                // 修改清除状态
                Update update = new Update().set(AppConstant.ALARM_CLEAN_STATUS, 1)
                        .set(AppConstant.ALARM_CLEAN_ID, userId).set(AppConstant.ALARM_CLEAN_NAME, userName)
                        .set(AppConstant.ALARM_CLEAN_TIME, string);
                mongoTemplate.updateFirst(queryOne, update, AlarmCurrent.class);
                // 记录日志
                updateLog(alarmCurrent.getId(), AppConstant.ALARM_LOG_THREE);
                // 查询修改后当前告警信息
                Query queryTwo = new Query(Criteria.where(AppConstant.ALARM_ID).is(alarmCurrent.getId()));
                AlarmCurrent alarmCurrentOne = mongoTemplate.findOne(queryTwo, AlarmCurrent.class);
                Query buery = new Query(Criteria.where(AppConstant.ALARM_CLEAN_STATUS).is(3)
                        .and(AppConstant.CONTROLID).is(alarmCurrentOne.getControlId()));
                List<AlarmCurrent> alarmCurrentList = mongoTemplate.find(buery, AlarmCurrent.class);
                alarmDisposeService.selectEquipmentId(alarmCurrentList, alarmCurrentOne.getControlId());
                // 查询历史设置时间
                Integer setTime = alarmSetFeign.queryAlarmHistorySetFeign();
                if (setTime == 0) {
                    currentTumHistory(alarmCurrentOne);
                } else {
                    long time = setTime * 60 * 60;
                    RedisUtils.set(AppConstant.MONGODB_PRE + alarmCurrentOne.getId(), alarmCurrentOne, time);
                }
            }
            return ResultUtils.warn(ResultCode.SUCCESS,
                    I18nUtils.getString(AppConstant.ALARM_REMOVE_SUCCESS));
        } catch (Exception e) {
            e.printStackTrace();
            throw new FilinkAlarmCurrentException(I18nUtils.getString(AppConstant.ALARM_REMOVE_FAILED));
        }
    }

    /**
     * 当前告警转历史告警
     *
     * @param alarmCurrentOne 当前告警
     */
    public void currentTumHistory(AlarmCurrent alarmCurrentOne) {
        AlarmHistory alarmHistory = new AlarmHistory();
        // 当前告警信息值复制给历史告警
        BeanUtils.copyProperties(alarmCurrentOne, alarmHistory);
        // 添加到历史告警中
        alarmHistoryFeigm.insertAlarmHistoryFeign(alarmHistory);
        // 删除已清除的数据
        mongoTemplate.remove(alarmCurrentOne);
    }

    /**
     * 获取当前告警状态信息
     *
     * @param alarmCurrents 当前告警信息
     * @param alarmStatus   状态字段信息
     * @param alarmStatusId 状态信息
     * @return 当前告警状态信息
     */
    public List<AlarmCurrent> selectStatus(List<AlarmCurrent> alarmCurrents, String alarmStatus,
                                           Integer alarmStatusId) {
        Set<String> strings = new HashSet<>();
        // 获取当前告警id
        alarmCurrents.forEach(alarmCurrent -> strings.add(alarmCurrent.getId()));
        // 查询当前告警符合状态的数据
        Query query = new Query(new Criteria().andOperator(
                Criteria.where(AppConstant.ALARM_ID).in(strings),
                Criteria.where(alarmStatus).is(alarmStatusId)));
        return mongoTemplate.find(query, AlarmCurrent.class);
    }

    /**
     * 查询告警各级别告警总数
     *
     * @return 告警级别告警信息
     */
    @Override
    public Result queryEveryAlarmCount(String alarmLevel) {
        Query query = new Query();
        User user = getUser();
        if (!user.getId().equals(AppConstant.YI)) {
            List<String> deviceTypes = getDeviceTypes(user);
            List<String> areaIds = getUserAreaIds(user);
            if (ListUtil.isEmpty(areaIds)) {
                query = new Query(Criteria.where(AppConstant.ALARM_CLEAN_STATUS).is(LogFunctionCodeConstant.ALARM_STATUS_THREE)
                        .and(AppConstant.ALARM_FIXED_LEVEL).is(alarmLevel)
                        .and("alarm_source_type_id").in(deviceTypes).and("areaId").in((Object) null));
            } else {
                query = new Query(Criteria.where(AppConstant.ALARM_CLEAN_STATUS).is(LogFunctionCodeConstant.ALARM_STATUS_THREE)
                        .and(AppConstant.ALARM_FIXED_LEVEL).is(alarmLevel)
                        .and("alarm_source_type_id").in(deviceTypes).and("areaId").in(areaIds));
            }
        } else {
            query = new Query(Criteria.where(AppConstant.ALARM_CLEAN_STATUS).is(LogFunctionCodeConstant.ALARM_STATUS_THREE)
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
        List<AlarmHistory> alarmHistories = alarmHistoryFeigm.queryAlarmHistoryByIdFeign(id);
        if (!ListUtil.isEmpty(alarmHistories)) {
            map.put("status", "6");
            return ResultUtils.success(map);
        }
        return ResultUtils.success(ResultCode.FAIL,
                I18nUtils.getString(AppConstant.ALARM_WORK_FAILED));
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
        // 删除当前告警
        Query query = new Query(Criteria.where(AppConstant.ALARM_SOURCE).in(deviceIds));
        WriteResult remove = mongoTemplate.remove(query, AlarmCurrent.class);
        if (remove.getN() == 0) {
            ResultUtils.warn(LogFunctionCodeConstant.DELETE_CURRENT_FALL, I18nUtils.getString(AppConstant.DELETE_CURRENT_FALL));
        }
        // 删除历史告警
        int writeResult = alarmHistoryFeigm.deleteAlarmHistoryFeign(deviceIds);
        if (writeResult == 0) {
            ResultUtils.warn(LogFunctionCodeConstant.DELETE_HISTORY_FALL, I18nUtils.getString(AppConstant.DELETE_HISTORY_FALL));
        }
        // 删除告警过滤
        WriteResult result = mongoTemplate.remove(query, AlarmFilter.class);
        if (result.getN() == 0) {
            ResultUtils.warn(LogFunctionCodeConstant.DELETE_FILTER_FALL, I18nUtils.getString(AppConstant.DELETE_FILTER_FALL));
        }
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getString(AppConstant.DELETE_ALARMS_SUCCESS));
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
            throw new FilinkAlarmCurrentException(I18nUtils.getString(AppConstant.ALARM_ID_NULL));
        }
    }

    /**
     * 插入当前告警数据
     */
    @Override
    public void addAlarmCurrentData() {
        Random r = new Random();
        List<AlarmCurrent> list = new ArrayList<>();
        //200w条记录
        int a = 2000001;
        for (int i = 1; i < a; i++) {
            AlarmCurrent alarmHistory = new AlarmCurrent();
            alarmHistory.setId(NineteenUUIDUtils.uuid());
            alarmHistory.setTrapOid("1");

            //alarmList
            int rCount = r.nextInt(alarmList.size());
            alarmHistory.setAlarmNameId(alarmList.get(rCount)[0]);
            alarmHistory.setAlarmName(alarmList.get(rCount)[1]);
            alarmHistory.setAlarmCode(alarmList.get(rCount)[2]);

            //deviceList
            rCount = r.nextInt(deviceList.size());
            alarmHistory.setAlarmSource(deviceList.get(rCount)[0]);
            alarmHistory.setAlarmObject(deviceList.get(rCount)[1]);
            alarmHistory.setAlarmSourceTypeId(deviceList.get(rCount)[2]);

            //areaList
            rCount = r.nextInt(areaList.size());
            alarmHistory.setAreaId(areaList.get(rCount)[0]);
            alarmHistory.setAreaName(areaList.get(rCount)[1]);
            alarmHistory.setAddress(areaList.get(rCount)[2]);

            //departmentList
            rCount = r.nextInt(departmentList.size());
            alarmHistory.setResponsibleDepartmentId(departmentList.get(rCount)[0]);
            alarmHistory.setResponsibleDepartment(departmentList.get(rCount)[1]);

            //timeList
            rCount = r.nextInt(timeList.size());
            alarmHistory.setAlarmBeginTime(timeList.get(rCount)[0]);
            alarmHistory.setAlarmNearTime(timeList.get(rCount)[1]);
            alarmHistory.setAlarmSystemTime(timeList.get(rCount)[0]);
            alarmHistory.setAlarmSystemNearTime(timeList.get(rCount)[1]);
            alarmHistory.setAlarmContinousTime(timeList.get(rCount)[2].intValue());

            //doorList
            rCount = r.nextInt(doorList.size());
            alarmHistory.setDoorNumber(doorList.get(rCount)[0]);
            alarmHistory.setDoorName(doorList.get(rCount)[1]);
            alarmHistory.setControlId(doorList.get(rCount)[2]);

            //其他
            alarmHistory.setAlarmContent("告警内容XX");
            alarmHistory.setAlarmType(r.nextInt(5) + 1);
            alarmHistory.setPrompt("1");
            alarmHistory.setIsOrder(r.nextBoolean());

            /**
             * 1 紧急  2  主要   3  次要   4  提示
             */
            alarmHistory.setAlarmFixedLevel(String.valueOf(r.nextInt(4) + 1));
            alarmHistory.setAlarmHappenCount(r.nextInt(9) + 1);
            alarmHistory.setAlarmCleanType(1);

            //当前告警
            alarmHistory.setAlarmCleanStatus(3);
            alarmHistory.setAlarmCleanTime(null);
            alarmHistory.setAlarmCleanPeopleId(null);
            alarmHistory.setAlarmCleanPeopleNickname(null);
            alarmHistory.setAlarmConfirmStatus(2);
            alarmHistory.setAlarmConfirmTime(null);
            alarmHistory.setAlarmConfirmPeopleId(null);
            alarmHistory.setAlarmConfirmPeopleNickname(null);


            alarmHistory.setExtraMsg("有新的告警信息，请尽快处理");
            alarmHistory.setAlarmProcessing("有新的告警信息，请尽快处理");
            alarmHistory.setRemark("有新的告警信息，请尽快处理");

            list.add(alarmHistory);
            if (i % 10000 == 0) {
                mongoTemplate.insertAll(list);
                list.clear();
                System.out.println("current time i = " + i);
            }


        }
    }

    @PostConstruct
    public void init(){
        //区域id、区域名称（name）、区域地址（address）
        String [] areaStr1 = {"bKOxJQ5mtzKUubutM7F","1w测试区域1","xx地址"};
        String [] areaStr2 = {"bKOxJQ5mtzKUubutM7F","1w测试区域2","xx地址"};
        String [] areaStr3 = {"bKOxJQ5mtzKUubutM7F","1w测试区域3","xx地址"};
        String [] areaStr4 = {"bKOxJQ5mtzKUubutM7F","1w测试区域4","xx地址"};
        String [] areaStr5 = {"bKOxJQ5mtzKUubutM7F","1w测试区域5","xx地址"};
        String [] areaStr6 = {"bKOxJQ5mtzKUubutM7F","1w测试区域6","xx地址"};
        String [] areaStr7 = {"bKOxJQ5mtzKUubutM7F","1w测试区域7","xx地址"};
        String [] areaStr8 = {"bKOxJQ5mtzKUubutM7F","1w测试区域8","xx地址"};
        String [] areaStr9 = {"bKOxJQ5mtzKUubutM7F","1w测试区域9","xx地址"};
        String [] areaStr10 = {"bKOxJQ5mtzKUubutM7F","1w测试区域10","xx地址"};
        areaList.add(areaStr1);
        areaList.add(areaStr2);
        areaList.add(areaStr3);
        areaList.add(areaStr4);
        areaList.add(areaStr5);
        areaList.add(areaStr6);
        areaList.add(areaStr7);
        areaList.add(areaStr8);
        areaList.add(areaStr9);
        areaList.add(areaStr10);

        //告警id，告警名称,告警编码
        String [] alarmStr1 = {"001","撬门","pryDoor"};
        String [] alarmStr2 = {"002","撬锁","pryLock"};
        String [] alarmStr3 = {"003","湿度","humidity"};
        String [] alarmStr4 = {"004","高温","highTemperature"};
        String [] alarmStr5 = {"005","低温","lowTemperature"};
        String [] alarmStr6 = {"006","通信中断","communicationInterrupt"};
        String [] alarmStr7 = {"007","水浸","leach"};
        String [] alarmStr8 = {"008","未关门","notClosed"};
        String [] alarmStr9 = {"009","未关锁","unLock"};
        String [] alarmStr10 = {"010","倾斜","lean"};
        String [] alarmStr11 = {"011","震动","shake"};
        String [] alarmStr12 = {"012","电量","electricity"};
        String [] alarmStr13 = {"013","非法开门","violenceClose"};
        String [] alarmStr14 = {"014","电压","voltage"};
        String [] alarmStr15 = {"015","工单超时","orderOutOfTime"};
        String [] alarmStr16 = {"016","应急开锁告警","emergencyLock"};
        alarmList.add(alarmStr1);
        alarmList.add(alarmStr2);
        alarmList.add(alarmStr3);
        alarmList.add(alarmStr4);
        alarmList.add(alarmStr5);
        alarmList.add(alarmStr6);
        alarmList.add(alarmStr7);
        alarmList.add(alarmStr8);
        alarmList.add(alarmStr9);
        alarmList.add(alarmStr10);
        alarmList.add(alarmStr11);
        alarmList.add(alarmStr12);
        alarmList.add(alarmStr13);
        alarmList.add(alarmStr14);
        alarmList.add(alarmStr15);
        alarmList.add(alarmStr16);

        //部门id，部门名称
        String [] department1 = {"mV10VEhptC6wIvq4CRv","1w测试单位1"};
        String [] department2 = {"TxGLFNAg0wGtpgveuHx","1w测试单位2"};
        String [] department3 = {"bItwnKLBnCQUKW3k23a","1w测试单位3"};
        String [] department4 = {"l8YRlQ0FOwxN7q8gYwK","1w测试单位4"};
        String [] department5 = {"LNuFfRfwOXjdHlQvlx4","1w测试单位5"};
        String [] department6 = {"IsxTpkDgb9TuyruYdTB","1w测试单位6"};
        String [] department7 = {"MBn8QnfmSoiG8vKGKXV","1w测试单位7"};
        String [] department8 = {"nCx8mCoPY5ZwsQEkHTm","1w测试单位8"};
        String [] department9 = {"Mxsmt3b9DMugaktupgp","1w测试单位9"};
        String [] department10 = {"Og1PyDdhN7uxOd5HA1e","1w测试单位10"};
        departmentList.add(department1);
        departmentList.add(department2);
        departmentList.add(department3);
        departmentList.add(department4);
        departmentList.add(department5);
        departmentList.add(department6);
        departmentList.add(department7);
        departmentList.add(department8);
        departmentList.add(department9);
        departmentList.add(department10);

        //设施id，设施名，设施类型
        String [] device1  = {"0009a1BfBqsXYcNAY4w","d-1040022157352244302","060"};
        String [] device2  = {"00azSkzkv0bEAv4WzJR","d2540418181981740420","090"};
        String [] device3  = {"00VIhXz2l7EwHwrrWsM","d-1135881195-15082718","060"};
        String [] device4  = {"00mKDQUiP5UZNRj05Hl","d-2057012486-656625427","090"};
        String [] device5  = {"00BlN7Gi3LWWKTiTvnM","d-1804131763-492801274","030"};
        String [] device6  = {"00jD3NaIYQvDQ8iecAf","d-12026604701484547286","150"};
        String [] device7  = {"00RmEg2RSlitdl754mX","d-961901806-823605188","090"};
        String [] device8  = {"00MZVgOauFfjfNnxhHp","d1537169169-1203948664","030"};
        String [] device9  = {"00xNtkJ4F6MPFNxfGSf","d1261240062-911087203","001"};
        String [] device10 = {"018cbgxDuFTjyiE5IY7","d-715617184253828198","001"};
        String [] device11 = {"00yvPRIuNeu5O9L9itq","d-9568428521105173789","060"};
        String [] device12 = {"01OxMosPHQrcU9EILg4","d-1996767070-180341394","001"};
        String [] device13 = {"01rcVQ8KAULrt9VY4SG","d1649206693-352202742","001"};
        String [] device14 = {"01TKGlRk9eE2uqAvtRV","d1563641450615838562","030"};
        String [] device15 = {"01Rq0CCBL02utiOjyst","d-1699119627-753397674","060"};
        String [] device17 = {"01qOSOaZzrzW9DoWi8G","d-1779666116-121251647","150"};
        String [] device16 = {"022OFmXv2aKthypMRaH","d-1953447911-1265463953","030"};
        String [] device18 = {"01WnpGTQimWuhFAbpSH","d-1793150688-1471189127","001"};
        String [] device19 = {"025hXEbsZIutsWsThTo","d-582942614-1325151763","030"};
        String [] device20 = {"02B7JF7wNcoqC9UcpqN","d16204118921234232581","030"};
        deviceList.add(device1);
        deviceList.add(device2);
        deviceList.add(device3);
        deviceList.add(device4);
        deviceList.add(device5);
        deviceList.add(device6);
        deviceList.add(device7);
        deviceList.add(device8);
        deviceList.add(device9);
        deviceList.add(device10);
        deviceList.add(device11);
        deviceList.add(device12);
        deviceList.add(device13);
        deviceList.add(device14);
        deviceList.add(device15);
        deviceList.add(device16);
        deviceList.add(device17);
        deviceList.add(device18);
        deviceList.add(device19);
        deviceList.add(device20);

        //发生/结束时间 、最近发生/接受时间 、持续时间
        Long[] timeStr1 = {1560401668000L,1560501668000L,1L};
        Long[] timeStr2 = {1560301668000L,1560401668000L,3L};
        Long[] timeStr3 = {1560201668000L,1560301668000L,9L};
        Long[] timeStr4 = {1560101668000L,1560201668000L,5L};
        Long[] timeStr5 = {1560001668000L,1560101668000L,1L};
        Long[] timeStr6 = {1546394400000L,1549062000000L,30L};
        Long[] timeStr7 = {1546394400000L,1551567851000L,61L};
        Long[] timeStr8 = {1551765760000L,1551765760000L,2L};
        Long[] timeStr9 = {1551760737000L,1552983406000L,14L};
        timeList.add(timeStr1);
        timeList.add(timeStr2);
        timeList.add(timeStr3);
        timeList.add(timeStr4);
        timeList.add(timeStr5);
        timeList.add(timeStr6);
        timeList.add(timeStr7);
        timeList.add(timeStr8);
        timeList.add(timeStr9);

        //门编号，门名称,主控id
        String[] door1 ={"1","1号门","hostId"};
        String[] door2 ={"2","2号门","hostId"};
        String[] door3 ={"3","3号门","hostId"};
        String[] door4 ={"4","4号门","hostId"};
        doorList.add(door1);
        doorList.add(door2);
        doorList.add(door3);
        doorList.add(door4);


    }


}