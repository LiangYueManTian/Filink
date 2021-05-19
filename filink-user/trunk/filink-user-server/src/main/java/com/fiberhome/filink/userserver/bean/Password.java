package com.fiberhome.filink.userserver.bean;

import lombok.Data;

/**
 * @author xgong
 */
@Data
public class Password {
    /**
     * token
     */
    private String token;
    /**
     * userId
     */
    private String userId;
    /**
     * 新密码
     */
    private String newPWD;
    /**
     * 老密码
     */
    private String oldPWD;
    /**
     * 确认密码
     */
    private String confirmPWD;
}
