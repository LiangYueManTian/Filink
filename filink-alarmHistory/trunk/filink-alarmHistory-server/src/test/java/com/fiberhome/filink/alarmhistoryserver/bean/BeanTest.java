package com.fiberhome.filink.alarmhistoryserver.bean;

import com.fiberhome.filink.server_common.utils.I18nUtils;
import mockit.Expectations;
import mockit.integration.junit4.JMockit;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMockit.class)
public class BeanTest {


    @Test
    public void AlarmDepartmentTest() {
        AlarmDepartment alarmDepartment = new AlarmDepartment();
        AlarmDepartment alarmDepartment1 = new AlarmDepartment();
        alarmDepartment.setResponsibleDepartmentId(alarmDepartment.getResponsibleDepartmentId());
        alarmDepartment.setResponsibleDepartment(alarmDepartment.getResponsibleDepartment());
        Assert.assertNotNull(alarmDepartment);
        Assert.assertNotNull(alarmDepartment1);
    }

    @Test
    public void AlarmHistoryTest() {
        new Expectations(I18nUtils.class) {
            {
                I18nUtils.getString(anyString, anyString);
                result = "test";
            }
        };
        AlarmHistory alarmHistory = new AlarmHistory();
        String[] device = {"001", "030", "060", "090", "210"};
        String result;
        for (int i = 1; i < 5; i++) {
            alarmHistory.setAlarmFixedLevel(String.valueOf(i));
            result = alarmHistory.getTranslationAlarmFixedLevel();
            Assert.assertNotNull(result);
            alarmHistory.setAlarmSourceTypeId(device[i]);
            result = alarmHistory.getTranslationAlarmSourceTypeId();
            Assert.assertNotNull(result);
        }
        alarmHistory.setAlarmSourceTypeId(device[0]);
        result = alarmHistory.getTranslationAlarmSourceTypeId();
        Assert.assertNotNull(result);
        alarmHistory.setAlarmBeginTime(2195969589000L);
        result = alarmHistory.getTranslationAlarmContinousTime();
        Assert.assertTrue(StringUtils.isEmpty(result));
        alarmHistory.setAlarmBeginTime(1564817589000L);
        alarmHistory.setAlarmCleanTime(1564817589000L);
        result = alarmHistory.getTranslationAlarmContinousTime();
        Assert.assertNotNull(result);
        alarmHistory.setAlarmCleanTime(1596439989000L);
        result = alarmHistory.getTranslationAlarmContinousTime();
        Assert.assertNotNull(result);
        alarmHistory.setAlarmCleanTime(1575358389000L);
        result = alarmHistory.getTranslationAlarmContinousTime();
        Assert.assertNotNull(result);
        alarmHistory.setAlarmCleanTime(1565249589000L);
        result = alarmHistory.getTranslationAlarmContinousTime();
        Assert.assertNotNull(result);
        alarmHistory.setAlarmCleanTime(1564835589000L);
        result = alarmHistory.getTranslationAlarmContinousTime();
        Assert.assertNotNull(result);
    }
}
