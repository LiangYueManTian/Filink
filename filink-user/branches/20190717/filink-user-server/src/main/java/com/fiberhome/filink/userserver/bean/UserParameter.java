package com.fiberhome.filink.userserver.bean;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-03
 */
@Data
public class UserParameter implements Serializable {

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
    private String countValidityTime;

    /**
     * 描述
     */
    private String userDesc;

    /**
     * 最后一次登录时间
     */
    private Long lastLoginTime;

    /**
     * 查询最后一次登录时间的截止时间
     */
    private Long lastLoginTimeEnd;

    /**
     * 最后一次登录时间操作符
     */
    private String lastLoginTimeRelation;

    /**
     * 最后一次登录ip
     */
    private String lastLoginIp;

    /**
     * 登录模式，1是单用户，2是多用户
     */
    private String loginType;

    /**
     * 最大用户数，单用户模式默认为1
     */
    private Integer maxUsers;

    /**
     * 是否被删除,0没有，1已删除
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
     * 所属单位名称
     */
    private String department;

    /**
     * 角色名称
     */
    private String role;

    /**
     * 数据源
     */
    private String loginSourse;

    /**
     * 登录时间
     */
    private Long loginTime;

    /**
     * 用户的id列表
     */
    private List<String> ids;

    /**
     * 登录ip
     */
    private String loginIp;

    /**
     * 每页大小
     */
    private int pageSize = 10;

    /**
     * 页数
     */
    private int page = 0;

    /**
     * 开始条数
     */
    private int startNum;

    /**
     * 排序的字段
     */
    private String sortProperties;

    /**
     * 降序还是升序
     */
    private String sort;

    /**
     * 用户登录以后的标识token
     */
    private String token;

    /**
     * 用户登录的端口
     */
    private int port;

    /**
     * 手机设备id
     */
    private String pushId;

    /**
     * 指定的权限id
     */
    private String permissionId;

    /**
     * 角色id列表
     */
    private List<String> roleIdList;

    /**
     * 角色名列表
     */
    private List<String> roleNameList;

    /**
     * 部门名称列表
     */
    private List<String> departmentNameList;

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
                + ", countValidityTime=" + countValidityTime
                + ", userDesc='" + userDesc + '\''
                + ", lastLoginTime=" + lastLoginTime
                + ", lastLoginIp='" + lastLoginIp + '\''
                + ", loginType='" + loginType + '\''
                + ", maxUsers=" + maxUsers
                + ", deleted='" + deleted + '\''
                + ", createUser='" + createUser + '\''
                + ", createTime=" + createTime
                + ", updateUser='" + updateUser + '\''
                + ", updateTime=" + updateTime
                + ", area='" + area + '\''
                + '}';
    }
}
