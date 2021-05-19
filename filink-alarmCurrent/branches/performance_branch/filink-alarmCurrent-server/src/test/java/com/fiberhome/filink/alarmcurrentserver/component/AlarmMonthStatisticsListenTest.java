package com.fiberhome.filink.alarmcurrentserver.component;

import com.fiberhome.filink.alarmcurrentserver.constant.AppConstant;
import com.fiberhome.filink.alarmcurrentserver.service.AlarmStatisticsService;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMockit.class)
public class AlarmMonthStatisticsListenTest {

    @Injectable
    private AlarmStatisticsService alarmStatisticsTempService;

    @Tested
    private AlarmMonthStatisticsListen alarmMonthStatisticsListen;
    @Test
    public void incrementalStatisticsListen() throws Exception {
        alarmMonthStatisticsListen.incrementalStatisticsListen(AppConstant.INCREMENTAL_MONTH_STATISTICSLISTEN);
    }

}