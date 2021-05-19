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
     * 告警发送者
     */
    String OCEANCONNECT_SENDER = "oceanConnect_sender";


    /**
     * oneNet生产者
     *
     * @return MessageChannel
     */
    @Output(OCEANCONNECT_SENDER)
    MessageChannel oceanConnectOutput();


}
