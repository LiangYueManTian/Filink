package com.fiberhome.filink.alarmsetserver.bean;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * <p>
 * 告警过滤规则的告警告警源
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-03-18
 */
@Data
@TableName("alarm_filter_rule_source")
public class AlarmFilterRuleSource extends Model<AlarmFilterRuleSource> {

    private static final long serialVersionUID = 1L;

    /**
     * 告警源id
     */
    @TableId("alarm_source")
    private String alarmSource;

    /**
     * 告警过滤规则id
     */
    @TableField("rule_id")
    private String ruleId;


    @Override
    protected Serializable pkVal() {
        return null;
    }

    @Override
    public String toString() {
        return "AlarmFilterRuleSource{" + "alarmSource='" + alarmSource + '\'' + ", ruleId='" + ruleId + '\'' + '}';
    }
}
