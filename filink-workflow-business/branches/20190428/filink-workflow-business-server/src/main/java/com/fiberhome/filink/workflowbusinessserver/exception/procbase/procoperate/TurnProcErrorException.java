package com.fiberhome.filink.workflowbusinessserver.exception.procbase.procoperate;

/**
 * <p>
 * TurnProcErrorException 转派流程异常
 * </p>
 *
 * @author hedongwei@wistronits.com
 * @since 2019-02-27
 */
public class TurnProcErrorException extends RuntimeException {

    public TurnProcErrorException() {
    }

    public TurnProcErrorException(String msg) {
        super(msg);
    }

}
