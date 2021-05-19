package com.fiberhome.filink.workflowapi.req;

import lombok.Data;

import java.util.Map;

/**
 * @author hedongwei@wistronits.com
 * description 发起流程参数类
 * date 2018/11/28 14:11
 */
@Data
public class StartProcessReq extends ProcessBaseReq {

    /**
     *  流程编号
     */
    private String procCode;

    /**
     *  流程类型
     */
    private String procType;

    /**
     *  用户code
     */
    private String userCode;

    /**
     *  流程标题
     */
    private String title;

    /**
     *  参数信息
     */
    private Map<String, Object> variables;
}
