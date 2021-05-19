package com.fiberhome.filink.userserver.bean;

import lombok.Data;

import java.util.List;

/**
 * 临时授权参数表
 * @author xgong
 */
@Data
public class TempAuthParameter extends PageParameter{

    /**
     * 授权任务名称
     */
    private String name;

    /**
     * 被授权人名字
     */
    private String userName;

    /**
     * 授权人名字
     */
    private String authUserName;

    /**
     * 申请原因
     */
    private String applyReason;

    /**
     * 授权状态
     */
    private Integer authStatus;

    /**
     * 备注
     */
    private String remark;

    /**
     *授权时间
     */
    private Long createTime;

    /**
     * 查询授权时间的截止时间
     */
    private Long createTimeEnd;

    /**
     * 授权时间的排序规则
     */
    private String createTimeRelation;

    /**
     * 生效时间
     */
    private Long authEffectiveTime;

    /**
     * 生效时间
     */
    private Long authEffectiveTimeEnd;

    /**
     * 生效时间的排序规则
     */
    private String authEffectiveTimeRelation;

    /**
     * 失效时间
     */
    private Long authExpirationTime;

    /**
     * 失效时间
     */
    private Long authExpirationTimeEnd;

    /**
     * 过期时间的排序规则
     */
    private String authExpirationTimeRelation;

    /**
     * 审核时间
     */
    private Long auditingTime;

    /**
     * 审核时间
     */
    private Long auditingTimeEnd;

    /**
     * 审核时间的排序规则
     */
    private String auditingTimeRelation;

    /**
     * 审核描述
     */
    private String auditingDesc;

    /**
     * 批量处理数据时候的id数组
     */
    private String[] idList;

    /**
     * 区域id列表
     */
    private List<String> areaIdList;

    /**
     * 角色能管理的设施类型列表
     */
    private List<String> roleDeviceIdList;

    /**
     * 批量审核信息时候，给对应的用户推送消息
     */
    private List<String> userIdList;
}
