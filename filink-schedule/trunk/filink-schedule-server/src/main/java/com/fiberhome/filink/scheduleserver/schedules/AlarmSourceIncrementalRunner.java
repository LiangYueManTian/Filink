package com.fiberhome.filink.scheduleserver.schedules;

import com.fiberhome.filink.scheduleserver.bean.TaskInfo;
import com.fiberhome.filink.scheduleserver.enums.JobCronEnum;
import com.fiberhome.filink.scheduleserver.enums.JobGroupEnum;
import com.fiberhome.filink.scheduleserver.enums.JobTriggerEnum;
import com.fiberhome.filink.scheduleserver.job.AlarmSourceIncrementalJob;
import com.fiberhome.filink.scheduleserver.service.JobService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

/**
 * 设施告警统计增量日定时任务
 *
 * @author weikuan@wistronits.com
 * @date 2019/6/18 9:37
 */
@Component
@Order(value = 13)
public class AlarmSourceIncrementalRunner implements ApplicationRunner {

    @Autowired
    private JobService jobService;

    /**
     * 定时任务名称
     */
    private static final String JOB_NAME = "INCREMENTAL_EXPIRE";

    /**
     * 定时任务触发器名称
     */
    private static final String TRIGGER_NAME = "alarmIncrementalExpireTrigger";

    @Override
    public void run(ApplicationArguments args) throws Exception {
        //根据任务名称查询任务是否存在
        List<TaskInfo> taskInfoList = jobService.queryTasklist();
        if (!ObjectUtils.isEmpty(taskInfoList)) {
            for (TaskInfo taskInfoOne : taskInfoList) {
                if (AlarmSourceIncrementalRunner.JOB_NAME.equals(taskInfoOne.getJobName())) {
                    //已存在任务，删除任务
                    jobService.deleteJob(JOB_NAME, JobGroupEnum.ALARM_SOURCE_INCREMENTAL);
                }
            }
        }

        //新增任务
        TaskInfo taskInfo = new TaskInfo();
        taskInfo.setJobGroup(JobGroupEnum.ALARM_SOURCE_INCREMENTAL);
        taskInfo.setJobName(AlarmSourceIncrementalRunner.JOB_NAME);
        taskInfo.setTriggerGroup(JobTriggerEnum.ALARM_SOURCE_INCREMENTAL);
        taskInfo.setTClass(AlarmSourceIncrementalJob.class);
        taskInfo.setCron(JobCronEnum.EVERY_DAY_AT_ONE_O_CLOCK);
        taskInfo.setTriggerName(AlarmSourceIncrementalRunner.TRIGGER_NAME);
        taskInfo.setCreateTime(System.currentTimeMillis());
        jobService.addJob(taskInfo);
    }
}
