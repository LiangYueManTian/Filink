package com.fiberhome.filink.workflowbusinessserver.exception.procbase.procoperate;

/**
 * <p>
 * RevokeProcErrorException 撤回流程异常
 * </p>
 *
 * @author hedongwei@wistronits.com
 * @since 2019-02-27
 */
public class RevokeProcErrorException extends RuntimeException {

    public RevokeProcErrorException() {
    }

    public RevokeProcErrorException(String msg) {
        super(msg);
    }

}
