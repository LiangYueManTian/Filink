package com.fiberhome.filink.userserver.bean;

import lombok.Data;

import java.io.Serializable;

/**
 * 记录账号数和在线用户数
 *
 * @author xgong
 */
@Data
public class UserCount implements Serializable {

    /**
     * 用户账号数
     */
    private Integer userAccountNumber;

    /**
     * 在线用户数
     */
    private Integer onlineUserNumber;

    public UserCount(Integer userAccountNumber, Integer onlineUserNumber) {
        this.userAccountNumber = userAccountNumber;
        this.onlineUserNumber = onlineUserNumber;
    }
}
