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
 * @since 2019/7/2
 */
@Component
public class ControlSender {
    @Autowired
    private ControlChannel controlChannel;

    /**
     * 发送消息
     *
     * @param listJson 参数
     */
    public void send(String listJson) {
        Message<String> message = MessageBuilder.withPayload(listJson).build();
        controlChannel.controlOutput().send(message);
    }
}
