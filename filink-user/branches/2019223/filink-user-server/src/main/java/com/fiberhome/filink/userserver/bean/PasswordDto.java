package com.fiberhome.filink.userserver.bean;

import lombok.Data;

/**
 * 密码结果集
 * @author xuangong
 */
@Data
public class PasswordDto {

    /**
     * token信息
     */
    private String token;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 新密码
     */
    private String newPWD;

    /**
     * 旧密码
     */
    private String oldPWD;

    /**
     * 确认密码
     */
    private String confirmPWD;
}
