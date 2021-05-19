package com.fiberhome.filink.userserver.bean;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 在线用户
 *
 * @author wtao103@fiberhome.com
 * @since 2019-03-08
 */
@TableName("online_user")
@Data
public class OnlineUser extends Model<OnlineUser> {

    private static final long serialVersionUID = 1L;

    private String id;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 用户编码
     */
    private String userCode;

    /**
     * 用户昵称
     */
    private String userNickname;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 地址信息
     */
    private String address;

    /**
     * 手机号码
     */
    private String phoneNumber;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 角色名
     */
    private String roleName;

    /**
     * 登录时间
     */
    private Long loginTime;

    /**
     * 登录ip
     */
    private String loginIp;

    /**
     * 登录源
     */
    private String loginSource;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "OnlineUser{"
                + "id=" + id
                + ", userId=" + userId
                + ", userCode=" + userCode
                + ", userNickname=" + userNickname
                + ", userName=" + userName
                + ", deptName=" + deptName
                + ", address=" + address
                + ", phoneNumber=" + phoneNumber
                + ", email=" + email
                + ", roleName=" + roleName
                + ", loginTime=" + loginTime
                + ", loginIp=" + loginIp
                + ", loginSource=" + loginSource
                + "}";
    }
}
