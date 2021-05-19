package com.fiberhome.filink.scheduleserver.schedules;

import com.fiberhome.filink.scheduleserver.bean.TaskInfo;
import com.fiberhome.filink.scheduleserver.enums.JobCronEnum;
import com.fiberhome.filink.scheduleserver.enums.JobGroupEnum;
import com.fiberhome.filink.scheduleserver.enums.JobTriggerEnum;
import com.fiberhome.filink.scheduleserver.job.RetryDeleteDeviceJob;
import com.fiberhome.filink.scheduleserver.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * 当前告警转历史告警定时任务
 *
 * @author taowei@wistronits.com
 * @date 2019/3/27 9:37
 */
@Component
@Order(value = 15)
public class DeviceRetryDeleteRunner implements ApplicationRunner {

    @Autowired
    private JobService jobService;

    /**
     * 定时任务名称
     */
    private static final String JOB_NAME = "retryDeleteDeviceTask";

    /**
     * 定时任务触发器名称
     */
    private static final String TRIGGER_NAME = "retryDeleteDeviceTaskTrigger";

    /**
     * @author taowei@wistronits.com
     * description 启动需要运行的方法
     * date 2019/3/27 9:37
     * param [applicationArguments]
     */
    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {

        //根据任务名称查询任务是否存在
        List<TaskInfo> taskInfoList = jobService.queryTasklist();
        if (!ObjectUtils.isEmpty(taskInfoList)) {
            for (TaskInfo taskInfoOne : taskInfoList) {
                if (DeviceRetryDeleteRunner.JOB_NAME.equals(taskInfoOne.getJobName())) {
                    //已存在任务，删除任务
                    jobService.deleteJob(JOB_NAME, JobGroupEnum.RETRY_DEVICE);
                }
            }
        }

        //新增任务
        TaskInfo taskInfo = new TaskInfo();
        taskInfo.setJobGroup(JobGroupEnum.RETRY_DEVICE);
        taskInfo.setJobName(DeviceRetryDeleteRunner.JOB_NAME);
        taskInfo.setTriggerGroup(JobTriggerEnum.RETRY_DEVICE);
        taskInfo.setTClass(RetryDeleteDeviceJob.class);
        taskInfo.setCron(JobCronEnum.EVERY_DAY_AT_THREE_O_CLOCK);
        taskInfo.setTriggerName(DeviceRetryDeleteRunner.TRIGGER_NAME);
        taskInfo.setCreateTime(System.currentTimeMillis());
        jobService.addJob(taskInfo);
    }

}
