package com.fiberhome.filink.workflowserver.resp;

import lombok.Data;

import java.io.Serializable;

/**
 * @author hedongwei@wistronits.com
 * description 流程任务返回类
 * date 2018/11/28 16:12
 */
@Data
public class ProcessBaseResp implements Serializable {

    private static final long serialVersionUID = 1L;

    public ProcessBaseResp(){
        this.code = "0";
    }

    /**
     * 返回标识
     */
    private String code;

    /**
     * 返回消息
     */
    private String message;
}
