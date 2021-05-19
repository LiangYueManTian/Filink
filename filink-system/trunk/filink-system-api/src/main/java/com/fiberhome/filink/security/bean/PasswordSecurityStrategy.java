package com.fiberhome.filink.security.bean;

import lombok.Data;

/**
 * <p>
 *     密码
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-02-20
 */
@Data
public class PasswordSecurityStrategy {
    /**
     * 密码最小长度
     */
    private Integer minLength;
    /**
     * 密码至少包含一个大写字母
     */
    private String containUpper;
    /**
     * 密码至少包含一个小写字母
     */
    private String containLower;
    /**
     * 密码至少包含一个数字
     */
    private String containNumber;
    /**
     * 密码至少包含一个特殊字符
     */
    private String containSpecialCharacter;
}
