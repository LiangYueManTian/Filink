package com.fiberhome.filink.userserver.constant;

/**
 * 常量表
 *
 * @author xuangong
 */
public class UserResultCode {

    /**
     * 用户已经在存在
     */
    public static final int USER_EXIST = 120000;
    /**
     * 用户名为null
     */
    public static final int USER_NAME_NULL = 120010;

    /**
     * 用户码已经存在
     */
    public static final int USER_CODE_EXIST = 120020;

    /**
     * 手机号已经存在
     */
    public static final int PHONE_NUMBER_EXIST = 120030;

    /**
     * 用户不存在
     */
    public static final int USER_NOT_EXIST = 120040;

    /**
     * 查询部门信息成功
     */
    public static final int QUERY_DEPARTMENT_SUCCESS = 120050;

    /**
     * 查询部门信息失败
     */
    public static final int QUERY_DEPARTMENT_FAIL = 120060;

    /**
     * 密码不能为空
     */
    public static final int NEWPWD_IS_NOT_NULL = 120070;

    /**
     * 旧密码不能为空
     */
    public static final int OLDPWD_IS_NOT_NULL = 120080;

    /**
     * 确认密码不能为空
     */
    public static final int CONFIRMPWD_IS_NOT_NULL = 120090;

    /**
     * 旧密码输入错误
     */
    public static final int OLDPWD_WRONG = 120100;

    /**
     * 更新密码成功
     */
    public static final int UPDATE_PWD_SUCCESS = 120110;

    /**
     * 更新密码失败
     */
    public static final int UPDATE_PWD_FAIL = 120120;

    /**
     * 删除用户成功
     */
    public static final int DELETE_USER_SUCCESS = 120130;

    /**
     * 删除用户失败
     */
    public static final int DELETE_USER_FAIL = 120140;

    /**
     * 删除的用户不能为空
     */
    public static final int DELETE_USER_IS_NOT_NULL = 120150;


    /**
     *获取在线用户信息失败
     */
    public static final int GET_ONLINE_USER_FAIL = 120160;

    /**
     * 部门不存在
     */
    public static final int DEPART_IS_NOT_EXIST = 120170;

    /**
     * 角色不存在
     */
    public static final int ROLE_IS_NOT_EXIST = 120180;

    /**
     * 新密码与旧密码相同
     */
    public static final int NEW_OLD_PASSWORD_EQUALS = 120190;

    /**
     * 用户添加失败
     */
    public static final int ADD_USER_FAIL = 120200;

    /**
     * 用户在线
     */
    public static final int ONLINE_USER = 120210;

    /**
     * 不允许删除admin用户
     */
    public static final int NOT_ALLOW_DELETE_ADMIN = 120220;

    /**
     * 不允许删除默认角色
     */
    public static final int NOT_ALLOW_DELETE_DEFAULT_ROLE = 120230;

    /**
     * 用户地址长多过长
     */
    public static final int USER_ADDRESS_TOLONG = 120240;

    /**
     * 有下级单位的部门不能删除
     */
    public static final int DEPT_HAS_CHILD_DEPT = 120250;

    /**
     * 有用户正在使用该部门
     */
    public static final int USER_USE_DEPT = 120260;

    /**
     * 有用户正在使用该角色
     */
    public static final int USER_USE_ROLE = 120270;

    /**
     * 不能删除自己
     */
    public static final int DOESNT_DELETE_MINE = 120280;

    /**
     * 不能强制自己下线
     */
    public static final int DOESNT_FORCE_MINE_OFFLINE = 120290;

    /**
     * 存在被删除的用户
     */
    public static final int EXIST_HAS_DELETE_USER = 120300;

    /**
     * 用户已被删除
     */
    public static final int USER_HAS_DELETED = 120310;

    /**
     * 必须选择需要操作的数据
     */
    public static final int SELECT_NEED_OPER_DATA = 120320;

    /**
     * 完善统一授权信息
     */
    public static final int WRITE_FULL_UNIFYAUTH_INFO = 120330;

    /**
     * 完善时间信息
     */
    public static final int PERFECT_TIME_INFORMATION = 120340;

    /**
     * 过期时间必须大于当前时间
     */
    public static final int EXPIRETIME_MUST_GT_CURRENTTIME = 120350;

    /**
     * 统一授权已被删除
     */
    public static final int UNIFYAUTH_HAS_DELETED = 120360;

    /**
     * 统一授权为过期
     */
    public static final int UNIFYAUTH_NOT_EXPIRE = 120370;

