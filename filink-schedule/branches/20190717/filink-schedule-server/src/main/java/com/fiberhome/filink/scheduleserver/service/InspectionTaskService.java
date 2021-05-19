package com.fiberhome.filink.scheduleserver.service;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.scheduleserver.bean.inspectiontask.InspectionTaskInfo;
import com.fiberhome.filink.workflowbusinessapi.bean.inspectiontask.InspectionTaskDetailBean;
import org.quartz.SchedulerException;

/**
 * 巡检任务service
 * @author hedongwei@wistronits.com
 * @date 2019/3/31 8:18
 */
public interface InspectionTaskService {

    /**
     * 新增任务
     * @param info 任务信息
     * @throws SchedulerException 抛出异常信息
     */
    void addInspectionTaskJob(InspectionTaskInfo info) throws SchedulerException;


    /**
     * 新增周期性的任务
     * @author hedongwei@wistronits.com
     * @date  2019/3/31 10:28
     * @param detailBean 巡检任务编号
     * @return 返回新增周期性的任务结果
     * @throws Exception 抛出异常信息
     */
    Result addTaskPeriodJob(InspectionTaskDetailBean detailBean) throws Exception;

    /**
     * 新增巡检工单
     * @author hedongwei@wistronits.com
     * @date  2019/3/31 9:26
     * @param detailBean 巡检任务详情
     * @return 新增巡检工单结果
     */
    Result addInspectionProc(InspectionTaskDetailBean detailBean);

    /**
     * 新增巡检工单过程
     * @author hedongwei@wistronits.com
     * @date  2019/3/31 10:13
     * @param inspectionTaskId 巡检任务编号
     * @return 新增巡检工单结果
     */
    Result addInspectionProcProcess(String inspectionTaskId);
}
