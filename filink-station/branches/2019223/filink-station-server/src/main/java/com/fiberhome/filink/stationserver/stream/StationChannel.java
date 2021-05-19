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

    String STATION_UDP_INPUT = "station_udp_input";

    String STATION_UDP_OUTPUT = "station_udp_output";

    /**
     * 输入流
     *
     * @return SubscribableChannel
     */
    @Input(STATION_UDP_INPUT)
    SubscribableChannel input();

    /**
     * 输出流
     *
     * @return MessageChannel
     */
    @Output(STATION_UDP_OUTPUT)
    MessageChannel output();
}
