package com.fiberhome.filink.scheduleserver.service;

import com.fiberhome.filink.scheduleserver.bean.TaskInfo;
import com.fiberhome.filink.scheduleserver.bean.TaskInfoForAny;
import com.fiberhome.filink.scheduleserver.enums.JobGroupEnum;
import org.quartz.SchedulerException;

import java.util.List;

/**
 *定时任务管理接口
 *
 * @author yuanyao@wistronits.com
 * create on 2019-01-24 15:50
 */
public interface JobService {

    /**
     * 查询任务列表
     * @return 返回任务列表
     */
    List<TaskInfo> queryTasklist();

    /**
     * 新增任务
     * @param info 任务信息
     * @throws SchedulerException 任务异常
     */
    void addJob(TaskInfo info) throws SchedulerException;

    /**
     * 新增任务 任意属性构建
     * @param info
     * @throws SchedulerException 任务异常
     */
    void addJob(TaskInfoForAny info) throws SchedulerException;

    /**
     * 新增时间周期任务
     * @param info
     * @throws SchedulerException 任务异常
     */
    void addSimpleTriggerJob(TaskInfo info) throws SchedulerException;

    /**
     * 修改定时任务
     * @param info 任务信息
     */
    void editJob(TaskInfo info);

    /**
     * 删除任务
     * @param jobName 任务名称
     * @param jobGroup 任务分组
     */
    void deleteJob(String jobName, JobGroupEnum jobGroup);

    /**
     * 暂停任务
     * @param jobName 任务名称
     * @param jobGroup 任务分组
     */
    void pauseJob(String jobName, JobGroupEnum jobGroup);

    /**
     * 恢复任务
     * @param jobName 任务名称
     * @param jobGroup 任务分组
     */
    void resumeJob(String jobName, JobGroupEnum jobGroup);

    /**
     * 检查任务是否存在
     * @param jobName 任务名称
     * @param jobGroup 任务分组
     * @return
     * @throws SchedulerException
     */
    boolean checkExists(String jobName, JobGroupEnum jobGroup)throws SchedulerException;
}
