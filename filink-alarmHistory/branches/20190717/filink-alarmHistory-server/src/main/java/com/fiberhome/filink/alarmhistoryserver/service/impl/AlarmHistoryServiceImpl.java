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
 * ???????????????????????????
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-22
 */
@Service
@Slf4j
public class AlarmHistoryServiceImpl extends ServiceImpl<AlarmHistoryDao, AlarmHistory> implements AlarmHistoryService {

    /**
     * mongodb??????
     */
    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * ??????api
     */
    @Autowired
    private LogProcess logProcess;

    /**
     * ??????api
     */
    @Autowired
    private DevicePicFeign devicePicFeign;

    /**
     * ??????Feign
     */
    @Autowired
    private ProcBaseFeign procBaseFeign;

    /**
     * ??????fegin
     */
    @Autowired
    private UserFeign userFeign;

    @Autowired
    private DepartmentFeign departmentFeign;

    @Autowired
    private SystemLanguageUtil systemLanguageUtil;

    /**
     * ??????????????????????????????
     *
     * @param queryCondition ????????????
     * @return ????????????????????????
     */
    @Override
    public PageBean queryAlarmHistoryList(QueryCondition<AlarmHistory> queryCondition, User user, boolean needsAuth) {
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
        query = queryResult.getQuery();
        // ??????????????????????????????
        List<AlarmHistory> alarmHistories = mongoTemplate.find(query, AlarmHistory.class);
        // ???????????????
        long count = mongoTemplate.count(query, AlarmHistory.class);
        if (!flags) {
            alarmHistories = new ArrayList<>();
            count = 0;
        }
        PageBean pageBean = PageBeanHelper.generatePageBean(alarmHistories, queryCondition, count);
        return pageBean;
    }

