package com.fiberhome.filink.alarmcurrentserver.constant;


import com.fiberhome.filink.clientcommon.utils.ResultCode;
import lombok.Data;

/**
 * <p>
 * 菜单返回码
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-22
 */

@Data
public class LogFunctionCodeConstant extends ResultCode {

    /**
     * 告警id不能为空
     */
    public static final Integer ALARM_ID_NULL = 170201;

    /**
     * pageCondition对象不能为空
     */
    public static final Integer PAGE_CONDITION_NULL = 170202;

    /**
     * 查询单个当前告警信息失败
     */
    public static final Integer SINGLE_ALARM_FAILED = 170203;

    /**
     * 修改备注成功
     */
    public static final Integer MODIFY_NOTE_SUCCESS = 170204;

    /**
     * 修改备注失败
     */
    public static final Integer MODIFY_NOTE_FAILURE = 170205;

    /**
     * 告警确认成功
     */
    public static final Integer ALARM_CONFIRM_SUCCESS = 170206;

    /**
     * 告警确认失败
     */
    public static final Integer ALARM_CONFIRM_FAILED = 170207;

    /**
     * 告警清除成功
     */
    public static final Integer ALARM_REMOVE_SUCCESS = 170208;

    /**
     * 告警清除失败
     */
    public static final Integer ALARM_REMOVE_FAILED = 170209;

    /**
     * 查询各级别告警总数失败
     */
    public static final Integer ALARM_NUMBER_FAILED = 170210;

    /**
     * 用户ID不为空
     */
    public static final Integer USER_ID_NULL = 170211;

    /**
     * 用户token不为空
     */
    public static final Integer USER_TOKEN_NULL = 170212;

    /**
     * 当前告警用户信息不为空
     */
    public static final Integer ALARM_USER_NULL = 170213;

    /**
     * 参数不正确
     */
    public static final Integer INCORRECT_PARAMETER = 170214;

    /**
     * 创建导出任务失败
     */
    public static final Integer FAILED_TO_CREATE_EXPORT_TASK = 170215;

    /**
     * 导出数据太大
     */
    public static final Integer EXPORT_DATA_TOO_LARGE = 170216;

    /**
     * 没有数据导出
     */
    public static final Integer EXPORT_NO_DATA = 170217;

    /**
     * 当前用户超过最大任务数量
     */
    public static final Integer EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS = 170218;

    /**
     * 无该数据
     */
    public static final Integer NO_DATA = 170219;

    /**
     * 表达式不正确
     */
    public static final Integer INCORRECT_EXPRESSION = 170220;

    /**
     * 删除当前告警失败
     */
    public static final Integer DELETE_CURRENT_FALL = 170221;

    /**
     * 删除历史告警失败
     */
    public static final Integer DELETE_HISTORY_FALL = 170222;

    /**
     * 删除告警过滤信息
     */
    public static final Integer DELETE_FILTER_FALL = 170223;

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
     * 数字10
     */
    public static final int ALARM_END = 10;

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

    /**
     * 数字15
     */
    public static final int ALARM_FIFTEEN = 15;

    /**
     * 数字12
     */
    public static final int ALARM_TWELVE = 12;
}
