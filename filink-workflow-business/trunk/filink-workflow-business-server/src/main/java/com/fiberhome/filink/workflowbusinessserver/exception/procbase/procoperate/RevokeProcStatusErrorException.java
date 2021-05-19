package com.fiberhome.filink.workflowbusinessserver.exception.procbase.procoperate;

/**
 * <p>
 * RevokeProcErrorException 撤回工单状态异常
 * </p>
 *
 * @author hedongwei@wistronits.com
 * @since 2019-02-27
 */
public class RevokeProcStatusErrorException extends RuntimeException {

    public RevokeProcStatusErrorException() {
    }

    public RevokeProcStatusErrorException(String msg) {
        super(msg);
    }

}
