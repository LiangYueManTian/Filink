package com.fiberhome.filink.alarmcurrentserver.bean;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.FieldStrategy;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 告警模板实体类
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-04-09
 */
@Data
@TableName("alarm_query_template")
public class AlarmQueryTemplate extends Model<AlarmQueryTemplate> {

    private static final long serialVersionUID = 1L;

    private String id;

    /**
     * 模板名称
     */
    @TableField("template_name")
    private String templateName;

    /**
     * 模板内容
     */
    @TableField("template_contemt")
    private String templateContemt;

    /**
     * 是否删除 1未删除 0已删除
     */
    @TableField("is_deleted")
    private String isDeleted = "0";

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Long createTime;

    /**
     * 创建用户
     */
    @TableField("create_user")
    private String createUser;

    /**
     * 告警级别
     */
    @TableField("alarm_fixed_level")
    private String alarmFixedLevel;

    /**
     * 告警名称
     */
    @TableField(exist = false)
    private List<AlarmTemplateName> alarmNameList;

    /**
     * 告警对象
     */
    @TableField(exist = false)
    private List<AlarmTemplateDevice> alarmObjectList;

    /**
     * 区域
     */
    @TableField(exist = false)
    private List<AlarmTemplateArea> areaNameList;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 单位
     */
    @TableField(exist = false)
    private List<AlarmTemplateDepartment> departmentList;
    /**
     * 用户id
     */
    @TableField("responsible_id")
    private String responsibleId;
    /**
     * 设施类型
     */
    @TableField("alarm_source_type_id")
    private String alarmSourceTypeId;

    /**
     * 频次
     */
    @TableField("alarm_happen_count")
    private Integer alarmHappenCount;

    /**
     * 清除状态
     */
    @TableField("alarm_clean_status")
    private Integer alarmCleanStatus;

    /**
     * 确认状态
     */
    @TableField("alarm_confirm_status")
    private Integer alarmConfirmStatus;

    /**
     * 首次发生时间前
     */
    @TableField(value = "alarm_begin_front_time", strategy = FieldStrategy.NOT_NULL)
    private Long alarmBeginFrontTime;

    /**
     * 首次发生时间后
     */
    @TableField(value = "alarm_begin_queen_time", strategy = FieldStrategy.NOT_NULL)
    private Long alarmBeginQueenTime;

    /**
     * 最近发生时间前
     */
    @TableField(value = "alarm_near_front_time", strategy = FieldStrategy.NOT_NULL)
    private Long alarmNearFrontTime;

    /**
     * 最近发生时间后
     */
    @TableField(value = "alarm_near_queen_time", strategy = FieldStrategy.NOT_NULL)
    private Long alarmNearQueenTime;

    /**
     * 告警持续时间
     */
    @TableField(exist = false)
    private AlarmContinous alarmContinous;

    /**
     * 确认时间前
     */
    @TableField(value = "alarm_confirm_front_time", strategy = FieldStrategy.NOT_NULL)
    private Long alarmConfirmFrontTime;

    /**
     * 确认时间前
     */
    @TableField(value = "alarm_confirm_queen_time", strategy = FieldStrategy.NOT_NULL)
    private Long alarmConfirmQueenTime;

    /**
     * 清除时间前
     */
    @TableField(value = "alarm_clean_front_time", strategy = FieldStrategy.NOT_NULL)
    private Long alarmCleanFrontTime;

    /**
     * 清除时间后
     */
    @TableField(value = "alarm_clean_queen_time", strategy = FieldStrategy.NOT_NULL)
    private Long alarmCleanQueenTime;

    /**
     * 清除用户
     */
    @TableField("alarm_clean_people_nickname")
    private String alarmCleanPeopleNickname;

    /**
     * 确认用户
     */
    @TableField("alarm_confirm_people_nickname")
    private String alarmConfirmPeopleNickname;

    /**
     * 附加消息
     */
    @TableField("extra_msg")
    private String extraMsg;

    /**
     * 告警处理建议
     */
    @TableField("alarm_processing")
    private String alarmProcessing;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}
