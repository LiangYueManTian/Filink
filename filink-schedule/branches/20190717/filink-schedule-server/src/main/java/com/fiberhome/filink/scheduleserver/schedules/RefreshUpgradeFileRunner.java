package com.fiberhome.filink.scheduleserver.schedules;

import com.fiberhome.filink.scheduleserver.bean.TaskInfo;
import com.fiberhome.filink.scheduleserver.enums.JobCronEnum;
import com.fiberhome.filink.scheduleserver.enums.JobGroupEnum;
import com.fiberhome.filink.scheduleserver.enums.JobTriggerEnum;
import com.fiberhome.filink.scheduleserver.job.RefreshUpgradeFileJob;
import com.fiberhome.filink.scheduleserver.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * 定时刷新设备升级文件
 * @author chaofang@wistronits.com
 * @since  2019/6/11
 */
@Component
@Order(value = 9)
public class RefreshUpgradeFileRunner implements ApplicationRunner {

    @Autowired
    private JobService jobService;

    /**
     * 定时任务名称
     */
    private static final String REFRESH_UPGRADE_FILE_JOB_NAME = "refreshUpgradeFile";

    /**
     * 定时任务触发器名称
     */
    private static final String REFRESH_UPGRADE_FILE_TRIGGER_NAME = "refreshUpgradeFileTrigger";


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
                if (REFRESH_UPGRADE_FILE_JOB_NAME.equals(taskInfoOne.getJobName())) {
                    //已存在任务，删除任务
                    jobService.deleteJob(REFRESH_UPGRADE_FILE_JOB_NAME, JobGroupEnum.UPGRADE_FILE);
                }
            }
        }

        //新增任务
        TaskInfo taskInfo = new TaskInfo();
        taskInfo.setJobGroup(JobGroupEnum.UPGRADE_FILE);
        taskInfo.setJobName(REFRESH_UPGRADE_FILE_JOB_NAME);
        taskInfo.setTriggerGroup(JobTriggerEnum.UPGRADE_FILE);
        taskInfo.setTClass(RefreshUpgradeFileJob.class);
        taskInfo.setCron(JobCronEnum.SIX_HOUR);
        taskInfo.setTriggerName(REFRESH_UPGRADE_FILE_TRIGGER_NAME);
        taskInfo.setCreateTime(System.currentTimeMillis());
        jobService.addJob(taskInfo);
    }
}
