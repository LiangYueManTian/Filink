package com.fiberhome.filink.userserver.bean;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fiberhome.filink.exportapi.utils.ExportApiUtils;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.userserver.consts.UserConst;
import com.fiberhome.filink.userserver.consts.UserI18n;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author xgong103@fiberhome.com
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
     * 用户账号
     */
    @TableField("user_code")
    private String userCode;

    /**
     * 用户昵称
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
    @TableField("phone_number")
    private String phoneNumber;

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
    private String userDesc;


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
     * 解锁时间
     */
    @TableField("unlocked_time")
    private Long unlockTime;

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

    /**
     * 手机设备id
     */
    @TableField(exist = false)
    private String pushId;

    /**
     * 国家编码
     */
    @TableField("country_code")
    private String countryCode;

    @TableField(exist = false)
    private Role role;

    @TableField(exist = false)
    private Department department;

    /**
     * 登录用户的token
     */
    @TableField(exist = false)
    private String token;

    /**
     * app的唯一标识
     */
    @TableField(exist = false)
    private String appKey;

    /**
     * 账号的有效期
     */
    @TableField(exist = false)
    private Integer expireTime;

    /**
     * 手机类型0:android  1:ios
     */
    @TableField(exist = false)
    private Integer phoneType;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }


    @JsonIgnore
    public String getTranslationRole() {
        if (this.role == null) {
            return null;
        }
        return this.role.getRoleName();
    }

    @JsonIgnore
    public String getTranslationDepartment() {

        if (this.department == null) {
            return null;
        }
        return this.department.getDeptName();
    }

    @JsonIgnore
    public String getTranslationUserStatus() {

        return UserConst.START_STATUS_TIP.equals(userStatus)
                ? I18nUtils.getString(ExportApiUtils.getExportLocales(),UserI18n.START_STATUS)
                : I18nUtils.getString(ExportApiUtils.getExportLocales(),UserI18n.START_NOT_STATUS);
    }

    @JsonIgnore
    public String getTranslationLastLoginTime() {
        if (ObjectUtils.isEmpty(this.lastLoginTime)) {
            return null;
        }
        return ExportApiUtils.getZoneTime(this.lastLoginTime);
    }

    @JsonIgnore
    public String getTranslationLoginType() {


        String status = UserConst.SINGLE_LOGIN_TIP.equals(this.loginType)
                ? I18nUtils.getString(ExportApiUtils.getExportLocales(),UserI18n.SINGLE_LOGIN_TYPE)
                : I18nUtils.getString(ExportApiUtils.getExportLocales(),UserI18n.MUITL_LOGIN_TYPE);
        return status;
    }

    @JsonIgnore
    public String getTranslationCountValidityTime() {

        if (StringUtils.isNotEmpty(this.countValidityTime)) {
            if (this.countValidityTime.endsWith(UserConst.DAY_TIP)) {
                return this.countValidityTime.substring(0, this.countValidityTime.length() - 1)
                        + I18nUtils.getString(ExportApiUtils.getExportLocales(),UserI18n.DAY_UNIT);
            } else if (this.countValidityTime.endsWith(UserConst.MOUNTH_TIP)) {
                return this.countValidityTime.substring(0, this.countValidityTime.length() - 1)
                        + I18nUtils.getString(ExportApiUtils.getExportLocales(),UserI18n.MOUNTH_UNIT);
            } else {
                return this.countValidityTime.substring(0, this.countValidityTime.length() - 1) + UserConst.YEAR_UNIT;
            }
        }
        return this.countValidityTime;
    }

    @Override
    public String toString() {
        return "User{"
                + "id='" + id + '\''
                + ", userCode='" + userCode + '\''
                + ", userNickname='" + userNickname + '\''
                + ", userName='" + userName + '\''
                + ", userStatus='" + userStatus + '\''
                + ", deptId='" + deptId + '\''
                + ", roleId='" + roleId + '\''
                + ", address='" + address + '\''
                + ", phoneNumber='" + phoneNumber + '\''
                + ", email='" + email + '\''
                + ", password='" + password + '\''
                + ", countValidityTime='" + countValidityTime + '\''
                + ", userDesc='" + userDesc + '\''
                + ", lastLoginTime=" + lastLoginTime
                + ", lastLoginIp='" + lastLoginIp + '\''
                + ", loginIp='" + loginIp + '\''
                + ", loginType='" + loginType + '\''
                + ", maxUsers=" + maxUsers
                + ", deleted='" + deleted + '\''
                + ", unlockTime=" + unlockTime
                + ", createUser='" + createUser + '\''
                + ", createTime=" + createTime
                + ", updateUser='" + updateUser + '\''
                + ", updateTime=" + updateTime
                + ", area='" + area + '\''
                + ", loginSourse='" + loginSourse + '\''
                + ", loginTime=" + loginTime
                + ", pushId='" + pushId + '\''
                + ", countryCode=" + countryCode
                + ", role=" + role
                + ", department=" + department
                + ", token='" + token + '\''
                + ", appKey='" + appKey + '\''
                + ", expireTime=" + expireTime
                + '}';
    }
}
