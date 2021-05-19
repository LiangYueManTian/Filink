package com.fiberhome.filink.alarmcurrentserver.bean;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 告警单位实体类
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-05-09
 */
@Data
@TableName("alarm_template_department")
public class AlarmTemplateDepartment extends Model<AlarmTemplateDepartment> {

    private static final long serialVersionUID = 1L;

    @TableField("template_id")
    private String templateId;

    /**
     * 单位名称
     */
    @TableField("department_name")
    private String departmentName;

    /**
     * 单位id
     */
    @TableField("department_id")
    private String departmentId;

    @Override
    protected Serializable pkVal() {
        return null;
    }
}
