package com.fiberhome.filink.userserver.bean;

import lombok.Data;

/**
 * 人员授权信息参数表
 * @author xgong
 */
@Data
public class UserAuthInfo {

    /**
     * 人员id
     */
    private String userId;

    /**
     * 设施id
     */
    private String deviceId;

    /**
     * 门编号
     */
    private String doorId;

    /**
     * 当前时间
     */
    private Long currentTime;
}
