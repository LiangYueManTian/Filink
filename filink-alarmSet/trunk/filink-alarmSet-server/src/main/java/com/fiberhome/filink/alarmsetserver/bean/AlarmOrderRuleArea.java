package com.fiberhome.filink.alarmsetserver.bean;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * <p>
 *
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-03-29
 */
@Data
@TableName("alarm_order_rule_area")
public class AlarmOrderRuleArea extends Model<AlarmOrderRuleArea> {

    private static final long serialVersionUID = 1L;

    /**
     * 告警转工单id
     */
    @TableField("rule_id")
    private String ruleId;

    /**
     * 区域id
     */
    @TableField("area_id")
    private String areaId;

    @Override
    protected Serializable pkVal() {
        return null;
    }
}
