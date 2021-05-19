package com.fiberhome.filink.scheduleserver.stream;

import org.springframework.cloud.stream.annotation.EnableBinding;

/**
 * 绑定通道
 *
 * @author yuanyao@wistronits.com
 * create on 2019/1/8 18:52
 */
@EnableBinding(ScheduleStreams.class)
public class ScheduleStreamsConfig {
}
