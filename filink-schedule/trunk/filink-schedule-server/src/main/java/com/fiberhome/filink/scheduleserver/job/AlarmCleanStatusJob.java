package com.fiberhome.filink.scheduleserver.job;

import com.fiberhome.filink.scheduleserver.stream.ScheduleStreams;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * 设施详情告警统计
 *
 * @author weikaun@wistronits.com
 * create on 2019-06-28 10:00
 */
@Slf4j
public class AlarmCleanStatusJob extends QuartzJobBean {

    private static final String ALARM_CLEAN_STATUS = "ALARM_CLEAN_STATUS";

    @Autowired
    private ScheduleStreams scheduleStreams;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {

    }
}
