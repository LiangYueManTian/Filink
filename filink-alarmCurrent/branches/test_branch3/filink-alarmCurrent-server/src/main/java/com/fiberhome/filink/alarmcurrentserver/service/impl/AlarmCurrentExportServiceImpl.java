package com.fiberhome.filink.alarmcurrentserver.service.impl;

import com.fiberhome.filink.alarmcurrentserver.bean.*;
import com.fiberhome.filink.alarmcurrentserver.constant.AlarmCurrent18n;
import com.fiberhome.filink.alarmcurrentserver.constant.AppConstant;
import com.fiberhome.filink.alarmcurrentserver.constant.LogFunctionCodeConstant;
import com.fiberhome.filink.alarmcurrentserver.service.AlarmCurrentExportService;
import com.fiberhome.filink.alarmcurrentserver.utils.AlarmCurrentExport;
import com.fiberhome.filink.alarmcurrentserver.utils.AlarmTopExport;
import com.fiberhome.filink.alarmcurrentserver.utils.ListUtil;
import com.fiberhome.filink.alarmhistoryapi.api.AlarmHistoryFeign;
import com.fiberhome.filink.alarmhistoryapi.bean.AlarmHistory;
import com.fiberhome.filink.alarmsetapi.api.AlarmSetFeign;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.exportapi.bean.ExportRequestInfo;
import com.fiberhome.filink.exportapi.exception.FilinkExportDataTooLargeException;
import com.fiberhome.filink.exportapi.exception.FilinkExportNoDataException;
import com.fiberhome.filink.exportapi.exception.FilinkExportTaskNumTooBigException;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.constant.LogConstants;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import com.fiberhome.filink.userapi.bean.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 导出服务实现类
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-22
 */
@Slf4j
@Service
public class AlarmCurrentExportServiceImpl implements AlarmCurrentExportService {

    /**
     * 最大导出条数
     */
    @Value("${maxExportDataSize}")
    private static Integer maxExportDataSize;

    /**
     * 当前告警导出
     */
    @Autowired
    private AlarmCurrentExport alarmCurrentExport;

    /**
     * 日志api
     */
    @Autowired
    private LogProcess logProcess;

    @Autowired
    private SystemLanguageUtil systemLanguageUtil;

    @Autowired
    private AlarmCurrentServiceImpl alarmCurrentService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private AlarmStatisticsServiceImpl alarmStatisticsService;

    @Autowired
    private AlarmTopExport alarmTopExport;

    @Autowired
    private AlarmSetFeign alarmSetFeign;

    /**
     * 历史告警api
     */
    @Autowired
    private AlarmHistoryFeign alarmHistoryFeign;


