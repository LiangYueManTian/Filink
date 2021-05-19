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
public class DeleteDeviceSensorJob extends QuartzJobBean {
    /**
     * kafka通道
     */
    @Autowired
    private ScheduleStreams scheduleStreams;

    /**
     * 任务码 当前只有一个
     */
    private static final Integer DELETE_OVERDUE_DEVICE_SENSOR_TASK = 001;

    /**
     * 执行方法
     *
     * @param context
     */
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        Message msg = MessageBuilder.withPayload( DELETE_OVERDUE_DEVICE_SENSOR_TASK).build();
        log.info("Wake-up lock service to delete expired device sensor data");
        scheduleStreams.deleteDeviceSensor().send(msg);
    }
}
