package com.fiberhome.filink.workflowbusinessserver.service.inspectiontaskjob;

import java.util.List;

/**
 * 巡检工单定时任务service
 * @author hedongwei@wistronits.com
 * @date 2019/4/23 10:49
 */
public interface InspectionTaskJobService {

    /**
     * 删除巡检任务定时任务
     * @author hedongwei@wistronits.com
     * @date  2019/4/23 10:55
     * @param inspectionTaskIds 巡检任务编号集合
     * @param isDeleted 是否删除信息
     */
    void deleteInspectionTaskJob(List<String> inspectionTaskIds, String isDeleted);
}
