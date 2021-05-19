package com.fiberhome.filink.userapi.bean;

import lombok.Data;

import java.io.Serializable;

/**
 * 记录账号数和在线用户数
 * @author Administrator
 */
@Data
public class UserCount implements Serializable{

    /**
     * 用户账号数
     */
    private Integer userAccountNumber;

    /**
     * 在线用户数
     */
    private Integer onlineUserNumber;
}
