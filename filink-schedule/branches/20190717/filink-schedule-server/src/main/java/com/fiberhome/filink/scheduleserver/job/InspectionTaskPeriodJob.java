package com.fiberhome.filink.scheduleserver.job;

import com.alibaba.fastjson.JSONObject;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.deviceapi.api.AreaFeign;
import com.fiberhome.filink.deviceapi.api.DeviceFeign;
import com.fiberhome.filink.scheduleserver.service.InspectionTaskService;
import com.fiberhome.filink.scheduleserver.service.JobService;
import com.fiberhome.filink.userapi.api.DepartmentFeign;
import com.fiberhome.filink.workflowbusinessapi.api.inspectiontask.InspectionTaskFeign;
import com.fiberhome.filink.workflowbusinessapi.api.inspectiontask.InspectionTaskRecordFeign;
import com.fiberhome.filink.workflowbusinessapi.api.procinspection.ProcInspectionFeign;
import com.fiberhome.filink.workflowbusinessapi.bean.inspectiontask.InspectionTaskDetailBean;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.util.ObjectUtils;

/**
 * 任务逻辑实现类demo
 * 需要实现Job接口
 *
 * @author hedongwei@wistronits.com
 * create on 2019-01-23 20:00
 */
@Slf4j
public class InspectionTaskPeriodJob extends QuartzJobBean {

    @Autowired
    private InspectionTaskFeign inspectionTaskFeign;

    @Autowired
    private InspectionTaskRecordFeign inspectionTaskRecordFeign;

    @Autowired
    private ProcInspectionFeign procInspectionFeign;

    @Autowired
    private AreaFeign areaFeign;

    @Autowired
    private DeviceFeign deviceFeign;

    @Autowired
    private DepartmentFeign departmentFeign;

    @Autowired
    private InspectionTaskService inspectionTaskService;

    @Autowired
    private JobService jobService;

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
        String inspectionTaskId = mergedJobDataMap.get("inspectionTaskId").toString();

        //不存在的巡检任务不新增巡检工单
        Result taskDetail = inspectionTaskFeign.getInspectionTaskDetail(inspectionTaskId);
        if (ResultCode.SUCCESS.equals(taskDetail.getCode())) {
            InspectionTaskDetailBean detailBean = JSONObject.toJavaObject(JSONObject.parseObject(JSONObject.toJSONString(taskDetail.getData())), InspectionTaskDetailBean.class);
            if (ObjectUtils.isEmpty(detailBean)) {
                return;
            }
            //新增巡检任务信息
            inspectionTaskService.addInspectionProcProcess(inspectionTaskId);
        }
    }
}
