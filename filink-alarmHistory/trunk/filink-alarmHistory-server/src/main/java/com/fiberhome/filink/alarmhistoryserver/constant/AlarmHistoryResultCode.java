package com.fiberhome.filink.alarmhistoryserver.constant;

/**
 * <p>
 *   历史告警返回码，日志编码
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-22
 */
public class AlarmHistoryResultCode {

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
    public static final Integer EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS = 170319;

    /**
     * 表达式不正确
     */
    public static final Integer INCORRECT_EXPRESSION = 170320;
}
