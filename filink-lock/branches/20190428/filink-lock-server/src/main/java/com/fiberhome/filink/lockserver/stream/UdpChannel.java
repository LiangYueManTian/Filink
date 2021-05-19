package com.fiberhome.filink.lockserver.stream;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/**
 * udp输入输出通道定义
 *
 * @author CongcaiYu
 */
public interface UdpChannel {


    /**
     * 告警发送者
     */
    String UDP_SENDER = "udp_sender";


    /**
     * udp生产者
     *
     * @return MessageChannel
     */
    @Output(UDP_SENDER)
    MessageChannel udpOutput();


}
