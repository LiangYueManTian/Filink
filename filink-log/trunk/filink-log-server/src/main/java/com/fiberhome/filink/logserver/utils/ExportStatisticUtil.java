package com.fiberhome.filink.logserver.utils;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.exportapi.bean.ExportRequestInfo;
import com.fiberhome.filink.exportapi.exception.FilinkExportDataTooLargeException;
import com.fiberhome.filink.exportapi.exception.FilinkExportNoDataException;
import com.fiberhome.filink.exportapi.exception.FilinkExportTaskNumTooBigException;
import com.fiberhome.filink.exportapi.job.AbstractExport;
import com.fiberhome.filink.logserver.constant.I18nConstants;
import com.fiberhome.filink.logserver.service.impl.LogServiceImpl;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * <p>
 * 统计导出服务工具
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/6/21
 */
@Component
@Slf4j
public class ExportStatisticUtil {

    @Autowired
    private LogServiceImpl logService;

    public static ExportStatisticUtil exportStatisticUtilInfo;

    public LogServiceImpl logServiceImpl;


    /**
     * @Author hedongwei@wistronits.com
     * @Description 初始化数据
     * @Date 10:15 2019/7/5
     * @Param []
     */
    @PostConstruct
    public void init() {
        exportStatisticUtilInfo = this;
        exportStatisticUtilInfo.logServiceImpl = this.logService;
    }

    /**
     * 数据导出
     *
     * @param exportDto 导出信息
     * @param listName  列表名称
     * @return Result
     */
    public static <T extends AbstractExport> Result exportProcessing(T abstractExport, ExportDto exportDto, String exportServerName, String listName) {
        ExportRequestInfo export = null;
        try {
            export = abstractExport.insertTask(exportDto, exportServerName, listName);
        } catch (FilinkExportNoDataException fe) {
            log.error("export no data error", fe);
            return ResultUtils.warn(LogResultCode.EXPORT_NO_DATA, I18nUtils.getSystemString(I18nConstants.EXPORT_NO_DATA));
        } catch (FilinkExportDataTooLargeException fe) {
            return exportStatisticUtilInfo.logServiceImpl.getExportToLargeMsg(fe);
        } catch (FilinkExportTaskNumTooBigException fe) {
            log.error("export num too big error", fe);
            return ResultUtils.warn(LogResultCode.EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS, I18nUtils.getSystemString(I18nConstants.EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS));
        } catch (Exception e) {
            log.error("export no data error", e);
            return ResultUtils.warn(LogResultCode.FAILED_TO_CREATE_EXPORT_TASK, I18nUtils.getSystemString(I18nConstants.FAILED_TO_CREATE_EXPORT_TASK));
        }
        abstractExport.exportData(export);
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(I18nConstants.THE_EXPORT_TASK_WAS_CREATED_SUCCESSFULLY));
    }

}
