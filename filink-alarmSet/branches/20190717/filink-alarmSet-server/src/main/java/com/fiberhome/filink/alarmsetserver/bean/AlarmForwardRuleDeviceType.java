package com.fiberhome.filink.alarmsetserver.bean;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * <p>
 * 告警远程通知设施实体类
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-03-26
 */
@Data
@TableName("alarm_forward_rule_device_type")
public class AlarmForwardRuleDeviceType extends Model<AlarmForwardRuleDeviceType> {

    private static final long serialVersionUID = 1L;

    /**
     * 告警id
     */
    @TableField("rule_id")
    private String ruleId;

    /**
     * 告警设施id
     */
    @TableField("device_type_id")
    private String deviceTypeId;

    @Override
    protected Serializable pkVal() {
        return null;
    }

    @Override
    public String toString() {
        return "AlarmForwardRuleDeviceType{" + "ruleId=" + ruleId + ", deviceTypeId=" + deviceTypeId + "}";
    }
}
