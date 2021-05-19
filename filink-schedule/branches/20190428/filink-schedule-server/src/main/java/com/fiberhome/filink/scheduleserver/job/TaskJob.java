package com.fiberhome.filink.scheduleserver.job;

import com.fiberhome.filink.scheduleserver.service.impl.JobServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 任务逻辑实现类demo
 * 需要实现Job接口
 *
 * @author yuanyao@wistronits.com
 * create on 2019-01-23 20:00
 */
@Slf4j
public class TaskJob extends QuartzJobBean {


    /**
     * 执行方法
     *
     * @param context
     * @see #execute
     */
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {

        JobDataMap mergedJobDataMap = context.getMergedJobDataMap();
        // 拿到传入的数据 根据需要强转
        Object o = mergedJobDataMap.get(JobServiceImpl.DATA_KEY);
        log.info("任务执行完成 ：" + LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        System.out.println(o.toString());
    }
}
