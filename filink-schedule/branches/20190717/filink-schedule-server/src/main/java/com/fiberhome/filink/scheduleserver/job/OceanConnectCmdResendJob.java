package com.fiberhome.filink.scheduleserver.job;

import com.fiberhome.filink.scheduleserver.stream.ScheduleStreams;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * oceanConnect指令重发定时任务
 * @author CongcaiYu
 */
public class OceanConnectCmdResendJob extends QuartzJobBean {

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
        String info = "execute ocean connect resend";
        Message msg = MessageBuilder.withPayload(info).build();
        scheduleStreams.oceanConnectCmdResendOutput().send(msg);
    }
}
