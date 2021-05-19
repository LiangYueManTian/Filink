package com.fiberhome.filink.parts.service.impl;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.exportapi.bean.Export;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.exportapi.bean.ExportRequestInfo;
import com.fiberhome.filink.exportapi.exception.FilinkExportDataTooLargeException;
import com.fiberhome.filink.exportapi.exception.FilinkExportNoDataException;
import com.fiberhome.filink.exportapi.exception.FilinkExportTaskNumTooBigException;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.constant.LogConstants;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.parts.constant.PartsResultCode;
import com.fiberhome.filink.parts.export.PartInfoExport;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import mockit.Expectations;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.when;

/**
 * @Author: zhaoliang
 * @Date: 2019/5/24 18:23
 * @Description: com.fiberhome.filink.parts.service.impl
 * @version: 1.0
 */
@RunWith(MockitoJUnitRunner.class)
public class ExportServiceImplTest {

    @InjectMocks
    private ExportServiceImpl exportService;

    @Mock
    private PartInfoExport partInfoExport;

    @Mock
    private SystemLanguageUtil systemLanguageUtil;
    @Mock
    LogProcess logProcess = LogProcess.logProcess;

    public static final String DEFAULT_RESULT = "i18n_result";

    /**
     * 服务名
     */
    private static String SERVER_NAME = "filink-device-parts";

    @Test
    public void exportPartList() {
        new Expectations() {
            {
                systemLanguageUtil.querySystemLanguage();
            }
        };
        new Expectations(I18nUtils.class) {
            {
                I18nUtils.getSystemString(anyString);
                result = DEFAULT_RESULT;
            }
        };
        AddLogBean addLogBean = new AddLogBean();
        LogProcess logProcessIns = new LogProcess();
        new Expectations(LogProcess.class) {
            {
                LogProcess.logProcess = logProcessIns;
                LogProcess.logProcess.generateAddLogToCallParam(LogConstants.LOG_TYPE_OPERATE);
                result = addLogBean;

            }

            {
                LogProcess.logProcess = logProcessIns;
                LogProcess.logProcess.addOperateLogInfoToCall(addLogBean, LogConstants.ADD_LOG_LOCAL_FILE);
            }
        };
        Export export = new Export();
        ExportDto exportDto = new ExportDto();
        ExportRequestInfo exportRequestInfo = new ExportRequestInfo();
        when(partInfoExport.insertTask(exportDto, SERVER_NAME, DEFAULT_RESULT)).thenReturn(exportRequestInfo);
        Result result = exportService.exportPartList(exportDto);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());

        when(partInfoExport.insertTask(exportDto, SERVER_NAME, DEFAULT_RESULT))
                .thenThrow(FilinkExportNoDataException.class)
                .thenThrow(FilinkExportDataTooLargeException.class)
                .thenThrow(FilinkExportTaskNumTooBigException.class)
                .thenThrow(Exception.class);
        result = exportService.exportPartList(exportDto);
        Assert.assertTrue(result.getCode() == PartsResultCode.EXPORT_NO_DATA);

        result = exportService.exportPartList(exportDto);
        Assert.assertTrue(result.getCode() == PartsResultCode.EXPORT_DATA_TOO_LARGE);

        result = exportService.exportPartList(exportDto);
        Assert.assertTrue(result.getCode() == PartsResultCode.EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS);

        result = exportService.exportPartList(exportDto);
        Assert.assertTrue(result.getCode() == PartsResultCode.FAILED_TO_CREATE_EXPORT_TASK);
    }
}
