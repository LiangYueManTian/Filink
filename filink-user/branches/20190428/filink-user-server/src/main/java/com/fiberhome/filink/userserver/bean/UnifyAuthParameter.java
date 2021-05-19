package com.fiberhome.filink.userserver.bean;

import lombok.Data;

/**
 * 统一授权参数类
 * @author xgong
 */
@Data
public class UnifyAuthParameter extends PageParameter{

    /**
     * 授权任务名称
     */
    private String name;

    /**
     * 授权用户名称
     */
    private String authUserName;

    /**
     *授权时间
     */
    private Long createTime;

    /**
     * 查询授权时间截止时间
     */
    private Long createTimeEnd;

    /**
     * 授权时间的排序规则
     */
    private String createTimeRelation;

    /**
     * 被授权用户
     */
    private String userName;

    /**
     * 生效时间
     */
    private Long authEffectiveTime;

    /**
     * 查询生效时间
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
     * 查询失效时间截止时间
     */
    private Long authExpirationTimeEnd;

    /**
     * 过期时间的排序规则
     */
    private String authExpirationTimeRelation;

    /**
     * 权限状态
     */
    private Integer authStatus;

    /**
     * 备注
     */
    private String remark;

    /**
     * 批量操作统一授权信息的id数组
     */
    private String[] idArray;

    /**
     * 当前用户id
     */
    private String currentUserId;
}
