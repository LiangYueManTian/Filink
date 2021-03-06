package com.fiberhome.filink.alarmhistoryserver.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.fiberhome.filink.alarmhistoryserver.bean.AlarmDepartment;
import com.fiberhome.filink.alarmhistoryserver.bean.AlarmHistory;
import com.fiberhome.filink.alarmhistoryserver.bean.AlarmHistoryEs;
import com.fiberhome.filink.alarmhistoryserver.bean.GetMongoQueryData;
import com.fiberhome.filink.alarmhistoryserver.constant.AppConstant;
import com.fiberhome.filink.alarmhistoryserver.constant.AlarmHistoryResultCode;
import com.fiberhome.filink.alarmhistoryserver.dao.AlarmHistoryDao;
import com.fiberhome.filink.alarmhistoryserver.exception.FilinkAlarmHistoryException;
import com.fiberhome.filink.alarmhistoryserver.service.AlarmHistoryService;
import com.fiberhome.filink.alarmhistoryserver.utils.ListUtil;
import com.fiberhome.filink.alarmhistoryserver.utils.PageBeanHelper;
import com.fiberhome.filink.bean.*;
import com.fiberhome.filink.deviceapi.api.DevicePicFeign;
import com.fiberhome.filink.deviceapi.bean.PicRelationInfo;
import com.fiberhome.filink.elasticsearch.ElasticsearchBuildHelper;
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
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    /**
     * ????????????fegin
     */
    @Autowired
    private SystemLanguageUtil systemLanguageUtil;
    /**
     *  es
     */
    @Autowired
    private RestHighLevelClient client;
    /**
     * es index
     */
    private static final String FILINK_ALARM = "filink_alarm";
    /**
     * es  type
     */
    private static final String ALARM_HISTORY = "alarm_history";
    /**
     * es  ???????????????????????????es????????????????????????????????????
     */
    private static final String PROCEED = "proceed";

    /**
     * ??????????????????????????????
     *
     * @param queryCondition ????????????
     * @return ????????????????????????
     */
    @Override
    public PageBean queryAlarmHistoryList(QueryCondition<AlarmHistoryEs> queryCondition) {
        // ????????????
        boolean flags = true;
        // ??????admin
        boolean userPermission = !AppConstant.YI.equals(RequestInfoUtils.getUserId());
        //??????admin????????????????????????
        if (userPermission) {
            User user = getUser();
            flags = addDataPermission(queryCondition, user);
        }
        checkQueryCondition(queryCondition);
        PageBean pageBean;
        if (flags) {
            // ??????????????????????????????
            SearchRequest searchRequest = ElasticsearchBuildHelper.buildQueryCondition(
                    queryCondition, FILINK_ALARM, ALARM_HISTORY);
            pageBean = queryAlarmHistoryElasticSearch(queryCondition, searchRequest);
        } else {
            pageBean = getPageBeanEmpty(queryCondition);
        }
        return pageBean;
    }

    /**
     * ??????????????????????????????(??????)
     *
     * @param queryCondition ??????????????????
     * @return ????????????????????????
     */
    @Override
    public PageBean queryAlarmHistoryListExport(QueryCondition<AlarmHistory> queryCondition) {
        // ??????????????????
        GetMongoQueryData queryResult = this.castToMongoQuery(queryCondition);
        if (null != queryResult.getResult()) {
            return new PageBean();
        }
        Query query = queryResult.getQuery();
        // ??????????????????????????????
        List<AlarmHistory> alarmHistories = mongoTemplate.find(query, AlarmHistory.class);
        // ???????????????
        long count = mongoTemplate.count(query, AlarmHistory.class);
        return PageBeanHelper.generatePageBean(alarmHistories, queryCondition, count);
    }

    /**
     * ?????????????????????
     * @param queryCondition ????????????
     * @return ???????????????
     */
    private PageBean getPageBeanEmpty(QueryCondition<AlarmHistoryEs> queryCondition) {
        List<AlarmHistoryEs> alarmHistoryEs = new ArrayList<>();
        long count = 0;
        return PageBeanHelper.generatePageBean(alarmHistoryEs, queryCondition, count);
    }

    /**
     * ??????ElasticSearch????????????????????????
     * @param queryCondition ????????????
     * @param searchRequest ????????????
     * @return ????????????????????????
     */
    private PageBean queryAlarmHistoryElasticSearch(QueryCondition<AlarmHistoryEs> queryCondition,
                                                    SearchRequest searchRequest) {
        try {
            SearchResponse search = client.search(searchRequest);
            SearchHit[] hits = search.getHits().getHits();

            List<AlarmHistoryEs> alarmHistoryEsList = new ArrayList<>();
            for (SearchHit hit : hits) {
                AlarmHistoryEs alarmHistoryEs = JSONObject.parseObject(hit.getSourceAsString(), AlarmHistoryEs.class);
                alarmHistoryEsList.add(alarmHistoryEs);
            }
            // ??????????????????
            PageBean pageBean = new PageBean();
            pageBean.setData(alarmHistoryEsList);
            pageBean.setSize(queryCondition.getPageCondition().getPageSize());
            pageBean.setPageNum(queryCondition.getPageCondition().getPageNum());
            Long totalHits = search.getHits().getTotalHits();
            pageBean.setTotalCount(totalHits.intValue());
            // ????????? = ?????????/???????????????
            pageBean.setTotalPage(totalHits.intValue() / queryCondition.getPageCondition().getPageSize() + 1);
            return pageBean;
        } catch (Exception e) {
            log.error("query elasticsearch error", e);
            return getPageBeanEmpty(queryCondition);
        }
    }


    /**
     * ?????????????????????????????????
     *
     * @param queryCondition ????????????
     * @return ??????????????????
     */
    public GetMongoQueryData castToMongoQuery(QueryCondition<AlarmHistory> queryCondition) {
        Query query = new Query();
        GetMongoQueryData queryResult = new GetMongoQueryData();
        //?????????????????? ??????filterCondition??????????????????
        if (null != queryCondition.getFilterConditions()) {
            //?????????????????????????????????????????????MongoQuery?????????
            List<FilterCondition> collect = queryCondition.getFilterConditions().stream().filter(
                    u -> !"[]".equals(u.getFilterValue().toString())).collect(Collectors.toList());
            MongoQueryHelper.convertFilterConditions(collect);
            queryCondition.setFilterConditions(collect);
        }
        checkQueryCondition(queryCondition);
        //??????pageCondition??????????????????
        if (null != queryCondition.getPageCondition()) {
            //?????????????????????MonogoQuery??????
            MongoQueryHelper.withPage(query, queryCondition.getPageCondition());
        } else {
            //??????pageCondition??????????????????
            Result result = ResultUtils.success(ResultCode.FAIL, I18nUtils.getSystemString(AppConstant.PAGE_CONDITION_NULL));
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
     * ??????????????????
     * @param queryCondition ????????????
     */
    private void checkQueryCondition(QueryCondition queryCondition) {
        // ??????
        if (null == queryCondition.getSortCondition() || StringUtils.isEmpty(queryCondition.getSortCondition().getSortRule())) {
            SortCondition sortCondition = new SortCondition();
            sortCondition.setSortField(AppConstant.ALARM_NEAR_TIME);
            sortCondition.setSortRule(AppConstant.DESC);
            queryCondition.setSortCondition(sortCondition);
        }
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
        boolean flags = addDataPermission(queryCondition, user);
        if (!flags) {
            return 0;
        }
        GetMongoQueryData queryResult = this.castToMongoQuery(queryCondition);
        Query query = queryResult.getQuery();
        return (int) mongoTemplate.count(query, AlarmHistory.class);
    }

    /**
     * ??????????????????????????????
     *
     * @param queryCondition ????????????
     * @param user           ????????????
     * @return ????????????
     */
    public boolean addDataPermission(QueryCondition queryCondition, User user) {
        // ??????admin
        if (AppConstant.YI.equals(user.getId())) {
            return true;
        }
        List<String> areaIds = getUserAreaIds(user);
        List<String> deviceTypes = getDeviceTypes(user);
        //?????????????????????
        if (ListUtil.isEmpty(areaIds) || ListUtil.isEmpty(deviceTypes)) {
            return false;
        } else {
            // ????????????????????????
            List<FilterCondition> conditions = queryCondition.getFilterConditions();
            // step1-?????????????????????????????????  ???????????????????????????????????????????????????????????????
            if (ListUtil.isEmpty(conditions)) {
                List<FilterCondition> filterConditions = new ArrayList<>();
                filterConditions.add(selectCondition(AppConstant.AREA_ID, "in", areaIds));
                filterConditions.add(selectCondition(AppConstant.ALARM_SOURCE_TYPE_ID, "in", deviceTypes));
                queryCondition.setFilterConditions(filterConditions);
            } else {
                //????????????????????????
                boolean containAreaId = true;
                //??????????????????????????????
                boolean containDeviceType = true;
                //??????????????????
                for (FilterCondition condition : conditions) {
                    //??????????????????????????????????????????
                    if (condition.getFilterField().equals(AppConstant.ALARM_SOURCE_TYPE_ID)) {
                        List<String> deviceTypeId = (List) condition.getFilterValue();
                        deviceTypes.retainAll(deviceTypeId);
                        if (ListUtil.isEmpty(deviceTypes)) {
                            //????????????????????????
                            return false;
                        }
                        condition.setFilterValue(deviceTypes);
                        containDeviceType = false;
                    }
                    //????????????????????????????????????
                    if (condition.getFilterField().equals(AppConstant.AREA_ID)) {
                        List<String> areaId = (List) condition.getFilterValue();
                        areaIds.retainAll(areaId);
                        if (ListUtil.isEmpty(areaIds)) {
                            //????????????????????????
                            return false;
                        }
                        containAreaId = false;
                        condition.setFilterValue(areaIds);
                    }
                }
                if (containAreaId) {
                    //?????????????????????????????????
                    conditions.add(selectCondition(AppConstant.AREA_ID, "in", areaIds));
                }
                if (containDeviceType) {
                    //???????????????????????????????????????
                    conditions.add(selectCondition(AppConstant.ALARM_SOURCE_TYPE_ID, "in", deviceTypes));
                }
                queryCondition.setFilterConditions(conditions);
            }
        }
        return true;
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
        alarmHistories.forEach((AlarmHistory alarmHistory) -> list.add(alarmHistory.getId()));
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
                    return ResultUtils.warn(AlarmHistoryResultCode.INCORRECT_EXPRESSION,
                            I18nUtils.getSystemString(AppConstant.INCORRECT_EXPRESSION));
                }
                // ??????????????????ID
                Query query = new Query(Criteria.where(AppConstant.ALARM_ID).is(alarmHistory.getId()));
                // ????????????
                Update update = new Update().set("remark", alarmHistory.getRemark());
                mongoTemplate.updateFirst(query, update, AlarmHistory.class);
                updateLog(alarmHistory.getId());
            }
            return ResultUtils.success(ResultCode.SUCCESS,
                    I18nUtils.getSystemString(AppConstant.MODIFY_NOTE_SUCCESS));
        } catch (Exception e) {
            log.error("update alarm history remark error???{}", e);
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
        String alarmHistoryId = alarmHistory.getId();
        //??????MongoDB
        mongoTemplate.insert(alarmHistory);
        log.info("alarm current to history: save to mongoDb success, id: {}",
                alarmHistory.getId());
                //??????ES
        AlarmHistoryEs alarmHistoryEs = transToAlarmHistoryEs(alarmHistory);
        IndexRequest indexRequest = new IndexRequest(FILINK_ALARM, ALARM_HISTORY, alarmHistoryId);
        indexRequest.source(JSONObject.toJSONString(alarmHistoryEs), XContentType.JSON);
        try {
            client.index(indexRequest);
            log.info("alarm current to history: save to elasticsearch success");
        } catch (Exception e) {
            log.error("alarm current to history: save to elasticsearch error", e);
        }
        return ResultUtils.success(ResultCode.SUCCESS);
    }

    /**
     * ????????????????????????????????????
     *
     * @param alarmHistoryList ??????????????????
     * @return ????????????
     */
    @Override
    public Result insertAlarmHistoryList(List<AlarmHistory> alarmHistoryList) {
        //??????MongoDB
        mongoTemplate.insertAll(alarmHistoryList);
        log.info("schedule alarm current to history: save to mongoDb success, count: {}",
                alarmHistoryList.size());
        //??????ES
        BulkRequest bulkRequest = new BulkRequest();
        for (AlarmHistory alarmHistory : alarmHistoryList) {
            AlarmHistoryEs alarmHistoryEs = transToAlarmHistoryEs(alarmHistory);
            IndexRequest indexRequest = new IndexRequest(FILINK_ALARM, ALARM_HISTORY,
                    alarmHistoryEs.getId());
            indexRequest.source(JSONObject.toJSONString(alarmHistoryEs), XContentType.JSON);
            bulkRequest.add(indexRequest);
        }
        try {
            client.bulk(bulkRequest);
            log.info("schedule alarm current to history: save to elasticsearch success");
        } catch (Exception e) {
            log.error("schedule alarm current to history: save to elasticsearch error", e);
        }
        return ResultUtils.success(ResultCode.SUCCESS);
    }


    /**
     * ??????es????????????
     * @param deviceIds ??????ID
     */
    private boolean deleteEsByDeviceIds(List<String> deviceIds) {
        // ?????????????????????index???
        DeleteByQueryRequest request = new DeleteByQueryRequest(FILINK_ALARM);
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        // in value???????????????
        for (String value : deviceIds) {
            queryBuilder.should(QueryBuilders.termQuery(AppConstant.ES_DEVICE_ID_KEY, value));
        }
        // ???????????????????????????
        request.setConflicts(PROCEED);
        // ????????????type????????????????????????????????????????????????
        request.setDocTypes(ALARM_HISTORY);
        // ??????????????????
        request.setQuery(queryBuilder);
        try {
            BulkByScrollResponse bulkResponse = client.deleteByQuery(request, RequestOptions.DEFAULT);
            log.info("delete device alarm: delete elasticsearch alarm history success, count???{}", bulkResponse.getDeleted());
            return true;
        } catch (Exception e) {
            log.error("delete device alarm: delete elasticsearch alarm history error", e);
            return false;
        }
    }

    /**
     * ??????????????????es
     * @param alarmHistory ????????????
     * @return es????????????
     */
    private AlarmHistoryEs transToAlarmHistoryEs(AlarmHistory alarmHistory) {
        AlarmHistoryEs alarmHistoryEs = new AlarmHistoryEs();
        alarmHistoryEs.setId(alarmHistory.getId());
        alarmHistoryEs.setTrapOid(alarmHistory.getTrapOid());
        alarmHistoryEs.setAlarmName(alarmHistory.getAlarmName());
        alarmHistoryEs.setAlarmNameId(alarmHistory.getAlarmNameId());
        alarmHistoryEs.setAlarmCode(alarmHistory.getAlarmCode());
        alarmHistoryEs.setAlarmContent(alarmHistory.getAlarmContent());
        alarmHistoryEs.setAlarmType(alarmHistory.getAlarmType());
        alarmHistoryEs.setAlarmSource(alarmHistory.getAlarmSource());
        alarmHistoryEs.setAlarmSourceType(alarmHistory.getAlarmSourceType());
        alarmHistoryEs.setAlarmSourceTypeId(alarmHistory.getAlarmSourceTypeId());
        alarmHistoryEs.setAreaId(alarmHistory.getAreaId());
        alarmHistoryEs.setAreaName(alarmHistory.getAreaName());
        alarmHistoryEs.setIsOrder(alarmHistory.getIsOrder());
        alarmHistoryEs.setAddress(alarmHistory.getAddress());
        alarmHistoryEs.setAlarmFixedLevel(alarmHistory.getAlarmFixedLevel());
        alarmHistoryEs.setAlarmObject(alarmHistory.getAlarmObject());
        alarmHistoryEs.setResponsibleDepartmentId(alarmHistory.getResponsibleDepartmentId());
        alarmHistoryEs.setResponsibleDepartment(alarmHistory.getResponsibleDepartment());
        alarmHistoryEs.setPrompt(alarmHistory.getPrompt());
        alarmHistoryEs.setAlarmBeginTime(alarmHistory.getAlarmBeginTime());
        alarmHistoryEs.setAlarmNearTime(alarmHistory.getAlarmNearTime());
        alarmHistoryEs.setAlarmSystemTime(alarmHistory.getAlarmSystemTime());
        alarmHistoryEs.setAlarmSystemNearTime(alarmHistory.getAlarmSystemNearTime());
        alarmHistoryEs.setAlarmContinousTime(alarmHistory.getAlarmContinousTime());
        alarmHistoryEs.setAlarmHappenCount(alarmHistory.getAlarmHappenCount());
        alarmHistoryEs.setAlarmCleanStatus(alarmHistory.getAlarmCleanStatus());
        alarmHistoryEs.setAlarmCleanTime(alarmHistory.getAlarmCleanTime());
        alarmHistoryEs.setAlarmCleanType(alarmHistory.getAlarmCleanType());
        alarmHistoryEs.setAlarmCleanPeopleId(alarmHistory.getAlarmCleanPeopleId());
        alarmHistoryEs.setAlarmCleanPeopleNickname(alarmHistory.getAlarmCleanPeopleNickname());
        alarmHistoryEs.setAlarmConfirmStatus(alarmHistory.getAlarmConfirmStatus());
        alarmHistoryEs.setAlarmConfirmTime(alarmHistory.getAlarmConfirmTime());
        alarmHistoryEs.setAlarmConfirmPeopleId(alarmHistory.getAlarmConfirmPeopleId());
        alarmHistoryEs.setAlarmConfirmPeopleNickname(alarmHistory.getAlarmConfirmPeopleNickname());
        alarmHistoryEs.setExtraMsg(alarmHistory.getExtraMsg());
        alarmHistoryEs.setAlarmProcessing(alarmHistory.getAlarmProcessing());
        alarmHistoryEs.setRemark(alarmHistory.getRemark());
        alarmHistoryEs.setDoorNumber(alarmHistory.getDoorNumber());
        alarmHistoryEs.setDoorName(alarmHistory.getDoorName());
        alarmHistoryEs.setIsPicture(alarmHistory.getIsPicture());
        alarmHistoryEs.setControlId(alarmHistory.getControlId());
        return alarmHistoryEs;
    }

    /**
     * ????????????????????????
     *
     * @param deviceIds ??????id
     * @return ????????????
     */
    @Override
    public Integer deleteAlarmHistory(List<String> deviceIds) {
        String devices = String.join(",", deviceIds);
        log.info("delete device alarm: delete alarm history, device ids: {}", devices);
        //??????es??????
        boolean deleteEs = deleteEsByDeviceIds(deviceIds);
        if (!deleteEs) {
            log.warn("delete device alarm: delete elasticsearch alarm history failed");
            return null;
        }
        //??????mongoDb??????
        Query query = new Query(Criteria.where(AppConstant.ALARM_SOURCE).in(deviceIds));
        WriteResult remove = mongoTemplate.remove(query, AlarmHistory.class);
        int delete = remove.getN();
        log.info("delete device alarm: delete MongoDB alarm history success, count???{}", delete);
        return delete;
    }

    /**
     * ??????????????????
     *  @param id    ????????????id
     *
     */
    private void updateLog(String id) {
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
        addLogBean.setFunctionCode(AppConstant.ALARM_LOG_ONE);
        // ??????????????????
        addLogBean.setOptObjId(id);
        addLogBean.setOptObj(alarmHistory.getAlarmName());
        // ???????????????
        addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_UPDATE);
        // ??????????????????
        logProcess.addOperateLogInfoToCall(addLogBean, LogConstants.ADD_LOG_LOCAL_FILE);
    }
}
