package com.fiberhome.filink.scheduleapi.utils;

/**
 * 巡检任务返回类
 * @author hedongwei@wistronits.com
 * @date 2019/3/15 9:31
 */

public class InspectionTaskResultCode {

    /**
     * 新增定时任务异常
     */
    public static final Integer ADD_TASK_JOB_ERROR = 270100;

    /**
     * 修改定时任务异常
     */
    public static final Integer UPDATE_TASK_JOB_ERROR = 270103;

    /**
     * 删除巡检任务异常
     */
    public static final Integer DELETE_TASK_JOB_ERROR = 270105;

    /**
     * 暂停巡检任务异常
     */
    public static final Integer PAUSE_TASK_JOB_ERROR = 270107;

    /**
     * 重新开始巡检任务异常
     */
    public static final Integer RESUME_TASK_JOB = 270111;
}
