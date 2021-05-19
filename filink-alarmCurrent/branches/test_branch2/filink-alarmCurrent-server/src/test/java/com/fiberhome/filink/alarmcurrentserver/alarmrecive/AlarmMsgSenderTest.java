package com.fiberhome.filink.alarmcurrentserver.alarmrecive;

import com.fiberhome.filink.alarmcurrentserver.bean.AlarmCurrentInfo;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMockit.class)
public class AlarmMsgSenderTest {

    @Injectable
    private AlarmChannel alarmChannel;

    @Tested
    private AlarmMsgSender alarmMsgSender;

    @Test
    public void send() throws Exception {
        alarmMsgSender.send(new HashMap());
    }

    @Test
    public void sendPicture() throws Exception {
        alarmMsgSender.sendPicture(new HashMap());
    }

    @Test
    public void sendAdvice() throws Exception {
        alarmMsgSender.sendAdvice(new AlarmInfo());
    }

    @Test
    public void orderCastAlarm() throws Exception {
        List<AlarmCurrentInfo> alarmCurrentInfos = new ArrayList<>();
        alarmMsgSender.orderCastAlarm(alarmCurrentInfos);
    }

}