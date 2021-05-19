package com.fiberhome.filink.scheduleserver.schedules;

import com.fiberhome.filink.scheduleserver.bean.TaskInfo;
import com.fiberhome.filink.scheduleserver.enums.JobCronEnum;
import com.fiberhome.filink.scheduleserver.enums.JobGroupEnum;
import com.fiberhome.filink.scheduleserver.enums.JobTriggerEnum;
import com.fiberhome.filink.scheduleserver.job.ProcCountStatisticalJob;
import com.fiberhome.filink.scheduleserver.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * 工单统计定时任务
 * @author hedongwei@wistronits.com
 * @date 2018/11/30 9:37
 */
@Component
@Order(value = 8)
public class ProcCountStatisticalInitRunner implements ApplicationRunner {

    @Autowired
    private JobService jobService;

    private static final String JOB_NAME = "procCountDayStatistical";

    private static final String TRIGGER_NAME = "procCountDayStatisticalTrigger";

    /**
     * @author hedongwei@wistronits.com
     * description 启动需要运行的方法
     * date 9:42 2018/11/30
     * param [applicationArguments]
     */
    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {

        //根据任务名称查询任务是否存在
        List<TaskInfo> taskInfoList = jobService.queryTasklist();
        if (!ObjectUtils.isEmpty(taskInfoList)) {
            for (TaskInfo taskInfoOne : taskInfoList) {
                if (ProcCountStatisticalInitRunner.JOB_NAME.equals(taskInfoOne.getJobName())) {
                    //已存在任务，不新增任务
                    return;
                }
            }
        }

        //新增任务
        TaskInfo taskInfo = new TaskInfo();
        taskInfo.setJobGroup(JobGroupEnum.STATISTICS);
        taskInfo.setJobName(ProcCountStatisticalInitRunner.JOB_NAME);
        taskInfo.setTriggerGroup(JobTriggerEnum.STATISTICS);
        taskInfo.setTClass(ProcCountStatisticalJob.class);
        taskInfo.setCron(JobCronEnum.EVERY_DAY_AT_TWO_O_CLOCK);
        taskInfo.setTriggerName(ProcCountStatisticalInitRunner.TRIGGER_NAME);
        taskInfo.setCreateTime(System.currentTimeMillis());
        jobService.addJob(taskInfo);
    }

}
