package com.fiberhome.filink.workflowbusinessserver.bean.procbase;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 工单关联告警关系表
 * </p>
 *
 * @author chaofanrong@wistronits.com
 * @since 2019-04-18
 */
@TableName("proc_related_alarm")
@Data
public class ProcRelatedAlarm extends Model<ProcRelatedAlarm> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableField("proc_related_alarm_id")
    private String procRelatedAlarmId;

    /**
     * 工单id
     */
    @TableField("proc_id")
    private String procId;

    /**
     * 关联告警id
     */
    @TableField("ref_alarm_id")
    private String refAlarmId;

    /**
     * 关联告警code
     */
    @TableField("ref_alarm_code")
    private String refAlarmCode;

    @Override
    protected Serializable pkVal() {
        return this.procRelatedAlarmId;
    }

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
