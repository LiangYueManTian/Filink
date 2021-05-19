package com.fiberhome.filink.scheduleserver.job;

import com.alibaba.fastjson.JSONObject;
import com.fiberhome.filink.scheduleserver.stream.ScheduleStreams;
import com.fiberhome.filink.workflowbusinessapi.api.procinspection.ProcInspectionFeign;
import com.fiberhome.filink.workflowbusinessapi.bean.inspectiontask.InspectionTask;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * 巡检任务定时任务逻辑类
 *
 * @author hedongwei@wistronits.com
 * create on 2019-01-23 20:00
 */
@Slf4j
public class InspectionTaskJob extends QuartzJobBean {

    @Autowired
    private ScheduleStreams scheduleStreams;

    @Autowired
    private ProcInspectionFeign procInspectionFeign;

    /**
     * 执行方法
     *
     * @param context
     * @see #execute
     */
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        JobDataMap mergedJobDataMap = context.getMergedJobDataMap();
        //获取巡检任务编号
        Object data = JSONObject.toJSON(mergedJobDataMap.get("data"));
        JSONObject paramMap = JSONObject.parseObject(data.toString());
        String inspectionTaskId = paramMap.get("inspectionTaskId").toString();
        log.info("巡检任务生成巡检工单!");
        InspectionTask inspectionTask = new InspectionTask();
        inspectionTask.setInspectionTaskId(inspectionTaskId);
        procInspectionFeign.jobAddInspectionProc(inspectionTask);
    }
}
