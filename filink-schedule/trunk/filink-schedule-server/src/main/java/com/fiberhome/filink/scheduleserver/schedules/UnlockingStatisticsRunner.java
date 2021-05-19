package com.fiberhome.filink.scheduleserver.schedules;

import com.fiberhome.filink.scheduleserver.bean.TaskInfo;
import com.fiberhome.filink.scheduleserver.enums.JobCronEnum;
import com.fiberhome.filink.scheduleserver.enums.JobGroupEnum;
import com.fiberhome.filink.scheduleserver.enums.JobTriggerEnum;
import com.fiberhome.filink.scheduleserver.job.ProcCountStatisticalJob;
import com.fiberhome.filink.scheduleserver.job.UnlockingStatisticsJob;
import com.fiberhome.filink.scheduleserver.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * 开锁统计定时任务
 * @Author: zhaoliang
 * @Date: 2019/6/11 19:22
 * @Description: com.fiberhome.filink.scheduleserver.schedules
 * @version: 1.0
 */
@Component
@Order(value = 10)
public class UnlockingStatisticsRunner implements ApplicationRunner {

    @Autowired
    private JobService jobService;

    private static final String JOB_NAME = "unlockingStatistics";

    private static final String TRIGGER_NAME = "unlockingStatisticsTrigger";

    /**
     * 统计每天开锁次数
     * @param applicationArguments
     * @throws Exception
     */
    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {

        //根据任务名称查询任务是否存在
        List<TaskInfo> taskInfoList = jobService.queryTasklist();
        if (!ObjectUtils.isEmpty(taskInfoList)) {
            for (TaskInfo taskInfoOne : taskInfoList) {
                if (UnlockingStatisticsRunner.JOB_NAME.equals(taskInfoOne.getJobName())) {
                    //已存在任务，不新增任务
                    jobService.deleteJob(JOB_NAME, JobGroupEnum.STATISTICS);
                }
            }
        }

        //新增任务
        TaskInfo taskInfo = new TaskInfo();
        taskInfo.setJobGroup(JobGroupEnum.STATISTICS);
        taskInfo.setJobName(UnlockingStatisticsRunner.JOB_NAME);
        taskInfo.setTriggerGroup(JobTriggerEnum.STATISTICS);
        taskInfo.setTClass(UnlockingStatisticsJob.class);
        taskInfo.setCron(JobCronEnum.FIVE_MINUTE);
        taskInfo.setTriggerName(UnlockingStatisticsRunner.TRIGGER_NAME);
        taskInfo.setCreateTime(System.currentTimeMillis());
        jobService.addJob(taskInfo);
    }
}
