package com.fiberhome.filink.workflowbusinessserver.req.procbase;

import lombok.Data;

/**
 * 工单关联设施信息
 * @author hedongwei@wistronits.com
 * @date 2019/3/27 20:21
 */
@Data
public class ProcRelationDeviceListReq {

    /**
     * 流程编号
     */
    private String procId;
}
