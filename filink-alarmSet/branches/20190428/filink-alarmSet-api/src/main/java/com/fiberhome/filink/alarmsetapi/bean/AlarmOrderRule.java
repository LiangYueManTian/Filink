package com.fiberhome.filink.alarmsetapi.bean;


import java.util.List;
import java.util.Set;
import lombok.Data;

/**
 * <p>
 *     告警转工单规则实体类
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-02-21
 */
@Data
public class AlarmOrderRule  {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private String id;

    /**
     * 规则名称
     */
    private String orderName;

    /**
     * 工单类型(0巡检工单1消障工单)
     */
    private Integer orderType;

    /**
     * 触发条件(0告警发生时触发)
     */
    private Integer triggerType;

    /**
     * 期待完工时长
     */
    private Integer completionTime;

    /**
     * 1启用2禁用
     */
    private Integer status;

    /**
     * 是否删除，1已删除，0未删除
     */
    private String isDeleted = "0";

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 修改时间
     */
    private Long updateTime;

    /**
     * 修改用户
     */
    private String updateUser;

    /**
     * 设施类型id
     */
    private List<AlarmOrderRuleDeviceType> alarmOrderDeviceType;

    /**
     * 区域id
     */
    private Set<String> alarmOrderRuleArea;

    /**
     * 区域名称
     */
    private List<String> alarmOrderRuleAreaName;

    /**
     * 告警名称id
     */
    private Set<String> alarmOrderRuleNameList;

    /**
     * 告警名称
     */
    private List<String> alarmOrderRuleNames;
}
