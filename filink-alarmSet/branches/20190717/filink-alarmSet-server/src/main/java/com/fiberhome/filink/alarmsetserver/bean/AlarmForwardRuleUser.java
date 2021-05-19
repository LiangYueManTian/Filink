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
 * @since 2019-03-25
 */
@Data
@TableName("alarm_forward_rule_user")
public class AlarmForwardRuleUser extends Model<AlarmForwardRuleUser> {

    private static final long serialVersionUID = 1L;

    /**
     * 告警id
     */
    @TableField("rule_id")
    private String ruleId;

    /**
     * 通知人id
     */
    @TableField("user_id")
    private String userId;

    @Override
    protected Serializable pkVal() {
        return null;
    }
}
