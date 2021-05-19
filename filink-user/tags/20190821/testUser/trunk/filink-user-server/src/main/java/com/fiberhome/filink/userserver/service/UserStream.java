package com.fiberhome.filink.userserver.service;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/**
 * websocket接口
 * @author xuangong
 */
public interface UserStream {

    String WEBSOCKET_OUTPUT = "websocket_output";

    /**
     * websocket输出方法
     * @return MessageChannel
     */
    @Output(WEBSOCKET_OUTPUT)
    MessageChannel webSocketoutput();
}
