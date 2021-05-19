package com.fiberhome.filink.alarmcurrentserver.service.impl;

import com.fiberhome.filink.alarmcurrentserver.bean.AlarmStatisticsTemp;
import com.fiberhome.filink.alarmcurrentserver.constant.LogFunctionCodeConstant;
import com.fiberhome.filink.alarmcurrentserver.dao.AlarmStatisticsTempDao;
import com.fiberhome.filink.alarmcurrentserver.service.AlarmCurrentService;
import com.fiberhome.filink.alarmcurrentserver.utils.AlarmStatisticsExport;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.exportapi.exception.FilinkExportNoDataException;
import com.fiberhome.filink.exportapi.exception.FilinkExportTaskNumTooBigException;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import com.fiberhome.filink.userapi.bean.User;
import java.util.List;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMockit.class)
public class AlarmStatistisTemplateServiceImplTest {

    @Injectable
    private AlarmStatisticsTempDao alarmStatisticsTempDao;

    @Injectable
    private AlarmCurrentService alarmCurrentService;

    @Injectable
    private AlarmStatisticsExport alarmStatisticsExport;

    @Injectable
    private SystemLanguageUtil systemLanguageUtil;

    @Tested
    private AlarmStatistisTemplateServiceImpl alarmStatistisTemplateService;

    /**
     * 日志api
     */
    @Injectable
    private LogProcess logProcess;

    @Injectable
    private AlarmCurrentExportServiceImpl alarmCurrentExportService;

    @Test
    public void queryAlarmStatisticsTempList() throws Exception {
        User user = new User();
        user.setId("1");
        new Expectations() {
            {
                alarmCurrentService.getUser();
                result = user;
            }
        };
        alarmStatistisTemplateService.queryAlarmStatisticsTempList("1");
    }

    @Test
    public void addAlarmStatisticsTemp() throws Exception {
        AlarmStatisticsTemp alarmStatisticsTemp = new AlarmStatisticsTemp();
        new Expectations() {
            {
                alarmStatisticsTempDao.addAlarmStatisticsTemp((List<AlarmStatisticsTemp>) any);
                result = 0;
            }
        };
        try {
            alarmStatistisTemplateService.addAlarmStatisticsTemp(alarmStatisticsTemp);
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
        new Expectations() {
            {
                alarmStatisticsTempDao.addAlarmStatisticsTemp((List<AlarmStatisticsTemp>) any);
                result = 1;
            }
        };
        alarmStatistisTemplateService.addAlarmStatisticsTemp(alarmStatisticsTemp);
    }

    @Test
    public void updateAlarmStatisticsTemp() throws Exception {
        AlarmStatisticsTemp alarmStatisticsTemp = new AlarmStatisticsTemp();
        new Expectations() {
            {
                alarmStatisticsTempDao.batchUpdateAlarmStatisticsTemp((AlarmStatisticsTemp) any);
                result = 0;
            }
        };
        try {
            alarmStatistisTemplateService.updateAlarmStatisticsTemp(alarmStatisticsTemp);
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
        new Expectations() {
            {
                alarmStatisticsTempDao.batchUpdateAlarmStatisticsTemp((AlarmStatisticsTemp) any);
                result = 1;
            }
        };
        alarmStatistisTemplateService.updateAlarmStatisticsTemp(alarmStatisticsTemp);
    }

    @Test
    public void deleteManyAlarmStatisticsTemp() throws Exception {
        new Expectations() {
            {
                alarmStatisticsTempDao.batchDeleteAlarmStatisticsTemp((String[]) any);
                result = 0;
            }
        };
        try {
            alarmStatistisTemplateService.deleteManyAlarmStatisticsTemp(new String[]{});
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
        new Expectations() {
            {
                alarmStatisticsTempDao.batchDeleteAlarmStatisticsTemp((String[]) any);
                result = 1;
            }
        };
        alarmStatistisTemplateService.deleteManyAlarmStatisticsTemp(new String[]{});
    }

    @Test
    public void queryAlarmStatisticsTempId() throws Exception {
        alarmStatistisTemplateService.queryAlarmStatisticsTempId("1");
    }

    @Test
    public void exportAlarmStatisticList() throws Exception {
        ExportDto exportDto = new ExportDto<>();
        Result result = alarmStatistisTemplateService.exportAlarmStatisticList(exportDto);
        Assert.assertTrue(result.getCode() == ResultCode.SUCCESS);
        new Expectations() {
            {
                alarmStatisticsExport.insertTask((ExportDto) any, anyString, anyString);
                result = new FilinkExportNoDataException();
            }
        };
        Result result2 = alarmStatistisTemplateService.exportAlarmStatisticList(exportDto);
        Assert.assertTrue(result2.getCode() == LogFunctionCodeConstant.EXPORT_NO_DATA);
        new Expectations() {
            {
                alarmStatisticsExport.insertTask((ExportDto) any, anyString, anyString);
                result = new FilinkExportTaskNumTooBigException();
            }
        };
        Result result3 = alarmStatistisTemplateService.exportAlarmStatisticList(exportDto);
        Assert.assertTrue(result3.getCode() == LogFunctionCodeConstant.EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS);
        new Expectations() {
            {
                alarmStatisticsExport.insertTask((ExportDto) any, anyString, anyString);
                result = new NullPointerException();
            }
        };
        Result result4 = alarmStatistisTemplateService.exportAlarmStatisticList(exportDto);
        Assert.assertTrue(result4.getCode() == LogFunctionCodeConstant.FAILED_TO_CREATE_EXPORT_TASK);
    }

    @Test
    public void deleteLog() {
        AlarmStatisticsTemp alarmStatisticsTemp = new AlarmStatisticsTemp();
        alarmStatisticsTemp.setId("1");
        new Expectations() {
            {
                alarmStatisticsTempDao.queryAlarmStatisticsTempByIds(anyString);
                result = alarmStatisticsTemp;
            }
        };
        String[] strings = new String[]{"1"};
        alarmStatistisTemplateService.deleteLog(strings, "1", "name");
    }

}