package com.fiberhome.filink.lockserver.stream;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/**
 * OceanConnect输入输出通道定义
 *
 * @author CongcaiYu
 */
public interface OceanConnectChannel {


    /**
     * oceanConnect发送者
     */
    String OCEANCONNECT_SENDER = "oceanConnect_sender";


    /**
     * oceanConnect生产者
     *
     * @return MessageChannel
     */
    @Output(OCEANCONNECT_SENDER)
    MessageChannel oceanConnectOutput();


}
