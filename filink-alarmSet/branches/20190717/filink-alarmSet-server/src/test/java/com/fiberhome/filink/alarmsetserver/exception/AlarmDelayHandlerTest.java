package com.fiberhome.filink.alarmsetserver.exception;

import mockit.Injectable;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMockit.class)
public class AlarmDelayHandlerTest {

    @Injectable
    private AlarmDelayHandler alarmDelayHandler;
    @Test
    public void handlerAlarmCurrentExceptionTest() {
        alarmDelayHandler.handlerAlarmCurrentException(null);
    }
}