package com.fiberhome.filink.alarmcurrentserver.bean;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 告警区域实体类
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-05-09
 */
@Data
@TableName("alarm_template_area")
public class AlarmTemplateArea extends Model<AlarmTemplateArea> {

    private static final long serialVersionUID = 1L;

    @TableField("template_id")
    private String templateId;

    /**
     * 区域名称
     */
    @TableField("area_name")
    private String areaName;

    /**
     * 区域Id
     */
    @TableField("area_Id")
    private String areaId;

    @Override
    protected Serializable pkVal() {
        return null;
    }

}
