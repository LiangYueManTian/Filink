package com.fiberhome.filink.alarmcurrentserver.exception;

import mockit.Injectable;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMockit.class)
public class AlarmCurrentHandlerTest {
    @Injectable
    private AlarmCurrentHandler alarmCurrentHandler;
    @Test
    public void handlerAlarmCurrentExceptionTest() {
        alarmCurrentHandler.handlerAlarmCurrentException(null);
    }
}