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
import java.util.List;
import java.util.Map;
import java.util.Random;
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

import javax.annotation.PostConstruct;

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

    /**
     * ??????fegin
     */
    @Autowired
    private DepartmentFeign departmentFeign;

    private static boolean flags = true;

    /**
     * ??????????????????
     */
    List<String[]> areaList = new ArrayList();
    /**
     * ????????????
     */
    List<String[]> alarmList = new ArrayList();
    /**
     * ????????????
     */
    List<String[]> departmentList = new ArrayList();
    /**
     * ????????????
     */
    List<String[]> deviceList = new ArrayList();
    /**
     * ????????????
     */
    List<Long[]> timeList = new ArrayList();
    /**
     * ?????????
     */
    List<String[]> doorList = new ArrayList();

    /**
     * ??????????????????????????????
     *
     * @param queryCondition ????????????
     * @return ????????????????????????
     */
    @Override
    public Result queryAlarmHistoryList(QueryCondition<AlarmHistory> queryCondition) {
        Query query = null;
        User user = getUser();
        // ????????????
        flags = true;
        addDataPermission(queryCondition, user);
        GetMongoQueryData queryResult = this.castToMongoQuery(queryCondition, query);
        if (null != queryResult.getResult()) {
            return queryResult.getResult();
        }
        query = queryResult.getQuery();
        // ??????????????????????????????
        List<AlarmHistory> alarmHistories = mongoTemplate.find(query, AlarmHistory.class);
        // ???????????????
        long count = mongoTemplate.count(query, AlarmHistory.class);
        //????????????????????????
        if (!flags) {
            alarmHistories = new ArrayList<>();
            count = 0;
        }
        PageBean pageBean = PageBeanHelper.generatePageBean(alarmHistories, queryCondition, count);
        return ResultUtils.pageSuccess(pageBean);
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
            result = ResultUtils.success(ResultCode.FAIL, I18nUtils.getString(AppConstant.PAGE_CONDITION_NULL));
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
    public User getUser() {
        String userId = RequestInfoUtils.getUserId();
        String token = RequestInfoUtils.getToken();
        // ??????????????????
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
    private void addDataPermission(QueryCondition<AlarmHistory> queryCondition, User user) {
        if (AppConstant.YI.equals(user.getId())) {
            return;
        }
        List<FilterCondition> filterConditions = new ArrayList<>();

        List<String> areaIds = getUserAreaIds(user);
        List<String> deviceTypes = getDeviceTypes(user);
        if (areaIds != null) {
            filterConditions.add(selectCondition("areaId", "in", areaIds));
        }
        if (deviceTypes != null) {

            // step1-?????????????????????????????????  ????????????????????????????????????
            if (ListUtil.isEmpty(queryCondition.getFilterConditions())) {
                filterConditions.add(selectCondition("alarm_source_type_id", "in", deviceTypes));
            } else {
                // ??????????????????????????????
                // ????????? ????????????????????????  ???????????????????????? ???????????????alarmSourceTypeId ??????????????????
                boolean bool = false;
                for (FilterCondition condition : queryCondition.getFilterConditions()) {
                    // ??????field??????alarmSourceTypeId
                    if (condition.getFilterField().equals("alarmSourceTypeId")) {
                        bool = true;
                        break;
                    }
                }


                // ????????????  ??????List<FilterCondition>??????
                List<FilterCondition> alarmSourceTypeId = new ArrayList<>();
                if (bool) {
                    // ????????????????????????
                    List<FilterCondition> conditions = queryCondition.getFilterConditions();
                    // ???????????? ????????????????????????????????????alarmSourceTypeId ????????????????????????List<FilterCondition>??????
                    for (FilterCondition filterCondition : conditions) {
                        if (filterCondition.getFilterField().equals("alarmSourceTypeId")) {
                            alarmSourceTypeId.add(filterCondition);
                        }
                    }

                    if (!ListUtil.isEmpty(alarmSourceTypeId)) {
                        // ?????????????????????????????????????????????
                        Object filterValue = alarmSourceTypeId.get(0).getFilterValue();
                        if (filterValue instanceof Collection) {
                            deviceTypes.retainAll((Collection<?>) filterValue);
                        } else if (filterValue instanceof String) {
                            ArrayList<String> arrayList = new ArrayList<>();
                            arrayList.add((String) filterValue);
                            deviceTypes.retainAll(arrayList);
                        }
                    }

                    // ??????deviceTypes???????????? ?????????false
                    if (ListUtil.isEmpty(deviceTypes)) {
                        flags = false;
                    } else {
                        filterConditions.add(selectCondition("alarm_source_type_id", "in", deviceTypes));
                    }

                } else {
                    // ????????????????????????alarmSourceTypeId ??????????????????
                    filterConditions.add(selectCondition("alarm_source_type_id", "in", deviceTypes));
                }
            }
        }

        // ???????????????????????? ????????????????????????????????? ??????????????????????????????????????????????????????????????????????????????????????????????????????
        if (ListUtil.isEmpty(queryCondition.getFilterConditions())) {
            queryCondition.setFilterConditions(filterConditions);
        } else if (!ListUtil.isEmpty(filterConditions)) {
            queryCondition.getFilterConditions().addAll(filterConditions);
        }
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
    private void getPicUrlByAlarmIds(List<AlarmHistory> alarmHistories, List<String> list) {
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
                    throw new FilinkAlarmHistoryException(I18nUtils.getString(AppConstant.ALARM_ID_NULL));
                }
                // ??????????????????????????????
                if (!alarmHistory.checkMark()) {
                    return ResultUtils.warn(LogFunctionCodeConstant.INCORRECT_EXPRESSION,
                            I18nUtils.getString(AppConstant.INCORRECT_EXPRESSION));
                }
                // ??????????????????ID
                Query query = new Query(Criteria.where(AppConstant.ALARM_ID).is(alarmHistory.getId()));
                // ????????????
                Update update = new Update().set("remark", alarmHistory.getRemark());
                mongoTemplate.updateFirst(query, update, AlarmHistory.class);
                updateLog(alarmHistory.getId(), AppConstant.ALARM_LOG_ONE);
            }
            return ResultUtils.success(ResultCode.SUCCESS,
                    I18nUtils.getString(AppConstant.MODIFY_NOTE_SUCCESS));
        } catch (Exception e) {
            e.printStackTrace();
            throw new FilinkAlarmHistoryException(I18nUtils.getString(AppConstant.MODIFY_NOTE_FAILURE));
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
     * @param alarmIds ??????id
     * @return ????????????
     */
    @Override
    public Result queryDepartmentHistory(List<String> alarmIds) {
        List<AlarmDepartment> list = new ArrayList<>();
        // ??????????????????
        Query query = new Query(Criteria.where(AppConstant.ALARM_ID).in(alarmIds));
        List<AlarmHistory> alarmHistories = mongoTemplate.find(query, AlarmHistory.class);
        List<AlarmHistory> alarmHistoryList = alarmHistories.stream().filter(alarmHistory ->
                alarmHistory.getResponsibleDepartment() != null).collect(Collectors.toList());
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

    /**
     * ????????????????????????
     */
    @Override
    public void addAlarmHistoryData() {
        Random r = new Random();
        List<AlarmHistory> list = new ArrayList<>();
        //200w?????????
        int a = 2000001;
        for (int i = 1; i < a; i++) {
            AlarmHistory alarmHistory = new AlarmHistory();
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

             //??????
            alarmHistory.setAlarmContent("????????????XX");
            alarmHistory.setAlarmType(r.nextInt(5) + 1);
            alarmHistory.setPrompt("1");
            alarmHistory.setIsOrder(r.nextBoolean());

            /**
             * 1 ??????  2  ??????   3  ??????   4  ??????
             */
            alarmHistory.setAlarmFixedLevel(String.valueOf(r.nextInt(4) + 1));
            alarmHistory.setAlarmHappenCount(r.nextInt(9) + 1);
            alarmHistory.setAlarmCleanType(1);

            //????????????
            alarmHistory.setAlarmCleanStatus(2);
            alarmHistory.setAlarmCleanTime(1552983406000L);
            alarmHistory.setAlarmCleanPeopleId("system");
            alarmHistory.setAlarmCleanPeopleNickname("system");
            alarmHistory.setAlarmConfirmStatus(2);
            alarmHistory.setAlarmConfirmTime(null);
            alarmHistory.setAlarmConfirmPeopleId(null);
            alarmHistory.setAlarmConfirmPeopleNickname(null);


            alarmHistory.setExtraMsg("???????????????????????????????????????");
            alarmHistory.setAlarmProcessing("???????????????????????????????????????");
            alarmHistory.setRemark("???????????????????????????????????????");

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
        //??????id??????????????????name?????????????????????address???
        String [] areaStr1 = {"bKOxJQ5mtzKUubutM7F","1w????????????1","xx??????"};
        String [] areaStr2 = {"bKOxJQ5mtzKUubutM7F","1w????????????2","xx??????"};
        String [] areaStr3 = {"bKOxJQ5mtzKUubutM7F","1w????????????3","xx??????"};
        String [] areaStr4 = {"bKOxJQ5mtzKUubutM7F","1w????????????4","xx??????"};
        String [] areaStr5 = {"bKOxJQ5mtzKUubutM7F","1w????????????5","xx??????"};
        String [] areaStr6 = {"bKOxJQ5mtzKUubutM7F","1w????????????6","xx??????"};
        String [] areaStr7 = {"bKOxJQ5mtzKUubutM7F","1w????????????7","xx??????"};
        String [] areaStr8 = {"bKOxJQ5mtzKUubutM7F","1w????????????8","xx??????"};
        String [] areaStr9 = {"bKOxJQ5mtzKUubutM7F","1w????????????9","xx??????"};
        String [] areaStr10 = {"bKOxJQ5mtzKUubutM7F","1w????????????10","xx??????"};
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

        //??????id???????????????,????????????
        String [] alarmStr1 = {"001","??????","pryDoor"};
        String [] alarmStr2 = {"002","??????","pryLock"};
        String [] alarmStr3 = {"003","??????","humidity"};
        String [] alarmStr4 = {"004","??????","highTemperature"};
        String [] alarmStr5 = {"005","??????","lowTemperature"};
        String [] alarmStr6 = {"006","????????????","communicationInterrupt"};
        String [] alarmStr7 = {"007","??????","leach"};
        String [] alarmStr8 = {"008","?????????","notClosed"};
        String [] alarmStr9 = {"009","?????????","unLock"};
        String [] alarmStr10 = {"010","??????","lean"};
        String [] alarmStr11 = {"011","??????","shake"};
        String [] alarmStr12 = {"012","??????","electricity"};
        String [] alarmStr13 = {"013","????????????","violenceClose"};
        String [] alarmStr14 = {"014","??????","voltage"};
        String [] alarmStr15 = {"015","????????????","orderOutOfTime"};
        String [] alarmStr16 = {"016","??????????????????","emergencyLock"};
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

        //??????id???????????????
        String [] department1 = {"mV10VEhptC6wIvq4CRv","1w????????????1"};
        String [] department2 = {"TxGLFNAg0wGtpgveuHx","1w????????????2"};
        String [] department3 = {"bItwnKLBnCQUKW3k23a","1w????????????3"};
        String [] department4 = {"l8YRlQ0FOwxN7q8gYwK","1w????????????4"};
        String [] department5 = {"LNuFfRfwOXjdHlQvlx4","1w????????????5"};
        String [] department6 = {"IsxTpkDgb9TuyruYdTB","1w????????????6"};
        String [] department7 = {"MBn8QnfmSoiG8vKGKXV","1w????????????7"};
        String [] department8 = {"nCx8mCoPY5ZwsQEkHTm","1w????????????8"};
        String [] department9 = {"Mxsmt3b9DMugaktupgp","1w????????????9"};
        String [] department10 = {"Og1PyDdhN7uxOd5HA1e","1w????????????10"};
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

        //??????id???????????????????????????
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

        //??????/???????????? ???????????????/???????????? ???????????????
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

        //?????????????????????,??????id
        String[] door1 ={"1","1??????","hostId"};
        String[] door2 ={"2","2??????","hostId"};
        String[] door3 ={"3","3??????","hostId"};
        String[] door4 ={"4","4??????","hostId"};
        doorList.add(door1);
        doorList.add(door2);
        doorList.add(door3);
        doorList.add(door4);


    }



}
