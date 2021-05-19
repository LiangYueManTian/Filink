package com.fiberhome.filink.schedule_server.demo;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.schedule_server.bean.TaskInfo;
import com.fiberhome.filink.schedule_server.job.TaskJob;
import com.fiberhome.filink.schedule_server.service.JobService;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * hello  world
 *
 * @author yuanyao@wistronits.com
 * create on 2019-01-24 14:36
 */
@RestController
public class JobDemo {


    @Autowired
    private JobService jobService;

    @GetMapping
    public Result testJobList() {

        List<TaskInfo> taskInfos = jobService.queryTasklist();
        return ResultUtils.success(taskInfos);
    }

    @PostMapping("/addjob")
    public Result addjob(@RequestBody TaskInfo info) throws SchedulerException {
        info.setTClass(TaskJob.class);
        jobService.addJob(info);
        return ResultUtils.success();
    }

    @PostMapping("/updatejob")
    public Result ujob(@RequestBody TaskInfo info) {
        info.setTClass(TaskJob.class);

        jobService.editJob(info);
        return ResultUtils.success();
    }

    @PostMapping("/deleteJob")
    public Result djob(@RequestBody TaskInfo info) {
        info.setTClass(TaskJob.class);

        jobService.deleteJob(info.getJobName(),info.getJobGroup());
        return ResultUtils.success();
    }

    @PostMapping("/pauseJob")
    public Result pjob(@RequestBody TaskInfo info) {
        jobService.pauseJob(info.getJobName(),info.getJobGroup());
        return ResultUtils.success();
    }

    @PostMapping("/resumeJob")
    public Result resumeJob(@RequestBody TaskInfo info) {
        info.setTClass(TaskJob.class);

        jobService.resumeJob(info.getJobName(),info.getJobGroup());
        return ResultUtils.success();
    }
}
