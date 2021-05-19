package com.fiberhome.filink.alarmcurrentserver.bean;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * <p>
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-05-15
 */
@Data
@TableName("alarm_continous")
public class AlarmContinous extends Model<AlarmContinous> {

    private static final long serialVersionUID = 1L;

    private String id;

    /**
     * 告警持续时间
     */
    @TableField("alarm_continous_time")
    private Integer alarmContinousTime;

    /**
     * 告警比较
     */
    @TableField("alarm_cmpare")
    private String alarmCmpare;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
