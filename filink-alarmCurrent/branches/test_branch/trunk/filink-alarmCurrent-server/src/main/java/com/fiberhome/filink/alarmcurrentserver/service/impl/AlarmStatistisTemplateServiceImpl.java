package com.fiberhome.filink.alarmcurrentserver.service.impl;

import com.fiberhome.filink.alarmcurrentserver.bean.AlarmStatisticsTemp;
import com.fiberhome.filink.alarmcurrentserver.constant.AlarmCurrent18n;
import com.fiberhome.filink.alarmcurrentserver.constant.AppConstant;
import com.fiberhome.filink.alarmcurrentserver.constant.LogFunctionCodeConstant;
import com.fiberhome.filink.alarmcurrentserver.dao.AlarmStatisticsTempDao;
import com.fiberhome.filink.alarmcurrentserver.exception.FilinkAlarmCurrentException;
import com.fiberhome.filink.alarmcurrentserver.service.AlarmCurrentService;
import com.fiberhome.filink.alarmcurrentserver.service.AlarmStatisticsTemplateService;
import com.fiberhome.filink.alarmcurrentserver.utils.AlarmStatisticsExport;
import com.fiberhome.filink.bean.NineteenUUIDUtils;
import com.fiberhome.filink.bean.RequestInfoUtils;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.exportapi.bean.ExportRequestInfo;
import com.fiberhome.filink.exportapi.exception.FilinkExportNoDataException;
import com.fiberhome.filink.exportapi.exception.FilinkExportTaskNumTooBigException;
import com.fiberhome.filink.logapi.annotation.AddLogAnnotation;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.constant.LogConstants;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import com.fiberhome.filink.userapi.bean.User;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-05-21
 */
@Slf4j
@Service
public class AlarmStatistisTemplateServiceImpl implements AlarmStatisticsTemplateService {

    /**
     * 告警统计模板dao
     */
    @Autowired
    private AlarmStatisticsTempDao alarmStatisticsTempDao;

    @Autowired
    private AlarmCurrentService alarmCurrentService;

    @Autowired
    private AlarmStatisticsExport alarmStatisticsExport;

    @Autowired
    private SystemLanguageUtil systemLanguageUtil;

    /**
     * 日志api
     */
    @Autowired
    private LogProcess logProcess;

    @Autowired
    private AlarmCurrentExportServiceImpl alarmCurrentExportService;


    /**
     * 查询告警统计模板
     *
     * @param pageType 条件信息
     * @return 告警统计模板信息
     */
    @Override
    public Result queryAlarmStatisticsTempList(String pageType) {
        User user = alarmCurrentService.getUser();
        AlarmStatisticsTemp alarmStatisticsTemp = new AlarmStatisticsTemp();
        alarmStatisticsTemp.setPageType(pageType);
        alarmStatisticsTemp.setCreateUser(user.getUserName());
        List<AlarmStatisticsTemp> list = alarmStatisticsTempDao.queryAlarmStatisticsTemp(alarmStatisticsTemp);
        return ResultUtils.success(list);
    }

