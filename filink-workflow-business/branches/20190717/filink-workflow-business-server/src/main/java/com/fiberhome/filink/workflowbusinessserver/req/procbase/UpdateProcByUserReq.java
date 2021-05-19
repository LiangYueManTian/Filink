package com.fiberhome.filink.workflowbusinessserver.req.procbase;

import lombok.Data;

/**
 * 下载工单参数
 * @author hedongwei@wistronits.com
 * @date 2019/3/21 21:23
 */
@Data
public class UpdateProcByUserReq {

    /**
     * 工单编号
     */
    private String procId;

    /**
     * 下载人
     */
    private String userId;
}
