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
 * 任务逻辑实现类demo
 * 需要实现Job接口
 *
 * @author yuanyao@wistronits.com
 * create on 2019-01-23 20:00
 */
@Slf4j
public class RetryDeleteDeviceJob extends QuartzJobBean {
    /**
     * kafka通道
     */
    @Autowired
    private ScheduleStreams scheduleStreams;


    /**
     * 删除设施关联信息重试
     */
    private static final Integer RETRY_DEVICE_DELETE = 0;

    /**
     * 执行方法
     *
     * @param context
     */
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        Message msg = MessageBuilder.withPayload(RETRY_DEVICE_DELETE).build();
        log.info("Wake-up device service to retry delete data");
        scheduleStreams.deviceTimedTask().send(msg);
    }

}
