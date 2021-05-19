package com.fiberhome.filink.schedule_server.service.impl;

import com.fiberhome.filink.schedule_server.bean.TaskInfo;
import com.fiberhome.filink.schedule_server.enums.JobCronEnum;
import com.fiberhome.filink.schedule_server.enums.JobGroupEnum;
import com.fiberhome.filink.schedule_server.service.JobService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * 定时任务管理实现类
 *
 * @author yuanyao@wistronits.com
 * create on 2019-01-24 17:02
 */
@Slf4j
@Service
public class JobServiceImpl implements JobService {


    public static final String DATA_KEY = "data";

    @Autowired
    private Scheduler filinkScheduler;

    /**
     * 查询任务列表
     *
     * @return
     */
    @Override
    public List<TaskInfo> queryTasklist() {
        List<TaskInfo> list = new ArrayList<>();

        try {
            for (String groupJob : filinkScheduler.getJobGroupNames()) {
                for (JobKey jobKey : filinkScheduler.getJobKeys(GroupMatcher.<JobKey>groupEquals(groupJob))) {
                    List<? extends Trigger> triggers = filinkScheduler.getTriggersOfJob(jobKey);
                    for (Trigger trigger : triggers) {
                        Trigger.TriggerState triggerState = filinkScheduler.getTriggerState(trigger.getKey());
                        JobDetail jobDetail = filinkScheduler.getJobDetail(jobKey);

                        String cronExpression = "";

                        if (trigger instanceof CronTrigger) {
                            CronTrigger cronTrigger = (CronTrigger) trigger;
                            cronExpression = cronTrigger.getCronExpression();
//                            createTime = cronTrigger.getDescription();
                        }
                        TaskInfo info = new TaskInfo();
                        info.setJobName(jobKey.getName());
                        info.setJobGroup(JobGroupEnum.getJobGroupEnumByStr(jobKey.getGroup()));
                        info.setJobDesc(jobDetail.getDescription());
                        info.setCron(JobCronEnum.getEnumByStr(cronExpression));
                        list.add(info);
                    }
                }
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * 新增任务
     *
     * @param quartzTask 任务信息
     */
    @Override
    public void addJob(TaskInfo quartzTask) throws SchedulerException {
        String jobName = quartzTask.getJobName();
        String groupName = quartzTask.getJobGroup().getGroupName();
        String triggerGroupName = quartzTask.getTriggerGroup().getGroupName();
        Object data = quartzTask.getData();
        String jobDesc = quartzTask.getJobDesc();
        String triggerName = quartzTask.getTriggerName();
        String cron = quartzTask.getCron().getCron();

        if (checkExists(jobName, quartzTask.getJobGroup())) {
            log.info("任务以及存在，不需要重复创建");
        }

        // TODO: 2019-01-23 获取scheduler  此处获取方式可能存在bug 后续研究
        // 改进版本  注入scheduler
//        StdSchedulerFactory stdSchedulerFactory = new StdSchedulerFactory();
//        Scheduler scheduler = stdSchedulerFactory.getScheduler();

        // 构建key值  以各自name + 分组名组成
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, triggerGroupName);
        JobKey jobKey = JobKey.jobKey(jobName, groupName);

        // 构建任务详情
        JobDetail jobDetail = JobBuilder.newJob(quartzTask.getTClass())
                .withIdentity(jobKey)
                .build();

        // 放入任务中需要使用的数据
        jobDetail.getJobDataMap().put(DATA_KEY, data);

        // 构建触发器
        Trigger trigger = TriggerBuilder.newTrigger()
                .withDescription(jobDesc)
                .withIdentity(triggerKey)
                .startNow()
                .withSchedule(CronScheduleBuilder.cronSchedule(cron))
                .build();

        // 调度任务
        filinkScheduler.scheduleJob(jobDetail, trigger);
        filinkScheduler.start();

        log.info("任务启动 ：" + jobName);
        log.info("任务描述 ：" + jobDesc);
        log.info("启动时间 ：" + LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
    }

    /**
     * 修改定时任务
     *
     * @param info 任务信息
     */
    @Override
    public void editJob(TaskInfo info) {
        String jobName = info.getJobName();
        JobGroupEnum jobGroup = info.getJobGroup();

        try {
            if (!checkExists(jobName, jobGroup)) {
                log.info("修改定时任务失败，", jobGroup, jobName);
            }
            TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup.getGroupName());
            JobKey jobKey = new JobKey(jobName, jobGroup.getGroupName());

            CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder
                    .cronSchedule(info.getCron().getCron());

            CronTrigger cronTrigger = TriggerBuilder
                    .newTrigger()
                    .withIdentity(triggerKey)
                    .withDescription(info.getJobDesc())
                    .withSchedule(cronScheduleBuilder).build();

            JobDetail jobDetail = filinkScheduler.getJobDetail(jobKey);
            // 放入任务中需要使用的数据
            jobDetail.getJobDataMap().put(DATA_KEY, info.getData());

            jobDetail.getJobBuilder().withDescription(info.getJobDesc());
            HashSet<Trigger> triggerSet = new HashSet<>();
            triggerSet.add(cronTrigger);

            filinkScheduler.scheduleJob(jobDetail, triggerSet, true);
            log.info("任务修改成功 ：" + jobName);
        } catch (SchedulerException e) {
            log.error("类名不存在或执行表达式错误,exception:{}",e.getMessage());
        }
    }

    /**
     * 删除任务
     *
     * @param jobName  任务名称
     * @param jobGroup 任务分组
     */
    @Override
    public void deleteJob(String jobName, JobGroupEnum jobGroup) {
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup.getGroupName());
        try {
            if (checkExists(jobName, jobGroup)) {
                filinkScheduler.pauseTrigger(triggerKey);
                filinkScheduler.unscheduleJob(triggerKey);
                log.info("任务被删除, triggerKey:{},jobGroup:{}, jobName:{}", triggerKey ,jobGroup, jobName);
            }
        } catch (SchedulerException e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 暂停任务
     *
     * @param jobName  任务名称
     * @param jobGroup 任务分组
     */
    @Override
    public void pauseJob(String jobName, JobGroupEnum jobGroup) {
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup.getGroupName());
        try {
            if (checkExists(jobName, jobGroup)) {
                filinkScheduler.pauseTrigger(triggerKey);
                log.info("任务已经被暂停, triggerKey:{},jobGroup:{}, jobName:{}", triggerKey ,jobGroup, jobName);
            }
        } catch (SchedulerException e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 恢复任务
     *
     * @param jobName  任务名称
     * @param jobGroup 任务分组
     */
    @Override
    public void resumeJob(String jobName, JobGroupEnum jobGroup) {
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup.getGroupName());

        try {
            if (checkExists(jobName, jobGroup)) {
                filinkScheduler.resumeTrigger(triggerKey);
                log.info("任务已经被恢复,triggerKey:{},jobGroup:{}, jobName:{}", triggerKey ,jobGroup, jobName);
            }
        } catch (SchedulerException e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 检查任务是否存在
     *
     * @param jobName  任务名称
     * @param jobGroup 任务分组
     * @return
     * @throws SchedulerException
     */
    @Override
    public boolean checkExists(String jobName, JobGroupEnum jobGroup) throws SchedulerException {
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup.getGroupName());
        return filinkScheduler.checkExists(triggerKey);
    }
}
