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

    /**
     * redis中所有存储的用户信息都以USER开头
     */
    public static String USER_PREFIX = "USER_";

    /**
     * 存储用户的分隔符
     */
    public static final String REDIS_SPLIT = "redis_split";

    /**
     * 对用户ip和端口进行连接的分隔符
     */
    public static final String REDIS_IP_PORT_SPLIT = "^^";

    public static final int USER_SITE = 1;

    /**
     * 默认存储时间
     */
    public static final Long EXPIRE_TIME = 1800L;

    /**
     * 用户id集合
     */
    public static final String USER_IDS = "USER_IDS";

    /**
     * 默认密码
     */
    public static final String DEFAULT_PWD = "123456";

    /**
     * admin用户的id
     */
    public static final String ADMIN_ID = "1";

    /**
     * admin用户的userName
     */
    public static final String ADMIN_USER_NAME = "admin";

    /**
     * 用户登录日志模板
     */
    public static final String USER_LOGIN_LOG = "1504101";

    /**
     * 用户登出日志模板
     */
    public static final String USER_LOGOUT_LOG  = "1504102";

    /**
     * m默认删除级别
     */
    public static final String DEFAULT_DELETED = "0";



    /**
     * 单位默认级别
     */
    public static final String DEFAULT_LEVEL = "1";

    /**
     * 重置密码用户模板编号
     */
    public static final String RESET_PWD_MODEL = "1501105";

    /**
     * 导出用户列表
     */
    public static final String EXPORT_USER_LIST = "1507101";

    /**
     * 用户重置默认密码
     */
    public static final String USER_RESET_NEW_PWD = "123456";

    /**
     * 设置删除用户的标志位
     */
    public static final String USER_DELETED_CODE = "1";

    /**
     * 删除用户日志模板编号
     */
    public static final String DELETE_USER_LOG = "1501103";

    /**
     * 删除用户日志模板编号
     */
    public static final String MODIFY_USER_LOG = "1501104";

    /**
     * 用户默认删除标识
     */
    public static final String USER_DEFAULT_DELETED = "0";

    /**
     * 统一授权默认删除标识
     */
    public static final Integer UNIFYAUTH_DEFAULT_DELETED = 0;

    /**
     * 临时授权默认删除标识
     */
    public static final Integer TEMPAUTH_DEFAULT_DELETED = 0;

    /**
     * 统一授权默认状态
     */
    public static final Integer UNIFYAUTH_DEFAULT_STATUS = 0;

    /**
     * redis模糊查询通配符
     */
    public static final String REDIS_WILDCARD = "*";

    /**
     *删除用户发送websocket信息
     */
    public static final String DELETE_USER_WEBSOCKET = "deleteUser";

    /**
     * 强制用户下线发送websocket的频道
     */
    public static final String FORCEOFFLINE_CHANNEL_ID = "forceOff";

    /**
     * 被挤下线
     */
    public static final String BE_OFFLINE = "beOffline";

    /**
     * 有临时授权需要审核
     */
    public static final String AUDIT_TEMP_AUTH = "auditTempAuth";

    /**
     * 强制用户下线发送websocket的key
     */
    public static final String FORCEOFFLINE_CHANNEL_KEY = "user";

    /**
     * 给你能够审核的人发送通知信息
     */
    public static final String TEMP_AUTH_CHANNEL_KEY = "tempAuth";

    /**
     * 删除用户日志模板编号
     */
    public static final String FORCE_USER_OFFLINE_MODEL = "1501106";

    /**
     * uuid的长度
     */
    public static final int UUID_LENGTH = 19;

    /**
     * 日志表中的添加操作
     */
    public static final String USER_ADD_OPT = "add";

    /**
     * 日志表中的添加操作
     */
    public static final String USER_UPDATE_OPT = "update";

    /**
     * kafka中更新用户信息的code标识
     */
    public static final String UPDATE_USER_INFO = "UPDATE_USER_INFO";

    /**
     * 第一个数的位置
     */
    public static final int FIRST_NUMBER_INDEX = 0;

    /**
     * 发送短信的有效期
     */
    public static final int MESSAGE_EXPIRE_TIME = 120;

    /**
     * 两条反斜杠
     */
    public static final String BACK_SLASH_TWO = "\\";

    /**
     * 四条反斜杠
     */
    public static final String BACK_SLASH_FOUR = "\\\\";

    /**
     * 百分号
     */
    public static final String PER_CENT = "%";

    /**
     * 反斜杠百分号
     */
    public static final String BACK_SLASH_PER_CENT = "\\%";

    /**
     * 下划线
     */
    public static final String UNDER_LINE = "_";

    /**
     * 反斜杠下划线
     */
    public static final String BACK_SLASH_UNDER_LINE = "\\_";

    /**
     * 正斜杠
     */
    public static final String FORWORD_SLASH = "/";

    /**
     * 反斜杠正斜杠
     */
    public static final String BACK_SLASH_FORWARD_SLASH = "\\/";

    /**
     * 双引号
     */
    public static final String DOUBLE_QUOTATION = "\"";

    public static final String DOUBLE_QUOTATION_SLASH = "\\\"";

    /**
     * 添加用户的锁标志
     */
    public static final String LOCK_ADD_USER = "lock_add_user";

    /**
     * 添加部门的锁标志
     */
    public static final String LOCK_ADD_DEPARTMENT = "lock_add_department";

    /**
     * 权限分隔符
     */
    public static final String PERMISSION_NAME_SPLIT = ",";

    /**
     * 权限名的起点
     */
    public static final int PERMISSION_NAME_START = 0;

    /**
     * 添加用户的模板
     */
    public static final String ADD_ROLE_MODEL = "1502101";

    /**
     * 添加用户的模板
     */
    public static final String UPDATE_ROLE_MODEL = "1502102";

    /**
     * 修改权限打印日志的的菜单id的长度
     */
    public static final int PERMISSION_NAME_LENGTH = 4;

    /**
     * 导入用户信息时需要用到的常量,用户是否为启用模式
     */
    public static final String START_STATUS = "启用";
    /**
     * 用户启用的标识
     */
    public static final String START_STATUS_TIP = "1";
    /**
     * 用户停用的标识
     */
    public static final String STOP_STATUS_TIP = "0";
    /**
     * 单位：天
     */
    public static final String DAY_UNIT = "天";
    /**
     * 单位：月
     */
    public static final String MOUNTH_UNIT = "月";
    /**
     * 单位：年
     */
    public static final String YEAR_UNIT = "年";
    /**
     * 单位天的简写：d
     */
    public static final String DAY_TIP = "d";
    /**
     * 单位月的简写：m
     */
    public static final String MOUNTH_TIP = "m";
    /**
     * 单位年的简写：y
     */
    public static final String YEAR_TIP = "y";
    /**
     * 单用户模式
     */
    public static final String SINGLE_LOGIN_TYPE = "单用户";

    /**
     * 单用户模式的标识
     */
    public static final String SINGLE_LOGIN_TIP = "1";

    /**
     * 数据唯一
     */
    public static final int ONLY_ONE_DATA = 1;

    /**
     * 多用户模式标识
     */
    public static final String MORE_LOGIN_TIP = "2";
    /**
     * 用户账号的位置
     */
    public static final int USER_CODE_SITE = 0;
    /**
     * 用户名的位置
     */
    public static final int USER_NAME_SITE = 1;
    /**
     * 用户昵称的位置
     */
    public static final int NICK_NAME_SITE = 2;
    /**
     * 用户状态位置
     */
    public static final int USER_STATU_SITE = 3;
    /**
     * 部门的位置
     */
    public static final int DEPARTMENT_SITE = 5;
    /**
     * 角色的位置
     */
    public static final int ROLE_SITE = 4;
    /**
     * 最大在线数位置
     */
    public static final int MAXUSER_SITE = 10;
    /**
     * 登录模式位置
     */
    public static final int LOGINT_TYPE_SITE = 9;

    /**
     * 用户手机号
     */
    public static final int PHONE_NUMBER_SITE = 6;

    /**
     * 邮箱
     */
    public static final int EMAIL_SITE = 7;

    /**
     * 用户描述的坐标位置
     */
    public static final int USER_DESC_SITE = 12;

    /**
     * 用户地址的坐标位置
     */
    public static final int USER_ADDRESS_SITE = 8;

    /**
     * 账号有效期
     */
    public static final int VALIDATE_TIME = 11;

    /**
     * 用户数据为空
     */
    public static final int DATA_SIZE_IS_ZERO = 0;
    /**
     * list的第一个数据
     */
    public static final int LIST_FIRST = 0;
    /**
     * 开始插入的坐标
     */
    public static final int BEGIN_INSERT = 0;
    /**
     * 部门分隔符
     */
    public static final String SPLIT_SIGN = "/";
    /**
     * 数据0格式化
     */
    public static final String ZERO_DECIMAL_FORMAT = "0";
    /**
     * 点
     */
    public static final String DOT = ".";

    /**
     * 用户账号的正则表达式
     */
    public static final String USER_CODE_PATTERN = "^[\\s\\da-zA-Z\u4e00-\u9fa5`\\-=\\[\\]\\\\;\',./~!@#$%^&*\\(\\)_"
            + "+{}|:\"<>?·【】、；\'、‘’，。、！￥……（）——+｛｝：“”《》？]+$";
    /**
     * 手机号的正则表达式
     */
    public static final String PHONENUMBERPATTERN = "^[1][3,4,5,6,7,8,9][0-9]{9}$";
    /**
     * 邮箱正则表达式
     */
    public static final String EMAILPATTERN = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";

    /**
     * 邮箱最大长度
     */
    public static final int EMAIL_MAX_LENGTH = 32;

    /**
     * 最大在线数表达式
     */
    public static final String maxUserPattern = "^([2-9]|[1-9]\\d|2|100)$";

    /**
     * 用户描述的最大长度
     */
    public static final int MAX_USER_DESC_LENGTH = 200;

    /**
     * 用户地址最大长度
     */
    public static final int MAX_USER_ADDRESS_LENGTH = 64;

    /**
     * 向下一行
     */
    public static final int UP_ROW = 1;

    /**
     * 向上一行
     */
    public static final int DOWN_ROW = -1;

    /**
     * 导入用户信息时需要用到的常量,用户是否为启用模式
     */
    public static final String START_NOT_STATUS = "停用";

    /**
     * 多用户模式
     */
    public static final String MUITL_LOGIN_TYPE = "多用户";

    /**
     * 短信登录
     */
    public static final String BASIC_FACILITIES = "基础设施";

    /**
     * 短信登录模板
     */
    public static final String LOGIN_MESSAGE_MODEL = "SMS_165411199";

    /**
     * 添加一条信息的返回值
     */
    public static final int ADD_ONE_INFO = 1;

    /**
     * 单用户登录数量
     */
    public static final int SINGLE_LOGIN_NUM = 1;

    /**
     * pc登录标志位
     */
    public static final String PC_WEBSITE = "1";

    /**
     * app登录标志位
     */
    public static final String APP_WEBSITE = "0";

    /**
     * 修改操作权限
     */
    public static final String USER_UPDATE_OPERATION_AUTHORITY = "修改了操作权限";

    /**
     * 修改了设施集
     */
    public static final String USER_UPDATE_FACILITY_SET = "修改了设施集";

    /**
     * 部门被删除的标志
     */
    public static final String DEPARTMENT_DELETED_FLAG = "1";

    /**
     * 统一授权为可用状态
     */
    public static final Integer UNIFY_AUTH_START_STATUS = 2;

    /**
     * 临时授权为可用状态
     */
    public static final Integer TEMP_AUTH_START_STATUS = 2;

    /**
     * app登录的权限id
     */
    public static final String APP_LOGIN_PERMISSION = "13";

    /**
     * 审核临时授权的的权限id
     */
    public static final String TEMP_AUTH_AUD_PERMISSION = "01-5-2-2";

    /**
     * 群发的标志
     */
    public static final int SEND_MANY = 10;

    /**
     * 有效期最大数
     */
    public static final int VALIDATE_TIME_MAX = 999;

    /**
     * 有效期最小数
     */
    public static final int VALIDATE_TIME_MIN = 1;

    /**
     * 多用户登录的最大登录数的最大值
     */
    public static final int MAX_USER_MAX = 100;

    /**
     * 多用户登录的最大登录数的最小值
     */
    public static final int MAX_USER_MIN = 2;

    /**
     * 用户代码最大长度
     */
    public static final int MAX_USER_CODE_LENGTH = 32;

    /**
     * 用户昵称最大长度
     */
    public static final int MAX_USER_NICK_NAME_LENGTH = 32;

    /**
     * 导入用户模板
     */
    public static final String IMPORT_USER_MODEL = "1501107";

    /**
     * 手机pushid分隔符
     */
    public static final String PUSH_SPLIT = ",";

    /**
     * 审核信息
     */
    public static final String AUDIT_INFORMATION = "AUDIT_INFORMATION";

    /**
     * 临时授权已被审核
     */
    public static final String TEMPORARY_AUTHORIZATION_HAS_BEEN_REVIEWED = "TEMPORARY_AUTHORIZATION_HAS_BEEN_REVIEWED";

    /**
     * 空格标识
     */
    public static final String BLANK_CODE = " ";

    /**
     * 禁用用户模板
     */
    public static final String FORBIDDEN_USER_MODEL = "1508101";

    /**
     * 解锁用户模板
     */
    public static final String UNLOCK_USER_MODEL = "1508102";

    /**
     * 禁用过期用户模板
     */
    public static final String FORBIDDEN_EXPIRED_USER_MODEL = "1508103";

    /**
     * 手机消息推送类型
     */
    public static final String PUSH_TYPE = "MESSAGE";

    /**
     * 审核结果
     */
    public static final String AUDIT_RESULT = "AUDIT_RESULT";

    /**
     * 通过
     */
    public static final String AUDIT_RESULT_PASS = "AUDIT_RESULT_PASS";

    /**
     * 不通过
     */
    public static final String AUDIT_RESULT_NO_PASS = "AUDIT_RESULT_NO_PASS";

    /**
     * 安卓类型
     */
    public static final Integer ANDROID_TYPE = 0;

    /**
     * ios类型
     */
    public static final Integer IOS_TYPE = 1;

    /**
     * 默认map长度
     */
    public static final Integer MAP_INIT_VALUE = 64;

    /**
     * aliyun提送设施类型
     */
    public static final String PUSH_TARGET = "DEVICE";

    /**
     * 使用aliyun推送的额外设备信息
     */
    public static final String PUSH_EXTRAS = "Extras";

    /**
     * 等待获取锁的时间，单位ms
     */
    public static final Integer ACQUIRE_TIME_OUT = 10000;

    /**
     * 拿到锁的超时时间
     */
    public static final Integer TIME_OUT = 5000;

    /**
     * 更新日志的类型
     */
    public static final String UPDATE_USER = "update";

    /**
     * 更新用户的日志类型
     */
    public static final String USER_LOG_TYPE = "2";

    /**
     * 更新用户的日志模板
     */
    public static final String UPDATE_MODEL_CODE = "1501102";

    /**
     * 更新用户中的用户名
     */
    public static final String LOG_USER_NAME = "userName";

    /**
     * 更新用户中的用户唯一标识
     */
    public static final String LOG_USER_ID = "id";

    /**
     * 添加日志的类型
     */
    public static final String ADD_USER = "add";

    /**
     * 添加用户的日志模板
     */
    public static final String ADD_USER_MODEL_CODE = "1501101";
}
