package com.fiberhome.filink.lockserver.stream;


import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

/**
 * <p>
 *   主控 输入输出通道定义
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/7/2
 */

public interface DeviceSensorChannel {
    /**
     * 发送者
     */
    String DELETE_DEVICE_SENSOR = "delete_device_sensor";


    /**
     * 消息中间件监听通道
     * @return
     */
    @Input(DELETE_DEVICE_SENSOR)
    SubscribableChannel input();

}
