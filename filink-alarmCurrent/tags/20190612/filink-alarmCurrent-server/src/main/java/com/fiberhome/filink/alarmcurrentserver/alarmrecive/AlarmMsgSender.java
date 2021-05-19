package com.fiberhome.filink.alarmcurrentserver.alarmrecive;


import com.alibaba.fastjson.JSONArray;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmCurrentInfo;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmInfo;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

/**
 * kafka消息发送类
 *
 * @author TAOWEI
 */
@Component
public class AlarmMsgSender {

    /**
     * 告警输出输入通道定义
     */
    @Autowired
    private AlarmChannel alarmChannel;

    /**
     * 告警接收,kafka不能发送 list 集合的数据,只能是 map 或者实体类,list 需要转换成 json
     *
     * @param alarmMsgMap 告警消息
     */
    public void send(Map alarmMsgMap) {
        Message<Map> message = MessageBuilder.withPayload(alarmMsgMap).build();
        alarmChannel.output().send(message);
    }

    /**
     * 告警图片
     *
     * @param alarmMsgMap 告警消息
     */
    public void sendPicture(Map alarmMsgMap) {
        Message<Map> message = MessageBuilder.withPayload(alarmMsgMap).build();
        alarmChannel.picOutput().send(message);
    }

    /**
     * 告警推送，通知，转工单发送消息
     *
     * @param alarmInfo 告警消息
     */
    public void sendAdvice(AlarmInfo alarmInfo) {
        Message<AlarmInfo> message = MessageBuilder.withPayload(alarmInfo).build();
        alarmChannel.adviceOutput().send(message);
    }


    /**
     * 工单转告警,list集合必须转为JSON
     *
     * @param alarmCurrentInfo 工单消息
     */
    public void orderCastAlarm(List<AlarmCurrentInfo> alarmCurrentInfo) {
        Message<String> message = MessageBuilder.withPayload(JSONArray.toJSONString(alarmCurrentInfo)).build();
        alarmChannel.orderOutput().send(message);
    }
}
