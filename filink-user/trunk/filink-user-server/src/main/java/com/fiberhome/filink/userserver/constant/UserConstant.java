package com.fiberhome.filink.userserver.constant;

/**
 * @Author:qiqizhu@wistronits.com
 * Date:2019/8/8
 */
public class UserConstant {

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
     * m默认删除级别
     */
    public static final String DEFAULT_DELETED = "0";



    /**
     * 单位默认级别
     */
    public static final String DEFAULT_LEVEL = "1";


    /**
     * 用户重置默认密码
     */
    public static final String USER_RESET_NEW_PWD = "123456";

    /**
     * 设置删除用户的标志位
     */
    public static final String USER_DELETED_CODE = "1";


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
}
