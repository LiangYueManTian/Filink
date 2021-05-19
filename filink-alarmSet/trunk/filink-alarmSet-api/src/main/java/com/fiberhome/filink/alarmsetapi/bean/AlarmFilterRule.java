package com.fiberhome.filink.alarmsetapi.bean;


import java.util.List;
import java.util.Set;
import lombok.Data;

/**
 * <p>
 *    告警过滤规则实体类
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-02-26
 */
@Data
public class AlarmFilterRule  {

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
     * 1启用0禁用
     */
    private Integer status;

    /**
     * 是否存库(1默认保存2不保存)
     */
    private Integer stored;

    /**
     * 起始时间
     */
    private Long beginTime;

    /**
     * 结束时间
     */
    private Long endTime;

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
     * 操作用户
     */
    private String operationUser;

    /**
     * 告警名称id集合
     */
    private Set<String> alarmFilterRuleNameList;

    /**
     * 告警名称
     */
    private List<String> alarmFilterRuleNames;

    /**
     * 告警源id集合
     */
    private Set<String> alarmFilterRuleSourceList;

    /**
     * 告警源名称
     */
    private List<String> alarmFilterRuleSourceName;

}
