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
    /**
     * 部门fegin
     */
    @Autowired
    private DepartmentFeign departmentFeign;
    /**
     * 系统语言fegin
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
     * es  删除设置参数，删除es数据时发生冲突的数据跳过
     */
    private static final String PROCEED = "proceed";

    /**
     * 查询历史告警列表信息
     *
     * @param queryCondition 封装条件
     * @return 历史告警列表信息
     */
    @Override
    public PageBean queryAlarmHistoryList(QueryCondition<AlarmHistoryEs> queryCondition) {
        // 数据权限
        boolean flags = true;
        // 判断admin
        boolean userPermission = !AppConstant.YI.equals(RequestInfoUtils.getUserId());
        //不为admin并且需要添加权限
        if (userPermission) {
            User user = getUser();
            flags = addDataPermission(queryCondition, user);
        }
        checkQueryCondition(queryCondition);
        PageBean pageBean;
        if (flags) {
            // 查询历史告警列表信息
            SearchRequest searchRequest = ElasticsearchBuildHelper.buildQueryCondition(
                    queryCondition, FILINK_ALARM, ALARM_HISTORY);
            pageBean = queryAlarmHistoryElasticSearch(queryCondition, searchRequest);
        } else {
            pageBean = getPageBeanEmpty(queryCondition);
        }
        return pageBean;
    }

    /**
     * 查询历史告警列表信息(导出)
     *
     * @param queryCondition 查询条件封装
     * @return 历史告警列表信息
     */
    @Override
    public PageBean queryAlarmHistoryListExport(QueryCondition<AlarmHistory> queryCondition) {
        // 权限校验改造
        GetMongoQueryData queryResult = this.castToMongoQuery(queryCondition);
        if (null != queryResult.getResult()) {
            return new PageBean();
        }
        Query query = queryResult.getQuery();
        // 查询历史告警列表信息
        List<AlarmHistory> alarmHistories = mongoTemplate.find(query, AlarmHistory.class);
        // 查询总条数
        long count = mongoTemplate.count(query, AlarmHistory.class);
        return PageBeanHelper.generatePageBean(alarmHistories, queryCondition, count);
    }

    /**
     * 获取空返回列表
     * @param queryCondition 封装条件
     * @return 空返回列表
     */
    private PageBean getPageBeanEmpty(QueryCondition<AlarmHistoryEs> queryCondition) {
        List<AlarmHistoryEs> alarmHistoryEs = new ArrayList<>();
        long count = 0;
        return PageBeanHelper.generatePageBean(alarmHistoryEs, queryCondition, count);
    }

    /**
     * 查询ElasticSearch历史告警列表信息
     * @param queryCondition 封装条件
     * @param searchRequest 封装条件
     * @return 历史告警列表信息
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
            // 构造返回结果
            PageBean pageBean = new PageBean();
            pageBean.setData(alarmHistoryEsList);
            pageBean.setSize(queryCondition.getPageCondition().getPageSize());
            pageBean.setPageNum(queryCondition.getPageCondition().getPageNum());
            Long totalHits = search.getHits().getTotalHits();
            pageBean.setTotalCount(totalHits.intValue());
            // 总页数 = 总条数/每一页数量
            pageBean.setTotalPage(totalHits.intValue() / queryCondition.getPageCondition().getPageSize() + 1);
            return pageBean;
        } catch (Exception e) {
            log.error("query elasticsearch error", e);
            return getPageBeanEmpty(queryCondition);
        }
    }


    /**
     * 查询符合条件对象的信息
     *
     * @param queryCondition 条件信息
     * @return 查询条件信息
     */
    public GetMongoQueryData castToMongoQuery(QueryCondition<AlarmHistory> queryCondition) {
        Query query = new Query();
        GetMongoQueryData queryResult = new GetMongoQueryData();
        //校验查询条件 判断filterCondition对象是否为空
        if (null != queryCondition.getFilterConditions()) {
            //不为空的情况需要添加过滤条件到MongoQuery对象中
            List<FilterCondition> collect = queryCondition.getFilterConditions().stream().filter(
                    u -> !"[]".equals(u.getFilterValue().toString())).collect(Collectors.toList());
            MongoQueryHelper.convertFilterConditions(collect);
            queryCondition.setFilterConditions(collect);
        }
        checkQueryCondition(queryCondition);
        //判断pageCondition对象是否为空
        if (null != queryCondition.getPageCondition()) {
            //添加分页条件到MonogoQuery条件
            MongoQueryHelper.withPage(query, queryCondition.getPageCondition());
        } else {
            //返回pageCondition对象不能为空
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
     * 校验查询条件
     * @param queryCondition 查询条件
     */
    private void checkQueryCondition(QueryCondition queryCondition) {
        // 排序
        if (null == queryCondition.getSortCondition() || StringUtils.isEmpty(queryCondition.getSortCondition().getSortRule())) {
            SortCondition sortCondition = new SortCondition();
            sortCondition.setSortField(AppConstant.ALARM_NEAR_TIME);
            sortCondition.setSortRule(AppConstant.DESC);
            queryCondition.setSortCondition(sortCondition);
        }
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
        boolean flags = addDataPermission(queryCondition, user);
        if (!flags) {
            return 0;
        }
        GetMongoQueryData queryResult = this.castToMongoQuery(queryCondition);
        Query query = queryResult.getQuery();
        return (int) mongoTemplate.count(query, AlarmHistory.class);
    }

    /**
     * 获取当前告警权限信息
     *
     * @param queryCondition 封装条件
     * @param user           用户信息
     * @return 判断结果
     */
    public boolean addDataPermission(QueryCondition queryCondition, User user) {
        // 判断admin
        if (AppConstant.YI.equals(user.getId())) {
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
                filterConditions.add(selectCondition(AppConstant.AREA_ID, "in", areaIds));
                filterConditions.add(selectCondition(AppConstant.ALARM_SOURCE_TYPE_ID, "in", deviceTypes));
                queryCondition.setFilterConditions(filterConditions);
            } else {
                //判断是否查询区域
                boolean containAreaId = true;
                //判断是否查询设施类型
                boolean containDeviceType = true;
                //遍历查询条件
                for (FilterCondition condition : conditions) {
                    //匹配设施类型查询，存在取交集
                    if (condition.getFilterField().equals(AppConstant.ALARM_SOURCE_TYPE_ID)) {
                        List<String> deviceTypeId = (List) condition.getFilterValue();
                        deviceTypes.retainAll(deviceTypeId);
                        if (ListUtil.isEmpty(deviceTypes)) {
                            //查询条件没有权限
                            return false;
                        }
                        condition.setFilterValue(deviceTypes);
                        containDeviceType = false;
                    }
                    //匹配区域查询，存在取交集
                    if (condition.getFilterField().equals(AppConstant.AREA_ID)) {
                        List<String> areaId = (List) condition.getFilterValue();
                        areaIds.retainAll(areaId);
                        if (ListUtil.isEmpty(areaIds)) {
                            //查询条件没有权限
                            return false;
                        }
                        containAreaId = false;
                        condition.setFilterValue(areaIds);
                    }
                }
                if (containAreaId) {
                    //没有查询区域，添加权限
                    conditions.add(selectCondition(AppConstant.AREA_ID, "in", areaIds));
                }
                if (containDeviceType) {
                    //没有查询设施类型，添加权限
                    conditions.add(selectCondition(AppConstant.ALARM_SOURCE_TYPE_ID, "in", deviceTypes));
                }
                queryCondition.setFilterConditions(conditions);
            }
        }
        return true;
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
        alarmHistories.forEach((AlarmHistory alarmHistory) -> list.add(alarmHistory.getId()));
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
                    return ResultUtils.warn(AlarmHistoryResultCode.INCORRECT_EXPRESSION,
                            I18nUtils.getSystemString(AppConstant.INCORRECT_EXPRESSION));
                }
                // 查询历史告警ID
                Query query = new Query(Criteria.where(AppConstant.ALARM_ID).is(alarmHistory.getId()));
                // 修改备注
                Update update = new Update().set("remark", alarmHistory.getRemark());
                mongoTemplate.updateFirst(query, update, AlarmHistory.class);
                updateLog(alarmHistory.getId());
            }
            return ResultUtils.success(ResultCode.SUCCESS,
                    I18nUtils.getSystemString(AppConstant.MODIFY_NOTE_SUCCESS));
        } catch (Exception e) {
            log.error("update alarm history remark error：{}", e);
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
        String alarmHistoryId = alarmHistory.getId();
        //插入MongoDB
        mongoTemplate.insert(alarmHistory);
        log.info("alarm current to history: save to mongoDb success, id: {}",
                alarmHistory.getId());
                //插入ES
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
     * 定时任务添加历史告警信息
     *
     * @param alarmHistoryList 历史告警信息
     * @return 判断结果
     */
    @Override
    public Result insertAlarmHistoryList(List<AlarmHistory> alarmHistoryList) {
        //插入MongoDB
        mongoTemplate.insertAll(alarmHistoryList);
        log.info("schedule alarm current to history: save to mongoDb success, count: {}",
                alarmHistoryList.size());
        //插入ES
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
     * 删除es历史告警
     * @param deviceIds 设施ID
     */
    private boolean deleteEsByDeviceIds(List<String> deviceIds) {
        // 设置删除索引（index）
        DeleteByQueryRequest request = new DeleteByQueryRequest(FILINK_ALARM);
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        // in value是一个集合
        for (String value : deviceIds) {
            queryBuilder.should(QueryBuilders.termQuery(AppConstant.ES_DEVICE_ID_KEY, value));
        }
        // 设置发生冲突即略过
        request.setConflicts(PROCEED);
        // 设置删除type（当前只有历史告警，可以不设置）
        request.setDocTypes(ALARM_HISTORY);
        // 设置删除条件
        request.setQuery(queryBuilder);
        try {
            BulkByScrollResponse bulkResponse = client.deleteByQuery(request, RequestOptions.DEFAULT);
            log.info("delete device alarm: delete elasticsearch alarm history success, count：{}", bulkResponse.getDeleted());
            return true;
        } catch (Exception e) {
            log.error("delete device alarm: delete elasticsearch alarm history error", e);
            return false;
        }
    }

    /**
     * 历史告警转换es
     * @param alarmHistory 历史告警
     * @return es历史告警
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
     * 删除历史告警信息
     *
     * @param deviceIds 设施id
     * @return 判断结果
     */
    @Override
    public Integer deleteAlarmHistory(List<String> deviceIds) {
        String devices = String.join(",", deviceIds);
        log.info("delete device alarm: delete alarm history, device ids: {}", devices);
        //删除es数据
        boolean deleteEs = deleteEsByDeviceIds(deviceIds);
        if (!deleteEs) {
            log.warn("delete device alarm: delete elasticsearch alarm history failed");
            return null;
        }
        //删除mongoDb数据
        Query query = new Query(Criteria.where(AppConstant.ALARM_SOURCE).in(deviceIds));
        WriteResult remove = mongoTemplate.remove(query, AlarmHistory.class);
        int delete = remove.getN();
        log.info("delete device alarm: delete MongoDB alarm history success, count：{}", delete);
        return delete;
    }

    /**
     * 修改日志记录
     *  @param id    当前告警id
     *
     */
    private void updateLog(String id) {
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
        addLogBean.setFunctionCode(AppConstant.ALARM_LOG_ONE);
        // 获取操作对象
        addLogBean.setOptObjId(id);
        addLogBean.setOptObj(alarmHistory.getAlarmName());
        // 操作为新增
        addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_UPDATE);
        // 新增操作日志
        logProcess.addOperateLogInfoToCall(addLogBean, LogConstants.ADD_LOG_LOCAL_FILE);
    }
}