    /**
     * 临时授权信息不能为空
     */
    public static final int TEMPAUTH_INFO_NOT_NULL = 120380;

    /**
     * 临时授权信息已被删除
     */
    public static final int TEMPAUTH_INFO_HAS_DELETED = 120390;

    /**
     * 临时授权信息未过期
     */
    public static final int TEMPAUTH_INFO_NOT_EXPIRE = 120400;

    /**
     * 导入文件为空
     */
    public static final int FILE_IS_NULL = 120410;

    /**
     * 邮箱长度太长
     */
    public static final int EMAIL_TOO_LONG = 120420;
    /**
     * 有效期长度太长
     */
    public static final int VALIDITYTIME_LENGTH_TOO_LONG = 120430;

    /**
     * 导出数据太大
     */
    public static final int EXPORT_DATA_TOO_LARGE = 120440;

    /**
     * 创建导出任务失败
     */
    public static final int FAILED_TO_CREATE_EXPORT_TASK = 120450;

    /**
     * 用户代码为空
     */
    public static final int USER_CODE_NULL = 120460;

    /**
     * 用户昵称为空
     */
    public static final int NICK_NAME_NULL = 120470;

    /**
     * 用户状态为空
     */
    public static final int USER_STATUS_NULL = 120480;

    /**
     * 用户部门为空
     */
    public static final int USER_DEPT_NULL = 120490;

    /**
     * 用户角色为空
     */
    public static final int USER_ROLE_NULL = 120500;

    /**
     * 用户模式为空
     */
    public static final int USER_MODEL_NULL = 120510;

    /**
     * 没有到处数据
     */
    public static final int EXPORT_NO_DATA = 120520;

    /**
     * 数据太大
     */
    public static final int EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS = 120530;

    /**
     * 用户为使用状态
     */
    public static final int USER_IN_USE_MODEL = 120540;

    /**
     * 部门下有工单信息
     */
    public static final int DEPARTMENT_HAS_WORK_ORDER = 120550;

    /**
     * 部门下有告警信息
     */
    public static final int DEPARTMENT_HAS_CURRENT_ALARM = 120560;

    /**
     * 部门下有巡检任务
     */
    public static final int DEPARTMENT_HAS_INSPECTION = 120570;

    /**
     * 完善临时授权信息
     */
    public static final int WRITE_FULL_TEMP_AUTH_INFO = 120580;

    /**
     * 用户手机号不能为空
     */
    public static final int PHONE_NUMBER_NULL = 120590;

    /**
     * 用户长度太长
     */
    public static final int USER_DESC_TOO_LONG = 120600;

    /**
     * 数据信息为空不存在
     */
    public static final int DATA_IS_NULL = 120610;

    /**
     * 存在不在线的用户信息
     */
    public static final int ONLINE_USER_NOT_EXIST = 120620;

    /**
     * 统一授权为启用状态
     */
    public static final int UNIFY_AUTH_IS_ENABLED_STATE = 120630;

    /**
     * 临时授权为启用状态
     */
    public static final int TEMP_AUTH_IS_ENABLED_STATE = 120640;

    /**
     * 邮箱不能为空
     */
    public static final int EMAIL_NULL = 120650;

    /**
     * 格式异常
     */
    public static final int VALIDATA_TIME_ABNORMAL_FORMAT = 120660;

    /**
     * 格式异常
     */
    public static final int MAX_USER_NUMBER_ABNORMAL_FORMAT = 120670;

    /**
     * 设施服务异常
     */
    public static final int DEVICE_SERVER_ERROR = 120680;

    /**
     * admin用户不能被禁用
     */
    public static final int ADMIN_USER_NOT_FORBIDDEN = 125010;

    /**
     * admin用户不能被其他人修改
     */
    public static final int ADMIN_USER_NOT_OTHER_UPDATE = 125020;

    /**
     * admin用户不能被修改角色
     */
    public static final int ADMIN_USER_NOT_UPDATE_ROLE = 125021;

    /**
     * admin所属的角色不能被修改
     */
    public static final int ADMIN_ROLE_IS_NOT_UPDATE = 125022;

    /**
     * 用数量超过最大用户数
     */
    public static final int USER_NUM_OVER_MAX_USER_NUM = 125300;


    /**
     * 不能修改有子单位的单位级别
     */
    public static final int NOT_UPDATE_FATHER_DEPARTMENT_LEVEL = 125400;


    /**
     * 有工单的用户
     */
    public static final int USER_IN_USE_ORDER = 125500;


}
