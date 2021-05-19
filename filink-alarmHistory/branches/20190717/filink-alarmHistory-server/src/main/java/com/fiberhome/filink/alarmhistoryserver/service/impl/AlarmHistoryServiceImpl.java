package com.fiberhome.filink.alarmhistoryserver.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.fiberhome.filink.alarmhistoryserver.bean.AlarmDepartment;
import com.fiberhome.filink.alarmhistoryserver.bean.AlarmHistory;
import com.fiberhome.filink.alarmhistoryserver.bean.GetMongoQueryData;
import com.fiberhome.filink.alarmhistoryserver.constant.AppConstant;
import com.fiberhome.filink.alarmhistoryserver.constant.LogFunctionCodeConstant;
import com.fiberhome.filink.alarmhistoryserver.dao.AlarmHistoryDao;
import com.fiberhome.filink.alarmhistoryserver.exception.FilinkAlarmHistoryException;
import com.fiberhome.filink.alarmhistoryserver.service.AlarmHistoryService;
import com.fiberhome.filink.alarmhistoryserver.utils.ListUtil;
import com.fiberhome.filink.alarmhistoryserver.utils.PageBeanHelper;
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
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.constant.LogConstants;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.mongo.MongoQueryHelper;
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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
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
 * 历史告警服务实现类
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-22
 */
@Service
@Slf4j
public class AlarmHistoryServiceImpl extends ServiceImpl<AlarmHistoryDao, AlarmHistory> implements AlarmHistoryService {

    /**
     * mongodb方法
     */
    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 日志api
     */
    @Autowired
    private LogProcess logProcess;

    /**
     * 设施api
     */
    @Autowired
    private DevicePicFeign devicePicFeign;

    /**
     * 工单Feign
     */
    @Autowired
    private ProcBaseFeign procBaseFeign;

    /**
     * 用户fegin
     */
    @Autowired
    private UserFeign userFeign;

    @Autowired
    private DepartmentFeign departmentFeign;

    @Autowired
    private SystemLanguageUtil systemLanguageUtil;

    /**
     * 查询历史告警列表信息
     *
     * @param queryCondition 封装条件
     * @return 历史告警列表信息
     */
    @Override
    public PageBean queryAlarmHistoryList(QueryCondition<AlarmHistory> queryCondition, User user, boolean needsAuth) {
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
        query = queryResult.getQuery();
        // 查询历史告警列表信息
        List<AlarmHistory> alarmHistories = mongoTemplate.find(query, AlarmHistory.class);
        // 查询总条数
        long count = mongoTemplate.count(query, AlarmHistory.class);
        if (!flags) {
            alarmHistories = new ArrayList<>();
            count = 0;
        }
        PageBean pageBean = PageBeanHelper.generatePageBean(alarmHistories, queryCondition, count);
        return pageBean;
    }

