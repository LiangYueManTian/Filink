package com.fiberhome.filink.alarmsetserver.bean;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * <p>
 * 告警转工单规则告警名称
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-03-29
 */
@Data
@TableName("alarm_order_rule_name")
public class AlarmOrderRuleName extends Model<AlarmOrderRuleName> {

    private static final long serialVersionUID = 1L;

    /**
     * 告警转工单id
     */
    @TableField("rule_id")
    private String ruleId;

    /**
     * 告警名称id
     */
    @TableField("alarm_name_id")
    private String alarmNameId;

    @Override
    protected Serializable pkVal() {
        return null;
    }
}
