package com.fiberhome.filink.scheduleserver.service.impl;

import com.fiberhome.filink.scheduleserver.bean.TaskInfo;
import com.fiberhome.filink.scheduleserver.bean.TaskInfoForAny;
import com.fiberhome.filink.scheduleserver.enums.JobCronEnum;
import com.fiberhome.filink.scheduleserver.enums.JobGroupEnum;
import com.fiberhome.filink.scheduleserver.service.JobService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.quartz.*;
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
     * trigger类型为cron
     */
    public static final String TRIGGER_TYPE_CRON = "cron";

    /**
     * trigger类型为simple
     */
    public static final String TRIGGER_TYPE_SIMPLE = "simple";

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
            log.error("查询任务列表异常", e);
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
        String trigger = JobServiceImpl.TRIGGER_TYPE_CRON;
        //新增任务
        this.addJobProcess(quartzTask, trigger);
    }

    /**
     * 新增任务 任意属性
     *
     * @param info
     */
    @SuppressWarnings("all")
    @Override
    public void addJob(TaskInfoForAny info) throws SchedulerException {
        String jobName = info.getJobName();
        String groupName = info.getJobGroup();
        String triggerGroupName = info.getTriggerGroup();
        Object data = info.getData();
        String jobDesc = info.getJobDesc();
        String triggerName = info.getTriggerName();
        String cron = info.getCron();
        Integer secondInterval = info.getIntervalSecond();

        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, info.getJobGroup());

        if (filinkScheduler.checkExists(triggerKey)) {
            log.info("任务以及存在，不需要重复创建");
        }


        String triggerType = TRIGGER_TYPE_CRON;
//        如果cron不传 则为simple触发器
        if (StringUtils.isEmpty(info.getCron())) {
            triggerType = TRIGGER_TYPE_SIMPLE;
        }

        // TODO: 2019-01-23 获取scheduler  此处获取方式可能存在bug 后续研究
        // 改进版本  注入scheduler
//        StdSchedulerFactory stdSchedulerFactory = new StdSchedulerFactory();
//        Scheduler scheduler = stdSchedulerFactory.getScheduler();

        // 构建key值  以各自name + 分组名组成
        JobKey jobKey = JobKey.jobKey(jobName, groupName);

        // 构建任务详情
        JobDetail jobDetail = JobBuilder.newJob(info.getTClass())
                .withIdentity(jobKey)
                .build();

        // 放入任务中需要使用的数据
        jobDetail.getJobDataMap().put(DATA_KEY, data);

        // 构建触发器
        Trigger trigger = null;
        if (JobServiceImpl.TRIGGER_TYPE_CRON.equals(triggerType)) {
            trigger = TriggerBuilder.newTrigger()
                    .withDescription(jobDesc)
                    .withIdentity(triggerKey)
                    .startNow()
                    .withSchedule(CronScheduleBuilder.cronSchedule(cron))
                    .build();
        } else {
            trigger = TriggerBuilder.newTrigger()
                    .withDescription(jobDesc)
                    .withIdentity(triggerKey)
                    .startNow()
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(secondInterval).repeatForever())
                    .build();
        }

        // 调度任务
        filinkScheduler.scheduleJob(jobDetail, trigger);
        filinkScheduler.start();

        log.info("任务启动 ：" + jobName);
        log.info("任务描述 ：" + jobDesc);
        log.info("启动时间 ：" + LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
    }

    /**
     *
     * @author hedongwei@wistronits.com
     * @date  2019/3/29 19:14
     * @param quartzTask 定时任务参数
     */
    @Override
    public void addSimpleTriggerJob(TaskInfo quartzTask) throws SchedulerException {
        String trigger = JobServiceImpl.TRIGGER_TYPE_SIMPLE;
        //新增任务
        this.addJobProcess(quartzTask, trigger);
    }

    /**
     * 添加定时任务
     * @author hedongwei@wistronits.com
     * @date  2019/3/29 19:26
     * @param quartzTask 定时任务
     */
    public void addJobProcess(TaskInfo quartzTask, String triggerType) throws SchedulerException{
        String jobName = quartzTask.getJobName();
        String groupName = quartzTask.getJobGroup().getGroupName();
        String triggerGroupName = quartzTask.getTriggerGroup().getGroupName();
        Object data = quartzTask.getData();
        String jobDesc = quartzTask.getJobDesc();
        String triggerName = quartzTask.getTriggerName();
        String cron = "";
        if (JobServiceImpl.TRIGGER_TYPE_CRON.equals(triggerType)) {
            cron = quartzTask.getCron().getCron();
        }
        int secondInterval = quartzTask.getIntervalSecond();

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
        Trigger trigger = null;
        if (JobServiceImpl.TRIGGER_TYPE_CRON.equals(triggerType)) {
            trigger = TriggerBuilder.newTrigger()
                    .withDescription(jobDesc)
                    .withIdentity(triggerKey)
                    .startNow()
                    .withSchedule(CronScheduleBuilder.cronSchedule(cron))
                    .build();
        } else {
            trigger = TriggerBuilder.newTrigger()
                    .withDescription(jobDesc)
                    .withIdentity(triggerKey)
                    .startNow()
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(secondInterval).repeatForever())
                    .build();
        }

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
