package com.fiberhome.filink.parts.service.impl;

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
import com.fiberhome.filink.parts.constant.LogFunctionCodeConstant;
import com.fiberhome.filink.parts.constant.PartsI18n;
import com.fiberhome.filink.parts.constant.PartsResultCode;
import com.fiberhome.filink.parts.export.PartInfoExport;
import com.fiberhome.filink.parts.service.ExportService;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;

/**
 * @Author: zl
 * @Date: 2019/4/28 20:39
 * @Description: com.fiberhome.filink.parts.service.impl
 * @version: 1.0
 */
@Component
public class ExportServiceImpl implements ExportService {
    /**
     * 配件信息导出类
     */
    @Autowired
    private PartInfoExport partInfoExport;

    /**
     * 服务名
     */
    private static String SERVER_NAME = "filink-device-parts";

    /**
     * 最大导出条数
     */
    @Value("${maxExportDataSize}")
    private Integer maxExportDataSize;
    /**
     * 注入语言环境api
     */
    @Autowired
    private SystemLanguageUtil systemLanguageUtil;
    /**
     * 导出配件列表
     *
     * @param exportDto 传入导出信息
     * @return 创建任务结果
     */
    @Override
    public Result exportPartList(ExportDto exportDto) {
        systemLanguageUtil.querySystemLanguage();
        ExportRequestInfo exportRequestInfo;
        try {
            exportRequestInfo = partInfoExport.insertTask(exportDto, SERVER_NAME, I18nUtils.getSystemString(PartsI18n.PART_INFO_LIST_NAME));
        } catch (FilinkExportNoDataException fe) {
            fe.printStackTrace();
            return ResultUtils.warn(PartsResultCode.EXPORT_NO_DATA, I18nUtils.getSystemString(PartsI18n.EXPORT_NO_DATA));
        } catch (FilinkExportDataTooLargeException fe) {
            fe.printStackTrace();
            String string = I18nUtils.getSystemString(PartsI18n.EXPORT_DATA_TOO_LARGE);
            String dataCount = fe.getMessage();
            Object[] params = {dataCount, maxExportDataSize};
            String msg = MessageFormat.format(string, params);
            return ResultUtils.warn(PartsResultCode.EXPORT_DATA_TOO_LARGE, msg);
        } catch (FilinkExportTaskNumTooBigException fe) {
            fe.printStackTrace();
            return ResultUtils.warn(PartsResultCode.EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS, I18nUtils.getSystemString(PartsI18n.EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.warn(PartsResultCode.FAILED_TO_CREATE_EXPORT_TASK, I18nUtils.getSystemString(PartsI18n.FAILED_TO_CREATE_EXPORT_TASK));
        }
        addLogByExport(exportDto);
        partInfoExport.exportData(exportRequestInfo);
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(PartsI18n.THE_EXPORT_TASK_WAS_CREATED_SUCCESSFULLY));
    }

    /**
     * 列表导出记录日志
     *
     * @param exportDto
     */
    private void addLogByExport(ExportDto exportDto) {
        String logType = LogConstants.LOG_TYPE_OPERATE;
        AddLogBean addLogBean = LogProcess.logProcess.generateAddLogToCallParam(logType);
        addLogBean.setDataName("listName");
        addLogBean.setDataId("export");
        //获得操作对象id
        addLogBean.setOptObjId("export");
        //操作为新增
        addLogBean.setDataOptType("export");
        addLogBean.setOptObj(exportDto.getListName());
        addLogBean.setFunctionCode(LogFunctionCodeConstant.EXPORT_PART_FUNCTION_CODE);
        //新增操作日志
        LogProcess.logProcess.addOperateLogInfoToCall(addLogBean, LogConstants.ADD_LOG_LOCAL_FILE);
    }
}