    /**
     * 导出用户列表信息
     *
     * @param exportDto 导出信息
     * @return 判断结果
     */
    @Override
    public Result exportAlarmList(ExportDto exportDto) {
        ExportRequestInfo exportRequestInfo;
        try {
            exportRequestInfo = alarmCurrentExport.insertTask(exportDto, AppConstant.SERVER_NAME,
                    I18nUtils.getSystemString(AppConstant.OPERATE_ALARM_LIST_NAME));
        } catch (FilinkExportNoDataException fe) {
            fe.printStackTrace();
            return ResultUtils.warn(LogFunctionCodeConstant.EXPORT_NO_DATA, I18nUtils.getSystemString(AppConstant.EXPORT_NO_DATA));
        } catch (FilinkExportDataTooLargeException fe) {
            return getExportToLargeMsg(fe);
        } catch (FilinkExportTaskNumTooBigException fe) {
            fe.printStackTrace();
            return ResultUtils.warn(LogFunctionCodeConstant.EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS,
                    I18nUtils.getSystemString(AppConstant.EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.warn(LogFunctionCodeConstant.FAILED_TO_CREATE_EXPORT_TASK,
                    I18nUtils.getSystemString(AppConstant.FAILED_TO_CREATE_EXPORT_TASK));
        }
        addLogByExport(exportDto, AlarmCurrent18n.ALARM_CURRENT_EXPORT);
        alarmCurrentExport.exportData(exportRequestInfo);
        return ResultUtils.success(ResultCode.SUCCESS,
                I18nUtils.getSystemString(AppConstant.THE_EXPORT_TASK_WAS_CREATED_SUCCESSFULLY));
    }

    /**
     * 导出数据超过最大限制返回信息
     *
     * @param fe 异常
     * @return 返回结果
     */
    private Result getExportToLargeMsg(FilinkExportDataTooLargeException fe) {
        fe.printStackTrace();
        String string = I18nUtils.getSystemString(AppConstant.EXPORT_DATA_TOO_LARGE);
        String dataCount = fe.getMessage();
        Object[] params = {dataCount, maxExportDataSize};
        String msg = MessageFormat.format(string, params);
        return ResultUtils.warn(LogFunctionCodeConstant.EXPORT_DATA_TOO_LARGE, msg);
    }

    /**
     * 记录导出日志
     *
     * @param exportDto 导出信息
     */
    protected void addLogByExport(ExportDto exportDto, String code) {
        String logType = LogConstants.LOG_TYPE_OPERATE;
        systemLanguageUtil.querySystemLanguage();
        AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
        addLogBean.setDataId("export");
        addLogBean.setDataName("listName");
        //获得操作对象id
        addLogBean.setOptObjId("export");
        //操作为新增
        addLogBean.setDataOptType("export");
        addLogBean.setOptObj(exportDto.getListName());
        addLogBean.setFunctionCode(code);
        //新增操作日志
        logProcess.addOperateLogInfoToCall(addLogBean, LogConstants.ADD_LOG_LOCAL_FILE);
    }

    /**
     * 告警top导出
     *
     * @param exportDto 条件信息
     * @return 返回结果
     */
    @Override
    public Result exportDeviceTop(ExportDto exportDto) {
        ExportRequestInfo exportRequestInfo;
        try {
            exportRequestInfo = alarmTopExport.insertTask(exportDto, AppConstant.SERVER_NAME,
                    I18nUtils.getSystemString(AppConstant.ALARM_DEVICE_COUNT_LIST_NAME));
        } catch (FilinkExportNoDataException fe) {
            fe.printStackTrace();
            return ResultUtils.warn(LogFunctionCodeConstant.EXPORT_NO_DATA, I18nUtils.getSystemString(AppConstant.EXPORT_NO_DATA));
        } catch (FilinkExportDataTooLargeException fe) {
            return getExportToLargeMsg(fe);
        } catch (FilinkExportTaskNumTooBigException fe) {
            fe.printStackTrace();
            return ResultUtils.warn(LogFunctionCodeConstant.EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS,
                    I18nUtils.getSystemString(AppConstant.EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.warn(LogFunctionCodeConstant.FAILED_TO_CREATE_EXPORT_TASK,
                    I18nUtils.getSystemString(AppConstant.FAILED_TO_CREATE_EXPORT_TASK));
        }
        addLogByExport(exportDto, AlarmCurrent18n.ALARM_DEVICE_EXPORT);
        alarmTopExport.exportData(exportRequestInfo);
        return ResultUtils.success(ResultCode.SUCCESS,
                I18nUtils.getSystemString(AppConstant.THE_EXPORT_TASK_WAS_CREATED_SUCCESSFULLY));
    }

    /**
     * 当前转历史
     */
    @Override
    public void updateAlarmCleanStatusCompensation() {
        Integer setTime = alarmSetFeign.queryAlarmHistorySetFeign();
        long time = setTime * 60 * 60 * 1000;
        long timeMillis = System.currentTimeMillis();
        long cleanTime = timeMillis - time;
        Query query = new Query(Criteria.where(AppConstant.ALARM_CLEAN_STATUS).ne(3).and(AppConstant.ALARM_CLEAN_TIME).lte(cleanTime));
        List<AlarmCurrent> alarmCurrentList = mongoTemplate.find(query, AlarmCurrent.class);
        //无数据返回
        if (ListUtil.isEmpty(alarmCurrentList)) {
            return;
        }
        List<AlarmHistory> alarmHistoryList = new ArrayList<>();
        List<String> idList = new ArrayList<>();
        alarmCurrentList.forEach(alarmCurrent -> {
            // 当前告警转历史告警
            AlarmHistory alarmHistory = new AlarmHistory();
            // 当前告警信息值复制给历史告警
            BeanUtils.copyProperties(alarmCurrent, alarmHistory);
            idList.add(alarmCurrent.getId());
            alarmHistoryList.add(alarmHistory);
        });
        Query query1 = new Query(Criteria.where(AppConstant.ALARM_IDS).in(idList));
        // 添加到历史告警中
        alarmHistoryFeign.insertAlarmHistoryListFeign(alarmHistoryList);
        // 删除已清除的数据
        mongoTemplate.remove(query1, AlarmCurrent.class);
    }

    /**
     * 告警设施总数信息
     *
     * @param alarmObject 设施类型
     * @return 总数信息
     */
    @Override
    public Result queryAlarmObjectCount(String alarmObject) {
        Query query = new Query();
        User user = alarmCurrentService.getUser();
        // 判断用户是否为admin
        if (!user.getId().equals(AppConstant.ONE)) {
            List<String> deviceTypes = alarmCurrentService.getDeviceTypes(user);
            List<String> areaIds = alarmCurrentService.getUserAreaIds(user);
            if (ListUtil.isEmpty(areaIds) || ListUtil.isEmpty(deviceTypes)) {
                query = new Query(Criteria.where(AppConstant.ALARM_CLEAN_STATUS).is(LogFunctionCodeConstant.ALARM_STATUS_THREE)
                        .and(AppConstant.ALARM_SOURCE_TYPE_ID).in((Object) null).and(AppConstant.AREA_ID).in((Object) null));
            } else {
                // 求交集
                List<String> arrayList = new ArrayList<>();
                arrayList.add(alarmObject);
                deviceTypes.retainAll(arrayList);
                // 判断交集是否为空
                if (!ListUtil.isEmpty(deviceTypes)) {
                    query = new Query(Criteria.where(AppConstant.ALARM_CLEAN_STATUS).is(LogFunctionCodeConstant.ALARM_STATUS_THREE)
                            .and(AppConstant.ALARM_SOURCE_TYPE_ID).in(deviceTypes).and(AppConstant.AREA_ID).in(areaIds));
                } else {
                    query = new Query(Criteria.where(AppConstant.ALARM_CLEAN_STATUS).is(LogFunctionCodeConstant.ALARM_STATUS_THREE)
                            .and(AppConstant.ALARM_SOURCE_TYPE_ID).in((Object) null).and(AppConstant.AREA_ID).in((Object) null));
                }
            }
        } else {
            query = new Query(Criteria.where(AppConstant.ALARM_CLEAN_STATUS).is(LogFunctionCodeConstant.ALARM_STATUS_THREE)
                    .and(AppConstant.ALARM_SOURCE_TYPE_ID).is(alarmObject));
        }
        long count = mongoTemplate.count(query, AlarmCurrent.class);
        return ResultUtils.success(count);
    }

    /**
     * 首页告警设施id更多信息
     *
     * @param alarmSourceLevelParameter 条件信息
     * @return 首页告警设施id更多信息
     */
    @Override
    public Result queryAlarmObjectCountHonePage(AlarmSourceLevelParameter alarmSourceLevelParameter) {
        Criteria criteria = new Criteria();
        criteria = getUserCriteria(criteria, alarmSourceLevelParameter);
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group(AppConstant.ALARM_SOURCE_TYPE_ID).count().as("groupNum"),
                Aggregation.project("groupNum").and("groupLevel").previousOperation());
        AggregationResults<AlarmStatisticsGroupInfo> results =
                this.mongoTemplate.aggregate(aggregation, AppConstant.ALARM_CURRENT, AlarmStatisticsGroupInfo.class);
        List<AlarmStatisticsGroupInfo> list = results.getMappedResults();
        AlarmStatisticsBySourceDto alarmStatisticsBySourceDto = getAlarmStatisticsBySourceDto(list);
        return ResultUtils.success(alarmStatisticsBySourceDto);
    }

    /**
     * 首页告警级别更多信息
     *
     * @param alarmSourceLevelParameter 条件信息
     * @return 首页告警级别更多信息
     */
    @Override
    public Result queryAlarmDeviceIdHonePage(AlarmSourceLevelParameter alarmSourceLevelParameter) {
        Criteria criteria = new Criteria();
        criteria = getUserCriteria(criteria, alarmSourceLevelParameter);
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group(AppConstant.ALARM_FIXED_LEVEL).count().as("groupNum"),
                Aggregation.project("groupNum").and("groupLevel").previousOperation());
        AggregationResults<AlarmStatisticsGroupInfo> results =
                this.mongoTemplate.aggregate(aggregation, AppConstant.ALARM_CURRENT, AlarmStatisticsGroupInfo.class);
        List<AlarmStatisticsGroupInfo> list = results.getMappedResults();
        AlarmStatisticsByLevelDto alarmStatisticsByLevelDto = alarmStatisticsService.getAlarmStatisticsByLevelDto(list);
        return ResultUtils.success(alarmStatisticsByLevelDto);
    }

    /**
     * 首页告警告警id查询设施类型
     *
     * @param alarmSourceLevelParameter 条件信息
     * @return 首页告警设施id更多信息
     */
    @Override
    public Result queryAlarmIdCountHonePage(AlarmSourceLevelParameter alarmSourceLevelParameter) {
        Criteria criteria = new Criteria();
        criteria = getUserCriteriaId(criteria, alarmSourceLevelParameter);
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group(AppConstant.ALARM_SOURCE_TYPE_ID).count().as("groupNum"),
                Aggregation.project("groupNum").and("groupLevel").previousOperation());
        AggregationResults<AlarmStatisticsGroupInfo> results =
                this.mongoTemplate.aggregate(aggregation, AppConstant.ALARM_CURRENT, AlarmStatisticsGroupInfo.class);
        List<AlarmStatisticsGroupInfo> list = results.getMappedResults();
        AlarmStatisticsBySourceDto alarmStatisticsBySourceDto = getAlarmStatisticsBySourceDto(list);
        return ResultUtils.success(alarmStatisticsBySourceDto);
    }

    /**
     * 首页告警告警id查询告警级别
     *
     * @param alarmSourceLevelParameter 条件信息
     * @return 首页告警级别更多信息
     */
    @Override
    public Result queryAlarmIdHonePage(AlarmSourceLevelParameter alarmSourceLevelParameter) {
        Criteria criteria = new Criteria();
        criteria = getUserCriteriaId(criteria, alarmSourceLevelParameter);
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group(AppConstant.ALARM_FIXED_LEVEL).count().as("groupNum"),
                Aggregation.project("groupNum").and("groupLevel").previousOperation());
        AggregationResults<AlarmStatisticsGroupInfo> results =
                this.mongoTemplate.aggregate(aggregation, AppConstant.ALARM_CURRENT, AlarmStatisticsGroupInfo.class);
        List<AlarmStatisticsGroupInfo> list = results.getMappedResults();
        AlarmStatisticsByLevelDto alarmStatisticsByLevelDto = alarmStatisticsService.getAlarmStatisticsByLevelDto(list);
        return ResultUtils.success(alarmStatisticsByLevelDto);
    }

