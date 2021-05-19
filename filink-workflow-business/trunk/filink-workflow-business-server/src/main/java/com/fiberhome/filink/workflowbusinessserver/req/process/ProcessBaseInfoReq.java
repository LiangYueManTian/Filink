package com.fiberhome.filink.workflowbusinessserver.req.process;

import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcBase;
import lombok.Data;

import java.util.Map;

/**
 * 流程基础参数
 * @author hedongwei@wistronits.com
 * @date 2019/3/16 10:19
 */
@Data
public class ProcessBaseInfoReq extends ProcBase {

    /**
     * 参数数组
     */
    private Map<String, Object> variables;

    /**
     * 消息
     */
    private String message;

    /**
     * 部门编号拼接
     */
    private String departmentIds;
}
