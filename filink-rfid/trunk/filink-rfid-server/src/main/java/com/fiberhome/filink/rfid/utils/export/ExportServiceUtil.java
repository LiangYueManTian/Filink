package com.fiberhome.filink.rfid.utils.export;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.exportapi.bean.ExportRequestInfo;
import com.fiberhome.filink.exportapi.exception.FilinkExportDataTooLargeException;
import com.fiberhome.filink.exportapi.exception.FilinkExportNoDataException;
import com.fiberhome.filink.exportapi.exception.FilinkExportTaskNumTooBigException;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.constant.LogConstants;
import com.fiberhome.filink.rfid.constant.RfIdI18nConstant;
import com.fiberhome.filink.rfid.constant.RfIdResultCode;
import com.fiberhome.filink.server_common.utils.I18nUtils;

import java.text.MessageFormat;


/**
 * 数据导出服务工具
 *
 * @author congcongsun2@wistronits.com
 * @since 2019/6/20
 */
public class ExportServiceUtil {
    /**
     * 数据导出
     *
     * @param exportServiceUtilDto 导出Dto
     * @return Result
     */
    public static Result exportProcessing(ExportServiceUtilDto exportServiceUtilDto) {
        ExportRequestInfo exportRequestInfo;
        try {
            exportRequestInfo = exportServiceUtilDto.getAbstractExport().insertTask(exportServiceUtilDto.getExportDto(), exportServiceUtilDto.getExportServerName(), exportServiceUtilDto.getListName());
        } catch (FilinkExportNoDataException fe) {
            fe.printStackTrace();
            return ResultUtils.warn(RfIdResultCode.EXPORT_NO_DATA, I18nUtils.getSystemString(RfIdI18nConstant.EXPORT_NO_DATA));
        } catch (FilinkExportDataTooLargeException fe) {
            return getExportToLargeMsg(fe, exportServiceUtilDto.getMaxExportDataSize());
        } catch (FilinkExportTaskNumTooBigException fe) {
            fe.printStackTrace();
            return ResultUtils.warn(RfIdResultCode.EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS, I18nUtils.getSystemString(RfIdI18nConstant.EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.warn(RfIdResultCode.FAILED_TO_CREATE_EXPORT_TASK, I18nUtils.getSystemString(RfIdI18nConstant.FAILED_TO_CREATE_EXPORT_TASK));
        }
        //导出
        exportServiceUtilDto.getAbstractExport().exportData(exportRequestInfo);
        //添加日志
        addLogByExport(exportServiceUtilDto);
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(RfIdI18nConstant.THE_EXPORT_TASK_WAS_CREATED_SUCCESSFULLY));
    }

    /**
     * 保存导出日志
     *
     * @param exportServiceUtilDto 导出类
     * @return Boolean
     */
    public static Boolean addLogByExport(ExportServiceUtilDto exportServiceUtilDto) {
        Boolean flag = false;
        exportServiceUtilDto.getSystemLanguageUtil().querySystemLanguage();
        String logType = LogConstants.LOG_TYPE_OPERATE;
        AddLogBean addLogBean = exportServiceUtilDto.getLogProces().generateAddLogToCallParam(logType);
        addLogBean.setDataId("export");
        addLogBean.setDataName("listName");
        //获得操作对象id
        addLogBean.setOptObjId("export");
        //操作为导出
        addLogBean.setDataOptType("export");
        addLogBean.setOptObj(exportServiceUtilDto.getExportDto().getListName());
        addLogBean.setFunctionCode(exportServiceUtilDto.getFunctionCode());
        //新增操作日志
        exportServiceUtilDto.getLogProces().addOperateLogInfoToCall(addLogBean, LogConstants.ADD_LOG_LOCAL_FILE);
        return flag;
    }

    /**
     * 设置导出数据超长异常提示语
     *
     * @param fe                导出数据超长异常
     * @param maxExportDataSize 最长导出长度
     * @return Result
     */
    public static Result getExportToLargeMsg(FilinkExportDataTooLargeException fe, Integer maxExportDataSize) {
        fe.printStackTrace();
        String string = I18nUtils.getSystemString(RfIdI18nConstant.EXPORT_DATA_TOO_LARGE);
        String dataCount = fe.getMessage();
        Object[] params = {dataCount, maxExportDataSize};
        String msg = MessageFormat.format(string, params);
        return ResultUtils.warn(RfIdResultCode.EXPORT_DATA_TOO_LARGE, msg);
    }
}
