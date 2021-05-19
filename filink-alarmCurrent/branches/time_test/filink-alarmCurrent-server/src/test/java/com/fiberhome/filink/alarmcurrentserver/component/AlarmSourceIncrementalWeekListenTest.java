package com.fiberhome.filink.alarmcurrentserver.component;

import com.fiberhome.filink.alarmcurrentserver.service.impl.AlarmStatisticsServiceImpl;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMockit.class)
public class AlarmSourceIncrementalWeekListenTest {
    @Injectable
    private AlarmStatisticsServiceImpl alarmStatisticsTempService;
    @Tested
    private AlarmSourceIncrementalWeekListen alarmSourceIncrementalWeekListen;
    @Test
    public void incrementalSourceListen() throws Exception {
        alarmSourceIncrementalWeekListen.incrementalSourceListen("INCREMENTAL_EXPIRE_WEEK");
    }

}