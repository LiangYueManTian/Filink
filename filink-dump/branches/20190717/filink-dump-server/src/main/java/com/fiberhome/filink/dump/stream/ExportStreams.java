package com.fiberhome.filink.dump.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

/**
 * 输出输入通道定义
 *
 * @author yuanyao@wistronits.com
 * create on 2019/1/8 18:04
 */
public interface ExportStreams {
    /**
     * webSocket输出通道
     */
    String EXPORT_WEB_SOCKET_OUTPUT = "websocket_output";
    /**
     * 消息中间件监听通道
     */
    String EXPORT_INPUT="filink_export_log";

    /**
     * webSocket输出通道
     * @return
     */
    @Output(EXPORT_WEB_SOCKET_OUTPUT)
    MessageChannel exportWebSocketOutput();

    /**
     * 消息中间件监听通道
     * @return
     */
    @Input(EXPORT_INPUT)
    SubscribableChannel input();

}
