package com.fiberhome.filink.workflowbusinessapi.bean.procbase;

import lombok.Data;

/**
 * <p>
 * 工单关联告警关系表
 * </p>
 *
 * @author chaofanrong@wistronits.com
 * @since 2019-04-18
 */
@Data
public class ProcRelatedAlarm {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private String procRelatedAlarmId;

    /**
     * 工单id
     */
    private String procId;

    /**
     * 关联告警id
     */
    private String refAlarmId;

    /**
     * 关联告警code
     */
    private String refAlarmCode;

    @Override
    public String toString() {
        return "ProcRelatedAlarm{" +
        "procRelatedAlarmId=" + procRelatedAlarmId +
        ", procId=" + procId +
        ", refAlarmId=" + refAlarmId +
        ", refAlarmCode=" + refAlarmCode +
        "}";
    }
}
