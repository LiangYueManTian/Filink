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
 * @author hedongwei@wistronits.com
 * create on 2019-01-23 20:00
 */
@Slf4j
public class ProcCastAlarmJob extends QuartzJobBean {

    @Autowired
    private ScheduleStreams scheduleStreams;

    /**
     * 执行方法
     *
     * @param context
     * @see #execute
     */
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        String info = "工单超时生成告警";
        log.info("工单超时生成告警!");
        Message msg = MessageBuilder.withPayload(info).build();
        //生成告警调用生成告警的方法 (使用消息推送)
        scheduleStreams.workflowBusinessOutput().send(msg);
    }
}
