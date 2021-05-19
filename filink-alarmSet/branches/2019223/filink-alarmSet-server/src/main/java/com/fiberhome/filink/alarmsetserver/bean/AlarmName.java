package com.fiberhome.filink.alarmsetserver.bean;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 *  当前告警名称设置实体类
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-22
 */
@Data
@TableName("alarm_name")
public class AlarmName extends Model<AlarmName> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private String id;

    /**
     * 告警名称
     */
    @TableField("alarm_name")
    private String alarmName;

    /**
     * 定制级别
     */
    @TableField("alarm_level")
    private String alarmLevel;

    /**
     * 告警描述
     */
    @TableField("alarm_desc")
    private String alarmDesc;

    /**
     * 告警编码
     */
    @TableField("alarm_code")
    private String alarmCode;

    /**
     * 默认级别
     */
    @TableField("alarm_default_level")
    private String alarmDefaultLevel;

    /**
     * 是否自动确认 0是未确认 1已确认
     */
    @TableField("alarm_automatic_confirmation")
    private String alarmAutomaticConfirmation;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "AlarmName{" +
                "id='" + id + '\'' +
                ", alarmName='" + alarmName + '\'' +
                ", alarmLevel='" + alarmLevel + '\'' +
                ", alarmDesc='" + alarmDesc + '\'' +
                ", alarmCode='" + alarmCode + '\'' +
                ", alarmDefaultLevel='" + alarmDefaultLevel + '\'' +
                ", alarmAutomaticConfirmation='" + alarmAutomaticConfirmation + '\'' +
                '}';
    }
}
