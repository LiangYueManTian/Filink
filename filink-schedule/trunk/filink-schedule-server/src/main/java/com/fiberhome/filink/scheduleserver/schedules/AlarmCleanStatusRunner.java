package com.fiberhome.filink.scheduleserver.schedules;

import com.fiberhome.filink.scheduleserver.bean.TaskInfo;
import com.fiberhome.filink.scheduleserver.enums.JobCronEnum;
import com.fiberhome.filink.scheduleserver.enums.JobGroupEnum;
import com.fiberhome.filink.scheduleserver.enums.JobTriggerEnum;
import com.fiberhome.filink.scheduleserver.job.AlarmCleanStatusJob;
import com.fiberhome.filink.scheduleserver.service.JobService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

/**
 * 当前转历史
 *
 * @author weikuan@wistronits.com
 * @date 2019/6/18 9:37
 */
@Component
@Order(value = 15)
public class AlarmCleanStatusRunner implements ApplicationRunner {

    @Autowired
    private JobService jobService;

    /**
     * 定时任务名称
     */
    private static final String JOB_NAME = "ALARM_CLEAN_STATUS";

    /**
     * 定时任务触发器名称
     */
    private static final String TRIGGER_NAME = "alarmCleanStatus";

    @Override
    public void run(ApplicationArguments args) throws Exception {
        //根据任务名称查询任务是否存在
        List<TaskInfo> taskInfoList = jobService.queryTasklist();
        if (!ObjectUtils.isEmpty(taskInfoList)) {
            for (TaskInfo taskInfoOne : taskInfoList) {
                if (AlarmCleanStatusRunner.JOB_NAME.equals(taskInfoOne.getJobName())) {
                    //已存在任务，删除任务
                    jobService.deleteJob(JOB_NAME, JobGroupEnum.ALARM_CLEAN_STATUS);
                }
            }
        }

        //新增任务
        TaskInfo taskInfo = new TaskInfo();
        taskInfo.setJobGroup(JobGroupEnum.ALARM_CLEAN_STATUS);
        taskInfo.setJobName(AlarmCleanStatusRunner.JOB_NAME);
        taskInfo.setTriggerGroup(JobTriggerEnum.ALARM_CLEAN_STATUS);
        taskInfo.setTClass(AlarmCleanStatusJob.class);
        taskInfo.setCron(JobCronEnum.HOURS_MINUTE);
        taskInfo.setTriggerName(AlarmCleanStatusRunner.TRIGGER_NAME);
        taskInfo.setCreateTime(System.currentTimeMillis());
        jobService.addJob(taskInfo);
    }
}
