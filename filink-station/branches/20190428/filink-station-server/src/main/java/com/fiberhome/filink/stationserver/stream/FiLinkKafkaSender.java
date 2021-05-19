package com.fiberhome.filink.stationserver.stream;

import com.fiberhome.filink.bean.WebSocketMessage;
import com.fiberhome.filink.commonstation.entity.protocol.DeviceMsg;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * kafka消息发送类
 * @author CongcaiYu
 */
@Log4j
@Component
public class FiLinkKafkaSender {


    @Autowired
    private StationChannel stationChannel;


    /**
     * 发送udp消息
     * @param deviceMsg DeviceMsg
     */
    public void sendUdp(DeviceMsg deviceMsg){
        Message<DeviceMsg> message = MessageBuilder.withPayload(deviceMsg).build();
        stationChannel.udpOutput().send(message);
    }

    /**
     * 发送告警消息
     * @param alarmMap 告警参数
     */
    public void sendAlarm(Map<String,Object> alarmMap){
        log.info("send alarm >>>>>>>>>>>>>>>>");
        Message<Map<String,Object>> message = MessageBuilder.withPayload(alarmMap).build();
        //TODO 以下时间log未实验代码，后期删除
        SimpleDateFormat ft=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date nowDate=new Date();
        log.info("发送告警信息给告警服务，时间："+ft.format(nowDate));
        //TODO 以上时间log未实验代码，后期删除
        stationChannel.alarmOutput().send(message);
    }

    /**
     * 发送webSocket消息
     * @param webSocketMessage webSocket实体
     */
    public void sendWebSocket(WebSocketMessage webSocketMessage){
        Message<WebSocketMessage> message = MessageBuilder.withPayload(webSocketMessage).build();
        stationChannel.webSocketOutput().send(message);
    }

    /**
     * 发送告警图片
     * @param picMap 图片信息
     */
    public void sendAlarmPic(Map<String,String> picMap){
        Message<Map<String,String>> message = MessageBuilder.withPayload(picMap).build();
        //TODO 以下时间log未实验代码，后期删除
        SimpleDateFormat ft=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date nowDate=new Date();
        log.info("发送告警图片给告警服务，时间："+ft.format(nowDate));
        //TODO 以上时间log未实验代码，后期删除
        stationChannel.alarmPicOutput().send(message);
    }

}
