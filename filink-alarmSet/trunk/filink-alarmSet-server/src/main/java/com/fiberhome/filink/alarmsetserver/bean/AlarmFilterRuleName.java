package com.fiberhome.filink.alarmsetserver.bean;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * <p>
 * 告警过滤规则的告警名称
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-03-18
 */
@Data
@TableName("alarm_filter_rule_name")
public class AlarmFilterRuleName extends Model<AlarmFilterRuleName> {

    private static final long serialVersionUID = 1L;

    /**
     * 告警名称id
     */
    @TableId("alarm_name_id")
    private String alarmNameId;

    /**
     * 告警id
     */
    @TableField("rule_id")
    private String ruleId;

    @Override
    protected Serializable pkVal() {
        return null;
    }
}
