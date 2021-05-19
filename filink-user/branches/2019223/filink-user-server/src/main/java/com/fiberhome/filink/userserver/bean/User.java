package com.fiberhome.filink.userserver.bean;

import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 *    用户表
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-03
 */
@Data
@TableName("user")
public class User extends Model<User> {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private String id;

    /**
     * 用户代码
     */
    @TableField("user_code")
    private String userCode;

    /**
     * 用户名称
     */
    @TableField("user_nickname")
    private String userNickname;

    /**
     * 姓名
     */
    @TableField("user_name")
    private String userName;

    /**
     * 用户状态
     */
    @TableField("user_status")
    private String userStatus;

    /**
     * 部门ID
     */
    @TableField("dept_id")
    private String deptId;

    /**
     * 角色ID
     */
    @TableField("role_id")
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
    @TableField("count_validity_time")
    private String countValidityTime;

    /**
     * 描述
     */
    private String userdesc;


    /**
     * 最后一次登录时间
     */
    @TableField("last_login_time")
    private Long lastLoginTime;

    /**
     * 最后一次登录ip
     */
    @TableField("last_login_ip")
    private String lastLoginIp;

    /**
     * 当前登录ip
     */
    @TableField("loginIp")
    private String loginIp;

    /**
     * 登录模式，1是单用户，2是多用户
     */
    @TableField("login_type")
    private String loginType;

    /**
     * 最大用户数，单用户模式默认为1
     */
    @TableField("max_users")
    private Integer maxUsers;

    /**
     * 是否被删除,0没有，1已删除
     */
    @TableField("is_deleted")
    private String deleted;

    /**
     * 创建人
     */
    @TableField("create_user")
    private String createUser;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Long createTime;

    /**
     * 修改人
     */
    @TableField("update_user")
    private String updateUser;

    /**
     * 修改时间
     */
    @TableField("update_time")
    private Long updateTime;
    /**
     * 区域
     */
    private String area;

    /**
     * 数据源
     */
    @TableField("login_sourse")
    private String loginSourse;

    @TableField("login_time")
    private Long loginTime;

    @TableField(exist = false)
    private Role role;

    @TableField(exist = false)
    private Department department;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

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
