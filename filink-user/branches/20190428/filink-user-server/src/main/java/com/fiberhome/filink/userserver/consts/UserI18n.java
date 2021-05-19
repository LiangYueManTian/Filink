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
     * 确认密码和新密码要相同
     */
    public static final String CONFIRM_NEW_PWD_EQUALS = "CONFIRM_NEW_PWD_EQUALS";

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
    public static final String DELETE_ROLE_SUCCESS = "DELETE_ROLE_SUCCESS";

    /**
     * 删除部门角色失败
     */
    public static final String DELETE_ROLE_FAIL = "DELETE_ROLE_FAIL";

    /**
     * 添加角色信息失败
     */
    public static final String ADD_ROLE_FAIL = "ADD_ROLE_FAIL";

    /**
     * 更新角色信息失败
     */
    public static final String UPDATE_ROLE_FAIL = "UPDATE_ROLE_FAIL";

    /**
     * 更新失败
     */
    public static final String UPDATE_FAIL = "UPDATE_FAIL";

    /**
     * 添加用户失败
     */
    public static final String ADD_USER_FAIL = "ADD_USER_FAIL";

    /**
     * 添加部门失败
     */
    public static final String ADD_DEPARTMENT_FAIL = "ADD_DEPARTMENT_FAIL";

    /**
     *更新部门信息失败
     */
    public static final String UPDATE_DEPART_FAIL = "UPDATE_DEPART_FAIL";

    /**
     * 删除部门信息失败
     */
    public static final String DELETE_DEPART_FAIL = "DELETE_DEPART_FAIL";

    /**
     * 用户在线
     */
    public static final String ONLINE_USER = "ONLINE_USER";

    /**
     * 不允许删除admin用户
     */
    public static final String NOT_ALLOW_DELETE_ADMIN = "NOT_ALLOW_DELETE_ADMIN";

    /**
     * 不允许删除默认角色
     */
    public static final String NOT_ALLOW_DELETE_DEFAULT_ROLE = "NOT_ALLOW_DELETE_DEFAULT_ROLE";

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
    public static final String UPDATE_ROLE_SUCCESS = "UPDATE_ROLE_SUCCESS";

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
    public static final String DOESNT_DELETE_MINE = "DOESNT_DELETE_MINE";

    /**
     * 不能强制自己下线
     */
    public static final String DOESNT_FORCE_MINE_OFFLINE = "DOESNT_FORCE_MINE_OFFLINE";

    /**
     * 更新的用户不能为空
     */
    public static final String UPDATE_USER_NOT_NULL = "UPDATE_USER_NOT_NULL";

    /**
     * 完善角色信息
     */
    public static final String WRITE_FULL_ROLE_INFO = "WRITE_FULL_ROLE_INFO";

    /**
     * 该角色名已经被使用
     */
    public static final String ROLE_NAME_HAS_USER = "ROLE_NAME_HAS_USER";

    /**
     * 存在被删除的用户
     */
    public static final String EXIST_HAS_DELETE_USER = "EXIST_HAS_DELETE_USER";

    /**
     * 用户已被删除
     */
    public static final String USER_HAS_DELETED = "USER_HAS_DELETED";

    /**
     * 选择需要操作的数据
     */
    public static final String SELECT_NEED_OPER_DATA = "SELECT_NEED_OPER_DATA";

    /**
     * 添加统一授权成功
     */
    public static final String ADD_UNIFYAUTH_SUCCESS = "ADD_UNIFYAUTH_SUCCESS";

    /**
     * 添加临时授权成功
     */
    public static final String ADD_TEMP_AUTH_SUCCESS = "ADD_TEMP_AUTH_SUCCESS";

    /**
     * 统一授权操作错误
     */
    public static final String UNIFYAUTH_OPER_ERROR = "UNIFYAUTH_OPER_ERROR";

    /**
     * 完善统一授权信息
     */
    public static final String WRITE_FULL_UNIFYAUTH_INFO = "WRITE_FULL_UNIFYAUTH_INFO";

    /**
     * 完善时间信息
     */
    public static final String PERFECT_TIME_INFORMATION = "PERFECT_TIME_INFORMATION";

    /**
     * 过期时间必须大于当前时间
     */
    public static final String EXPIRETIME_MUST_GT_CURRENTTIME = "EXPIRETIME_MUST_GT_CURRENTTIME";

    /**
     * 统一授权信息已被删除
     */
    public static final String UNIFYAUTH_HAS_DELETED = "UNIFYAUTH_HAS_DELETED";

    /**
     * 修改统一授权成功
     */
    public static final String MODIFY_UNIFYAUTH_SUCCESS = "MODIFY_UNIFYAUTH_SUCCESS";

    /**
     * 删除统一授权成功
     */
    public static final String DELETE_UNIFYAUTH_SUCCESS = "DELETE_UNIFYAUTH_SUCCESS";

    /**
     * 修改统一授权状态成功
     */
    public static final String MODIFY_UNIFYAUTHSTATUS_SUCCESS = "MODIFY_UNIFYAUTHSTATUS_SUCCESS";

    /**
     * 统一授权信息未过期
     */
    public static final String UNIFYAUTH_NOT_EXPIRE = "UNIFYAUTH_NOT_EXPIRE";

    /**
     * 临时授权信息不能为空
     */
    public static final String TEMPAUTH_INFO_NOT_NULL = "TEMPAUTH_INFO_NOT_NULL";

    /**
     * 临时授权信息已被删除
     */
    public static final String TEMPAUTH_INFO_HAS_DELETED = "TEMPAUTH_INFO_HAS_DELETED";

    /**
     * 临时授权操作错误
     */
    public static final String TEMPAUTH_OPER_ERROR = "TEMPAUTH_OPER_ERROR";

    /**
     * 审核临时授权信息成功
     */
    public static final String AUDITING_TEMPAUTH_SUCCESS = "AUDITING_TEMPAUTH_SUCCESS";

    /**
     * 临时授权信息未过期
     */
    public static final String TEMPAUTH_INFO_NOT_EXPIRE = "TEMPAUTH_INFO_NOT_EXPIRE";

    /**
     * 删除临时授权成功
     */
    public static final String DELETE_TEMPAUTH_SUCCESS = "DELETE_TEMPAUTH_SUCCESS";

    /**
     * 文件为空
     */
    public static final String FILE_IS_NULL = "FILE_IS_NULL";

    /**
     * 邮箱长度太长
     */
    public static final String EMAIL_TOO_LONG = "EMAIL_TOO_LONG";

    /**
     * 有效期长度太长
     */
    public static final String VALIDITYTIME_LENGTH_TOO_LONG = "VALIDITYTIME_LENGTH_TOO_LONG";

    /**
     * 操作用户列表名称
     */
    public static final String OPERATE_USER_LIST_NAME = "OPERATE_USER_LIST_NAME";

    /**
     * 导出的数据太大
     */
    public static final String EXPORT_DATA_TOO_LARGE = "EXPORT_DATA_TOO_LARGE";

    /**
     * 创建导出任务失败
     */
    public static final String FAILED_TO_CREATE_EXPORT_TASK = "FAILED_TO_CREATE_EXPORT_TASK";

    /**
     * 导出数据成功
     */
    public static final String THE_EXPORT_TASK_WAS_CREATED_SUCCESSFULLY = "THE_EXPORT_TASK_WAS_CREATED_SUCCESSFULLY";

    /**
     * 用户代码为空
     */
    public static final String USER_CODE_NULL = "USER_CODE_NULL";

    /**
     * 用户昵称为空
     */
    public static final String NICK_NAME_NULL = "NICK_NAME_NULL";

    /**
     * 用户状态为空
     */
    public static final String USER_STATUS_NULL = "USER_STATUS_NULL";

    /**
     * 用户部门为空
     */
    public static final String USER_DEPT_NULL = "USER_DEPT_NULL";

    /**
     * 用户角色为空
     */
    public static final String USER_ROLE_NULL = "USER_ROLE_NULL";

    /**
     * 用户模式为空
     */
    public static final String USER_MODEL_NULL = "USER_MODEL_NULL";

    /**
     * 用户账号为
     */
    public static final String USER_CODE_IS = "USER_CODE_IS";

    /**
     * 手机号为
     */
    public static final String PHONE_NUMBER_IS = "PHONE_NUMBER_IS";

    /**
     * 邮箱为
     */
    public static final String EMAIL_IS = "EMAIL_IS";

    /**
     * 数据格式异常
     */
    public static final String DATA_FORMAT_ERROR = "DATA_FORMAT_ERROR";

    /**
     * 重复导入
     */
    public static final String REPEAT_IMPORT = "REPEAT_IMPORT";

    /**
     * 手机格式异常
     */
    public static final String PHONE_NUMBER_ERROR = "PHONE_NUMBER_ERROR";

    /**
     * 邮箱格式异常
     */
    public static final String EMAIL_ERROR = "EMAIL_ERROR";

    /**
     * 用户地址异常
     */
    public static final String ADDRESS_ERROR = "ADDRESS_ERROR";

    /**
     * 用户昵称异常
     */
    public static final String USER_NICK_NAME_ERROR = "USER_NICK_NAME_ERROR";

    /**
     * 用户昵称异常
     */
    public static final String USER_NAME_ERROR = "USER_NAME_ERROR";

    /**
     * 用户描述异常
     */
    public static final String USER_DESC_ERROR = "USER_DESC_ERROR";

    /**
     * 数据格式异常
     */
    public static final String MAX_USER_NUM_FALL_OUTER_SIDE = "MAX_USER_NUM_FALL_OUTER_SIDE";

    /**
     * 重复的用户账号
     */
    public static final String USER_CODE_REPEAT = "USER_CODE_REPEAT";

    /**
     * 最大在线数必须为整数
     */
    public static final String MAX_ONLINEUSER_MUST_BE_INTEGER = "MAX_ONLINEUSER_MUST_BE_INTEGER";

    /**
     * 部门名
     */
    public static final String DEPARTMENT_NAME = "DEPARTMENT_NAME";

    /**
     * 不存在
     */
    public static final String NOT_EXIST = "NOT_EXIST";

    /**
     * 已被使用
     */
    public static final String HAS_USED = "HAS_USED";

    /**
     * 角色
     */
    public static final String ROLE = "ROLE";

    /**
     * 导入用户信息成功
     */
    public static final String IMPORT_USER_INFO_SUCCESS = "IMPORT_USER_INFO_SUCCESS";

    /**
     * 手机号不存在
     */
    public static final String PHONE_NOT_EXIST = "PHONE_NOT_EXIST";

    /**
     * 用户有效期
     */
    public static final String USER_EXPIRE_TIME = "USER_EXPIRE_TIME";

    /**
     * 没有导出的数据
     */
    public static final String EXPORT_NO_DATA = "EXPORT_NO_DATA";

    /**
     * 超过最大导出数量
     */
    public static final String EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS = "EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS";

    /**
     * 权限名为
     */
    public static final String PERMISSION_NAME_IS = "PERMISSION_NAME_IS";

    /**
     * 用户为使用状态
     */
    public static final String USER_IN_USE_MODEL = "USER_IN_USE_MODEL";

    /**
     * 部门下有工单信息
     */
    public static final String DEPARTMENT_HAS_WORK_ORDER = "DEPARTMENT_HAS_WORK_ORDER";

    /**
     * 部门下有当前告警信息
     */
    public static final String DEPARTMENT_HAS_CURRENT_ALARM = "DEPARTMENT_HAS_CURRENT_ALARM";

    /**
     * 部门下有巡检任务
     */
    public static final String DEPARTMENT_HAS_INSPECTION = "DEPARTMENT_HAS_INSPECTION";

    /**
     * 完善临时授权信息
     */
    public static final String WRITE_FULL_TEMP_AUTH_INFO = "WRITE_FULL_TEMP_AUTH_INFO";

    /**
     *  当前用户没有权限
     */
    public static final String CURRENT_USER_PERMISSION_DENIED = "CURRENT_USER_PERMISSION_DENIED";

    /**
     *  ADMIN用户不能被禁用
     */
    public static final String ADMIN_USER_NOT_FORBIDDEN = "ADMIN_USER_NOT_FORBIDDEN";

    /**
     *  ADMIN用户不能被其他人修改
     */
    public static final String ADMIN_USER_NOT_OTHER_UPDATE = "ADMIN_USER_NOT_OTHER_UPDATE";

    /**
     *  用户数量超过最大用户数
     */
    public static final String USER_NUM_OVER_MAX_USER_NUM = "USER_NUM_OVER_MAX_USER_NUM";

    /**
     *  用户数量超过最大用户数
     */
    public static final String NOT_UPDATE_FATHER_DEPARTMENT_LEVEL = "NOT_UPDATE_FATHER_DEPARTMENT_LEVEL";

    /**
     * 不能删除有工单的用户
     */
    public static final String USER_IN_USE_ORDER = "USER_IN_USE_ORDER";

    /**
     * admin用户不能被修改角色
     */
    public static final String ADMIN_USER_NOT_UPDATE_ROLE = "ADMIN_USER_NOT_UPDATE_ROLE";

    /**
     * admin所属角色不能被修改
     */
    public static final String ADMIN_ROLE_IS_NOT_UPDATE = "ADMIN_ROLE_IS_NOT_UPDATE";

    /**
     * 获取redis锁错误
     */
    public static final String ERROR_GETTING_REDIS_LOCK = "ERROR_GETTING_REDIS_LOCK";

    /**
     * 用户手机号不能为空
     */
    public static final String PHONE_NUMBER_NULL = "PHONE_NUMBER_NULL";

    /**
     * 邮箱不能为空
     */
    public static final String EMAIL_NULL = "EMAIL_NULL";

    /**
     * 用户长度太长
     */
    public static final String USER_DESC_TOO_LONG = "USER_DESC_TOO_LONG";

    /**
     * 部门ID不能为空
     */
    public static final String DEPARTMENT_ID_NOT_NULL = "DEPARTMENT_ID_NOT_NULL";

    /**
     * 角色ID不能为空
     */
    public static final String ROLE_ID_NOT_NULL = "ROLE_ID_NOT_NULL";

    /**
     * 更新用户登录信息成功
     */
    public static final String MODIFY_USER_LOGIN_DEVICE_SUCCESS = "MODIFY_USER_LOGIN_DEVICE_SUCCESS";

    /**
     * 存在不在线的用户信息
     */
    public static final String ONLINE_USER_NOT_EXIST = "ONLINE_USER_NOT_EXIST";

    /**
     * 统一授权为启用状态
     */
    public static final String UNIFY_AUTH_IS_ENABLED_STATE = "UNIFY_AUTH_IS_ENABLED_STATE";

    /**
     * 临时授权为启用状态
     */
    public static final String TEMP_AUTH_IS_ENABLED_STATE = "TEMP_AUTH_IS_ENABLED_STATE";

    /**
     * 格式异常
     */
    public static final String VALIDATA_TIME_ABNORMAL_FORMAT = "VALIDATA_TIME_ABNORMAL_FORMAT";

    /**
     * 最大在线用户数格式异常
     */
    public static final String MAX_USER_NUMBER_ABNORMAL_FORMAT = "MAX_USER_NUMBER_ABNORMAL_FORMAT";

    /**
     * 有效期不在2~999之间
     */
    public static final String VALID_TIME_OUT_RANGE = "VALID_TIME_OUT_RANGE";

    /**
     * 用户安全策略错误
     */
    public static final String STRATEGY_SERVER_ERROR = "STRATEGY_SERVER_ERROR";

    /**
     * 用户账号不在指定长度之间
     */
    public static final String USER_CODE_OUT_RANGE = "USER_CODE_OUT_RANGE";

    /**
     * 用户昵称不在指定长度之间
     */
    public static final String USER_NICK_NAME_OUT_RANGE = "USER_NICK_NAME_OUT_RANGE";

    /**
     * 数据格式存在异常
     */
    public static final String IMPORT_DATA_ERROR = "IMPORT_DATA_ERROR";

    /**
     * 单用户模式
     */
    public static final String SINGLE_LOGIN_TYPE = "SINGLE_LOGIN_TYPE";

    /**
     * 多用户模式
     */
    public static final String MUITL_LOGIN_TYPE = "MUITL_LOGIN_TYPE";

    /**
     * 单位：天
     */
    public static final String DAY_UNIT = "DAY_UNIT";

    /**
     * 单位：月
     */
    public static final String MOUNTH_UNIT = "MOUNTH_UNIT";

    /**
     * 设施服务异常
     */
    public static final String DEVICE_SERVER_ERROR = "DEVICE_SERVER_ERROR";

    /**
     * 审核信息
     */
    public static final String AUDIT_INFORMATION = "AUDIT_INFORMATION";
}
