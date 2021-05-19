package com.fiberhome.filink.alarmhistoryserver.constant;

/**
 * <p>
 *  历史告警返回码，日志编码
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-22
 */
public class LogFunctionCodeConstant {

    /**
     * pageCondition对象不能为空
     */
    public static final Integer PAGE_CONDITION_NULL = 170301;

    /**
     * 历史告警ID不能为空
     */
    public static final Integer ALARM_ID_NULL = 170302;

    /**
     * 查询单个历史告警的信息失败
     */
    public static final Integer ALARM_SINGLE_FAILURE = 170303;

    /**
     * 创建导出任务失败
     */
    public static final Integer FAILED_TO_CREATE_EXPORT_TASK = 170315;

    /**
     * 导出数据太大
     */
    public static final Integer EXPORT_DATA_TOO_LARGE = 170316;

    /**
     * 没有数据导出
     */
    public static final Integer EXPORT_NO_DATA = 170317;

    /**
     * 参数不正确
     */
    public static final Integer INCORRECT_PARAMETER = 170318;

    /**
     * 当前用户超过最大任务数量
     */
    public static final Integer EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS = 170318;

    /**
     * 表达式不正确
     */
    public static final Integer INCORRECT_EXPRESSION = 170319;

    /**
     * 数字1
     */
    public static final int ALARM_STATUS_ONE = 1;

    /**
     * 数字2
     */
    public static final int ALARM_STATUS_TWO = 2;

    /**
     * 数字3
     */
    public static final int ALARM_STATUS_THREE = 3;

    /**
     * 数字1000
     */
    public static final int ALARM_ENDS = 1000;

    /**
     * 数字8760 年
     */
    public static final int ALARM_BQLO = 8760;

    /**
     * 数字720 月
     */
    public static final int ALARM_QLO = 720;

    /**
     * 数字24 天
     */
    public static final int ALARM_ES = 24;
}
