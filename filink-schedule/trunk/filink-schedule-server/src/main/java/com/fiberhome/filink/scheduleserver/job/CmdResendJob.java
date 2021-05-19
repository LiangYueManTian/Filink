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
 * 设施指令重发定时任务
 * @author CongcaiYu
 */
@Slf4j
public class CmdResendJob extends QuartzJobBean {

    @Autowired
    private ScheduleStreams scheduleStreams;

    /**
     * 任务执行逻辑
     *
     * @param jobExecutionContext 任务参数
     * @throws JobExecutionException 异常信息
     */
    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String info = "execute resend";
        Message msg = MessageBuilder.withPayload(info).build();
        scheduleStreams.cmdResendOutput().send(msg);
    }
}