    /**
     * 新建告警统计模板
     *
     * @param alarmStatisticsTemp 封装条件
     * @return 当前告警列表信息
     */
    @Transactional(rollbackFor = Exception.class)
    @AddLogAnnotation(value = AlarmCurrent18n.ADD, logType = "1", functionCode = AlarmCurrent18n.ALARM_LOG_TEMPLATE_NAME_ADD,
            dataGetColumnName = "templateName", dataGetColumnId = "id")
    @Override
    public Result addAlarmStatisticsTemp(AlarmStatisticsTemp alarmStatisticsTemp) {
        alarmStatisticsTemp.setId(NineteenUUIDUtils.uuid());
        String userName = RequestInfoUtils.getUserName();
        alarmStatisticsTemp.setCreateUser(userName);
        int isSuccess = alarmStatisticsTempDao.addAlarmStatisticsTemp(Arrays.asList(alarmStatisticsTemp));
        if (isSuccess == 0) {
            throw new FilinkAlarmCurrentException(I18nUtils.getSystemString(AppConstant.ADD_STATISTICAL_TEMPLATE_FAILED));
        }
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(AppConstant.ADD_STATISTICAL_TEMPLATE_SUCCESS));

    }

    /**
     * 修改告警统计模板信息
     *
     * @param alarmStatisticsTemp 封装条件
     * @return 当前告警列表信息
     */
    @Transactional(rollbackFor = Exception.class)
    @AddLogAnnotation(value = AlarmCurrent18n.UPDATE, logType = "1", functionCode = AlarmCurrent18n.ALARM_LOG_TEMPLATE_NAME_UPDATE,
            dataGetColumnName = "templateName", dataGetColumnId = "id")
    @Override
    public Result updateAlarmStatisticsTemp(AlarmStatisticsTemp alarmStatisticsTemp) {
        int isSuccess = alarmStatisticsTempDao.batchUpdateAlarmStatisticsTemp(alarmStatisticsTemp);
        if (isSuccess == 0) {
            throw new FilinkAlarmCurrentException(I18nUtils.getSystemString(AppConstant.UPDATE_STATISTICAL_TEMPLATE_FAILED));
        }
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(AppConstant.UPDATE_STATISTICAL_TEMPLATE_SUCCESS));
    }

    /**
     * 删除告警统计模板信息
     *
     * @param ids 封装条件
     * @return 当前告警列表信息
     */
    @Override
    public Result deleteManyAlarmStatisticsTemp(String[] ids) {
        int isSuccess = alarmStatisticsTempDao.batchDeleteAlarmStatisticsTemp(ids);
        if (isSuccess == 0) {
            throw new FilinkAlarmCurrentException(I18nUtils.getSystemString(AppConstant.DELETE_STATISTICAL_TEMPLATE_FAILED));
        }
        // 记录日志
        this.deleteLog(ids, AlarmCurrent18n.ALARM_LOG_TEMPLATE_NAME_DELETE, AlarmCurrent18n.TEMPLATE_NAME);
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(AppConstant.DELETE_STATISTICAL_TEMPLATE_SUCCESS));
    }

    /**
     * 根据ID查询告警统计模板列表信息
     *
     * @param tempId 封装条件
     * @return 当前告警列表信息
     */
    @Override
    public AlarmStatisticsTemp queryAlarmStatisticsTempId(String tempId) {
        return alarmStatisticsTempDao.queryAlarmStatisticsTempById(tempId);
    }

    /**
     * 告警统计导出
     *
     * @param exportDto 封装条件
     * @return 当前告警列表信息
     */
    @Override
    public Result exportAlarmStatisticList(ExportDto exportDto) {
        ExportRequestInfo exportRequestInfo;
        try {
            exportRequestInfo = alarmStatisticsExport.insertTask(exportDto, AppConstant.SERVER_NAME,
                    I18nUtils.getSystemString(AppConstant.OPERATE_ALARM_STATISTICS_LIST_NAME));
        } catch (FilinkExportNoDataException fe) {
            fe.printStackTrace();
            return ResultUtils.warn(LogFunctionCodeConstant.EXPORT_NO_DATA, I18nUtils.getSystemString(AppConstant.EXPORT_NO_DATA));
        } catch (FilinkExportTaskNumTooBigException fe) {
            fe.printStackTrace();
            return ResultUtils.warn(LogFunctionCodeConstant.EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS,
                    I18nUtils.getSystemString(AppConstant.EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.warn(LogFunctionCodeConstant.FAILED_TO_CREATE_EXPORT_TASK,
                    I18nUtils.getSystemString(AppConstant.FAILED_TO_CREATE_EXPORT_TASK));
        }
        alarmCurrentExportService.addLogByExport(exportDto, AlarmCurrent18n.ALARM_STATISTICS_EXPORT);
        alarmStatisticsExport.exportData(exportRequestInfo);
        return ResultUtils.success(ResultCode.SUCCESS,
                I18nUtils.getSystemString(AppConstant.THE_EXPORT_TASK_WAS_CREATED_SUCCESSFULLY));
    }

    /**
     * 删除日志记录
     *
     * @param ids   用户id
     * @param model 模板
     * @param name  名称
     */
    protected void deleteLog(String[] ids, String model, String name) {
        for (String id : ids) {
            AlarmStatisticsTemp alarmStatisticsTemp = alarmStatisticsTempDao.queryAlarmStatisticsTempByIds(id);
            systemLanguageUtil.querySystemLanguage();
            // 获取日志类型
            String logType = LogConstants.LOG_TYPE_OPERATE;
            AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
            addLogBean.setDataId(AppConstant.ALARM_ID);
            addLogBean.setDataName(name);
            addLogBean.setFunctionCode(model);
            // 获取操作对象
            addLogBean.setOptObjId(id);
            addLogBean.setOptObj(alarmStatisticsTemp.getTemplateName());
            // 操作为删除
            addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_DELETE);
            // 新增操作日志
            logProcess.addOperateLogInfoToCall(addLogBean, LogConstants.ADD_LOG_LOCAL_FILE);
        }
    }
}
