package com.fiberhome.filink.fdevice.service.devicelog.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.fiberhome.filink.bean.*;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.exportapi.bean.ExportRequestInfo;
import com.fiberhome.filink.exportapi.exception.FilinkExportDataTooLargeException;
import com.fiberhome.filink.exportapi.exception.FilinkExportNoDataException;
import com.fiberhome.filink.exportapi.exception.FilinkExportTaskNumTooBigException;
import com.fiberhome.filink.fdevice.bean.area.AreaInfo;
import com.fiberhome.filink.fdevice.bean.devicelog.DeviceLog;
import com.fiberhome.filink.fdevice.bean.devicelog.UnlockingStatistics;
import com.fiberhome.filink.fdevice.constant.device.*;
import com.fiberhome.filink.fdevice.dao.statistics.UnlockingStatisticsDao;
import com.fiberhome.filink.fdevice.dto.DeviceInfoDto;
import com.fiberhome.filink.fdevice.dto.DeviceParam;
import com.fiberhome.filink.fdevice.export.DeviceLogExport;
import com.fiberhome.filink.fdevice.service.device.DeviceInfoService;
import com.fiberhome.filink.fdevice.service.devicelog.DeviceLogService;
import com.fiberhome.filink.fdevice.utils.PageBeanHelper;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.constant.LogConstants;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.mongo.MongoQueryHelper;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import com.fiberhome.filink.userapi.api.UserFeign;
import com.fiberhome.filink.userapi.bean.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


/**
 * ?????????????????????
 *
 * @author CongcaiYu@wistronits.com
 */
@Slf4j
@Service
public class DeviceLogServiceImpl implements DeviceLogService {

    @Autowired
    private DeviceInfoService deviceInfoService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private UnlockingStatisticsDao unlockingStatisticsDao;


    /**
     * ?????????????????????
     */
    @Autowired
    private DeviceLogExport deviceLogExport;

    /**
     * userFeign
     */
    @Autowired
    private UserFeign userFeign;

    @Autowired
    private LogProcess logProcess;

    @Autowired
    private SystemLanguageUtil systemLanguageUtil;
    /**
     * ?????????
     */
    private static String SERVER_NAME = "filink-device-server";

    /**
     * ??????????????????
     */
    @Value("${maxExportDataSize}")
    private Integer maxExportDataSize;


    /**
     * ????????????????????????
     *
     * @param queryCondition ????????????
     * @param user           ????????????
     * @param needsAuth      ????????????????????????
     * @return
     */
    @Override
    public PageBean deviceLogListByPage(QueryCondition queryCondition, User user, boolean needsAuth) {
        //?????????????????????,??????????????????
        SortCondition sortCondition = queryCondition.getSortCondition();
        if (sortCondition == null || StringUtils.isEmpty(sortCondition.getSortField())
                || StringUtils.isEmpty(sortCondition.getSortRule())) {
            SortCondition condition = new SortCondition();
            condition.setSortField(ConstantParam.CURRENT_TIME);
            condition.setSortRule(ConstantParam.DESC);
            queryCondition.setSortCondition(condition);
        }
        //??????????????????
        if (needsAuth) {
            queryCondition = addDataPermission(queryCondition, user);
        }
        if (queryCondition == null) {
            return new PageBean();
        }

        Query query = generateQuery(queryCondition);
        //????????????????????????
        long count = mongoTemplate.count(query, DeviceLog.class);
        //??????????????????????????????
        List<DeviceLog> deviceLogList = mongoTemplate.find(query, DeviceLog.class);
        //?????????????????????????????????????????????
        PageBean pageBean = PageBeanHelper.generatePageBean(deviceLogList, queryCondition.getPageCondition(), count);
        return pageBean;
    }

    /**
     * ????????????????????????
     *
     * @param queryCondition ????????????
     * @param user
     * @return ????????????????????????
     */
    @Override
    public Integer deviceLogCount(QueryCondition queryCondition, User user) {
        //??????????????????
        queryCondition = addDataPermission(queryCondition, user);
        if (queryCondition == null) {
            return 0;
        }
        Query query = generateQuery(queryCondition);
        //????????????????????????
        return (int) mongoTemplate.count(query, DeviceLog.class);
    }

    /**
     * ??????Mongo????????????
     *
     * @param queryCondition
     * @return
     */
    public Query generateQuery(QueryCondition queryCondition) {
        //??????filterCondition??????????????????
        if (queryCondition != null && ObjectUtils.isEmpty(queryCondition.getFilterConditions())) {
            //?????????????????????????????????????????????MongoQuery?????????
            MongoQueryHelper.convertFilterConditions(queryCondition.getFilterConditions());
        }
        //?????????????????????MongoQuery??????
        Query query = new Query();
        MongoQueryHelper.withPage(query, queryCondition.getPageCondition());
        //???????????????????????????
        MongoQueryHelper.buildQuery(query, queryCondition);
        return query;
    }

