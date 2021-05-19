package com.fiberhome.filink.alarmcurrentserver.bean;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 告警设施名称实体类
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-05-09
 */
@Data
@TableName("alarm_template_device")
public class AlarmTemplateDevice extends Model<AlarmTemplateDevice> {

    private static final long serialVersionUID = 1L;

    @TableField("template_id")
    private String templateId;

    /**
     * 设施名称
     */
    @TableField("device_name")
    private String deviceName;

    /**
     * 设施ID
     */
    @TableField("device_id")
    private String deviceId;


    @Override
    protected Serializable pkVal() {
        return null;
    }

}
