package com.fiberhome.filink.alarmcurrentserver.stream;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/**
 * <p>
 * 输出输入通道定义
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-22
 */
public interface AlarmStreams {
    String WEBSOCKET_OUTPUT = "websocket_output";

    /**
     * 输入定义
     * @return 判断信息
     */
    @Output(WEBSOCKET_OUTPUT)
    MessageChannel webSocketoutput();
}
