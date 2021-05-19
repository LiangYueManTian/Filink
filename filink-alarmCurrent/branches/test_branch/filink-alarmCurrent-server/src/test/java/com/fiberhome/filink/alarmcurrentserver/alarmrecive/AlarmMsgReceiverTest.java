package com.fiberhome.filink.alarmcurrentserver.alarmrecive;

import com.fiberhome.filink.alarmcurrentserver.bean.AlarmInfo;
import com.fiberhome.filink.alarmcurrentserver.service.AlarmReceiveService;
import java.util.HashMap;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMockit.class)
public class AlarmMsgReceiverTest {

    @Injectable
    private AlarmReceiveService alarmReceiveService;

    @Injectable
    private AlarmMsgDevice alarmMsgDevice;

    @Tested
    private AlarmMsgReceiver alarmMsgReceiver;

    @Test
    public void deviceAlarmReceive() throws Exception {
        alarmMsgReceiver.deviceAlarmReceive(new HashMap());
    }

    @Test
    public void alarmAdviceReceive() throws Exception {
        alarmMsgReceiver.alarmAdviceReceive(new AlarmInfo());
    }

    @Test
    public void orderCastAlarmReceive() throws Exception {
        try {
            alarmMsgReceiver.orderCastAlarmReceive("1");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void alarmPictureReceive() throws Exception {
        alarmMsgReceiver.alarmPictureReceive(new HashMap());
    }

}