package com.fiberhome.filink.fdevice.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/**
 * 输出输入通道定义
 *
 * @Author: zl
 * @Date: 2019/4/8 10:43
 * @Description: com.fiberhome.filink.fdevice.stream
 * @version: 1.0
 */
public interface DeviceStreams {
    /**
     * 设施信息输出通道
     */
    String DEVICE_WEBSOCKET_OUTPUT = "device_websocket_output";

    /**
     * 更新用户信息输出通道
     */
    String UPDATE_USER_INFO = "update_userInfo";
    /**
     * 刷新首页设施Redis缓存信息
     */
    String HOME_DEVICE_INPUT = "home_device_input";

    /**
     * webSocket输出通道
     *
     * @return 返回结果
     */
    @Output(DEVICE_WEBSOCKET_OUTPUT)
    MessageChannel deviceWebSocketOutput();
    /**
     * kafka用户更新输出通道
     *
     * @return 返回结果
     */
    @Output(UPDATE_USER_INFO)
    MessageChannel updateUserInfo();
    /**
     * 刷新首页设施Redis缓存信息输入通道
     */
    @Input(HOME_DEVICE_INPUT)
    MessageChannel homeDeviceRedisRefresh();
}
