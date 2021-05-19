package com.fiberhome.filink.scheduleserver.controller;

import com.alibaba.fastjson.JSONObject;
import com.fiberhome.filink.bean.FiLinkTimeUtils;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.scheduleserver.bean.InspectionTaskBizBean;
import com.fiberhome.filink.scheduleserver.bean.TaskInfo;
import com.fiberhome.filink.scheduleserver.bean.inspectiontask.InspectionTaskInfo;
import com.fiberhome.filink.scheduleserver.enums.JobGroupEnum;
import com.fiberhome.filink.scheduleserver.enums.JobTriggerEnum;
import com.fiberhome.filink.scheduleserver.job.InspectionTaskJob;
import com.fiberhome.filink.scheduleserver.service.InspectionTaskService;
import com.fiberhome.filink.scheduleserver.service.JobService;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.*;


/**
 * 巡检任务控制层
 * @author hedongwei@wistronits.com
 * @date 2019/3/8 18:49
 */
@RestController
@RequestMapping("/inspectionTaskJob")
public class InspectionTaskJobController {

    @Autowired
    private JobService jobService;

    @Autowired
    private InspectionTaskService inspectionTaskService;

    /**
     * 新增巡检任务
     * @author hedongwei@wistronits.com
     * @date  2019/3/8 17:17
     * @param req 新增巡检任务参数
     * @return 返回周期新增巡检任务结果
     */
    @PostMapping("/addTaskJob")
    public Result addTaskJob(@RequestBody InspectionTaskBizBean req) throws SchedulerException {
        //解析传入的参数 并将参数转化成taskInfo
        InspectionTaskInfo inspectionTaskInfo = this.generateTaskInfo(req);
        inspectionTaskService.addInspectionTaskJob(inspectionTaskInfo);
        return ResultUtils.success();
    }

    /**
     * 新增二月份的定时任务
     * @author hedongwei@wistronits.com
     * @date  2019/4/17 10:42
     * @param req 新增定时任务参数
     * @return 新增二月份的定时任务结果
     */
    @PostMapping("/addFebruaryTaskJob")
    public Result addFebruaryTaskJob(@RequestBody InspectionTaskBizBean req) throws SchedulerException {
        //解析传入的参数 并将参数转化成taskInfo
        InspectionTaskInfo inspectionTaskInfo = this.generateFebruaryTaskInfo(req);
        inspectionTaskService.addInspectionTaskJob(inspectionTaskInfo);
        return ResultUtils.success();
    }


