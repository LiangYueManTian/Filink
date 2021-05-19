package com.fiberhome.filink.workflowbusinessserver.constant;

/**
 * 工单业务返回码类
 * @author hedongwei@wistronits.com
 * @date 2019/8/8 10:03
 */

public class WorkflowBusinessResultCode {


    /**********************************************巡检任务返回码************************************************************/
    /**
     * 参数错误
     */
    public static final Integer PARAM_ERROR = 164201;

    /**
     * 巡检任务名称重复
     */
    public static final Integer INSPECTION_NAME_IS_REPEAT = 164204;

    /**
     * 巡检任务不存在
     */
    public static final Integer INSPECTION_TASK_NOT_EXISTS = 164207;

    /**
     * 存在开启的巡检数据
     */
    public static final Integer EXISTS_OPEN_DATA = 164210;

    /**
     * 存在关闭的巡检数据
     */
    public static final Integer EXISTS_CLOSE_DATA = 164213;

    /**
     * 新增巡检任务失败
     */
    public static final Integer FAIL_INSERT_INSPECTION_TASK = 164216;

    /**
     * 存在已经删除的任务数据
     */
    public static final Integer EXIST_IS_DELETED_TASK_DATA = 164219;


    /**********************************************巡检任务返回码************************************************************/










    /**********************************************工单通用返回码************************************************************/

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

    /**
     * 无当前工单操作权限
     */
    public static final Integer PROC_IS_NOT_PERMISSION = 160152;

    /**
     * 指派工单状态异常
     */
    public static final Integer ASSIGN_STATUS_ERROR = 160153;

    /**
     * 撤回工单状态异常
     */
    public static final Integer REVOKE_STATUS_ERROR = 160154;

    /**
     * 确认退单状态异常
     */
    public static final Integer CHECK_SINGLE_BACK_STATUS_ERROR = 160155;


    /**
     * 回单消息编码
     */
    public static final Integer RECEIPT_MSG_RESULT_CODE = 160156;


    /**********************************************工单通用返回码************************************************************/









    /**********************************************工单返回码************************************************************/

    /**
     * 数据错误
     */
    public static final Integer DATA_ERROR = 161401;

    /**
     * 修改失败
     */
    public static final Integer UPDATE_FAIL = 161404;

    /**
     * 导出没有数据
     */
    public static final Integer EXPORT_NO_DATA = 161408;


    /**********************************************工单返回码************************************************************/











    /**********************************************巡检工单返回码************************************************************/

    /**
     * 巡检设施为空
     */
    public static final Integer INSPECTION_DEVICE_IS_EMPTY = 163210;

    /**
     * 巡检设施失败
     */
    public static final Integer INSPECTION_DEVICE_FAILED = 163214;

    /**
     * 用户编号为空
     */
    public static final Integer USER_ID_IS_EMPTY = 163218;

    /**
     * 提交巡检失败
     */
    public static final Integer COMMIT_INSPECTION_FAILED = 163222;

    /**
     * 设施没有巡检完成
     */
    public static final Integer INSPECTION_DEVICE_NOT_COMPLETE = 163226;

    /**********************************************巡检工单返回码************************************************************/





}