    /**
     * 用户权限根据告警id
     *
     * @param criteria                  条件信息
     * @param alarmSourceLevelParameter 参数信息
     * @return 判断结果
     */
    private Criteria getUserCriteriaId(Criteria criteria, AlarmSourceLevelParameter alarmSourceLevelParameter) {
        User user = alarmCurrentService.getUser();
        // 判断用户是否为admin
        if (!user.getId().equals(AppConstant.ONE)) {
            List<String> deviceTypes = alarmCurrentService.getDeviceTypes(user);
            List<String> areaIds = alarmCurrentService.getUserAreaIds(user);
            if (ListUtil.isEmpty(areaIds) || ListUtil.isEmpty(deviceTypes)) {
                criteria = Criteria.where(AppConstant.ALARM_CLEAN_STATUS).is(LogFunctionCodeConstant.ALARM_STATUS_THREE)
                        .and(AppConstant.ALARM_IDS).is(alarmSourceLevelParameter.getId())
                        .and(AppConstant.ALARM_SOURCE_TYPE_ID).in((Object) null).and(AppConstant.AREA_ID).in((Object) null);
            } else {
                criteria = Criteria.where(AppConstant.ALARM_CLEAN_STATUS).is(LogFunctionCodeConstant.ALARM_STATUS_THREE)
                        .and(AppConstant.ALARM_IDS).is(alarmSourceLevelParameter.getId())
                        .and(AppConstant.ALARM_SOURCE_TYPE_ID).in(deviceTypes).and(AppConstant.AREA_ID).in(areaIds);
            }
        } else {
            criteria = Criteria.where(AppConstant.ALARM_CLEAN_STATUS).is(LogFunctionCodeConstant.ALARM_STATUS_THREE)
                    .and(AppConstant.ALARM_IDS).is(alarmSourceLevelParameter.getId());
        }
        return criteria;
    }

