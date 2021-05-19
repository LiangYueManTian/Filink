package com.fiberhome.filink.scheduleserver.schedules;

import com.fiberhome.filink.scheduleserver.bean.TaskInfo;
import com.fiberhome.filink.scheduleserver.enums.JobCronEnum;
import com.fiberhome.filink.scheduleserver.enums.JobGroupEnum;
import com.fiberhome.filink.scheduleserver.enums.JobTriggerEnum;
import com.fiberhome.filink.scheduleserver.job.ProcCountMonthStatisticalJob;
import com.fiberhome.filink.scheduleserver.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * 工单统计月增量定时任务
 * @author hedongwei@wistronits.com
 * @date 2018/11/30 9:37
 */
@Component
@Order(value = 20)
public class ProcCountMonthStatisticalInitRunner implements ApplicationRunner {

    @Autowired
    private JobService jobService;

    private static final String JOB_NAME = "procCountMonthStatistical";

    private static final String TRIGGER_NAME = "procCountMonthStatisticalTrigger";

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
                if (ProcCountMonthStatisticalInitRunner.JOB_NAME.equals(taskInfoOne.getJobName())) {
                    //已存在任务，不新增任务
                    return;
                }
            }
        }

        //新增任务
        TaskInfo taskInfo = new TaskInfo();
        taskInfo.setJobGroup(JobGroupEnum.STATISTICS);
        taskInfo.setJobName(ProcCountMonthStatisticalInitRunner.JOB_NAME);
        taskInfo.setTriggerGroup(JobTriggerEnum.STATISTICS);
        taskInfo.setTClass(ProcCountMonthStatisticalJob.class);
        taskInfo.setCron(JobCronEnum.EVERY_MONTH_FIRST_HALF_PASS_TWO);
        taskInfo.setTriggerName(ProcCountMonthStatisticalInitRunner.TRIGGER_NAME);
        taskInfo.setCreateTime(System.currentTimeMillis());
        jobService.addJob(taskInfo);
    }

}
