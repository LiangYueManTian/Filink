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
 *  告警远程通知实体类
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-02-25
 */
@Data
@TableName("alarm_forward_rule")
public class AlarmForwardRule extends Model<AlarmForwardRule> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private String id;

    /**
     * 规则名称
     */
    @TableField("rule_name")
    private String ruleName;

    /**
     * 告警类型
     */
    @TableField("alarm_type")
    private String alarmType;

    /**
     * 1启用2禁用
     */
    private Integer status;

    /**
     * 是否删除，1已删除，0没删除
     */
    @TableField("is_deleted")
    private String isDeleted = "0";

    /**
     * 推送方式(1短信0邮件)
     */
    private Integer pushType;

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
     * 通知人id
     */
    @TableField(exist = false)
    private Set<String> alarmForwardRuleUserList;

    /**
     * 通知人名称
     */
    @TableField(exist = false)
    private List<String> alarmForwardRuleUserName;

    /**
     * 区域id
     */
    @TableField(exist = false)
    private Set<String> alarmForwardRuleAreaList;

    /**
     * 区域名称
     */
    @TableField(exist = false)
    private List<String> alarmForwardRuleAreaName;

    /**
     * 设施类型id
     */
    @TableField(exist = false)
    private List<AlarmForwardRuleDeviceType> alarmForwardRuleDeviceTypeList;

    /**
     * 告警级别
     */
    @TableField(exist = false)
    private List<AlarmForwardRuleLevel> alarmForwardRuleLevels;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}
