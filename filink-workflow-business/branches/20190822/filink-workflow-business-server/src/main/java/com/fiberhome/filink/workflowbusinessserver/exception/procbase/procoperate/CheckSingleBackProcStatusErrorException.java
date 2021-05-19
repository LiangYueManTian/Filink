package com.fiberhome.filink.workflowbusinessserver.exception.procbase.procoperate;

/**
 * <p>
 * CheckSingleBackProcErrorException 确认退单状态异常
 * </p>
 *
 * @author hedongwei@wistronits.com
 * @since 2019-02-27
 */
public class CheckSingleBackProcStatusErrorException extends RuntimeException {

    public CheckSingleBackProcStatusErrorException() {
    }

    public CheckSingleBackProcStatusErrorException(String msg) {
        super(msg);
    }

}
