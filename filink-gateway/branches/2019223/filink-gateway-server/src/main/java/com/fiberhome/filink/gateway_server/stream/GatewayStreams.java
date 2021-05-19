package com.fiberhome.filink.gateway_server.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

/**
 * geteway channel 通道
 *
 * @author yuanyao@wistronits.com
 * create on 2019-01-26 14:37
 */
public interface GatewayStreams {


    /**
     * webSocket监听通道
     */
    String WEBSOCKET_INPUT = "websocket_input";

    /**
     * WebSocket监听
     * @return
     */
    @Input(WEBSOCKET_INPUT)
    SubscribableChannel webSocketInput();
}

