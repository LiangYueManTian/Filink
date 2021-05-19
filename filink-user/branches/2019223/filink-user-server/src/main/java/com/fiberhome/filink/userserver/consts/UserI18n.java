package com.fiberhome.filink.userserver.consts;

import lombok.Data;

/**
 * 国际化名称表
 *
 * @author xuangong
 * @since 2019-01-03
 */
@Data
public class UserI18n {
    /**
     * 用户名已存在
     */
    public static final String USER_EXIST = "USER_EXIST";
    /**
     * 用户名已存在
     */
    public static final String USER_NAME_NULL = "USER_NAME_NULL";
    /**
     * 用户码已存在
     */
    public static final String USER_CODE_EXIST = "USER_CODE_EXIST";
    /**
     * 号码已经存在
     */
    public static final String PHONE_NUMBER_EXIST = "PHONE_NUMBER_EXIST";

    /**
     * 用户不存在
     */
    public static final String USER_NOT_EXIST = "USER_NOT_EXIST";

    /**
     * 成功查询部门信息
     */
    public static final String QUERY_DEPARTMENT_SUCCESS = "QUERY_DEPARTMENT_SUCCESS";

    /**
     * 查询部门信息失败
     */
    public static final String QUERY_DEPARTMENT_FAIL = "QUERY_DEPARTMENT_FAIL";

    /**
     * 密码不能为空
     */
    public static final String NEWPWD_IS_NOT_NULL = "NEWPWD_IS_NOT_NULL";

    /**
     * 旧密码不能为空
     */
    public static final String OLDPWD_IS_NOT_NULL = "OLDPWD_IS_NOT_NULL";

    /**
     * 确认密码不能为空
     */
    public static final String CONFIRMPWD_IS_NOT_NULL = "CONFIRMPWD_IS_NOT_NULL";

    /**
     * 新旧密码不能相同
     */
    public static final String NEW_OLD_PASSWORD_EQUALS = "NEW_OLD_PASSWORD_EQUALS";

    /**
     * 旧密码输入错误
     */
    public static final String OLDPWD_WRONG = "OLDPWD_WRONG";

    /**
     * 更新密码成功
     */
    public static final String UPDATE_PWD_SUCCESS = "UPDATE_PWD_SUCCESS";

    /**
     * 更新密码失败
     */
    public static final String UPDATE_PWD_FAIL = "UPDATE_PWD_FAIL";

    /**
     * 删除用户成功
     */
    public static final String DELETE_USER_SUCCESS = "DELETE_USER_SUCCESS";

    /**
     * 删除用户失败
     */
    public static final String DELETE_USER_FAIL = "DELETE_USER_FAIL";

    /**
     * 删除的用户不能为空
     */
    public static final String DELETE_USER_IS_NOT_NULL = "DELETE_USER_IS_NOT_NULL";

    /**
     * 成功获取在线用户信息
     */
    public static final String GET_ONLINE_USER_SUCCESS = "GET_ONLINE_USER_SUCCESS";

    /**
     *获取在线用户信息失败
     */
    public static final String GET_ONLINE_USER_FAIL = "GET_ONLINE_USER_FAIL";

    /**
     * 数据库操作错误
     */
    public static final String DATABASE_OPER_ERROR = "DATABASE_OPER_ERROR";

    /**
     * 部门不存在
     */
    public static final String DEPART_IS_NOT_EXIST = "DEPART_IS_NOT_EXIST";

    /**
     * 成功删除部门
     */
    public static final String DELETE_DEPART_SUCCESS = "DELETE_DEPART_SUCCESS";

    /**
     * 角色信息不能为空
     */
    public static final String ROLE_IS_NOT_EXIST = "ROLE_IS_NOT_EXIST";

    /**
     * 成功删除角色
     */
    public static String DELETE_ROLE_SUCCESS = "DELETE_ROLE_SUCCESS";

    /**
     * 删除部门角色失败
     */
    public static String DELETE_ROLE_FAIL = "DELETE_ROLE_FAIL";

    /**
     * 添加角色信息失败
     */
    public static String ADD_ROLE_FAIL = "ADD_ROLE_FAIL";

    /**
     * 更新角色信息失败
     */
    public static String UPDATE_ROLE_FAIL = "UPDATE_ROLE_FAIL";

    /**
     * 更新失败
     */
    public static String UPDATE_FAIL = "UPDATE_FAIL";

    /**
     * 添加用户失败
     */
    public static String ADD_USER_FAIL = "ADD_USER_FAIL";

    /**
     * 添加部门失败
     */
    public static String ADD_DEPARTMENT_FAIL = "ADD_DEPARTMENT_FAIL";

    /**
     *更新部门信息失败
     */
    public static String UPDATE_DEPART_FAIL = "UPDATE_DEPART_FAIL";

    /**
     * 删除部门信息失败
     */
    public static String DELETE_DEPART_FAIL = "DELETE_DEPART_FAIL";

    /**
     * 用户在线
     */
    public static String ONLINE_USER = "ONLINE_USER";

    /**
     * 不允许删除admin用户
     */
    public static String NOT_ALLOW_DELETE_ADMIN = "NOT_ALLOW_DELETE_ADMIN";

    /**
     * 不允许删除默认角色
     */
    public static String NOT_ALLOW_DELETE_DEFAULT_ROLE = "NOT_ALLOW_DELETE_DEFAULT_ROLE";

    /**
     * 地址长度过长
     */
    public static final String USER_ADDRESS_TOLONG = "USER_ADDRESS_TOLONG";

    /**
     * 有下级部门的单位不能被删除
     */
    public static final String DEPT_HAS_CHILD_DEPT = "DEPT_HAS_CHILD_DEPT";

    /**
     * 用户正在使用该部门
     */
    public static final String USER_USE_DEPT = "USER_USE_DEPT";

    /**
     * 用户正在使用角色
     */
    public static final String USER_USE_ROLE = "USER_USE_ROLE";

    /**
     * 添加角色成功
     */
    public static final String ADD_ROLE_SUCCESS = "ADD_ROLE_SUCCESS";

    /**
     * 更新角色信息成功
     */
    public static String UPDATE_ROLE_SUCCESS = "UPDATE_ROLE_SUCCESS";

    /**
     * 不能跨等级修改
     */
    public static final String DONT_CROSS_DEPT_LEVEL = "DONT_CROSS_DEPT_LEVEL";

    /**
     * 用户被强制下线
     */
    public static final String USER_HAS_FORCE_OFFLINE = "USER_HAS_FORCE_OFFLINE";

    /**
     * 用户不能删除自己
     */
    public static String DOESNT_DELETE_MINE = "DOESNT_DELETE_MINE";

    /**
     * 不能强制自己下线
     */
    public static String DOESNT_FORCE_MINE_OFFLINE = "DOESNT_FORCE_MINE_OFFLINE";

    /**
     * 更新的用户不能为空
     */
    public static String UPDATE_USER_NOT_NULL = "UPDATE_USER_NOT_NULL";

    /**
     * 完善角色信息
     */
    public static String WRITE_FULL_ROLE_INFO = "WRITE_FULL_ROLE_INFO";

    /**
     * 该角色名已经被使用
     */
    public static String ROLE_NAME_HAS_USER = "ROLE_NAME_HAS_USER";

    /**
     * 邮箱长度太长
     */
    public static String EMAIL_TOO_LONG = "EMAIL_TOO_LONG";

    /**
     * 有效期长度太长
     */
    public static String VALIDITYTIME_LENGTH_TOO_LONG = "VALIDITYTIME_LENGTH_TOO_LONG";
}
