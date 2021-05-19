package com.fiberhome.filink.alarmhistoryserver.service.impl;

import com.fiberhome.filink.alarmhistoryserver.constant.AppConstant;
import com.fiberhome.filink.alarmhistoryserver.constant.LogFunctionCodeConstant;
import com.fiberhome.filink.alarmhistoryserver.utils.AlarmHistoryExport;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.exportapi.exception.FilinkExportDataTooLargeException;
import com.fiberhome.filink.exportapi.exception.FilinkExportNoDataException;
import com.fiberhome.filink.exportapi.exception.FilinkExportTaskNumTooBigException;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMockit.class)
public class AlarmHistoryExportServiceImplTest {

    @Tested
    private AlarmHistoryExportServiceImpl alarmHistoryExportService;
    /**
     * 最大导出条数
     */
    @Injectable
    private Integer maxExportDataSize = 1000;

    /**
     * 导出
     */
    @Injectable
    private AlarmHistoryExport alarmHistoryExport;

    /**
     * 日志api
     */
    @Injectable
    private LogProcess logProcess;

    @Injectable
    private SystemLanguageUtil systemLanguageUtil;

    @Test
    public void exportAlarmList() throws Exception {
        ExportDto exportDto = new ExportDto<>();
        Result result = alarmHistoryExportService.exportAlarmList(exportDto);
        Assert.assertTrue(result.getCode() == ResultCode.SUCCESS);
        new Expectations() {
            {
                alarmHistoryExport.insertTask((ExportDto) any, anyString, anyString);
                result = new FilinkExportNoDataException();
            }
        };
        Result result2 = alarmHistoryExportService.exportAlarmList(exportDto);
        Assert.assertTrue(result2.getCode() == LogFunctionCodeConstant.EXPORT_NO_DATA);
        new Expectations() {
            {
                I18nUtils.getSystemString(AppConstant.EXPORT_DATA_TOO_LARGE);
                result = "导出数据{0}条，超过最大限制{1}条";
            }

            {
                alarmHistoryExport.insertTask((ExportDto) any, anyString, anyString);
                result = new FilinkExportDataTooLargeException("1000");
            }
        };
        Result result1 = alarmHistoryExportService.exportAlarmList(exportDto);
        Assert.assertTrue(result1.getCode() == LogFunctionCodeConstant.EXPORT_DATA_TOO_LARGE);
        new Expectations() {
            {
                alarmHistoryExport.insertTask((ExportDto) any, anyString, anyString);
                result = new FilinkExportTaskNumTooBigException();
            }
        };
        Result result3 = alarmHistoryExportService.exportAlarmList(exportDto);
        Assert.assertTrue(result3.getCode() == LogFunctionCodeConstant.EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS);
        new Expectations() {
            {
                alarmHistoryExport.insertTask((ExportDto) any, anyString, anyString);
                result = new NullPointerException();
            }
        };
        Result result4 = alarmHistoryExportService.exportAlarmList(exportDto);
        Assert.assertTrue(result4.getCode() == LogFunctionCodeConstant.FAILED_TO_CREATE_EXPORT_TASK);
    }

}