    /**
     * ???????????????????????? for pda
     *
     * @param queryCondition ????????????
     * @return ????????????
     */
    @Override
    public PageBean deviceLogListByPageForPda(QueryCondition queryCondition) {
        PageBean pageBean = new PageBean();
        Query query = new Query();
        //???????????????

        queryCondition = addDataPermission(queryCondition, null);
        if (queryCondition == null) {
            return pageBean;
        }

        //??????filterCondition??????????????????
        if (null != queryCondition.getFilterConditions()) {
            //?????????????????????????????????????????????MongoQuery?????????
            MongoQueryHelper.convertFilterConditions(queryCondition.getFilterConditions());
        }
        //?????????????????????MongoQuery??????
        MongoQueryHelper.withPage(query, queryCondition.getPageCondition());
        Query buildQuery = MongoQueryHelper.buildQuery(query, queryCondition);
        //?????????????????????
        Map<String, Long> bizCondition = (Map<String, Long>) queryCondition.getBizCondition();
        if (bizCondition != null) {
            Criteria criteria = Criteria.where(ConstantParam.CURRENT_TIME).gte(bizCondition
                    .get(ConstantParam.START_TIME)).lte(bizCondition.get(ConstantParam.END_TIME));
            buildQuery.addCriteria(criteria);
        }
        //????????????????????????
        long count = mongoTemplate.count(buildQuery, DeviceLog.class);
        //??????????????????????????????
        List<DeviceLog> deviceLogList = mongoTemplate.find(buildQuery, DeviceLog.class);
        //?????????????????????????????????????????????
        pageBean = PageBeanHelper.generatePageBean(deviceLogList, queryCondition.getPageCondition(), count);
        return pageBean;
    }

    /**
     * ??????????????????
     *
     * @param deviceLog ??????????????????
     * @return ????????????
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result saveDeviceLog(DeviceLog deviceLog) throws Exception {
        //?????????????????????????????????
        String deviceId = deviceLog.getDeviceId();
        DeviceInfoDto deviceInfoDto = (DeviceInfoDto) deviceInfoService.getDeviceById(deviceId, null).getData();
        if (deviceInfoDto == null) {
            log.info("the device id : {} is not exist", deviceId);
            return null;
        }
        //??????????????????
        deviceLog.setDeviceType(deviceInfoDto.getDeviceType());
        deviceLog.setDeviceCode(deviceInfoDto.getDeviceCode());
        deviceLog.setDeviceName(deviceInfoDto.getDeviceName());
        //??????????????????
        AreaInfo areaInfo = deviceInfoDto.getAreaInfo();
        if (areaInfo == null) {
            log.error("the device : {} areaInfo is not exist", deviceId);
        } else {
            deviceLog.setAreaId(areaInfo.getAreaId());
            deviceLog.setAreaName(areaInfo.getAreaName());
        }
        mongoTemplate.save(deviceLog);
        return ResultUtils.success();
    }

    /**
     * ??????????????????
     *
     * @param deviceIdList
     * @return
     */
    @Override
    public Result deleteDeviceLogByDeviceIds(List<String> deviceIdList) {
        //????????????
        if (ObjectUtils.isEmpty(deviceIdList)) {
            return ResultUtils.warn(DeviceResultCode.DEVICE_PARAM_ERROR,
                    I18nUtils.getSystemString(DeviceI18n.DEVICE_PARAM_ERROR));
        }
        Query query = new Query(Criteria.where("deviceId").in(deviceIdList));
        mongoTemplate.remove(query, DeviceLog.class);
        return ResultUtils.success();
    }

