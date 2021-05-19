package com.fiberhome.filink.scheduleserver.job;

import com.fiberhome.filink.alarmcurrentapi.api.AlarmCurrentFeign;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.Date;

/**
 * 任务逻辑实现类
 * 需要实现Job接口
 *
 * @author taowei@wistronits.com
 * create on 2019-01-23 20:00
 */
@Slf4j
public class AlarmCurrentCastAalarmHistoryJob extends QuartzJobBean {

    @Autowired
    private AlarmCurrentFeign alarmCurrentFeign;

    /**
     * 执行方法
     * 每隔两个小时执行定时任务，定时任务的内容包含：
     * 1、根据历史告警设置时长和当前时间，计算出需要转到历史告警表的数据的告警清除时间的临界值
     * 2、查询出告警清除时间小于临界值的数据，从当前告警表清除，同时在历史告警表新增
     * @param context
     */
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        //alarmCurrentFeign.alarmCurrentCastAlarmHistoryTaskFeign();
        log.info("执行了当前告警转历史告警的定时任务--："+new Date());
    }

}
