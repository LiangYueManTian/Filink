package com.fiberhome.filink.alarmsetserver.bean;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * <p>
 *  告警转工单规则实体类
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-02-27
 */
@Data
@TableName("alarm_order_rule")
public class AlarmOrderRule extends Model<AlarmOrderRule> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private String id;

    /**
     * 规则名称
     */
    @TableField("order_name")
    private String orderName;

    /**
     * 工单类型(1巡检工单2消障工单)
     */
    @TableField("order_type")
    private Integer orderType;
    /**
     * 责任单位
     */
    @TableField("department_id")
    private String departmentId;

    /**
     * 触发条件(0告警发生时触发)
     */
    @TableField("trigger_type")
    private Integer triggerType;

    /**
     * 期待完工时长
     */
    @TableField("completion_time")
    private Integer completionTime;

    /**
     * 1启用2禁用
     */
    private Integer status;

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
     * 创建人
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
     * 设施类型id
     */
    @TableField(exist = false)
    private List<AlarmOrderRuleDeviceType> alarmOrderRuleDeviceTypeList;

    /**
     * 区域id
     */
    @TableField(exist = false)
    private Set<String> alarmOrderRuleArea;

    /**
     * 区域名称
     */
    @TableField(exist = false)
    private List<String> alarmOrderRuleAreaName;

    /**
     * 告警名称id
     */
    @TableField(exist = false)
    private Set<String> alarmOrderRuleNameList;

    /**
     * 告警名称
     */
    @TableField(exist = false)
    private List<String> alarmOrderRuleNames;
    /**
     * 责任单位名称
     */
    @TableField(exist = false)
    private String departmentName;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}
