package com.fiberhome.filink.alarmsetserver.bean;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * <p>
 * 告警远程通知的告警级别实体类
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-04-02
 */
@Data
@TableName("alarm_forward_rule_level")
public class AlarmForwardRuleLevel extends Model<AlarmForwardRuleLevel> {

    private static final long serialVersionUID = 1L;

    /**
     * 告警id
     */
    @TableField("rule_id")
    private String ruleId;

    /**
     * 告警级别id
     */
    @TableField("alarm_level_id")
    private String alarmLevelId;

    @Override
    protected Serializable pkVal() {
        return null;
    }
}
