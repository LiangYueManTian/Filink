package com.fiberhome.filink.filinkoceanconnectserver.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

/**
 * 输出输入通道定义
 *
 * @author CongcaiYu
 */
public interface OceanConnectChannel {

    /**
     * 命令重发消费者
     */
    String OCEAN_CMD_RESEND = "ocean_cmd_resend";
    /**
     * 内部指令接收
     */
    String OCEAN_CONNECT_INPUT = "ocean_connect_input";
    /**
     * 请求指令消费者
     */
    String OCEAN_REQUEST_INPUT = "ocean_request_input";
    /**
     * 告警发送者
     */
    String OCEAN_ALARM_OUTPUT = "station_alarm_output";
    /**
     * webSocket发送者
     */
    String WEB_SOCKET_OUTPUT = "web_socket_output";
    /**
     * 内部指令发送
     */
    String OCEAN_CONNECT_OUTPUT = "ocean_connect_output";
    /**
     * 告警图片发送
     */
    String ALARM_PIC_OUTPUT = "alarm_pic_output";

    /**
     * 定时刷新设备升级文件
     */
    String UPGRADE_FILE_INPUT = "upgrade_file_input";

    /**
     * 清除主控redis信息
     */
    String OCEAN_CONTROL_CLEAR = "ocean_control_clear";

    /**
     * 清除redis主控信息
     *
     * @return SubscribableChannel
     */
    @Input(OCEAN_CONTROL_CLEAR)
    SubscribableChannel clearControl();

    /**
     * 设备升级文件消息消费者
     *
     * @return SubscribableChannel
     */
    @Input(UPGRADE_FILE_INPUT)
    SubscribableChannel upgradeFile();
    /**
     * 内部指令接收
     *
     * @return SubscribableChannel
     */
    @Input(OCEAN_CONNECT_INPUT)
    SubscribableChannel oceanInput();

    /**
     * 命令重发消费者
     *
     * @return SubscribableChannel
     */
    @Input(OCEAN_CMD_RESEND)
    SubscribableChannel cmdResendInput();

    /**
     * 指令请求消费者
     *
     * @return SubscribableChannel
     */
    @Input(OCEAN_REQUEST_INPUT)
    SubscribableChannel requestInput();

    /**
     * websocket发送者
     *
     * @return SubscribableChannel
     */
    @Output(WEB_SOCKET_OUTPUT)
    MessageChannel webSocketOutput();

    /**
     * 告警图片发送
     *
     * @return SubscribableChannel
     */
    @Output(ALARM_PIC_OUTPUT)
    MessageChannel alarmPicOutput();

    /**
     * 内部指令发送者
     *
     * @return MessageChannel
     */
    @Output(OCEAN_CONNECT_OUTPUT)
    MessageChannel oceanOutput();

    /**
     * 告警生产者
     *
     * @return MessageChannel
     */
    @Output(OCEAN_ALARM_OUTPUT)
    MessageChannel alarmOutput();

}
