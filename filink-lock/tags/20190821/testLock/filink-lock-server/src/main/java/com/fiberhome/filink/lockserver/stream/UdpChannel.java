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
     * UDP发送者
     */
    String UDP_SENDER = "udp_sender";


    /**
     * UDP生产者
     *
     * @return MessageChannel
     */
    @Output(UDP_SENDER)
    MessageChannel udpOutput();


}
