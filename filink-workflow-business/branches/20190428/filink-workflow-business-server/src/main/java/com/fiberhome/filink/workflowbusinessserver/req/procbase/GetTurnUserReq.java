package com.fiberhome.filink.workflowbusinessserver.req.procbase;

import lombok.Data;

/**
 * 获取转办用户信息
 * @author hedongwei@wistronits.com
 * @date 2019/3/27 20:28
 */
@Data
public class GetTurnUserReq {

    /**
     * 流程编号
     */
    private String procId;
}
