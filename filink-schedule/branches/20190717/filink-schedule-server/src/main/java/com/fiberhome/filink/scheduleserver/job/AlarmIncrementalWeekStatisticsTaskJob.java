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
public class AlarmIncrementalWeekStatisticsTaskJob extends QuartzJobBean{

    private static final String INCREMENTAL_WEEK_STATISTICSLISTEN = "INCREMENTAL_WEEK_STATISTICSLISTEN";

    @Autowired
    private ScheduleStreams scheduleStreams;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        Message msg = MessageBuilder.withPayload(INCREMENTAL_WEEK_STATISTICSLISTEN).build();
        log.info("告警增量周统计");
        scheduleStreams.incrementalWeekOutput().send(msg);
    }
}
