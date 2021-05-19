package com.fiberhome.filink.alarmhistoryserver.service.impl;


import com.fiberhome.filink.alarmhistoryserver.constant.AppConstant;
import com.fiberhome.filink.alarmhistoryserver.constant.LogFunctionCodeConstant;
import com.fiberhome.filink.alarmhistoryserver.service.AlarmHistoryExportService;
import com.fiberhome.filink.alarmhistoryserver.utils.AlarmHistoryExport;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.exportapi.bean.Export;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.exportapi.exception.FilinkExportDataTooLargeException;
import com.fiberhome.filink.exportapi.exception.FilinkExportNoDataException;
import com.fiberhome.filink.exportapi.exception.FilinkExportTaskNumTooBigException;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.constant.LogConstants;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import java.text.MessageFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 导出告警服务实现类
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-22
 */
@Service
public class AlarmHistoryExportServiceImpl implements AlarmHistoryExportService {

    /**
     * 最大导出条数
     */
    @Value("${maxExportDataSize}")
    private static Integer maxExportDataSize;

    /**
     * 导出
     */
    @Autowired
    private AlarmHistoryExport alarmHistoryExport;

    /**
     * 日志api
     */
    @Autowired
    private LogProcess logProcess;

    /**
     * 导出用户列表信息
     *
     * @param exportDto 导出信息
     * @return 判断结果
     */
    @Override
    public Result exportAlarmList(ExportDto exportDto) {
        Export export;
        try {
            export = alarmHistoryExport.insertTask(exportDto, AppConstant.SERVER_NAME,
                    I18nUtils.getString(AppConstant.OPERATE_ALARM_LIST_NAME));
        } catch (FilinkExportNoDataException fe) {
            fe.printStackTrace();
            return ResultUtils.warn(LogFunctionCodeConstant.EXPORT_NO_DATA,
                    I18nUtils.getString(AppConstant.EXPORT_NO_DATA));
        } catch (FilinkExportDataTooLargeException fe) {
            return getExportToLargeMsg(fe);
        } catch (FilinkExportTaskNumTooBigException fe) {
            fe.printStackTrace();
            return ResultUtils.warn(LogFunctionCodeConstant.EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS,
                    I18nUtils.getString(AppConstant.EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.warn(LogFunctionCodeConstant.FAILED_TO_CREATE_EXPORT_TASK,
                    I18nUtils.getString(AppConstant.FAILED_TO_CREATE_EXPORT_TASK));
        }
        addLogByExport(exportDto);
        alarmHistoryExport.exportData(export);
        return ResultUtils.success(ResultCode.SUCCESS,
                I18nUtils.getString(AppConstant.THE_EXPORT_TASK_WAS_CREATED_SUCCESSFULLY));
    }

    /**
     * 导出数据超过最大限制返回信息
     *
     * @param fe 异常
     * @return 返回结果
     */
    private Result getExportToLargeMsg(FilinkExportDataTooLargeException fe) {
        fe.printStackTrace();
        String string = I18nUtils.getString(AppConstant.EXPORT_DATA_TOO_LARGE);
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
    private void addLogByExport(ExportDto exportDto) {
        String logType = LogConstants.LOG_TYPE_OPERATE;
        AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
        addLogBean.setDataId("export");
        addLogBean.setDataName("listName");
        //获得操作对象id
        addLogBean.setOptObjId("export");
        //操作为新增
        addLogBean.setDataOptType("export");
        addLogBean.setOptObj(exportDto.getListName());
        addLogBean.setFunctionCode("1705006");
        //新增操作日志
        logProcess.addOperateLogInfoToCall(addLogBean, LogConstants.ADD_LOG_LOCAL_FILE);
    }

}
