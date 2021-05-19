package com.fiberhome.filink.workflowserver.resp;

import lombok.Data;

/**
 * @author hedongwei@wistronits.com
 * description 启动流程返回类
 * date 2018/11/28 14:11
 */
@Data
public class StartProcessResp extends ProcessBaseResp{

    /**
     * 流程编号
     */
    private String procId;

    /**
     * 流程实例编号
     */
    private String procInstId;
}