    /**
     * 查询符合条件对象的信息
     *
     * @param queryCondition 条件信息
     * @param query          查询对象
     * @return 查询条件信息
     */
    public GetMongoQueryData castToMongoQuery(QueryCondition<AlarmHistory> queryCondition, Query query) {
        query = new Query();
        GetMongoQueryData queryResult = new GetMongoQueryData();
        Result result = null;

        //判断filterCondition对象是否为空
        if (null != queryCondition.getFilterConditions()) {
            //不为空的情况需要添加过滤条件到MongoQuery对象中
            List<FilterCondition> collect = queryCondition.getFilterConditions().stream().filter(
                    u -> !"[]".equals(u.getFilterValue().toString())).collect(Collectors.toList());
            MongoQueryHelper.convertFilterConditions(collect);
            queryCondition.setFilterConditions(collect);
        }
        // 排序
        if (null == queryCondition.getSortCondition() || StringUtils.isEmpty(queryCondition.getSortCondition().getSortRule())) {
            SortCondition sortCondition = new SortCondition();
            sortCondition.setSortField("alarm_near_time");
            sortCondition.setSortRule("desc");
            queryCondition.setSortCondition(sortCondition);
        }
        //判断pageCondition对象是否为空
        if (null != queryCondition.getPageCondition()) {
            //添加分页条件到MonogoQuery条件
            MongoQueryHelper.withPage(query, queryCondition.getPageCondition());
        } else {
            //返回pageCondition对象不能为空
            result = ResultUtils.success(ResultCode.FAIL, I18nUtils.getSystemString(AppConstant.PAGE_CONDITION_NULL));
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
     * 获取当前用户信息
     *
     * @return 用户信息
     */
    public User getExportUser() {
        String userId = ExportApiUtils.getCurrentUserId();
        String token = ExportApiUtils.getCurrentUserToken();
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
    public Integer getCount(QueryCondition queryCondition, User user) {
        boolean flags = true;
        flags = addDataPermission(queryCondition, user, flags);
        if (!flags) {
            return 0;
        }
        Query query = null;
        GetMongoQueryData queryResult = this.castToMongoQuery(queryCondition, query);
        query = queryResult.getQuery();
        return (int) mongoTemplate.count(query, AlarmHistory.class);
    }

    /**
     * 获取当前告警权限信息
     *
     * @param queryCondition 封装条件
     * @param user           用户信息
     * @return 判断结果
     */
    public boolean addDataPermission(QueryCondition<AlarmHistory> queryCondition, User user, boolean flags) {
        if (AppConstant.YI.equals(user.getId())) {
            return true;
        }
        List<FilterCondition> filterConditions = new ArrayList<>();

        List<String> areaIds = getUserAreaIds(user);
        List<String> deviceTypes = getDeviceTypes(user);
        if (!ListUtil.isEmpty(areaIds)) {
            filterConditions.add(selectCondition(AppConstant.AREA_ID, "in", areaIds));
        } else {
            return false;
        }
        if (!ListUtil.isEmpty(deviceTypes)) {

            // step1-判断传递的参数是否为空  如果为空，就添加一个条件
            if (ListUtil.isEmpty(queryCondition.getFilterConditions())) {
                filterConditions.add(selectCondition(AppConstant.ALARM_SOURCE_TYPE_ID, "in", deviceTypes));
            } else {
                // 获取条件里的集合
                List<FilterCondition> conditions = queryCondition.getFilterConditions();
                List<FilterCondition> alarmSourceTypeId = conditions.stream().filter(filterCondition ->
                        filterCondition.getFilterField().equals(AppConstant.ALARM_DEVICE_IDS)).collect(Collectors.toList());
                if (!ListUtil.isEmpty(alarmSourceTypeId)) {
                    // 获取集合中第一个元素的值取交集
                    Object filterValue = alarmSourceTypeId.get(0).getFilterValue();
                    if (filterValue instanceof Collection) {
                        deviceTypes.retainAll((Collection<?>) filterValue);
                    } else if (filterValue instanceof String) {
                        List<String> arrayList = new ArrayList<>();
                        arrayList.add((String) filterValue);
                        deviceTypes.retainAll(arrayList);
                    }
                    // 如果deviceTypes交集为空 开关为false
                    if (ListUtil.isEmpty(deviceTypes)) {
                        flags = false;
                    } else {
                        filterConditions.add(selectCondition(AppConstant.ALARM_SOURCE_TYPE_ID, "in", deviceTypes));
                    }
                }
            }
        } else {
            flags = false;
        }
        // 如果传入条件为空 就添加进自己构造的条件 如果不为空并且自己构造的条件不为空，就把自己构造的条件添加进原始条件
        if (ListUtil.isEmpty(queryCondition.getFilterConditions())) {
            queryCondition.setFilterConditions(filterConditions);
        } else if (!ListUtil.isEmpty(filterConditions)) {
            queryCondition.getFilterConditions().addAll(filterConditions);
        }
        return flags;

    }

    /**
     * 传递普通参数
     *
     * @param name      过滤字段
     * @param condition 操作符
     * @param string    过滤值
     * @return 参数信息
     */
    public FilterCondition selectCondition(String name, String condition, Object string) {
        FilterCondition filterCondition = new FilterCondition();
        filterCondition.setFilterField(name);
        filterCondition.setOperator(condition);
        filterCondition.setFilterValue(string);
        return filterCondition;
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
     * 查询单个历史告警的信息
     *
     * @param alarmId 告警id
     * @return 查询结果
     */
    @Override
    public Result queryAlarmHistoryInfoById(String alarmId) {
        AlarmHistory alarmHistory = mongoTemplate.findById(alarmId, AlarmHistory.class);
        return ResultUtils.success(alarmHistory);
    }

    /**
     * 查询单个历史告警的信息
     *
     * @param alarmId 告警id
     * @return 查询结果
     */
    @Override
    public List<AlarmHistory> queryAlarmHistoryById(String alarmId) {
        Query query = new Query(Criteria.where(AppConstant.ALARM_ID).is(alarmId));
        List<AlarmHistory> alarmHistories = mongoTemplate.find(query, AlarmHistory.class);
        if (ListUtil.isEmpty(alarmHistories)) {
            return null;
        }
        return alarmHistories;
    }

    /**
     * 根据id查询历史告警信息
     *
     * @param alarmIds 告警id
     * @return 历史告警信息
     */
    @Override
    public List<AlarmHistory> queryAlarmHistoryByIds(List<String> alarmIds) {
        Query query = new Query(Criteria.where(AppConstant.ALARM_ID).in(alarmIds));
        List<AlarmHistory> alarmHistories = mongoTemplate.find(query, AlarmHistory.class);
        if (ListUtil.isEmpty(alarmHistories)) {
            return null;
        }
        return alarmHistories;
    }

    /**
     * 查询单位信息
     *
     * @param alarmIds 告警id
     * @return 单位信息
     */
    @Override
    public Result queryDepartmentHistory(List<String> alarmIds) {
        List<AlarmDepartment> list = new ArrayList<>();
        // 查询单位信息
        Query query = new Query(Criteria.where(AppConstant.ALARM_ID).in(alarmIds));
        List<AlarmHistory> alarmHistorys = mongoTemplate.find(query, AlarmHistory.class);
        List<AlarmHistory> alarmHistoryList = alarmHistorys.stream().filter(alarmHistory ->
                StringUtils.isEmpty(alarmHistory.getResponsibleDepartment())).collect(Collectors.toList());
        alarmHistoryList.forEach(alarmHistory -> {
            String responsibleDepartmentId = alarmHistory.getResponsibleDepartmentId();
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
     * 根据设施id查询告警信息
     *
     * @param deviceId 设施id
     * @return 告警信息
     */
    @Override
    public Result queryAlarmHistoryDeviceId(String deviceId) {
        Query query = new Query();
        // 默认排序
        Sort sort = new Sort(Sort.Direction.DESC, "alarm_clean_time");
        // 查询字段
        Criteria criteria = Criteria.where("alarm_source").is(deviceId);
        query.with(sort).addCriteria(criteria).limit(5);
        List<AlarmHistory> alarmHistories = mongoTemplate.find(query, AlarmHistory.class);
        List<String> list = new ArrayList<>();
        alarmHistories.forEach((AlarmHistory alarmHistory) -> {
            list.add(alarmHistory.getId());
        });
        // 查询工单信息
        if (!ListUtil.isEmpty(list)) {
            getPicUrlByAlarmIds(alarmHistories, list);
            getProcBase(alarmHistories, list);
        }
        return ResultUtils.success(alarmHistories);
    }

    /**
     * 查询图片关系
     *
     * @param alarmHistories 告警信息
     * @param list           告警id
     */
    protected void getPicUrlByAlarmIds(List<AlarmHistory> alarmHistories, List<String> list) {
        Result picUrlByAlarmIds = devicePicFeign.getPicUrlByAlarmIds(list);
        if (ObjectUtils.isEmpty(picUrlByAlarmIds)) {
            return;
        }
        List<Object> picRelationInfos = (List<Object>) picUrlByAlarmIds.getData();
        alarmHistories.forEach(alarmHistory -> {
            if (ListUtil.isEmpty(picRelationInfos)) {
                alarmHistory.setIsPicture(false);
            } else {
                picRelationInfos.forEach(picRelationInfo -> {
                    PicRelationInfo picRelationInfoOne =
                            JSONArray.toJavaObject((JSON) JSONArray.toJSON(picRelationInfo), PicRelationInfo.class);
                    if (picRelationInfoOne.getResourceId().equals(alarmHistory.getId())) {
                        if (StringUtils.isEmpty(picRelationInfoOne.getResourceId())) {
                            alarmHistory.setIsPicture(false);
                        } else {
                            alarmHistory.setIsPicture(true);
                        }
                    }
                });
            }
        });
    }

    /**
     * 查询工单信息
     *
     * @param alarmHistories 告警信息
     * @param list           告警id
     */
    private void getProcBase(List<AlarmHistory> alarmHistories, List<String> list) {
        // 查询工单
        Map<String, Object> map = procBaseFeign.queryExistsProcForAlarmList(list);
        if (ObjectUtils.isEmpty(map)) {
            return;
        }
        for (String s : map.keySet()) {
            String string = (String) map.get(s);
            alarmHistories.forEach(alarmHistory -> {
                if (alarmHistory.getId().equals(s)) {
                    if (string.equals(AppConstant.LING)) {
                        alarmHistory.setIsOrder(false);
                    } else {
                        alarmHistory.setIsOrder(true);
                    }
                }
            });
        }
    }

    /**
     * 批量修改历史告警备注信息
     *
     * @param alarmHistories 当前告警信息
     * @return 判断结果
     */
    @Override
    public Result batchUpdateAlarmRemark(List<AlarmHistory> alarmHistories) {
        try {
            for (AlarmHistory alarmHistory : alarmHistories) {
                if (alarmHistory.getId() == null) {
                    throw new FilinkAlarmHistoryException(I18nUtils.getSystemString(AppConstant.ALARM_ID_NULL));
                }
                // 判断参数的值是否符合
                if (!alarmHistory.checkMark()) {
                    return ResultUtils.warn(LogFunctionCodeConstant.INCORRECT_EXPRESSION,
                            I18nUtils.getSystemString(AppConstant.INCORRECT_EXPRESSION));
                }
                // 查询历史告警ID
                Query query = new Query(Criteria.where(AppConstant.ALARM_ID).is(alarmHistory.getId()));
                // 修改备注
                Update update = new Update().set("remark", alarmHistory.getRemark());
                mongoTemplate.updateFirst(query, update, AlarmHistory.class);
                updateLog(alarmHistory.getId(), AppConstant.ALARM_LOG_ONE);
            }
            return ResultUtils.success(ResultCode.SUCCESS,
                    I18nUtils.getSystemString(AppConstant.MODIFY_NOTE_SUCCESS));
        } catch (Exception e) {
            log.error("告警备注出错：{}", e);
            throw new FilinkAlarmHistoryException(I18nUtils.getSystemString(AppConstant.MODIFY_NOTE_FAILURE));
        }
    }

    /**
     * 添加历史告警信息
     *
     * @param alarmHistory 历史告警信息
     * @return 判断结果
     */
    @Override
    public Result insertAlarmHistoryFeign(AlarmHistory alarmHistory) {
        mongoTemplate.insert(alarmHistory);
        return ResultUtils.success(ResultCode.SUCCESS);
    }

    /**
     * 定时任务添加历史告警信息
     *
     * @param alarmHistoryList 历史告警信息
     * @return 判断结果
     */
    @Override
    public void insertAlarmHistoryList(List<AlarmHistory> alarmHistoryList) {
        mongoTemplate.insertAll(alarmHistoryList);
    }

    /**
     * 删除历史告警信息
     *
     * @param deviceIds 设施id
     * @return 判断结果
     */
    @Override
    public Integer deleteAlarmHistory(List<String> deviceIds) {
        Query query = new Query(Criteria.where(AppConstant.ALARM_SOURCE).in(deviceIds));
        WriteResult remove = mongoTemplate.remove(query, AlarmHistory.class);
        if (remove.getN() == 0) {
            return AppConstant.LIN;
        }
        return AppConstant.ONE;
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
        AlarmHistory alarmHistory = mongoTemplate.findOne(
                new Query(Criteria.where(AppConstant.ALARM_ID).is(id)), AlarmHistory.class);
        // 获取日志类型
        String logType = LogConstants.LOG_TYPE_OPERATE;
        systemLanguageUtil.querySystemLanguage();
        AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
        addLogBean.setDataId(AppConstant.ALARM_ID);
        addLogBean.setDataName("alarmName");
        addLogBean.setFunctionCode(model);
        // 获取操作对象
        addLogBean.setOptObjId(id);
        addLogBean.setOptObj(alarmHistory.getAlarmName());
        // 操作为新增
        addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_UPDATE);
        // 新增操作日志
        logProcess.addOperateLogInfoToCall(addLogBean, LogConstants.ADD_LOG_LOCAL_FILE);
    }
}
