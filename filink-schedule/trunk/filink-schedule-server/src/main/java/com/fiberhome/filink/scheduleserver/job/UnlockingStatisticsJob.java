package com.fiberhome.filink.scheduleserver.job;

import com.fiberhome.filink.scheduleserver.stream.ScheduleStreams;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.time.LocalDateTime;

/**
 * @Author: zhaoliang
 * @Date: 2019/6/11 19:40
 * @Description: com.fiberhome.filink.scheduleserver.job
 * @version: 1.0
 */
@Slf4j
public class UnlockingStatisticsJob extends QuartzJobBean {

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
        String info = "每天计算设施开锁次数时间：" + LocalDateTime.now();
        log.info("Unlocking statistics...");
        Message msg = MessageBuilder.withPayload(info).build();
        //计算开锁次数 (使用消息推送)
        scheduleStreams.unlockingStatistics().send(msg);
    }
}
