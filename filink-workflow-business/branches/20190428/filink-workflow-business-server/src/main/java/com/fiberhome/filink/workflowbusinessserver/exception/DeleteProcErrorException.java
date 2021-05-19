package com.fiberhome.filink.workflowbusinessserver.exception;

/**
 * <p>
 * DeleteProcErrorException 删除工单异常
 * </p>
 *
 * @author hedongwei@wistronits.com
 * @since 2019-02-27
 */
public class DeleteProcErrorException extends RuntimeException {

    public DeleteProcErrorException() {
    }

    public DeleteProcErrorException(String msg) {
        super(msg);
    }

}
