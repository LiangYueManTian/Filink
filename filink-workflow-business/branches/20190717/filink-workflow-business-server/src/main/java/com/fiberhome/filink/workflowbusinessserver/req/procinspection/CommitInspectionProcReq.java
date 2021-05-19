package com.fiberhome.filink.workflowbusinessserver.req.procinspection;

import lombok.Data;

/**
 * 提交巡检工单
 * @author hedongwei@wistronits.com
 * @date 2019/3/23 12:20
 */
@Data
public class CommitInspectionProcReq {

    /**
     * 用户编号
     */
    private String userId;

    /**
     * 流程编号
     */
    private String procId;
}


