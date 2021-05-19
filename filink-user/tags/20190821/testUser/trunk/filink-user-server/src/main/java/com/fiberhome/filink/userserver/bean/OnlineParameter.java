package com.fiberhome.filink.userserver.bean;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 在线用户参数类
 *
 * @author xuangong
 */
@Data
public class OnlineParameter implements Serializable {

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
     * 手机号
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
     * 条件查询中，登录时间的截止时间
     */
    private Long loginTimeEnd;

    /**
     * 登录ip
     */
    private String loginIp;

    /**
     * 登录源
     */
    private String loginSource;

    /**
     * 角色名称列表
     */
    private List<String> roleNameList;

    /**
     * 部门名称列表
     */
    private List<String> departmentNameList;

    /**
     * 登录时间的关系
     */
    private String relation;

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
     * 当前登录用户的角色名
     */
    private String currentUserRoleName;

    /**
     * 当前登录用户的部门名
     */
    private String currentUserDepartmentName;
}
