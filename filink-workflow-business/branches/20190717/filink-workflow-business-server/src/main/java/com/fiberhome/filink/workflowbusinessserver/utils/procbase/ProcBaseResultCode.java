package com.fiberhome.filink.workflowbusinessserver.utils.procbase;

import com.fiberhome.filink.bean.ResultCode;

/**
 * 工单返回码
 *
 * @author chaofanrong@wistronits.com
 * @since 2019-02-18
 */
public class ProcBaseResultCode extends ResultCode {

    /**
     * 工单参数错误
     */
    public static final Integer PROC_PARAM_ERROR = 160101;

    /**
     * 工单名称重复
     */
    public static final Integer PROC_NAME_SAME = 160102;

    /**
     * 工单名称格式不正确
     */
    public static final Integer PROC_NAME_ERROR =  160106;

    /**
     * 缺少工单id
     */
    public static final Integer PROC_ID_LOSE = 160103;

    /**
     * 工单不存在
     */
    public static final Integer PROC_NOT_EXIST = 160104;

    /**
     * 工单不可删除
     */
    public static final Integer PROC_NOT_DELETE = 160105;

    /**
     * 存在已删除的工单
     */
    public static final Integer EXIST_IS_DELETED_PROC_DATA = 160107;

    /**
     * 工单新增异常
     */
    public static final Integer PROC_ADD_ERROR = 160109;

    /**
     * 工单修改异常
     */
    public static final Integer PROC_UPDATE_ERROR = 160113;

    /**
     * 工单指派异常
     */
    public static final Integer PROC_ASSIGN_ERROR = 160117;

    /**
     * 工单指派自己
     */
    public static final Integer PROC_ASSIGN_MYSELF = 160119;

    /**
     * 转办用户没有权限
     */
    public static final Integer ASSIGN_USER_NOT_HAVE_PERMISSION = 160120;

    /**
     * 工单转派异常
     */
    public static final Integer PROC_TURN_ERROR = 160122;

    /**
     * 工单撤回异常
     */
    public static final Integer PROC_REVOKE_ERROR = 160126;

    /**
     * 工单下载异常
     */
    public static final Integer PROC_DOWNLOAD_ERROR = 160130;

    /**
     * 工单退单异常
     */
    public static final Integer PROC_SINGLE_BACK_ERROR = 160134;

    /**
     * 工单确认退单异常
     */
    public static final Integer PROC_CHECK_SINGLE_BACK_ERROR = 160138;

    /**
     * 导出超过最大限制
     */
    public static final Integer EXPORT_DATA_TOO_LARGE = 160140;

    /**
     * 创建导出任务失败
     */
    public static final Integer FAILED_TO_CREATE_EXPORT_TASK = 160141;

    /**
     * 下载条件不对
     */
    public static final Integer APP_DOWNLOAD_CONDITION_WRONG = 160144;

    /**
     * 获取用户信息异常
     */
    public static final Integer USER_SERVER_ERROR = 160145;

    /**
     * 当前用户超过最大任务数量
     */
    public static final Integer EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS = 160146;

    /**
     * 没有工单能被下载
     */
    public static final Integer NOT_PROC_CAN_DOWNLOAD = 160147;

    /**
     * 获取告警信息异常
     */
    public static final Integer ALARM_SERVER_ERROR = 160148;

    /**
     * 获取设施信息异常
     */
    public static final Integer DEVICE_SERVER_ERROR = 160149;

    /** 告警存在进行中工单 */
    public static final Integer IS_EXISTS_PROC_FOR_ALARM = 160150;

    /**
     * 删除工单失败
     */
    public static final Integer DELETE_PROC_FAIL = 160151;
}
