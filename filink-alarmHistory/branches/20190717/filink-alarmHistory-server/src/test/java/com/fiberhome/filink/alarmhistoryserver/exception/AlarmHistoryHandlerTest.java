package com.fiberhome.filink.alarmhistoryserver.exception;

import mockit.Injectable;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMockit.class)
public class AlarmHistoryHandlerTest {

    @Injectable
    private AlarmHistoryHandler alarmHistoryHandler;
    @Test
    public void handlerAlarmCurrentExceptionTest() {
        alarmHistoryHandler.handlerAlarmCurrentException(null);
    }
}