    /**
     * ?????????????????????????????????
     *
     * @param queryCondition ????????????
     * @param query          ????????????
     * @return ??????????????????
     */
    public GetMongoQueryData castToMongoQuery(QueryCondition<AlarmHistory> queryCondition, Query query) {
        query = new Query();
        GetMongoQueryData queryResult = new GetMongoQueryData();
        Result result = null;

        //??????filterCondition??????????????????
        if (null != queryCondition.getFilterConditions()) {
            //?????????????????????????????????????????????MongoQuery?????????
            List<FilterCondition> collect = queryCondition.getFilterConditions().stream().filter(
                    u -> !"[]".equals(u.getFilterValue().toString())).collect(Collectors.toList());
            MongoQueryHelper.convertFilterConditions(collect);
            queryCondition.setFilterConditions(collect);
        }
        // ??????
        if (null == queryCondition.getSortCondition() || StringUtils.isEmpty(queryCondition.getSortCondition().getSortRule())) {
            SortCondition sortCondition = new SortCondition();
            sortCondition.setSortField("alarm_near_time");
            sortCondition.setSortRule("desc");
            queryCondition.setSortCondition(sortCondition);
        }
        //??????pageCondition??????????????????
        if (null != queryCondition.getPageCondition()) {
            //?????????????????????MonogoQuery??????
            MongoQueryHelper.withPage(query, queryCondition.getPageCondition());
        } else {
            //??????pageCondition??????????????????
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
     * ????????????????????????
     *
     * @return ????????????
     */
    public User getExportUser() {
        String userId = ExportApiUtils.getCurrentUserId();
        String token = ExportApiUtils.getCurrentUserToken();
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
     * ??????????????????????????????
     *
     * @param queryCondition ????????????
     * @param user           ????????????
     * @return ????????????
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

            // step1-?????????????????????????????????  ????????????????????????????????????
            if (ListUtil.isEmpty(queryCondition.getFilterConditions())) {
                filterConditions.add(selectCondition(AppConstant.ALARM_SOURCE_TYPE_ID, "in", deviceTypes));
            } else {
                // ????????????????????????
                List<FilterCondition> conditions = queryCondition.getFilterConditions();
                List<FilterCondition> alarmSourceTypeId = conditions.stream().filter(filterCondition ->
                        filterCondition.getFilterField().equals(AppConstant.ALARM_DEVICE_IDS)).collect(Collectors.toList());
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
                        filterConditions.add(selectCondition(AppConstant.ALARM_SOURCE_TYPE_ID, "in", deviceTypes));
                    }
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
     * ??????????????????
     *
     * @param name      ????????????
     * @param condition ?????????
     * @param string    ?????????
     * @return ????????????
     */
    public FilterCondition selectCondition(String name, String condition, Object string) {
        FilterCondition filterCondition = new FilterCondition();
        filterCondition.setFilterField(name);
        filterCondition.setOperator(condition);
        filterCondition.setFilterValue(string);
        return filterCondition;
    }

    /**
     * ????????????????????????
     *
     * @param user ????????????
     * @return ??????????????????
     */
    private static List<String> getUserAreaIds(User user) {
        return user.getDepartment().getAreaIdList();
    }

    /**
     * ??????????????????????????????
     *
     * @param user ????????????
     * @return ????????????????????????
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
     * ?????????????????????????????????
     *
     * @param alarmId ??????id
     * @return ????????????
     */
    @Override
    public Result queryAlarmHistoryInfoById(String alarmId) {
        AlarmHistory alarmHistory = mongoTemplate.findById(alarmId, AlarmHistory.class);
        return ResultUtils.success(alarmHistory);
    }

    /**
     * ?????????????????????????????????
     *
     * @param alarmId ??????id
     * @return ????????????
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
     * ??????id????????????????????????
     *
     * @param alarmIds ??????id
     * @return ??????????????????
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
     * ??????????????????
     *
     * @param alarmIds ??????id
     * @return ????????????
     */
    @Override
    public Result queryDepartmentHistory(List<String> alarmIds) {
        List<AlarmDepartment> list = new ArrayList<>();
        // ??????????????????
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
     * ????????????id??????????????????
     *
     * @param deviceId ??????id
     * @return ????????????
     */
    @Override
    public Result queryAlarmHistoryDeviceId(String deviceId) {
        Query query = new Query();
        // ????????????
        Sort sort = new Sort(Sort.Direction.DESC, "alarm_clean_time");
        // ????????????
        Criteria criteria = Criteria.where("alarm_source").is(deviceId);
        query.with(sort).addCriteria(criteria).limit(5);
        List<AlarmHistory> alarmHistories = mongoTemplate.find(query, AlarmHistory.class);
        List<String> list = new ArrayList<>();
        alarmHistories.forEach((AlarmHistory alarmHistory) -> {
            list.add(alarmHistory.getId());
        });
        // ??????????????????
        if (!ListUtil.isEmpty(list)) {
            getPicUrlByAlarmIds(alarmHistories, list);
            getProcBase(alarmHistories, list);
        }
        return ResultUtils.success(alarmHistories);
    }

    /**
     * ??????????????????
     *
     * @param alarmHistories ????????????
     * @param list           ??????id
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
     * ??????????????????
     *
     * @param alarmHistories ????????????
     * @param list           ??????id
     */
    private void getProcBase(List<AlarmHistory> alarmHistories, List<String> list) {
        // ????????????
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
     * ????????????????????????????????????
     *
     * @param alarmHistories ??????????????????
     * @return ????????????
     */
    @Override
    public Result batchUpdateAlarmRemark(List<AlarmHistory> alarmHistories) {
        try {
            for (AlarmHistory alarmHistory : alarmHistories) {
                if (alarmHistory.getId() == null) {
                    throw new FilinkAlarmHistoryException(I18nUtils.getSystemString(AppConstant.ALARM_ID_NULL));
                }
                // ??????????????????????????????
                if (!alarmHistory.checkMark()) {
                    return ResultUtils.warn(LogFunctionCodeConstant.INCORRECT_EXPRESSION,
                            I18nUtils.getSystemString(AppConstant.INCORRECT_EXPRESSION));
                }
                // ??????????????????ID
                Query query = new Query(Criteria.where(AppConstant.ALARM_ID).is(alarmHistory.getId()));
                // ????????????
                Update update = new Update().set("remark", alarmHistory.getRemark());
                mongoTemplate.updateFirst(query, update, AlarmHistory.class);
                updateLog(alarmHistory.getId(), AppConstant.ALARM_LOG_ONE);
            }
            return ResultUtils.success(ResultCode.SUCCESS,
                    I18nUtils.getSystemString(AppConstant.MODIFY_NOTE_SUCCESS));
        } catch (Exception e) {
            log.error("?????????????????????{}", e);
            throw new FilinkAlarmHistoryException(I18nUtils.getSystemString(AppConstant.MODIFY_NOTE_FAILURE));
        }
    }

    /**
     * ????????????????????????
     *
     * @param alarmHistory ??????????????????
     * @return ????????????
     */
    @Override
    public Result insertAlarmHistoryFeign(AlarmHistory alarmHistory) {
        mongoTemplate.insert(alarmHistory);
        return ResultUtils.success(ResultCode.SUCCESS);
    }

    /**
     * ????????????????????????????????????
     *
     * @param alarmHistoryList ??????????????????
     * @return ????????????
     */
    @Override
    public void insertAlarmHistoryList(List<AlarmHistory> alarmHistoryList) {
        mongoTemplate.insertAll(alarmHistoryList);
    }

    /**
     * ????????????????????????
     *
     * @param deviceIds ??????id
     * @return ????????????
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
     * ??????????????????
     *
     * @param id    ????????????id
     * @param model ????????????
     */
    private void updateLog(String id, String model) {
        if (StringUtils.isEmpty(id)) {
            return;
        }
        AlarmHistory alarmHistory = mongoTemplate.findOne(
                new Query(Criteria.where(AppConstant.ALARM_ID).is(id)), AlarmHistory.class);
        // ??????????????????
        String logType = LogConstants.LOG_TYPE_OPERATE;
        systemLanguageUtil.querySystemLanguage();
        AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
        addLogBean.setDataId(AppConstant.ALARM_ID);
        addLogBean.setDataName("alarmName");
        addLogBean.setFunctionCode(model);
        // ??????????????????
        addLogBean.setOptObjId(id);
        addLogBean.setOptObj(alarmHistory.getAlarmName());
        // ???????????????
        addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_UPDATE);
        // ??????????????????
        logProcess.addOperateLogInfoToCall(addLogBean, LogConstants.ADD_LOG_LOCAL_FILE);
    }
}
