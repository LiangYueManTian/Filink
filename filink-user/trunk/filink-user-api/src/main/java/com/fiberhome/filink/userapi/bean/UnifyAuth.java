package com.fiberhome.filink.userapi.bean;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * <p>
 * 统一授权
 * </p>
 *
 * @author xgong103@fiberhome.com
 * @since 2019-03-25
 */
@Data
public class UnifyAuth implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    /**
     * 授权任务名称
     */
    private String name;

    /**
     * 被授权用户
     */
    private String userId;

    /**
     * 授权用户
     */
    private String authUserId;

    /**
     * 授权生效时间
     */
    private Long authEffectiveTime;

    /**
     * 授权失效时间
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
     * 授权时间
     */
    private Long createTime;

    /**
     * 0：未删除 1：删除
     */
    private Integer isDeleted;

    private String createUser;

    private Long updateTime;

    /**
     * 存放设施数据，key为设施的门id，value为设施id
     */
    private Map<String, String> deviceMap;

    /**
     * 被授权用户
     */
    private User user;

    /**
     * 授权用户
     */
    private User authUser;

    @Override
    public String toString() {
        return "UnifyAuth{" +
                "id=" + id +
                ", name=" + name +
                ", userId=" + userId +
                ", authUserId=" + authUserId +
                ", authEffectiveTime=" + authEffectiveTime +
                ", authExpirationTime=" + authExpirationTime +
                ", authStatus=" + authStatus +
                ", remark=" + remark +
                ", createTime=" + createTime +
                ", isDeleted=" + isDeleted +
                ", createUser=" + createUser +
                ", updateTime=" + updateTime +
                "}";
    }
}
