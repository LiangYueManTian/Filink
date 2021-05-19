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
public class UserTaskJob extends QuartzJobBean{

    private static final String USER_TASK_INFO = "USER_INFO";

    @Autowired
    private ScheduleStreams scheduleStreams;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        Message msg = MessageBuilder.withPayload(USER_TASK_INFO).build();
        log.info("查询是否禁用用户");
        scheduleStreams.userForbiddenOutPut().send(msg);
    }
}
