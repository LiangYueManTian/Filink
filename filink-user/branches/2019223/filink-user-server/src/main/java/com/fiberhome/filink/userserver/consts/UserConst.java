package com.fiberhome.filink.userserver.consts;

/**
 * 常量表
 *
 * @author xuangong
 */
public class UserConst {

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
     * 邮箱长度太长
     */
    public static final int EMAIL_TOO_LONG = 120420;

    /**
     * 有效期长度太长
     */
    public static final int VALIDITYTIME_LENGTH_TOO_LONG = 120430;

    /**
     * redis中所有存储的用户信息都以USER开头
     */
    public static String USER_PREFIX = "USER_";

    /**
     * redis中所有存储的部门都以DEPT_开头
     */
    public static String DEPT_PREFIX = "DEPT_";


    /**
     * 存储用户的分隔符
     */
    public static String REDIS_SPLIT = "_";

    public static final int USER_SITE = 1;

    /**
     * 默认存储时间
     */
    public static final Long EXPIRE_TIME = 1800L;

    /**
     * 用户id集合
     */
    public static String USER_IDS = "USER_IDS";

    /**
     * 默认密码
     */
    public static String DEFAULT_PWD = "123456";

    /**
     * admin用户的id
     */
    public static String ADMIN_ID = "1";

    /**
     * 用户登录日志模板
     */
    public static String USER_LOGIN_LOG = "1504101";

    /**
     * 用户登出日志模板
     */
    public static String USER_LOGOUT_LOG  = "1504102";

    /**
     * m默认删除级别
     */
    public static String DEFAULT_DELETED = "0";



    /**
     * 单位默认级别
     */
    public static String DEFAULT_LEVEL = "1";

    /**
     * 重置密码用户模板编号
     */
    public static String RESET_PWD_MODEL = "1501105";

    /**
     * 用户重置默认密码
     */
    public static String USER_RESET_NEW_PWD = "123456";

    /**
     * 设置删除用户的标志位
     */
    public static String USER_DELETED_CODE = "1";

    /**
     * 删除用户日志模板编号
     */
    public static String DELETE_USER_LOG = "1501103";

    /**
     * 删除用户日志模板编号
     */
    public static String MODIFY_USER_LOG = "1501104";

    /**
     * 用户默认删除标识
     */
    public static String USER_DEFAULT_DELETED = "0";

    /**
     * redis模糊查询通配符
     */
    public static String REDIS_WILDCARD = "*";

    /**
     *删除用户发送websocket信息
     */
    public static String DELETE_USER_WEBSOCKET = "deleteUser";

    /**
     * 强制用户下线发送websocket的频道
     */
    public static String FORCEOFFLINE_CHANNEL_ID = "forceOff";

    /**
     * 强制用户下线发送websocket的key
     */
    public static String FORCEOFFLINE_CHANNEL_KEY = "user";

    /**
     * 删除用户日志模板编号
     */
    public static String FORCE_USER_OFFLINE_MODEL = "1501106";

    /**
     * uuid的长度
     */
    public static int UUID_LENGTH = 32;
}
