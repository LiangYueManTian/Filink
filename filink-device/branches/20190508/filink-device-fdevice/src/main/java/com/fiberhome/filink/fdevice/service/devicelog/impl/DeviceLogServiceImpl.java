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
 * 设施日志实现类
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
     * 设施日志导出类
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
     * 服务名
     */
    private static String SERVER_NAME = "filink-device-server";

    /**
     * 最大导出条数
     */
    @Value("${maxExportDataSize}")
    private Integer maxExportDataSize;


    /**
     * 分页查询设施日志
     *
     * @param queryCondition 查询条件
     * @param user           查询用户
     * @param needsAuth      是否需要控制权限
     * @return
     */
    @Override
    public PageBean deviceLogListByPage(QueryCondition queryCondition, User user, boolean needsAuth) {
        //若排序条件为空,默认时间排序
        SortCondition sortCondition = queryCondition.getSortCondition();
        if (sortCondition == null || StringUtils.isEmpty(sortCondition.getSortField())
                || StringUtils.isEmpty(sortCondition.getSortRule())) {
            SortCondition condition = new SortCondition();
            condition.setSortField(ConstantParam.CURRENT_TIME);
            condition.setSortRule(ConstantParam.DESC);
            queryCondition.setSortCondition(condition);
        }
        //添加数据权限
        if (needsAuth) {
            queryCondition = addDataPermission(queryCondition, user);
        }
        if (queryCondition == null) {
            return new PageBean();
        }

        Query query = generateQuery(queryCondition);
        //查询设施日志条数
        long count = mongoTemplate.count(query, DeviceLog.class);
        //查询设施日志列表信息
        List<DeviceLog> deviceLogList = mongoTemplate.find(query, DeviceLog.class);
        //返回分页对象信息，组装分页对象
        PageBean pageBean = PageBeanHelper.generatePageBean(deviceLogList, queryCondition.getPageCondition(), count);
        return pageBean;
    }

    /**
     * 查询设施日志数量
     *
     * @param queryCondition 查询条件
     * @param user
     * @return 返回设施日志数量
     */
    @Override
    public Integer deviceLogCount(QueryCondition queryCondition, User user) {
        //添加数据权限
        queryCondition = addDataPermission(queryCondition, user);
        if (queryCondition == null) {
            return 0;
        }
        Query query = generateQuery(queryCondition);
        //查询设施日志条数
        return (int) mongoTemplate.count(query, DeviceLog.class);
    }

    /**
     * 生成Mongo查询对象
     *
     * @param queryCondition
     * @return
     */
    public Query generateQuery(QueryCondition queryCondition) {
        //判断filterCondition对象是否为空
        if (queryCondition != null && ObjectUtils.isEmpty(queryCondition.getFilterConditions())) {
            //不为空的情况需要添加过滤条件到MongoQuery对象中
            MongoQueryHelper.convertFilterConditions(queryCondition.getFilterConditions());
        }
        //添加分页条件到MongoQuery条件
        Query query = new Query();
        MongoQueryHelper.withPage(query, queryCondition.getPageCondition());
        //添加查询条件，排序
        MongoQueryHelper.buildQuery(query, queryCondition);
        return query;
    }

    /**
     * 分页查询设施日志 for pda
     *
     * @param queryCondition 查询条件
     * @return 分页数据
     */
    @Override
    public PageBean deviceLogListByPageForPda(QueryCondition queryCondition) {
        PageBean pageBean = new PageBean();
        Query query = new Query();
        //加数据权限

        queryCondition = addDataPermission(queryCondition, null);
        if (queryCondition == null) {
            return pageBean;
        }

        //判断filterCondition对象是否为空
        if (null != queryCondition.getFilterConditions()) {
            //不为空的情况需要添加过滤条件到MongoQuery对象中
            MongoQueryHelper.convertFilterConditions(queryCondition.getFilterConditions());
        }
        //添加分页条件到MongoQuery条件
        MongoQueryHelper.withPage(query, queryCondition.getPageCondition());
        Query buildQuery = MongoQueryHelper.buildQuery(query, queryCondition);
        //加上时间戳条件
        Map<String, Long> bizCondition = (Map<String, Long>) queryCondition.getBizCondition();
        if (bizCondition != null) {
            Criteria criteria = Criteria.where(ConstantParam.CURRENT_TIME).gte(bizCondition
                    .get(ConstantParam.START_TIME)).lte(bizCondition.get(ConstantParam.END_TIME));
            buildQuery.addCriteria(criteria);
        }
        //查询设施日志条数
        long count = mongoTemplate.count(buildQuery, DeviceLog.class);
        //查询设施日志列表信息
        List<DeviceLog> deviceLogList = mongoTemplate.find(buildQuery, DeviceLog.class);
        //返回分页对象信息，组装分页对象
        pageBean = PageBeanHelper.generatePageBean(deviceLogList, queryCondition.getPageCondition(), count);
        return pageBean;
    }

    /**
     * 保存设施日志
     *
     * @param deviceLog 设施日志对象
     * @return 操作结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result saveDeviceLog(DeviceLog deviceLog) throws Exception {
        //根据序列号查询设施信息
        String deviceId = deviceLog.getDeviceId();
        DeviceInfoDto deviceInfoDto = (DeviceInfoDto) deviceInfoService.getDeviceById(deviceId, null).getData();
        if (deviceInfoDto == null) {
            log.info("the device id : {} is not exist", deviceId);
            return null;
        }
        //设置设施信息
        deviceLog.setDeviceType(deviceInfoDto.getDeviceType());
        deviceLog.setDeviceCode(deviceInfoDto.getDeviceCode());
        deviceLog.setDeviceName(deviceInfoDto.getDeviceName());
        //设置区域信息
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
     * 删除设施日志
     *
     * @param deviceIdList
     * @return
     */
    @Override
    public Result deleteDeviceLogByDeviceIds(List<String> deviceIdList) {
        //校验参数
        if (ObjectUtils.isEmpty(deviceIdList)) {
            return ResultUtils.warn(DeviceResultCode.DEVICE_PARAM_ERROR,
                    I18nUtils.getSystemString(DeviceI18n.DEVICE_PARAM_ERROR));
        }
        Query query = new Query(Criteria.where("deviceId").in(deviceIdList));
        mongoTemplate.remove(query, DeviceLog.class);
        return ResultUtils.success();
    }

    /**
     * 导出设施日志列表
     *
     * @param exportDto 传入导出信息
     * @return 创建任务结果
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
     * 列表导出记录日志
     *
     * @param exportDto
     */
    private void addLogByExport(ExportDto exportDto) {
        AddLogBean addLogBean = logProcess.generateAddLogToCallParam(LogConstants.LOG_TYPE_OPERATE);
        addLogBean.setDataId("export");
        addLogBean.setDataName("listName");
        //获得操作对象id
        addLogBean.setOptObjId("export");
        //操作为新增
        addLogBean.setOptObj(exportDto.getListName());
        addLogBean.setDataOptType("export");
        addLogBean.setFunctionCode(LogFunctionCodeConstant.EXPORT_DEVICE_LOG_FUNCTION_CODE);
        //新增操作日志
        logProcess.addOperateLogInfoToCall(addLogBean, LogConstants.ADD_LOG_LOCAL_FILE);
    }

    /**
     * 查询最近一次操作日志时间
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
     * 同步开锁次数统计
     */
    @Override
    public void synchronizeUnlockingStatistics() {
        //当前时间
        LocalDateTime nowDateTime = LocalDateTime.now();
        Timestamp currentTime = Timestamp.valueOf(nowDateTime);

        //今天
        LocalDate nowDate = nowDateTime.toLocalDate();

        //起始日期默认15天前
        LocalDate startDate = nowDate.minusDays(15L);

        //查询数据库最近开锁数据日期
        String unlockingDateString = unlockingStatisticsDao.queryMaxStatisticsDate();
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMdd");
        if (!StringUtils.isEmpty(unlockingDateString)) {
            startDate = LocalDate.parse(unlockingDateString, df).plusDays(1L);
        }

        for (LocalDate oneDate = startDate; oneDate.isBefore(nowDate); oneDate = oneDate.plusDays(1L)) {
            //查询当天的设施开锁数据
            Long startTime = Timestamp.valueOf(LocalDateTime.of(oneDate, LocalTime.MIN)).getTime();
            Long endTime = Timestamp.valueOf(LocalDateTime.of(oneDate, LocalTime.MAX)).getTime();
            List<UnlockingStatistics> unlockingStatistics = queryUnlockingCount(startTime, endTime);
            if (!ObjectUtils.isEmpty(unlockingStatistics)) {
                for (UnlockingStatistics us : unlockingStatistics) {
                    us.setUnlockingStatisticsId(NineteenUUIDUtils.uuid());
                    us.setStatisticsDate(oneDate.format(df));
                    us.setCurrentTime(currentTime);
                    //存入数据库
                    unlockingStatisticsDao.insert(us);
                }
            }
        }

        //删除15天前的数据
//        System.out.println(nowDate.minusDays(16L).format(df));
        deleteUnlockingStatistics(null, startDate.minusDays(1L).format(df));
    }

    /**
     * 删除一段日期内的开锁统计数据
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
     * 查询一定时间内所有设施的开锁次数
     */
    private List<UnlockingStatistics> queryUnlockingCount(Long startTime, Long endTime) {
        //构建查询参数
        Criteria criteria1 = Criteria.where("currentTime").gte(startTime).lte(endTime);
        Criteria criteria2 = Criteria.where("type").is("1");
        //查询计数值
        GroupOperation groupOperation = Aggregation.group("deviceId").count().as("unlockingCount");
        //字段映射
        ProjectionOperation projectionOperation = Aggregation.project().and("_id").as("deviceId")
                .and("unlockingCount").as("unlockingCount");
        //组合查询条件
        Aggregation aggregation = Aggregation.newAggregation(DeviceLog.class, Aggregation.match(criteria1),
                Aggregation.match(criteria2), groupOperation, projectionOperation);
        //mongo查询
        AggregationResults<UnlockingStatistics> results = mongoTemplate.aggregate(aggregation, "deviceLog", UnlockingStatistics.class);
        return results.getMappedResults();
    }

    /**
     * 查询指定设施一段时间内的开锁次数
     *
     * @param deviceId
     * @param startTime
     * @param endTime
     * @return
     */
    @Override
    public Integer queryUnlockingCountByDeviceId(String deviceId, Long startTime, Long endTime) {
        //构建查询参数
        Criteria criteria1 = Criteria.where("currentTime").gte(startTime).lte(endTime);
        Criteria criteria2 = Criteria.where("type").is("1");
        Criteria criteria3 = Criteria.where("deviceId").is(deviceId);
        Query query = new Query();
        query.addCriteria(criteria1).addCriteria(criteria2).addCriteria(criteria3);
        int deviceLogCount = (int) mongoTemplate.count(query, "deviceLog");
        return deviceLogCount;
    }

    /**
     * 查询当前用户信息
     *
     * @return
     */
    private User getCurrentUser() {
        //查询当前用户对象
        Object userObj = userFeign.queryCurrentUser(RequestInfoUtils.getUserId(), RequestInfoUtils.getToken());
        //转换为User
        return DeviceInfoService.convertObjectToUser(userObj);
    }

    /**
     * 将数据权限添加到筛选条件
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
            //保存所有过滤项名称
            Set<String> filterFieldSet = new HashSet<>();

            //权限过滤合并到当前过滤器中
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
            //判断是否含有区域ID和设施类型
            if (!filterFieldSet.contains(ConstantParam.AREA_ID)) {
                //添加区域ID过滤
                filterConditions.add(DeviceInfoService.generateFilterCondition(ConstantParam.AREA_ID,
                        ConstantParam.IN_STRING, deviceParam.getAreaIds()));
            }
            if (!filterFieldSet.contains(ConstantParam.DEVICE_TYPE)) {
                //添加区域ID过滤
                filterConditions.add(DeviceInfoService.generateFilterCondition(ConstantParam.DEVICE_TYPE,
                        ConstantParam.IN_STRING, deviceParam.getDeviceTypes()));
            }

            return queryCondition;
        }
    }

}
