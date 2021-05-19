package com.fiberhome.filink.alarmsetserver.bean;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * <p>
 *  告警过滤实体类
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-03-29
 */
@Data
@TableName("alarm_order_rule_device_type")
public class AlarmOrderRuleDeviceType extends Model<AlarmOrderRuleDeviceType> {

    private static final long serialVersionUID = 1L;

    /**
     * 告警转工单id
     */
    @TableField("rule_id")
    private String ruleId;

    /**
     * 设施类型id
     */
    @TableField("device_type_id")
    private String deviceTypeId;

    @Override
    protected Serializable pkVal() {
        return null;
    }
}
