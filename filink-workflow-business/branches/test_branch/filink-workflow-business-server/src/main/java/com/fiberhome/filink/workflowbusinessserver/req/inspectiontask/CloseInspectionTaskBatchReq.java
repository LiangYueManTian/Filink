package com.fiberhome.filink.workflowbusinessserver.req.inspectiontask;

import lombok.Data;

import java.util.List;

/**
 * 关闭巡检任务参数类
 * @author hedongwei@wistronits.com
 * @date 2019/3/6 16:37
 */
@Data
public class CloseInspectionTaskBatchReq {

    /**
     * 需要关闭的巡检任务编号
     */
    private List<String> inspectionTaskIds;
}
