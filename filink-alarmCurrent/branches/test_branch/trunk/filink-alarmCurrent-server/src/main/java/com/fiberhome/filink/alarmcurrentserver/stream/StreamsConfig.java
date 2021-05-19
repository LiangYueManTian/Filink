package com.fiberhome.filink.alarmcurrentserver.stream;

import org.springframework.cloud.stream.annotation.EnableBinding;

/**
 * 绑定通道
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-22
 */
@EnableBinding(AlarmStreams.class)
public class StreamsConfig {
}
