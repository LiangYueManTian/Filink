package com.fiberhome.filink.logserver.utils;


import com.fiberhome.filink.bean.ResultCode;

/**
 * @author hedongwei@wistronits.com
 * description 日志返回编码
 * date 2019/1/16 9:44
 */

public class LogResultCode extends ResultCode {

    /**
     * pageCondition对象不能为空编码
     */
    public static final Integer PAGE_CONDITION_NULL = 220101;

    /**
     * 日志编码不能为空
     */
    public static final Integer LOG_ID_NULL = 220102;


    /**
     * 操作名称参数不正确
     */
    public static final Integer OPT_NAME_IS_WRONG = 220109;

    /**
     * 操作用户参数不正确
     */
    public static final Integer OPT_USER_CODE_IS_WRONG = 220113;

    /**
     * 操作时间参数不正确
     */
    public static final Integer OPT_TIME_IS_WRONG = 220116;

    /**
     * 操作终端参数不正确
     */
    public static final Integer OPT_TERMINAL_IS_WRONG =  220119;

    /**
     * 操作对象参数不正确
     */
    public static final Integer OPT_OBJ_IS_WRONG = 220123;

    /**
     * 操作结果参数不正确
     */
    public static final Integer OPT_RESULT_IS_WRONG = 220127;

    /**
     * 详细信息参数不正确
     */
    public static final Integer DETAIL_INFO_IS_WRONG = 220132;

    /**
     * 危险级别参数不正确
     */
    public static final Integer DANGER_LEVEL_IS_WRONG = 220136;

    /**
     * 新增操作日志失败
     */
    public static final Integer ADD_OPERATE_FAIL = 220144;

    /**
     * 参数为空
     */
    public static final Integer PARAM_NULL = 220103;
    /**
     * 创建导出任务失败
     */
    public static final Integer FAILED_TO_CREATE_EXPORT_TASK = 220104;
    /**
     * 导出超过最大限制
     */
    public static final Integer EXPORT_DATA_TOO_LARGE = 220105;
    /**
     * 没有导出数据
     */
    public static final Integer EXPORT_NO_DATA = 220106;
    /**
     * 当前用户超过最大任务数量
     */
    public static final Integer EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS = 220107;
    /**
     * 查询模板不存在
     */
    public static final Integer  FILTER_TEMPLATE_MISS = 220108;
}
