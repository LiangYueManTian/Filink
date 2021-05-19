package com.fiberhome.filink.alarmcurrentserver.component;

import com.fiberhome.filink.alarmcurrentserver.service.impl.AlarmStatisticsServiceImpl;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMockit.class)
public class AlarmSourceIncrementalMonthListenTest {

    @Injectable
    private AlarmStatisticsServiceImpl alarmStatisticsTempService;
    @Tested
    private AlarmSourceIncrementalMonthListen alarmSourceIncrementalMonthListen;
    @Test
    public void incrementalSourceListen() throws Exception {
        alarmSourceIncrementalMonthListen.incrementalSourceListen("INCREMENTAL_EXPIRE_MONTH");
    }

}