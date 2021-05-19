package com.fiberhome.filink.workflowbusinessserver.exception.procbase.procoperate;

/**
 * 指派工单状态异常
 * @author hedongwei@wistronits.com
 * @date 2019/8/14 14:05
 */

public class AssignProcStatusErrorException extends RuntimeException {

    public AssignProcStatusErrorException() {
    }

    public AssignProcStatusErrorException(String msg) {
        super(msg);
    }
}
