package com.fiberhome.filink.workflowbusinessserver.exception.procbase.procoperate;

/**
 * <p>
 * SingleBackProcErrorException 退单流程异常
 * </p>
 *
 * @author hedongwei@wistronits.com
 * @since 2019-02-27
 */
public class SingleBackProcErrorException extends RuntimeException {

    public SingleBackProcErrorException() {
    }

    public SingleBackProcErrorException(String msg) {
        super(msg);
    }

}
