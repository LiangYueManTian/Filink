package com.fiberhome.filink.rfid.utils.export;

import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.exportapi.exception.FilinkExportNoDataException;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.rfid.constant.LogFunctionCodeConstant;
import com.fiberhome.filink.rfid.constant.RfIdI18nConstant;
import com.fiberhome.filink.rfid.export.statistics.OpticalFiberStatisticsExport;
import com.fiberhome.filink.rfid.req.statistics.export.ExportOpticCableInfoStatisticReq;
import com.fiberhome.filink.rfid.service.template.impl.TemplateServiceImpl;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import mockit.Mock;
import mockit.MockUp;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * @author liyj
 * @date 2019/7/26
 */
@RunWith(JMockit.class)
public class ExportServiceUtilTest {


    @Tested
    private ExportServiceUtil serviceUtil;

    private ExportServiceUtilDto exportServiceUtilDto;


    @Before
    public void setupInfo() {
        new MockUp<ExportServiceUtilDto>() {
            SystemLanguageUtil getSystemLanguageUtil() {
                SystemLanguageUtil util = new SystemLanguageUtil();
                return util;
            }
        };
        new MockUp<SystemLanguageUtil>() {
            String querySystemLanguage() {
                return "en";
            }
        };
        ExportDto<ExportOpticCableInfoStatisticReq> exportDto = new ExportDto<>();
        exportServiceUtilDto = new ExportServiceUtilDto<>(
                new OpticalFiberStatisticsExport(),
                exportDto,
                "listName",
                1000,
                I18nUtils.getSystemString(RfIdI18nConstant.EXPORT_OPTICAL_FIBER_STATISTICS),
                new SystemLanguageUtil(),
                new LogProcess(),
                LogFunctionCodeConstant.EXPORT_OPTICAL_FIBER_STATISTICS_FUNCTION_CODE);
    }

    /**
     * testExportProcessing
     *
     * @throws Exception
     */
    @Test
    public void exportProcessing() throws Exception {
        try {
            serviceUtil.exportProcessing(exportServiceUtilDto);
        } catch (FilinkExportNoDataException e) {
            Assert.assertTrue(e instanceof Exception);
        }
    }

    @Test
    public void addLogByExport() throws Exception {



    }

    @Test
    public void getExportToLargeMsg() throws Exception {
    }

}