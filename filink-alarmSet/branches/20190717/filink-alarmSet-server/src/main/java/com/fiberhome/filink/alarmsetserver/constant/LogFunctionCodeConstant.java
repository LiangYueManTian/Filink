package com.fiberhome.filink.alarmsetserver.constant;


import com.fiberhome.filink.clientcommon.utils.ResultCode;

/**
 * <p>
 *  菜单返回码
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-22
 */
public class LogFunctionCodeConstant extends ResultCode {

    /**
     * 告警名称id不能为空
     */
    public static final Integer ALARM_NAME_ID_NULL = 170102;

    /**
     * 告警级别id不能为空
     */
    public static final Integer ALARM_LEVEL_ID_NULL = 170103;

    /**
     * 查询告警级别信息列表失败
     */
    public static final Integer ALARM_LEVEL_FAILED = 170104;

    /**
     * 修改告警设置成功
     */
    public static final Integer SET_UP_SUCCESS = 170105;

    /**
     * 修改告警设置失败
     */
    public static final Integer SET_UP_FAILED = 170106;

    /**
     * 添加告警设置成功
     */
    public static final Integer ALARM_NAME_ADD_SUCCESS = 170107;

    /**
     * 添加告警设置失败
     */
    public static final Integer ALARM_NAME_ADD_FAILED = 170108;

    /**
     * 查询单个告警设置失败
     */
    public static final Integer SINGLE_ALARM_FAILED = 170109;

    /**
     * 删除告警设置失败
     */
    public static final Integer DELETE_ALARM_FAILED = 170110;

    /**
     * 删除告警设置成功
     */
    public static final Integer DELETE_ALARM_SUCCESS = 170111;

    /**
     * 修改告警设置成功
     */
    public static final Integer UPDATE_ALARM_SUCCESS = 170112;

    /**
     * 修改告警设置失败
     */
    public static final Integer UPDATE_ALARM_FAILED = 170113;

    /**
     * 查询单个告警级别信息失败
     */
    public static final Integer ALARM_ID_FAILED = 170114;

    /**
     * 修改延时入库时间失败
     */
    public static final Integer UPDATE_TIAM_FAILED = 170115;

    /**
     * 修改延时入库时间成功
     */
    public static final Integer UPDATE_TIAM_SUCCESS = 170116;

    /**
     * 查询历史告警设置信息失败
     */
    public static final Integer HISTORY_QUERY_FAILED = 170117;

    /**
     * 历史告警范围值不正确
     */
    public static final Integer INCORRECT_RANGE_VALUE = 170118;

    /**
     * 查询告警名称为空
     */
    public static final Integer ALARM_NAME_NULL = 170119;

    /**
     * 查询告警级别为空
     */
    public static final Integer ALARM_LEVEL_NULL = 170120;

    /**
     * 查询告警名称信息列表失败
     */
    public static final Integer ALARM_NAME_FAILED = 170121;

    /**
     * 播放次数超出范围
     */
    public static final Integer PLAY_TIMES_WRONG = 170122;

    /**
     * 延长时间范围最大值
     */
    public static final Integer ALARMDELAY = 720;

    /**
     *  范围最小值
     */
    public static final Integer ALARMLIN = 0;

    /**
     *
     */
    public static final Integer ALARM_NIAN = 365;

    /**
     * 该颜色已被使用
     */
    public static final Integer COLOR_USED = 170122;

    /**
     * 参数不正确
     */
    public static final Integer INCORRECT_PARAMETER = 170123;

    /**
     * 播放次数参数
     */
    public static final Integer PLAY_WU = 5;

}