    /**
     * ????????????????????????
     *
     * @param exportDto ??????????????????
     * @return ??????????????????
     */
    @Override
    public Result exportDeviceLogList(ExportDto exportDto) {
        systemLanguageUtil.querySystemLanguage();
        ExportRequestInfo exportRequestInfo;
        try {
            exportRequestInfo = deviceLogExport.insertTask(exportDto, SERVER_NAME, I18nUtils.getSystemString(DeviceLogI18n.DEVICE_LOG_LIST_NAME));
        } catch (FilinkExportNoDataException fe) {
            fe.printStackTrace();
            return ResultUtils.warn(DeviceLogResultCode.EXPORT_NO_DATA, I18nUtils.getSystemString(DeviceLogI18n.EXPORT_NO_DATA));
        } catch (FilinkExportDataTooLargeException fe) {
            fe.printStackTrace();
            String string = I18nUtils.getSystemString(DeviceLogI18n.EXPORT_DATA_TOO_LARGE);
            String dataCount = fe.getMessage();
            Object[] params = {dataCount, maxExportDataSize};
            String msg = MessageFormat.format(string, params);
            return ResultUtils.warn(DeviceLogResultCode.EXPORT_DATA_TOO_LARGE, msg);
        } catch (FilinkExportTaskNumTooBigException fe) {
            fe.printStackTrace();
            return ResultUtils.warn(DeviceLogResultCode.EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS, I18nUtils.getSystemString(DeviceLogI18n.EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.warn(DeviceLogResultCode.FAILED_TO_CREATE_EXPORT_TASK, I18nUtils.getSystemString(DeviceLogI18n.FAILED_TO_CREATE_EXPORT_TASK));
        }
        addLogByExport(exportDto);
        deviceLogExport.exportData(exportRequestInfo);
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(DeviceLogI18n.THE_EXPORT_TASK_WAS_CREATED_SUCCESSFULLY));
    }

    /**
     * ????????????????????????
     *
     * @param exportDto
     */
    private void addLogByExport(ExportDto exportDto) {
        AddLogBean addLogBean = logProcess.generateAddLogToCallParam(LogConstants.LOG_TYPE_OPERATE);
        addLogBean.setDataId("export");
        addLogBean.setDataName("listName");
        //??????????????????id
        addLogBean.setOptObjId("export");
        //???????????????
        addLogBean.setOptObj(exportDto.getListName());
        addLogBean.setDataOptType("export");
        addLogBean.setFunctionCode(LogFunctionCodeConstant.EXPORT_DEVICE_LOG_FUNCTION_CODE);
        //??????????????????
        logProcess.addOperateLogInfoToCall(addLogBean, LogConstants.ADD_LOG_LOCAL_FILE);
    }

    /**
     * ????????????????????????????????????
     *
     * @param deviceId
     * @return
     */
    @Override
    public Result queryRecentDeviceLogTime(String deviceId) {
        Query query = new Query(Criteria.where("deviceId").is(deviceId));
        query.with(new Sort(Sort.Direction.DESC, "currentTime")).limit(1);
        List<DeviceLog> deviceLogList = mongoTemplate.find(query, DeviceLog.class);

        Map<String, Long> map = new HashMap<>(1);
        if (ObjectUtils.isEmpty(deviceLogList)) {
            return ResultUtils.warn(DeviceLogResultCode.DEVICE_LOG_NOT_EXISTED, I18nUtils.getSystemString(DeviceLogI18n.DEVICE_LOG_NOT_EXISTED));
        } else {
            map.put("recentLogTime", deviceLogList.get(0).getCurrentTime());
        }
        return ResultUtils.success(map);
    }

    /**
     * ????????????????????????
     */
    @Override
    public void synchronizeUnlockingStatistics() {
        //????????????
        LocalDateTime nowDateTime = LocalDateTime.now();
        Timestamp currentTime = Timestamp.valueOf(nowDateTime);

        //??????
        LocalDate nowDate = nowDateTime.toLocalDate();

        //??????????????????15??????
        LocalDate startDate = nowDate.minusDays(15L);

        //???????????????????????????????????????
        String unlockingDateString = unlockingStatisticsDao.queryMaxStatisticsDate();
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMdd");
        if (!StringUtils.isEmpty(unlockingDateString)) {
            startDate = LocalDate.parse(unlockingDateString, df).plusDays(1L);
        }

        for (LocalDate oneDate = startDate; oneDate.isBefore(nowDate); oneDate = oneDate.plusDays(1L)) {
            //?????????????????????????????????
            Long startTime = Timestamp.valueOf(LocalDateTime.of(oneDate, LocalTime.MIN)).getTime();
            Long endTime = Timestamp.valueOf(LocalDateTime.of(oneDate, LocalTime.MAX)).getTime();
            List<UnlockingStatistics> unlockingStatistics = queryUnlockingCount(startTime, endTime);
            if (!ObjectUtils.isEmpty(unlockingStatistics)) {
                for (UnlockingStatistics us : unlockingStatistics) {
                    us.setUnlockingStatisticsId(NineteenUUIDUtils.uuid());
                    us.setStatisticsDate(oneDate.format(df));
                    us.setCurrentTime(currentTime);
                    //???????????????
                    unlockingStatisticsDao.insert(us);
                }
            }
        }

        //??????15???????????????
//        System.out.println(nowDate.minusDays(16L).format(df));
        deleteUnlockingStatistics(null, startDate.minusDays(1L).format(df));
    }

    /**
     * ??????????????????????????????????????????
     *
     * @param startDate
     * @param endDate
     * @return
     */
    private Integer deleteUnlockingStatistics(String startDate, String endDate) {
        EntityWrapper<UnlockingStatistics> wrapper = new EntityWrapper<>();
        if (ObjectUtils.isEmpty(startDate)) {
            wrapper.ge("statistics_date", startDate);
        }
        if (ObjectUtils.isEmpty(endDate)) {
            wrapper.le("statistics_date", endDate);
        }
        return unlockingStatisticsDao.delete(wrapper);
    }

    /**
     * ????????????????????????????????????????????????
     */
    private List<UnlockingStatistics> queryUnlockingCount(Long startTime, Long endTime) {
        //??????????????????
        Criteria criteria1 = Criteria.where("currentTime").gte(startTime).lte(endTime);
        Criteria criteria2 = Criteria.where("type").is("1");
        //???????????????
        GroupOperation groupOperation = Aggregation.group("deviceId").count().as("unlockingCount");
        //????????????
        ProjectionOperation projectionOperation = Aggregation.project().and("_id").as("deviceId")
                .and("unlockingCount").as("unlockingCount");
        //??????????????????
        Aggregation aggregation = Aggregation.newAggregation(DeviceLog.class, Aggregation.match(criteria1),
                Aggregation.match(criteria2), groupOperation, projectionOperation);
        //mongo??????
        AggregationResults<UnlockingStatistics> results = mongoTemplate.aggregate(aggregation, "deviceLog", UnlockingStatistics.class);
        return results.getMappedResults();
    }

    /**
     * ????????????????????????????????????????????????
     *
     * @param deviceId
     * @param startTime
     * @param endTime
     * @return
     */
    @Override
    public Integer queryUnlockingCountByDeviceId(String deviceId, Long startTime, Long endTime) {
        //??????????????????
        Criteria criteria1 = Criteria.where("currentTime").gte(startTime).lte(endTime);
        Criteria criteria2 = Criteria.where("type").is("1");
        Criteria criteria3 = Criteria.where("deviceId").is(deviceId);
        Query query = new Query();
        query.addCriteria(criteria1).addCriteria(criteria2).addCriteria(criteria3);
        int deviceLogCount = (int) mongoTemplate.count(query, "deviceLog");
        return deviceLogCount;
    }

    /**
     * ????????????????????????
     *
     * @return
     */
    private User getCurrentUser() {
        //????????????????????????
        Object userObj = userFeign.queryCurrentUser(RequestInfoUtils.getUserId(), RequestInfoUtils.getToken());
        //?????????User
        return DeviceInfoService.convertObjectToUser(userObj);
    }

    /**
     * ????????????????????????????????????
     */
    private QueryCondition addDataPermission(QueryCondition queryCondition, User user) {
        if (user == null) {
            user = getCurrentUser();
        }
        if (DeviceConstant.ADMIN.equals(user.getId())) {
            return queryCondition;
        }
        DeviceParam deviceParam = deviceInfoService.getUserAuth(user);
        if (ObjectUtils.isEmpty(deviceParam) || ObjectUtils.isEmpty(deviceParam.getAreaIds())
                || ObjectUtils.isEmpty(deviceParam.getDeviceTypes())) {
            return null;
        } else {
            List<FilterCondition> filterConditions = queryCondition.getFilterConditions();
            if (filterConditions == null) {
                filterConditions = new ArrayList<>();
            }
            //???????????????????????????
            Set<String> filterFieldSet = new HashSet<>();

            //???????????????????????????????????????
            for (FilterCondition oldCondition : filterConditions) {
                if (ConstantParam.AREA_ID.equals(oldCondition.getFilterField())) {
                    List<String> filterValue = (List<String>) oldCondition.getFilterValue();
                    List<String> areaIds = deviceParam.getAreaIds();
                    filterValue.retainAll(areaIds);
                    oldCondition.setFilterValue(filterValue);
                    filterFieldSet.add(ConstantParam.AREA_ID);
                } else if (ConstantParam.DEVICE_TYPE.equals(oldCondition.getFilterField())) {
                    List<String> filterValue = (List<String>) oldCondition.getFilterValue();
                    List<String> deviceTypes = deviceParam.getDeviceTypes();
                    filterValue.retainAll(deviceTypes);
                    oldCondition.setFilterValue(filterValue);
                    filterFieldSet.add(ConstantParam.DEVICE_TYPE);
                }
            }
            //????????????????????????ID???????????????
            if (!filterFieldSet.contains(ConstantParam.AREA_ID)) {
                //????????????ID??????
                filterConditions.add(DeviceInfoService.generateFilterCondition(ConstantParam.AREA_ID,
                        ConstantParam.IN_STRING, deviceParam.getAreaIds()));
            }
            if (!filterFieldSet.contains(ConstantParam.DEVICE_TYPE)) {
                //????????????ID??????
                filterConditions.add(DeviceInfoService.generateFilterCondition(ConstantParam.DEVICE_TYPE,
                        ConstantParam.IN_STRING, deviceParam.getDeviceTypes()));
            }

            return queryCondition;
        }
    }

}
