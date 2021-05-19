package com.fiberhome.filink.stationserver.stream;

import com.fiberhome.filink.stationserver.entity.protocol.DeviceMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

/**
 * kafka消息发送类
 * @author CongcaiYu
 */
@Component
public class FiLinkUdpMsgSender {

    @Autowired
    private StationChannel stationChannel;

    /**
     * 发送消息
     *
     * @param deviceMsg DeviceMsg
     */
    public void send(DeviceMsg deviceMsg){
        Message<DeviceMsg> message = MessageBuilder.withPayload(deviceMsg).build();
        stationChannel.output().send(message);
    }
}
