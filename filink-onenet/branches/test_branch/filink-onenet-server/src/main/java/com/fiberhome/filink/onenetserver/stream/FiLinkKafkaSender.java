package com.fiberhome.filink.onenetserver.stream;

import com.fiberhome.filink.bean.WebSocketMessage;
import com.fiberhome.filink.commonstation.entity.protocol.DeviceMsg;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * kafka消息发送类
 * @author CongcaiYu
 */
@Log4j
@Component
public class FiLinkKafkaSender {


    @Autowired
    private OneNetChannel oneNetChannel;


    /**
     * 发送消息到kafka
     * @param deviceMsg DeviceMsg
     */
    public void sendMsg(DeviceMsg deviceMsg){
        Message<DeviceMsg> message = MessageBuilder.withPayload(deviceMsg).build();
        oneNetChannel.oceanOutput().send(message);
    }

    /**
     * 发送告警消息
     * @param alarmMap 告警参数
     */
    public void sendAlarm(Map<String,Object> alarmMap){
        log.info("send alarm >>>>>>>>>>>>>>>>");
        Message<Map<String,Object>> message = MessageBuilder.withPayload(alarmMap).build();
        oneNetChannel.alarmOutput().send(message);
    }

    /**
     * 发送webSocket消息
     * @param webSocketMessage webSocket实体
     */
    public void sendWebSocket(WebSocketMessage webSocketMessage){
        Message<WebSocketMessage> message = MessageBuilder.withPayload(webSocketMessage).build();
        oneNetChannel.webSocketOutput().send(message);
    }

    /**
     * 发送告警图片
     * @param picMap 图片信息
     */
    public void sendAlarmPic(Map<String,String> picMap){
        Message<Map<String,String>> message = MessageBuilder.withPayload(picMap).build();
        oneNetChannel.alarmPicOutput().send(message);
    }

}
