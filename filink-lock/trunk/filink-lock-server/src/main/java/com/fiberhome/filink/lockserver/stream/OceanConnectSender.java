package com.fiberhome.filink.lockserver.stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

/**
 * <p>
 * kafka消息发送类
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/4/16
 */
@Component
public class OceanConnectSender {
    @Autowired
    private OceanConnectChannel oceanConnectChannel;

    /**
     * 发送消息
     *
     * @param listJson
     */
    public void send(String listJson) {
        Message<String> message = MessageBuilder.withPayload(listJson).build();
        oceanConnectChannel.oceanConnectOutput().send(message);

    }

}
