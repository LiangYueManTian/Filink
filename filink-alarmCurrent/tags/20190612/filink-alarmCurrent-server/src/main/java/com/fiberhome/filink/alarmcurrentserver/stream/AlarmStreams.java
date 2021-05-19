package com.fiberhome.filink.alarmcurrentserver.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

/**
 * <p>
 * 告警 websocket 输出输入通道定义
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-22
 */
public interface AlarmStreams {

    String WEBSOCKET_ALARM_OUTPUT = "websocket_alarm_output";

    String WEBSOCKET_ALARM_INPUT = "websocket_alarm_input";

    /**
     * 输出定义
     *
     * @return 判断信息
     */
    @Output(WEBSOCKET_ALARM_OUTPUT)
    MessageChannel webSocketoutput();

    /**
     * 输入定义
     *
     * @return 判断信息
     */
    @Input(WEBSOCKET_ALARM_INPUT)
    SubscribableChannel webSocketinput();


}
