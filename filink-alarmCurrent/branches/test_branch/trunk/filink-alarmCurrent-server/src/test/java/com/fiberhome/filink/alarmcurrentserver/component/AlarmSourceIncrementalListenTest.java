package com.fiberhome.filink.alarmcurrentserver.component;

import com.fiberhome.filink.alarmcurrentserver.service.impl.AlarmStatisticsServiceImpl;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMockit.class)
public class AlarmSourceIncrementalListenTest {

    @Injectable
    private AlarmStatisticsServiceImpl alarmStatisticsService;

    @Tested
    private AlarmSourceIncrementalListen alarmSourceIncrementalListen;
    @Test
    public void incrementalSourceListen() throws Exception {
        alarmSourceIncrementalListen.incrementalSourceListen("INCREMENTAL_EXPIRE");
    }

}