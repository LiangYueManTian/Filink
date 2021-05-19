package com.fiberhome.filink.workflowbusinessserver.exception;

/**
 * <p>
 * UpdateProcErrorException 修改工单异常
 * </p>
 *
 * @author hedongwei@wistronits.com
 * @since 2019-02-27
 */
public class UpdateProcErrorException extends RuntimeException {

    public UpdateProcErrorException() {
    }

    public UpdateProcErrorException(String msg) {
        super(msg);
    }

}
