package com.fiberhome.filink.logapi.constant;



/**
 * @author hedongwei@wistronits.com
 * 常量类
 * 2019/1/16 10:15
 */

public class LogConstants {
    /**
     * 新增本地日志文件
     */
    public static final String ADD_LOG_LOCAL_FILE = "1";

    /**
     * 不新增本地日志文件
     */
    public static final String NOT_ADD_LOG_LOCAL_FILE = "0";

    /**
     * 操作日志表名称
     */
    public static final String OPERATE_LOG_TABLE_NAME = "operateLog";

    /**
     * 安全日志表名称
     */
    public static final String SECURITY_LOG_TABLE_NAME = "securityLog";

    /**
     * 系统日志表名称
     */
    public static final String SYSTEM_LOG_TABLE_NAME = "systemLog";

    /**
     * 日志信息表名属性值
     */
    public static final String LOG_INFO_TABLE_NAME_ATTR = "tableName";

    /**
     * 日志模板详细信息属性值
     */
    public static final String LOG_DETAIL_INFO_TEMPLATE_ATTR =  "detailInfoTemplate";

    /**
     * 日志本地文件类型
     */
    public static final String LOG_LOCAL_FILE_TYPE = "txt";

    /**
     * 操作日志
     */
    public static final String LOG_TYPE_OPERATE = "1";

    /**
     * 安全日志
     */
    public static final String LOG_TYPE_SECURITY = "2";

    /**
     * 系统日志
     */
    public static final String LOG_TYPE_SYSTEM = "3";

    /**
     * 菜单模块新增日志信息
     */
    public static final String ADD_LOG_MENU_MODEL = "menu";

    /**
     * 数据操作类型新增
     */
    public static final String DATA_OPT_TYPE_ADD = "add";

    /**
     * 数据操作类型修改
     */
    public static final String DATA_OPT_TYPE_UPDATE = "update";

    /**
     * 数据操作类型删除
     */
    public static final String DATA_OPT_TYPE_DELETE = "delete";

    /**
     * 功能配置文件路径
     */
    public static final String FUNCTION_CONFIG_FILE_PATH = "/xml/FunctionConfig.xml";

    /**
     * 默认用户
     */
    public static final String DEFAULT_USER_ID = "system";

    /**
     * 默认用户中文名
     */
    public static final String DEFAULT_USER_NAME_CN = "system";

    /**
     * 默认用户英文名
     */
    public static final String DEFAULT_USER_NAME_US = "system";

    /**
     * 默认角色编号
     */
    public static final String DEFAULT_ROLE_ID = "system";

    /**
     * 默认角色中文名称
     */
    public static final String DEFAULT_ROLE_NAME_CN = "system";

    /**
     * 默认角色英文名称
     */
    public static final String DEFAULT_ROLE_NAME_US = "system";


    /**
     * 解码base64编码格式utf-8
     */
    public static final String DECODE_BASE64_CHARSET_UTF8 = "UTF-8";

    /**
     * 解码base64编码格式gbk
     */
    public static final String DECODE_BASE64_CHARSET_GBK = "GBK";


    /**
     * 网管日志操作类型
     */
    public static final String OPT_TYPE_WEB = "web";

    /**
     * PDA日志操作类型
     */
    public static final String OPT_TYPE_PDA = "pda";

    /**
     * 返回属性名
     */
    public static final String RESULT_CODE_ATTR_NAME = "code";

}
