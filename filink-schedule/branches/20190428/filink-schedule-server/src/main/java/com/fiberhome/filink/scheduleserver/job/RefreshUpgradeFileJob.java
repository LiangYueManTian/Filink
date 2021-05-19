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
 * 定时刷新设备升级文件
 * @author chaofang@wistronits.com
 * @since  2019/6/11
 */
@Slf4j
public class RefreshUpgradeFileJob extends QuartzJobBean{
    /**
     * 定时刷新设备升级文件
     */
    private static final String REFRESH_UPGRADE_FILE = "REFRESH_UPGRADE_FILE";

    @Autowired
    private ScheduleStreams scheduleStreams;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        Message msg = MessageBuilder.withPayload(REFRESH_UPGRADE_FILE).build();
        log.info("send refresh upgrade file****");
        scheduleStreams.refreshUpgradeFile().send(msg);
    }
}
