package com.fiberhome.filink.alarmcurrentserver.component;

import com.fiberhome.filink.alarmcurrentserver.constant.AlarmCurrent18n;
import com.fiberhome.filink.alarmcurrentserver.service.AlarmCurrentExportService;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMockit.class)
public class AlarmCleanStatusListenTest {
    @Injectable
    private AlarmCurrentExportService alarmCurrentExportService;
    @Tested
    private AlarmCleanStatusListen alarmCleanStatusListen;
    @Test
    public void incrementalStatisticsListen() throws Exception {
        alarmCleanStatusListen.incrementalStatisticsListen(AlarmCurrent18n.ALARM_CLEAN_STATUS);
    }

}