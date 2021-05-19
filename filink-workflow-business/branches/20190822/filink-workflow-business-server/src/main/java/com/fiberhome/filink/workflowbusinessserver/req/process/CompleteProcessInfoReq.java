package com.fiberhome.filink.workflowbusinessserver.req.process;

import lombok.Data;

/**
 * 完成流程参数类
 * @author hedongwei@wistronits.com
 * @date 2019/3/16 10:24
 */
@Data
public class CompleteProcessInfoReq extends ProcessBaseInfoReq {

    /**
     * 操作
     */
    private String operation;
}
