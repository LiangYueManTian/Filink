package com.fiberhome.filink.lockserver.stream;

import com.fiberhome.filink.lockserver.service.StatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

/**
 * 任务中心kafka消费通道
 * @Author: qiqizhu@wistronits.com
 * Date:2019/7/17
 */
@Slf4j
@Component
public class DeviceSensorConsumer {
    /**
     * 自动注入导出服务
     */
    @Autowired
    private StatisticsService statisticsService;
    /**
     * 删除过期数据消息码
     */
    private static final int DELETE_OVERDUE_DATA = 1;
    /**
     * 监听消息
     *
     * @param code 消息码
     */
    @StreamListener(DeviceSensorChannel.DELETE_DEVICE_SENSOR)
    public void exportConsumer(int code) {
        if (code == DELETE_OVERDUE_DATA) {
            log.info("Deleting expired data");
            statisticsService.deleteExpiredData();
        }
    }
}
