package com.fiberhome.filink.scheduleserver.job;

import com.fiberhome.filink.scheduleserver.stream.ScheduleStreams;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.quartz.QuartzJobBean;

@Slf4j
public class UnLockUserTaskJob extends QuartzJobBean{

    private static final String UNLOCK_USER = "UNLOCK_USER";

    @Autowired
    private ScheduleStreams scheduleStreams;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        Message msg = MessageBuilder.withPayload(UNLOCK_USER).build();
        log.info("查询是否解锁用户信息");
        scheduleStreams.unlockUser().send(msg);
    }
}
