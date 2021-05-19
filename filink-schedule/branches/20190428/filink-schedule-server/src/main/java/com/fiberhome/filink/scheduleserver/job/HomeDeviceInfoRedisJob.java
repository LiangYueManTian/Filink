package com.fiberhome.filink.scheduleserver.job;

import com.fiberhome.filink.scheduleserver.stream.ScheduleStreams;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.quartz.QuartzJobBean;
/**
 * 定时刷新首页设施Redis缓存信息
 * @author chaofang@wistronits.com
 * @since  2019/5/15
 */
@Slf4j
public class HomeDeviceInfoRedisJob extends QuartzJobBean{
    /**
     * 定时刷新首页设施Redis缓存信息
     */
    private static final String HOME_DEVICE_INFO_REDIS = "HOME_DEVICE_INFO_REDIS";

    @Autowired
    private ScheduleStreams scheduleStreams;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        Message msg = MessageBuilder.withPayload(HOME_DEVICE_INFO_REDIS).build();
        log.info("send refresh home device info redis****");
        scheduleStreams.homeDeviceOutput().send(msg);
    }
}
