package com.fiberhome.filink.alarmcurrentserver.utils;


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
public class AlarmRusultCode extends ResultCode {

    /**
     * 请求成功
     */
    public static final Integer SUCCESS = 0;

    /**
     * 请求失败
     */
    public static final Integer FALL = 1;

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
}
