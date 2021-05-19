package com.fiberhome.filink.workflowbusinessserver.exception;

/**
 * <p>
 * AddProcErrorException 新增工单异常
 * </p>
 *
 * @author hedongwei@wistronits.com
 * @since 2019-02-27
 */
public class AddProcErrorException extends RuntimeException {

    public AddProcErrorException() {
    }

    public AddProcErrorException(String msg) {
        super(msg);
    }

}
