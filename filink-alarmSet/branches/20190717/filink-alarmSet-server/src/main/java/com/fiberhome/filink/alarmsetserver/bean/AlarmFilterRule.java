package com.fiberhome.filink.alarmsetserver.bean;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;
import java.util.List;
import java.util.Set;
import lombok.Data;

/**
 * <p>
 * 告警过滤实体类
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-03-01
 */
@Data
@TableName("alarm_filter_rule")
public class AlarmFilterRule extends Model<AlarmFilterRule> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private String id;

    /**
     * 用户id
     */
    @TableField("user_id")
    private String userId;

    /**
     * 规则名称
     */
    @TableField("rule_name")
    private String ruleName;

    /**
     * 1启用2禁用
     */
    private Integer status;

    /**
     * 是否存库(1默认保存2不保存)
     */
    private Integer stored;

    /**
     * 起始时间
     */
    @TableField("begin_time")
    private Long beginTime;

    /**
     * 结束时间
     */
    @TableField("end_time")
    private Long endTime;

    /**
     * 是否删除，1已删除，0未删除
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
     * 修改时间
     */
    @TableField("update_time")
    private Long updateTime;

    /**
     * 修改用户
     */
    @TableField("update_user")
    private String updateUser;

    /**
     * 操作用户
     */
    @TableField("operation_user")
    private String operationUser;

    /**
     * 告警名称id集合
     */
    @TableField(exist = false)
    private Set<String> alarmFilterRuleNameList;

    /**
     * 告警名称
     */
    @TableField(exist = false)
    private List<String> alarmFilterRuleNames;

    /**
     * 告警源id集合
     */
    @TableField(exist = false)
    private Set<String> alarmFilterRuleSourceList;

    /**
     * 告警源名称
     */
    @TableField(exist = false)
    private List<String> alarmFilterRuleSourceName;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}
