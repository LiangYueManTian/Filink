package com.fiberhome.filink.workflowbusinessserver.exception.procinspection;

/**
 * 提交巡检工单异常
 * @author hedongwei@wistronits.com
 * @date 2019/4/2 13:43
 */

public class CommitProcInspectionException extends RuntimeException {

    public CommitProcInspectionException() {
    }

    public CommitProcInspectionException(String msg) {
        super(msg);
    }
}
