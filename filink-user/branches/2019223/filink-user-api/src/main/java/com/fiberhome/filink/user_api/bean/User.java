package com.fiberhome.filink.user_api.bean;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户表
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-03
 */
@Data
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private String id;

    /**
     * 用户代码
     */
    private String userCode;

    /**
     * 用户名称
     */
    private String userNickname;

    /**
     * 姓名
     */
    private String userName;

    /**
     * 用户状态
     */
    private String userStatus;

    /**
     * 部门ID
     */
    private String deptId;

    /**
     * 角色ID
     */
    private String roleId;

    /**
     * 地址
     */
    private String address;

    /**
     * 手机号
     */
    private String phonenumber;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 密码
     */
    private String password;

    /**
     * 账号有效期
     */
    private String countValidityTime;

    /**
     * 描述
     */
    private String userdesc;


    /**
     * 最后一次登录时间
     */
    private Long lastLoginTime;

    /**
     * 最后一次登录ip
     */
    private String lastLoginIp;

    /**
     * 本次登录ip
     */
    private String loginIp;

    /**
     * 登录模式，1是单用户，2是多用户
     */
    private String loginType;

    /**
     * 最大用户数，单用户模式默认为1
     */
    private Integer maxUsers;

    /**
     * 是否被删除,1没有，0已删除
     */
    private String deleted;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 修改人
     */
    private String updateUser;

    /**
     * 修改时间
     */
    private Long updateTime;
    /**
     * 区域
     */
    private String area;

    /**
     * 数据源
     */
    private String loginSourse;

    /**
     * 用户角色信息
     */
    private Role role;

    /**
     * 部门信息
     */
    private Department department;

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", userCode='" + userCode + '\'' +
                ", userNickname='" + userNickname + '\'' +
                ", userName='" + userName + '\'' +
                ", userStatus='" + userStatus + '\'' +
                ", deptId='" + deptId + '\'' +
                ", roleId='" + roleId + '\'' +
                ", address='" + address + '\'' +
                ", phonenumber='" + phonenumber + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", countValidityTime=" + countValidityTime +
                ", userdesc='" + userdesc + '\'' +
                ", lastLoginTime=" + lastLoginTime +
                ", lastLoginIp='" + lastLoginIp + '\'' +
                ", loginType='" + loginType + '\'' +
                ", maxUsers=" + maxUsers +
                ", deleted='" + deleted + '\'' +
                ", createUser='" + createUser + '\'' +
                ", createTime=" + createTime +
                ", updateUser='" + updateUser + '\'' +
                ", updateTime=" + updateTime +
                ", area='" + area + '\'' +
                '}';
    }
}
