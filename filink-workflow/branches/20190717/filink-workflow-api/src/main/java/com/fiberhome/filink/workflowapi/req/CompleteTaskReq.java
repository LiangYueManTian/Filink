package com.fiberhome.filink.workflowapi.req;

import lombok.Data;

import java.util.Map;

/**
 * @author hedongwei@wistronits.com
 * description 办结任务参数类
 * date 2018/11/28 16:00
 */
@Data
public class CompleteTaskReq extends ProcessBaseReq {

    /**
     *  需要办结的taskId
     */
    private String taskId;

    /**
     *  流程code信息
     */
    private String procCode;

    /**
     *  用户code
     */
    private String userCode;

    /**
     *  办结的意见
     */
    private String message;

    /**
     *  参数信息
     */
    private Map<String, Object> variables;
}
