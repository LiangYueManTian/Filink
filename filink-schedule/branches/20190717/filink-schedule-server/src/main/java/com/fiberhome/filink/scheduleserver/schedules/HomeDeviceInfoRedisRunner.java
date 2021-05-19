package com.fiberhome.filink.scheduleserver.schedules;

import com.fiberhome.filink.scheduleserver.bean.TaskInfo;
import com.fiberhome.filink.scheduleserver.enums.JobCronEnum;
import com.fiberhome.filink.scheduleserver.enums.JobGroupEnum;
import com.fiberhome.filink.scheduleserver.enums.JobTriggerEnum;
import com.fiberhome.filink.scheduleserver.job.HomeDeviceInfoRedisJob;
import com.fiberhome.filink.scheduleserver.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * 定时刷新首页设施Redis缓存信息
 * @author chaofang@wistronits.com
 * @since  2019/5/15
 */
@Component
@Order(value = 6)
public class HomeDeviceInfoRedisRunner implements ApplicationRunner {

    @Autowired
    private JobService jobService;

    /**
     * 定时任务名称
     */
    private static final String HOME_DEVICE_INFO_REDIS_JOB_NAME = "refreshHomeDeviceInfoRedis";

    /**
     * 定时任务触发器名称
     */
    private static final String HOME_DEVICE_INFO_REDIS_TRIGGER_NAME = "refreshHomeDeviceInfoRedisTrigger";


    /**
     * Callback used to run the bean.
     *
     * @param args incoming application arguments
     * @throws Exception on error
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        //根据任务名称查询任务是否存在
        List<TaskInfo> taskInfoList = jobService.queryTasklist();
        if (!ObjectUtils.isEmpty(taskInfoList)) {
            for (TaskInfo taskInfoOne : taskInfoList) {
                if (HOME_DEVICE_INFO_REDIS_JOB_NAME.equals(taskInfoOne.getJobName())) {
                    //已存在任务，删除任务
                    jobService.deleteJob(HOME_DEVICE_INFO_REDIS_JOB_NAME, JobGroupEnum.HOME_DEVICE);
                }
            }
        }

        //新增任务
        TaskInfo taskInfo = new TaskInfo();
        taskInfo.setJobGroup(JobGroupEnum.HOME_DEVICE);
        taskInfo.setJobName(HOME_DEVICE_INFO_REDIS_JOB_NAME);
        taskInfo.setTriggerGroup(JobTriggerEnum.HOME_DEVICE);
        taskInfo.setTClass(HomeDeviceInfoRedisJob.class);
        taskInfo.setCron(JobCronEnum.EVERY_DAY_AT_ONE_O_CLOCK);
        taskInfo.setTriggerName(HOME_DEVICE_INFO_REDIS_TRIGGER_NAME);
        taskInfo.setCreateTime(System.currentTimeMillis());
        jobService.addJob(taskInfo);
    }
}
