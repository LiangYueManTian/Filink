package com.fiberhome.filink.fdevice.consume;

import com.fiberhome.filink.fdevice.service.device.DeviceInfoService;
import com.fiberhome.filink.fdevice.service.devicelog.DeviceLogService;
import com.fiberhome.filink.fdevice.stream.DeviceStreams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @Author: zhaoliang
 * @Date: 2019/6/11 20:30
 * @Description: com.fiberhome.filink.fdevice.consume
 * @version: 1.0
 */
@Component
@Slf4j
public class UnlockingStatisticsConsume {
    /**
     * 删除设施关联信息重试
     */
    private static final Integer RETRY_DEVICE_DELETE = 0;

    @Autowired
    private DeviceLogService deviceLogService;
    @Autowired
    private DeviceInfoService deviceInfoService;

    @StreamListener(DeviceStreams.UNLOCKING_STATISTICS_INPUT)
    public void unlockingStatisticsConsume(String msg) {
        if (!StringUtils.isEmpty(msg)) {
            //调用计算开锁次数的方法
            log.info(msg);
            deviceLogService.synchronizeUnlockingStatistics();
        }
    }

    @StreamListener(DeviceStreams.TIMED_TASK_INPUT)
    public void deviceTimedTaskConsume(int code) {
        if (RETRY_DEVICE_DELETE == code) {
            deviceInfoService.retryDelete();
            log.info("Kafka received the message and is re-deleting the facility association information");
        }
    }
}
