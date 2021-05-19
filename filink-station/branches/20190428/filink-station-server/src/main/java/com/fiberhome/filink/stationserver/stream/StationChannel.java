package com.fiberhome.filink.stationserver.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

/**
 * 输出输入通道定义
 *
 * @author CongcaiYu
 */
public interface StationChannel {

    /**
     * 命令重发消费者
     */
    String STATION_CMD_RESEND = "station_cmd_resend";
    /**
     * udp指令接收
     */
    String STATION_UDP_INPUT = "station_udp_input";
    /**
     * 请求指令消费者
     */
    String STATION_REQUEST_INPUT = "station_request_input";
    /**
     * 告警发送者
     */
    String STATION_ALARM_OUTPUT = "station_alarm_output";
    /**
     * webSocket发送者
     */
    String WEB_SOCKET_OUTPUT = "web_socket_output";
    /**
     * udp指令发送
     */
    String STATION_UDP_OUTPUT = "station_udp_output";
    /**
     * 告警图片发送
     */
    String ALARM_PIC_OUTPUT = "alarm_pic_output";
    /**
     * 定时刷新设备升级文件
     */
    String UPGRADE_FILE_INPUT = "upgrade_file_input";
    /**
     * 设备升级文件消息消费者
     *
     * @return SubscribableChannel
     */
    @Input(UPGRADE_FILE_INPUT)
    SubscribableChannel upgradeFile();

    /**
     * udp消息消费者
     *
     * @return SubscribableChannel
     */
    @Input(STATION_UDP_INPUT)
    SubscribableChannel udpInput();

    /**
     * 命令重发消费者
     *
     * @return SubscribableChannel
     */
    @Input(STATION_CMD_RESEND)
    SubscribableChannel cmdResendInput();

    /**
     * 指令请求消费者
     *
     * @return SubscribableChannel
     */
    @Input(STATION_REQUEST_INPUT)
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
     * udp生产者
     *
     * @return MessageChannel
     */
    @Output(STATION_UDP_OUTPUT)
    MessageChannel udpOutput();

    /**
     * 告警生产者
     * @return MessageChannel
     */
    @Output(STATION_ALARM_OUTPUT)
    MessageChannel alarmOutput();

}
