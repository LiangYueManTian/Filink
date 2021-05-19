package com.fiberhome.filink.alarmsetapi.bean;


import java.util.List;
import java.util.Set;
import lombok.Data;

/**
 * <p>
 *      告警远程通知规则实体类
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-02-21
 */
@Data
public class AlarmForwardRule  {

    private static final long serialVersionUID = 1L;
    /**
     * 主键id
     */
    private String id;

    /**
     * 规则名称
     */
    private String ruleName;

    /**
     * 告警类型
     */
    private String alarmType;

    /**
     * 1启用2禁用
     */
    private Integer status;

    /**
     * 是否删除，1已删除，0没删除
     */
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
    private Long createTime;

    /**
     * 创建用户
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
     * 通知人id
     */
    private Set<String> alarmForwardRuleUserList;

    /**
     * 通知人名称
     */
    private List<String> alarmForwardRuleUserName;

    /**
     * 区域id
     */
    private Set<String> alarmForwardRuleAreaList;

    /**
     * 区域名称
     */
    private List<String> alarmForwardRuleAreaName;

    /**
     * 设施类型id
     */
    private List<AlarmForwardRuleDeviceType> alarmForwardRuleDeviceTypeList;

    /**
     * 告警级别
     */
    private List<AlarmForwardRuleLevel> alarmForwardRuleLevels;


}
