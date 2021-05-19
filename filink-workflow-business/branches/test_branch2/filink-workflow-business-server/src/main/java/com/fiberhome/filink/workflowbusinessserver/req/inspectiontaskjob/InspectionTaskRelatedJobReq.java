package com.fiberhome.filink.workflowbusinessserver.req.inspectiontaskjob;

import com.fiberhome.filink.workflowbusinessserver.bean.inspectiontask.InspectionTaskRelatedJob;
import lombok.Data;

import java.util.List;

/**
 * 巡检任务关联定时任务参数
 * @author hedongwei@wistronits.com
 * @date 2019/3/6 16:37
 */
@Data
public class InspectionTaskRelatedJobReq extends InspectionTaskRelatedJob {

    /**
     * 巡检任务编号
     */
    private List<String> inspectionTaskIds;
}
