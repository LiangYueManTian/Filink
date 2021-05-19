package com.fiberhome.filink.workflowserver.constant;

/**
 * 流程常量类
 * @author hedongwei@wistronits.com
 * @date 2019/3/19 13:45
 */

public class ProcessConstants {

    /**
     * 启动
     */
    public static final String PROC_OPERATION_START = "startProcess";

    /**
     * 提交
     */
    public static final String PROC_OPERATION_COMPLETE = "complete";

    /**
     * 退单
     */
    public static final String PROC_OPERATION_SINGLE_BACK = "singleBack";

    /**
     * 撤回
     */
    public static final String PROC_OPERATION_REVOKE = "revoke";

    /**
     * 指派
     */
    public static final String PROC_OPERATION_ASSIGN = "assign";

    /**
     * 转办
     */
    public static final String PROC_OPERATION_TURN = "turn";

    /**
     * 流程节点部门
     */
    public static final String PROC_POINT_DEPARTMENT = "department";

    /**
     * 流程节点下载人
     */
    public static final String PROC_POINT_DOWNLOAD_USER = "downloadUser";

    /**
     * 流程节点转办人
     */
    public static final String PROC_POINT_TURN_USER = "turnUser";

    /**
     * 流程节点待指派
     */
    public static final String PROC_POINT_ASSIGNED = "assigned";

    /**
     * 流程变量操作
     */
    public static final String PROC_VAR_OPERATION = "operation";
}
