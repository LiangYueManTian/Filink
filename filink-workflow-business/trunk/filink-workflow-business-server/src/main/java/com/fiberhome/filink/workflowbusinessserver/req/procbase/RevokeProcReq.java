package com.fiberhome.filink.workflowbusinessserver.req.procbase;

import lombok.Data;

/**
 * 撤销单据
 * @author hedongwei@wistronits.com
 * @date 2019/3/22 15:57
 */
@Data
public class RevokeProcReq {

    /**
     * 流程编号
     */
    private String procId;
}
