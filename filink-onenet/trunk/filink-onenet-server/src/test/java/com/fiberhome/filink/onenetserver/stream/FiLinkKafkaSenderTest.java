package com.fiberhome.filink.onenetserver.stream;

import com.fiberhome.filink.bean.WebSocketMessage;
import com.fiberhome.filink.commonstation.entity.protocol.DeviceMsg;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

@RunWith(JMockit.class)
public class FiLinkKafkaSenderTest {
    /**测试对象 FiLinkKafkaSender*/
    @Tested
    private FiLinkKafkaSender kafkaSender;
    /**Mock OneNetChannel*/
    @Injectable
    private OneNetChannel oneNetChannel;

    /**
     * sendMsg
     */
    @Test
    public void sendMsgTest() {
        DeviceMsg deviceMsg = new DeviceMsg();
        kafkaSender.sendMsg(deviceMsg);
    }

    /**
     * sendAlarm
     */
    @Test
    public void sendAlarmTest() {
        Map<String,Object> alarmMap = new HashMap<>(16);
        kafkaSender.sendAlarm(alarmMap);
    }

    /**
     * sendWebSocket
     */
    @Test
    public void sendWebSocketTest() {
        WebSocketMessage webSocketMessage = new WebSocketMessage();
        kafkaSender.sendWebSocket(webSocketMessage);
    }

    /**
     * sendAlarmPic
     */
    @Test
    public void sendAlarmPicTest() {
        Map<String,String> picMap = new HashMap<>(16);
        kafkaSender.sendAlarmPic(picMap);
    }
}
