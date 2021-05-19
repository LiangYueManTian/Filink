package com.fiberhome.filink.lockserver.stream;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/**
 * oneNet输入输出通道定义
 *
 * @author CongcaiYu
 */
public interface OneNetChannel {


    /**
     * 告警发送者
     */
    String ONENET_SENDER = "oneNet_sender";


    /**
     * oneNet生产者
     *
     * @return MessageChannel
     */
    @Output(ONENET_SENDER)
    MessageChannel oneNetOutput();


}
