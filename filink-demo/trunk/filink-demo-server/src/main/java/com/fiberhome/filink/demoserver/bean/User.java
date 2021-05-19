package com.fiberhome.filink.demoserver.bean;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import java.io.Serializable;

/**
 * 实体类示例
 * 此实体类是使用mybaties-plus根据mysql表生成的，需要提前在数据库建表并写好注释
 *
 * 具体生成方法见
 * @see com.fiberhome.filink.demoserver.utils.CodeGenerator
 *
 *
 * <p>
 * 用户信息表
 * </p>
 *
 * @author 姚远
 * @since 2019-09-18
 */
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
     * 用户状态,1启用，0停用
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
    @TableField("user_desc")
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
     * 登录模式，1是单用户，2是多用户
     */
    @TableField("login_type")
    private String loginType;

    /**
     * 解禁时间
     */
    @TableField("unlocked_time")
    private Long unlockedTime;

    /**
     * 登录源，1是Web网管，0是APP
     */
    @TableField("login_sourse")
    private String loginSourse;

    /**
     * 最大用户数，单用户模式默认为1
     */
    @TableField("max_users")
    private Integer maxUsers;

    /**
     * 是否被删除,0没有，1已删除
     */
    @TableField("is_deleted")
    private String isDeleted;

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

    @TableField("login_time")
    private Long loginTime;

    private String loginIp;

    private String pushId;

    /**
     * 国家编码
     */
    @TableField("country_code")
    private String countryCode;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }
    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }
    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }
    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public String getCountValidityTime() {
        return countValidityTime;
    }

    public void setCountValidityTime(String countValidityTime) {
        this.countValidityTime = countValidityTime;
    }
    public String getUserDesc() {
        return userDesc;
    }

    public void setUserDesc(String userDesc) {
        this.userDesc = userDesc;
    }
    public Long getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Long lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }
    public String getLastLoginIp() {
        return lastLoginIp;
    }

    public void setLastLoginIp(String lastLoginIp) {
        this.lastLoginIp = lastLoginIp;
    }
    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }
    public Long getUnlockedTime() {
        return unlockedTime;
    }

    public void setUnlockedTime(Long unlockedTime) {
        this.unlockedTime = unlockedTime;
    }
    public String getLoginSourse() {
        return loginSourse;
    }

    public void setLoginSourse(String loginSourse) {
        this.loginSourse = loginSourse;
    }
    public Integer getMaxUsers() {
        return maxUsers;
    }

    public void setMaxUsers(Integer maxUsers) {
        this.maxUsers = maxUsers;
    }
    public String getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(String isDeleted) {
        this.isDeleted = isDeleted;
    }
    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }
    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }
    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }
    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }
    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }
    public Long getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Long loginTime) {
        this.loginTime = loginTime;
    }
    public String getLoginIp() {
        return loginIp;
    }

    public void setLoginIp(String loginIp) {
        this.loginIp = loginIp;
    }
    public String getPushId() {
        return pushId;
    }

    public void setPushId(String pushId) {
        this.pushId = pushId;
    }
    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "User{" +
        "id=" + id +
        ", userCode=" + userCode +
        ", userNickname=" + userNickname +
        ", userName=" + userName +
        ", userStatus=" + userStatus +
        ", deptId=" + deptId +
        ", roleId=" + roleId +
        ", address=" + address +
        ", phoneNumber=" + phoneNumber +
        ", email=" + email +
        ", password=" + password +
        ", countValidityTime=" + countValidityTime +
        ", userDesc=" + userDesc +
        ", lastLoginTime=" + lastLoginTime +
        ", lastLoginIp=" + lastLoginIp +
        ", loginType=" + loginType +
        ", unlockedTime=" + unlockedTime +
        ", loginSourse=" + loginSourse +
        ", maxUsers=" + maxUsers +
        ", isDeleted=" + isDeleted +
        ", createUser=" + createUser +
        ", createTime=" + createTime +
        ", updateUser=" + updateUser +
        ", updateTime=" + updateTime +
        ", area=" + area +
        ", loginTime=" + loginTime +
        ", loginIp=" + loginIp +
        ", pushId=" + pushId +
        ", countryCode=" + countryCode +
        "}";
    }
}
