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
 * 实现告警月定时任务job
 *
 * @author weikaun@wistronits.com
 * create on 2019-06-23 20:00
 */
@Slf4j
public class AlarmSourceIncrementalMonthJob extends QuartzJobBean {

    private static final String INCREMENTAL_EXPIRE_MONTH = "INCREMENTAL_EXPIRE_MONTH";

    @Autowired
    private ScheduleStreams scheduleStreams;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        Message msg = MessageBuilder.withPayload(INCREMENTAL_EXPIRE_MONTH).build();
        log.info("设施详情告警统计");
        scheduleStreams.alarmSourceIncrementalMonth().send(msg);
    }
}
