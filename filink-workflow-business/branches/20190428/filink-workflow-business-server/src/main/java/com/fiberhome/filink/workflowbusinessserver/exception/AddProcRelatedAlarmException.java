package com.fiberhome.filink.workflowbusinessserver.exception;

/**
 * <p>
 * AddProcRelatedAlarmException 新增工单关联告警异常
 * </p>
 *
 * @author chaofanrong@wistronits.com
 * @since 2019-04-18
 */
public class AddProcRelatedAlarmException extends RuntimeException {

    public AddProcRelatedAlarmException() {
    }

    public AddProcRelatedAlarmException(String msg) {
        super(msg);
    }

}
