package com.fiberhome.filink.alarmsetserver.bean;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * <p>
 * <p>
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-03-26
 */
@Data
@TableName("alarm_forward_rule_area")
public class AlarmForwardRuleArea extends Model<AlarmForwardRuleArea> {

    private static final long serialVersionUID = 1L;

    /**
     * 告警id
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

    @Override
    public String toString() {
        return "AlarmForwardRuleArea{" + "ruleId=" + ruleId + ", areaId=" + areaId + "}";
    }
}
