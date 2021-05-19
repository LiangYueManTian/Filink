package com.fiberhome.filink.scheduleapi.fallback;


import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.scheduleapi.api.InspectionTaskFeign;
import com.fiberhome.filink.scheduleapi.bean.InspectionTaskBizBean;
import com.fiberhome.filink.scheduleapi.utils.InspectionTaskResultCode;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * @author hedongwei@wistronits.com
 * ferign 熔断
 * 13:45 2019/1/19
 */
@Slf4j
@Component
public class InspectionTaskFeignFallback implements InspectionTaskFeign {

    private final Logger logger = LoggerFactory.getLogger(InspectionTaskFeignFallback.class);


    /**
     * 新增巡检任务
     * @author hedongwei@wistronits.com
     * @date  2019/3/8 17:17
     * @param req 新增巡检任务参数
     * @return 返回新增巡检任务结果
     */
    @Override
    public Result addTaskJob(InspectionTaskBizBean req) {
        //新增巡检任务异常
        String addTaskJobError = "Add inspection task job error";
        return ResultUtils.warn(InspectionTaskResultCode.ADD_TASK_JOB_ERROR, addTaskJobError);
    }

    /**
     * 新增二月份的定时任务
     * @author hedongwei@wistronits.com
     * @date  2019/4/17 10:42
     * @param req 新增定时任务参数
     * @return 新增二月份的定时任务结果
     */
    @Override
    public Result addFebruaryTaskJob(InspectionTaskBizBean req) {
        String addTaskJobError = "Add inspection task february job error";
        return ResultUtils.warn(InspectionTaskResultCode.ADD_TASK_JOB_ERROR, addTaskJobError);
    }

    /**
     * 巡检任务是否存在
     * @author hedongwei@wistronits.com
     * @date  2019/4/1 12:36
     * @param req 巡检任务业务数据
     * @return 返回巡检任务是否存在
     */
    @Override
    public boolean searchInspectionTaskExists(InspectionTaskBizBean req) {
        return false;
    }

    /**
     * 修改巡检任务
     * @author hedongwei@wistronits.com
     * @date  2019/3/8 17:17
     * @param req 修改巡检任务参数
     * @return 返回修改巡检任务结果
     */
    @Override
    public Result updateTaskJob(InspectionTaskBizBean req) {
        //修改巡检任务异常
        String updateTaskJobError = "Update inspection task job error<<<<<<<<<<";
        return ResultUtils.warn(InspectionTaskResultCode.UPDATE_TASK_JOB_ERROR, updateTaskJobError);
    }

    /**
     * 批量删除巡检任务
     * @author hedongwei@wistronits.com
     * @date  2019/3/8 17:17
     * @param req 删除巡检任务参数
     * @return 返回删除巡检任务结果
     */
    @Override
    public Result deleteTaskJobList(List<String> req) {
        //删除巡检任务异常
        String deleteTaskJobError = "Delete batch inspection task job error<<<<<<<<<<<<<<<";
        return ResultUtils.warn(InspectionTaskResultCode.DELETE_TASK_JOB_ERROR, deleteTaskJobError);
    }

    /**
     * 删除巡检任务
     * @author hedongwei@wistronits.com
     * @date  2019/3/8 17:17
     * @param req 删除巡检任务参数
     * @return 返回删除巡检任务结果
     */
    @Override
    public Result deleteTaskJob(InspectionTaskBizBean req) {
        //删除巡检任务异常
        String deleteTaskJobError = "Delete inspection task job error<<<<<<<<<<<<<<<<";
        return ResultUtils.warn(InspectionTaskResultCode.DELETE_TASK_JOB_ERROR, deleteTaskJobError);
    }


    /**
     * 暂停巡检任务
     * @author hedongwei@wistronits.com
     * @date  2019/3/8 17:17
     * @param req 暂停巡检任务参数
     * @return 暂停巡检任务结果
     */
    @Override
    public Result pauseTaskJob(InspectionTaskBizBean req) {
        //暂停巡检定时任务异常
        String pauseTaskJobError = "pause inspection task job error<<<<<<<<<<<<<<<<";
        return ResultUtils.warn(InspectionTaskResultCode.PAUSE_TASK_JOB_ERROR, pauseTaskJobError);
    }

    /**
     * 重新开始巡检任务
     * @author hedongwei@wistronits.com
     * @date  2019/3/8 17:17
     * @param req 重新开始巡检任务参数
     * @return 重新开始巡检任务结果
     */
    @Override
    public Result resumeTaskJob(InspectionTaskBizBean req) {
        //重新开始巡检任务异常
        String resumeTaskJobError = "Resume inspection task error<<<<<<<<<<<<<<<<<<<";
        return ResultUtils.warn(InspectionTaskResultCode.RESUME_TASK_JOB, resumeTaskJobError);
    }
}
