package com.fiberhome.filink.fdevice.stream;

import com.fiberhome.filink.fdevice.constant.device.DeviceConstant;
import com.fiberhome.filink.fdevice.service.device.DeviceInfoService;
import com.fiberhome.filink.redis.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

/**
 * DeviceStreams消费者
 * @author chaofang@wistronits.com
 * @since  2019/5/15
 */
@Slf4j
@Component
public class DeviceConsumer {

    @Autowired
    private DeviceInfoService deviceInfoService;
    /**
     * 定时刷新首页设施Redis缓存信息
     */
    private static final String HOME_DEVICE_INFO_REDIS = "HOME_DEVICE_INFO_REDIS";
    /**
     * 监听消息
     *
     * @param msg 消息
     */
    @StreamListener(DeviceStreams.HOME_DEVICE_INPUT)
    public void exportConsumer(String msg) {
        if (HOME_DEVICE_INFO_REDIS.equals(msg)) {
            log.info("to refresh home device info redis****");
            // 判断Redis缓存
            if (RedisUtils.hasKey(DeviceConstant.DEVICE_GIS_MAP)) {
                // Redis有缓存信息，删除缓存
                RedisUtils.remove(DeviceConstant.DEVICE_GIS_MAP);
            }
            deviceInfoService.queryDeviceAreaAll();
        }
    }
}