    /**
     * 查询巡检任务是否存在
     * @author hedongwei@wistronits.com
     * @date  2019/4/1 12:29
     * @param req 巡检任务参数
     * @return 查询巡检任务是否存在
     */
    @PostMapping("searchInspectionTaskExists")
    public boolean searchInspectionTaskExists(@RequestBody InspectionTaskBizBean req) {
        List<TaskInfo> taskInfos = jobService.queryTasklist();
        if (!ObjectUtils.isEmpty(taskInfos)) {
            for (TaskInfo taskInfoOne : taskInfos) {
                if (req.getInspectionTaskId().equals(taskInfoOne.getJobName())) {
                    //已存在任务
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 修改巡检任务
     * @author hedongwei@wistronits.com
     * @date  2019/3/8 17:17
     * @param req 修改巡检任务参数
     * @return 返回修改巡检任务结果
     */
    @PostMapping("/updateTaskJob")
    public Result updateTaskJob(@RequestBody InspectionTaskBizBean req)throws SchedulerException {
        //TODO 解析传入的参数 并将参数转化成taskInfo
        return null;
    }

    /**
     * 删除巡检任务
     * @author hedongwei@wistronits.com
     * @date  2019/3/8 17:17
     * @param req 删除巡检任务参数
     * @return 返回删除巡检任务结果
     */
    @PostMapping("/deleteTaskJobList")
    public Result deleteTaskJobList(@RequestBody List<String> req)throws SchedulerException {
        if (!ObjectUtils.isEmpty(req)) {
            for (String jobName : req) {
                jobService.deleteJob(jobName, JobGroupEnum.INSPECTION_TASK);
            }
        }
        return ResultUtils.success();
    }

    /**
     * 删除巡检任务
     * @author hedongwei@wistronits.com
     * @date  2019/3/8 17:17
     * @param req 删除巡检任务参数
     * @return 返回删除巡检任务结果
     */
    @PostMapping("/deleteTaskJob")
    public Result deleteTaskJob(@RequestBody InspectionTaskBizBean req)throws SchedulerException {
        //解析传入的参数 并将参数转化成taskInfo
        jobService.deleteJob(req.getJobTaskName(), JobGroupEnum.INSPECTION_TASK);
        return ResultUtils.success();
    }

    /**
     * 暂停巡检任务
     * @author hedongwei@wistronits.com
     * @date  2019/3/8 17:17
     * @param req 暂停巡检任务参数
     * @return 暂停巡检任务结果
     */
    @PostMapping("/pauseTaskJob")
    public Result pauseTaskJob(@RequestBody InspectionTaskBizBean req)throws SchedulerException {
        //TODO 解析传入的参数 并将参数转化成taskInfo
        return null;
    }

    /**
     * 重新开始巡检任务
     * @author hedongwei@wistronits.com
     * @date  2019/3/8 17:17
     * @param req 重新开始巡检任务参数
     * @return 重新开始巡检任务结果
     */
    @PostMapping("/resumeTaskJob")
    public Result resumeTaskJob(@RequestBody InspectionTaskBizBean req)throws SchedulerException {
        //TODO 解析传入的参数 并将参数转化成taskInfo
        return null;
    }

    /**
     *
     * @author hedongwei@wistronits.com
     * @date  2019/4/17 10:35
     * @param inspectionTaskBiz 巡检任务业务数据
     */
    public InspectionTaskInfo generateFebruaryTaskInfo(InspectionTaskBizBean inspectionTaskBiz) {
        //获取新增巡检定时任务的信息
        InspectionTaskInfo addCronTaskInfo = this.generateInspectionTaskInfo(inspectionTaskBiz);

        Long nowTime = System.currentTimeMillis();

        Long startTime = inspectionTaskBiz.getStartTime();

        LocalDateTime localDateTime = FiLinkTimeUtils.getLocalDateTimeForTimeStamp(startTime);

        //时
        int hour = localDateTime.getHour();
        //分
        int minute = localDateTime.getMinute();
        //秒
        int second = localDateTime.getSecond();

        String cron = "";
        cron += second + " ";
        cron += minute + " ";
        cron += hour + " ";
        cron += "L ";
        cron += "2 ? ";

        //任务执行情况
        addCronTaskInfo.setCron(cron);

        //任务创建时间
        addCronTaskInfo.setCreateTime(nowTime);

        //任务分组
        addCronTaskInfo.setJobGroup(JobGroupEnum.INSPECTION_TASK);

        //任务触发器分组
        addCronTaskInfo.setTriggerGroup(JobTriggerEnum.INSPECTION_TASK);

        //任务执行类
        addCronTaskInfo.setTClass(InspectionTaskJob.class);

        return addCronTaskInfo;
    }

    /**
     * 生成任务信息
     * @author hedongwei@wistronits.com
     * @date  2019/3/8 19:44
     * @param inspectionTaskBiz 生成任务信息
     */
    public InspectionTaskInfo generateTaskInfo(InspectionTaskBizBean inspectionTaskBiz) {
        //获取新增巡检定时任务的信息
        InspectionTaskInfo addCronTaskInfo = this.generateInspectionTaskInfo(inspectionTaskBiz);

        //查询定时任务是否存在

        if (!StringUtils.isEmpty(inspectionTaskBiz.getTaskPeriod())) {

            Long nowTime = System.currentTimeMillis();

            Long startTime = inspectionTaskBiz.getStartTime();

            LocalDateTime localDateTime = FiLinkTimeUtils.getLocalDateTimeForTimeStamp(startTime);

            //年
            int year = localDateTime.getYear();
            //月
            int month = localDateTime.getMonthValue();
            //日
            int day = localDateTime.getDayOfMonth();
            //时
            int hour = localDateTime.getHour();
            //分
            int minute = localDateTime.getMinute();
            //秒
            int second = localDateTime.getSecond();

            int lastDay = this.getLastDayOfMonth(year, month);

            String cronDay = "";
            if (lastDay == day) {
                cronDay = "L";
            } else {
                cronDay = day + "";
            }

            String cron = "";
            cron += second + " ";
            cron += minute + " ";
            cron += hour + " ";
            cron += cronDay + " ";
            cron += "* ? ";

            //任务执行情况
            addCronTaskInfo.setCron(cron);

            //任务创建时间
            addCronTaskInfo.setCreateTime(nowTime);

            //任务分组
            addCronTaskInfo.setJobGroup(JobGroupEnum.INSPECTION_TASK);

            //任务触发器分组
            addCronTaskInfo.setTriggerGroup(JobTriggerEnum.INSPECTION_TASK);
        }


        //任务执行类
        addCronTaskInfo.setTClass(InspectionTaskJob.class);
        return addCronTaskInfo;
    }

    /**
     * 获取指定年月的最后一天
     * @param year
     * @param month
     * @return 获取指定年月的最后一天
     */
    public int getLastDayOfMonth(int year, int month) {
        Calendar cal = Calendar.getInstance();
        //设置年份
        cal.set(Calendar.YEAR, year);
        //设置月份
        cal.set(Calendar.MONTH, month-1);
        //获取某月最大天数
        int lastDay = cal.getActualMaximum(Calendar.DATE);
        //设置日历中月份的最大天数
        cal.set(Calendar.DAY_OF_MONTH, lastDay);
        //最后一天的日期
        int day = cal.get(Calendar.DATE);
        return day;
    }

    /**
     * 获取生成巡检任务定时任务信息
     * @author hedongwei@wistronits.com
     * @date  2019/4/17 10:19
     * @param inspectionTaskBiz 巡检任务业务
     * @return 生成巡检任务定时任务信息
     */
    public InspectionTaskInfo generateInspectionTaskInfo(InspectionTaskBizBean inspectionTaskBiz) {
        InspectionTaskInfo addCronTaskInfo = new InspectionTaskInfo();
        if (!StringUtils.isEmpty(inspectionTaskBiz.getInspectionTaskId())) {
            //任务名称
            addCronTaskInfo.setJobName(inspectionTaskBiz.getJobTaskName());
            //新增任务参数
            int mapInitLength = 64;
            Map<String, Object> data = new HashMap<String, Object>(mapInitLength);
            data.put("inspectionTaskId", inspectionTaskBiz.getInspectionTaskId());
            addCronTaskInfo.setData(JSONObject.toJSONString(data));
        }
        return addCronTaskInfo;
    }
}
