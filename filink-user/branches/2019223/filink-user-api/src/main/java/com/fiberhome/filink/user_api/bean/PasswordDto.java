package com.fiberhome.filink.user_api.bean;

import lombok.Data;

/**
 * 密码实体信息
 * @author xuangong
 */

@Data
public class PasswordDto {

    /**
     * 用户登录token
     */
    private String token;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 用户输入的新密码
     */
    private String newPWD;

    /**
     * 用户输入的旧密码
     */
    private String oldPWD;

    /**
     * 用户输入的确认密码
     */
    private String confirmPWD;
}
