package com.fiberhome.filink.alarmcurrentserver.bean;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 告警名称实体类
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-05-09
 */
@Data
@TableName("alarm_template_name")
public class AlarmTemplateName extends Model<AlarmTemplateName> {

    private static final long serialVersionUID = 1L;

    @TableField("template_id")
    private String templateId;

    /**
     * 告警名称
     */
    @TableField("alarm_name")
    private String alarmName;

    /**
     * 告警名称ID
     */
    @TableField("alarm_name_id")
    private String alarmNameId;


    @Override
    protected Serializable pkVal() {
        return null;
    }

}
