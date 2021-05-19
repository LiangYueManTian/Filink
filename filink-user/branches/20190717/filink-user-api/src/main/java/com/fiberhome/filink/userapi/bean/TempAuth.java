package com.fiberhome.filink.userapi.bean;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 临时授权表
 * </p>
 *
 * @author xgong103@fiberhome.com
 * @since 2019-03-25
 */
@Data
public class TempAuth implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    /**
     * 授权任务名称
     */
    private String name;

    /**
     * 被授权人
     */
    private String userId;

    /**
     * 申请原因
     */
    private String applyReason;

    /**
     * 授权人
     */
    private String authUserId;

    /**
     * 申请时间
     */
    private Long applyTime;

    /**
     * 授权生效时间
     */
    private Long authEffectiveTime;

    /**
     * 授权过期时间
     */
    private Long authExpirationTime;

    /**
     * 授权状态
     */
    private Integer authStatus;

    /**
     * 备注
     */
    private String remark;

    /**
     * 审核时间
     */
    private Long auditingTime;

    /**
     * 审核的备注
     */
    private String auditingDesc;

    /**
     * 被授权用户
     */
    private User user;

    /**
     * 授权用户
     */
    private User authUser;

    private Long createTime;

    private Long updateTime;

    private String createUser;

//    private List<AuthDevice> authDeviceList;

    /**
     * 申请申请状态
     */
    private Integer applyStatus;

    /**
     * 0：为被删除 1：已删除
     */
    private Integer isDeleted;


    @Override
    public String toString() {
        return "TempAuth{"
                + "id=" + id
                + ", name=" + name
                + ", userId=" + userId
                + ", applyReason=" + applyReason
                + ", authUserId=" + authUserId
                + ", applyTime=" + applyTime
                + ", authEffectiveTime=" + authEffectiveTime
                + ", authExpirationTime=" + authExpirationTime
                + ", authStatus=" + authStatus
                + ", remark=" + remark
                + ", createTime=" + createTime
                + ", updateTime=" + updateTime
                + ", createUser=" + createUser
                + ", applyStatus=" + applyStatus
                + ", isDeleted=" + isDeleted
                + "}";
    }
}