    /**
     * 用户权限根据设施id
     *
     * @param criteria                  条件信息
     * @param alarmSourceLevelParameter 参数信息
     * @return 判断结果
     */
    private Criteria getUserCriteria(Criteria criteria, AlarmSourceLevelParameter alarmSourceLevelParameter) {
        User user = alarmCurrentService.getUser();
        // 判断用户是否为admin
        if (!user.getId().equals(AppConstant.ONE)) {
            List<String> deviceTypes = alarmCurrentService.getDeviceTypes(user);
            List<String> areaIds = alarmCurrentService.getUserAreaIds(user);
            if (ListUtil.isEmpty(areaIds) || ListUtil.isEmpty(deviceTypes)) {
                criteria = Criteria.where(AppConstant.ALARM_CLEAN_STATUS).is(LogFunctionCodeConstant.ALARM_STATUS_THREE)
                        .and(AppConstant.ALARM_SOURCE).is(alarmSourceLevelParameter.getDeviceId())
                        .and(AppConstant.ALARM_SOURCE_TYPE_ID).in((Object) null).and(AppConstant.AREA_ID).in((Object) null);
            } else {
                criteria = Criteria.where(AppConstant.ALARM_CLEAN_STATUS).is(LogFunctionCodeConstant.ALARM_STATUS_THREE)
                        .and(AppConstant.ALARM_SOURCE).is(alarmSourceLevelParameter.getDeviceId())
                        .and(AppConstant.ALARM_SOURCE_TYPE_ID).in(deviceTypes).and(AppConstant.AREA_ID).in(areaIds);
            }
        } else {
            criteria = Criteria.where(AppConstant.ALARM_CLEAN_STATUS).is(LogFunctionCodeConstant.ALARM_STATUS_THREE)
                    .and(AppConstant.ALARM_SOURCE).is(alarmSourceLevelParameter.getDeviceId());
        }
        return criteria;
    }

    protected AlarmStatisticsBySourceDto getAlarmStatisticsBySourceDto(List<AlarmStatisticsGroupInfo> list) {
        // 初始化对象数据(返回的结果不会为null)
        AlarmStatisticsBySourceDto alarmStatistics =
                new AlarmStatisticsBySourceDto(0, 0, 0, 0, 0);
        list.forEach(u -> {
            switch (u.getGroupLevel()) {
                case "001":
                    alarmStatistics.setOpticalBok(u.getGroupNum());
                    break;
                case "030":
                    alarmStatistics.setWell(u.getGroupNum());
                    break;
                case "060":
                    alarmStatistics.setDistributionFrame(u.getGroupNum());
                    break;
                case "090":
                    alarmStatistics.setJunctionBox(u.getGroupNum());
                    break;
                case "150":
                    alarmStatistics.setSplittingBox(u.getGroupNum());
                    break;
                default:
            }
        });
        return alarmStatistics;
    }

}
