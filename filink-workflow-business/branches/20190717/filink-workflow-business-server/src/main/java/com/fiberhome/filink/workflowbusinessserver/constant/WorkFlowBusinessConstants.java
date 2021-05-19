package com.fiberhome.filink.workflowbusinessserver.constant;

/**
 * 流程常量类
 * @author hedongwei@wistronits.com
 * @date 2019/2/26 18:19
 */

public class WorkFlowBusinessConstants {

    /**
     * 已经删除
     */
    public static final String IS_DELETED = "1";

    /**
     * 未删除
     */
    public static final String IS_NOT_DELETED = "0";

    /**
     * map集合初始值
     */
    public static final Integer MAP_INIT_VALUE = 64;

    /**
     * 单位类型人
     */
    public static final String DEPT_TYPE_PERSON = "0";

    /**
     * 单位类型部门
     */
    public static final String DEPT_TYPE_DEPT = "1";

    /**
     * 没有找到数据
     */
    public static final int NOT_SEARCH_DATA = -1;

    /**
     * 解码base64编码格式utf-8
     */
    public static final String DECODE_BASE64_CHARSET_UTF8 = "UTF-8";

    /**
     * 解码base64编码格式gbk
     */
    public static final String DECODE_BASE64_CHARSET_GBK = "GBK";

    /**
     * 操作类型（新增，修改）
     */
    public static final String OPERATE_TYPE_ADD = "add";
    public static final String OPERATE_TYPE_UPDATE = "update";

    /**
     * 办理类型(保存，提交)
     */
    public static final String COMPLETE_TYPE_COMMIT = "commit";
    public static final String COMPLETE_TYPE_SAVED = "saved";


    /**
     * 服务名
     */
    public static final String SERVER_NAME = "filink-workflow-business-server";

    /**
     * 安卓appKey
     */
    public static final Long PROC_NOTICE_ANDROID_APP_KEY = 25926001L;

    /**
     * iosAppKey
     */
    public static final Long PROC_NOTICE_IOS_APP_KEY = 25894059L;

    /**
     * 手机类型安卓
     */
    public static final Integer PHONE_TYPE_ANDROID = 0;

    /**
     * 手机类型IOS
     */
    public static final Integer PHONE_TYPE_IOS = 1;

    /**
     * 超级管理员
     */
    public static final String ADMIN_USER_ID = "1";
}
