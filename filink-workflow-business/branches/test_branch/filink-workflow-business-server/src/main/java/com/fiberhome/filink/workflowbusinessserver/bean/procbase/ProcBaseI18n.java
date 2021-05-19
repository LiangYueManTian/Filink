package com.fiberhome.filink.workflowbusinessserver.bean.procbase;

import lombok.Data;

/**
 * 工单国际化实体类
 *
 * @author chaofanrong@wistronits.com
 */
@Data
public class ProcBaseI18n {

    //procbase
    /*----------------新增工单----------------*/
    /**新增工单失败*/
    public static final String ADD_PROC_FAIL = "ADD_PROC_FAIL";
    /**新增工单成功*/
    public static final String ADD_PROC_SUCCESS = "ADD_PROC_SUCCESS";

    /*----------------新增工单----------------*/
    /**删除工单失败*/
    public static final String DELETE_PROC_FAIL = "DELETE_PROC_FAIL";
    /**删除工单成功*/
    public static final String DELETE_PROC_SUCCESS = "DELETE_PROC_SUCCESS";

    /*----------------修改工单----------------*/
    /**修改工单失败*/
    public static final String UPDATE_PROC_FAIL = "UPDATE_PROC_FAIL";
    /**修改工单成功*/
    public static final String UPDATE_PROC_SUCCESS = "UPDATE_PROC_SUCCESS";

    /*----------------查看工单----------------*/
    /**查看工单失败*/
    public static final String QUERY_PROC_FAIL = "QUERY_PROC_FAIL";
    /**查询工单成功*/
    public static final String QUERY_PROC_SUCCESS = "QUERY_PROC_SUCCESS";

    /*----------------删除工单关联设施信息----------------*/
    /**删除工单关联设施信息失败*/
    public static final String DELETE_PROC_RELATED_DEVICE_FAIL = "DELETE_PROC_RELATED_DEVICE_FAIL";
    /**删除工单关联设施信息成功*/
    public static final String DELETE_PROC_RELATED_DEVICE_SUCCESS = "DELETE_PROC_RELATED_DEVICE_SUCCESS";

    /*----------------删除工单关联部门信息----------------*/
    /**删除工单关联部门信息失败*/
    public static final String DELETE_PROC_RELATED_UNIT_FAIL = "DELETE_PROC_RELATED_UNIT_FAIL";
    /**删除工单关联部门信息成功*/
    public static final String DELETE_PROC_RELATED_UNIT_SUCCESS = "DELETE_PROC_RELATED_UNIT_SUCCESS";

    /*----------------删除工单关联巡检记录信息----------------*/
    /**删除工单关联巡检记录失败*/
    public static final String DELETE_PROC_RELATED_RECORD_FAIL = "DELETE_PROC_RELATED_RECORD_FAIL";
    /**删除工单关联巡检记录成功*/
    public static final String DELETE_PROC_RELATED_RECORD_SUCCESS = "DELETE_PROC_RELATED_RECORD_SUCCESS";

    /*----------------查询状态总数----------------*/
    /**查询状态总数失败*/
    public static final String QUERY_PROC_STATUS_COUNT_FAIL = "QUERY_PROC_STATUS_COUNT_FAIL";
    /**查询状态总数成功*/
    public static final String QUERY_PROC_STATUS_COUNT_SUCCESS = "QUERY_PROC_STATUS_COUNT_SUCCESS";

    /**有必填参数未填写*/
    public static final String PROC_PARAM_ERROR = "PROC_PARAM_ERROR";
    /**工单名称格式不正确*/
    public static final String PROC_NAME_ERROR =  "PROC_NAME_ERROR";
    /**工单名称重复*/
    public static final String PROC_NAME_SAME = "PROC_NAME_SAME";

    /**缺少工单id*/
    public static final String PROC_ID_LOSE = "PROC_ID_LOSE";
    /**工单不存在*/
    public static final String PROC_IS_NOT_EXIST = "PROC_IS_NOT_EXIST";

    /**工单类型不可修改*/
    public static final String PROC_TYPE_NOT_MODIFY = "PROC_TYPE_NOT_MODIFY";

    /**工单名称为空*/
    public static final String PROC_NAME_NULL = "PROC_NAME_NULL";
    /**工单名可用*/
    public static final String PROC_NAME_AVAILABLE = "PROC_NAME_AVAILABLE";
    /**工单不可删除*/
    public static final String PROC_NOT_DELETE = "PROC_NOT_DELETE";
    /**存在已删除的工单*/
    public static final String EXIST_IS_DELETED_PROC_DATA = "EXIST_IS_DELETED_PROC_DATA";

    /**获取设施信息失败*/
    public static final String FAILED_TO_OBTAIN_DEVICE_INFORMATION = "FAILED_TO_OBTAIN_DEVICE_INFORMATION";

    /**获取部门信息失败*/
    public static final String FAILED_TO_OBTAIN_DEPARTMENT_INFORMATION = "FAILED_TO_OBTAIN_DEPARTMENT_INFORMATION";

    /**获取告警信息失败*/
    public static final String FAILED_TO_OBTAIN_ALARM_INFORMATION = "FAILED_TO_OBTAIN_ALARM_INFORMATION";

    /**确认退单操作 */
    public static final String CHECK_SINGLE_BACK_SUCCESS = "CHECK_SINGLE_BACK_SUCCESS";
    public static final String CHECK_SINGLE_BACK_FAIL = "CHECK_SINGLE_BACK_FAIL";

    /**退单操作*/
    public static final String SINGLE_BACK_FAIL = "SINGLE_BACK_FAIL";
    public static final String SINGLE_BACK_SUCCESS = "SINGLE_BACK_SUCCESS";

    /**撤回操作*/
    public static final String REVOKE_FAIL = "REVOKE_FAIL";
    public static final String REVOKE_SUCCESS = "REVOKE_SUCCESS";


    /**下载数据操作*/
    public static final String DOWNLOAD_PROC_FAIL = "DOWNLOAD_PROC_FAIL";
    public static final String DOWNLOAD_PROC_SUCCESS = "DOWNLOAD_PROC_SUCCESS";
    public static final String APP_DOWNLOAD_CONDITION_WRONG = "APP_DOWNLOAD_CONDITION_WRONG";
    public static final String NOT_PROC_CAN_DOWNLOAD = "NOT_PROC_CAN_DOWNLOAD";

    /**工单指派操作 */
    public static final String PROC_ASSIGN_FAIL = "PROC_ASSIGN_FAIL";
    public static final String PROC_ASSIGN_SUCCESS = "PROC_ASSIGN_SUCCESS";
    public static final String PROC_ASSIGN_MYSELF = "PROC_ASSIGN_MYSELF";
    public static final String ASSIGN_USER_NOT_HAVE_PERMISSION = "ASSIGN_USER_NOT_HAVE_PERMISSION";

    /**工单转派操作 */
    public static final String PROC_TURN_FAIL = "PROC_TURN_FAIL";
    public static final String PROC_TURN_SUCCESS = "PROC_TURN_SUCCESS";

    /**工单回单操作*/
    public static final String PROC_RECEIPT_FAIL = "PROC_RECEIPT_FAIL";
    public static final String PROC_RECEIPT_SUCCESS = "PROC_RECEIPT_SUCCESS";

    /**销障工单未完工列表*/
    public static final String PROC_CLEAR_FAILURE_UNFINISHED_LIST = "PROC_CLEAR_FAILURE_UNFINISHED_LIST";

    /**销障工单历史列表*/
    public static final String PROC_CLEAR_FAILURE_HISTORY_LIST = "PROC_CLEAR_FAILURE_HISTORY_LIST";

    /** 巡检工单未完工列表*/
    public static final String PROC_INSPECTION_PROCESS_LIST = "PROC_INSPECTION_PROCESS_LIST";

    /**巡检工单完工列表*/
    public static final String PROC_INSPECTION_COMPLETE_LIST = "PROC_INSPECTION_COMPLETE_LIST";

    /**销障工单状态统计*/
    public static final String CLEAR_FAILURE_STATUS_EXPORT_STATISTICAL = "CLEAR_FAILURE_STATUS_EXPORT_STATISTICAL";

    /**销障工单设施类型统计*/
    public static final String CLEAR_FAILURE_DEVICE_TYPE_EXPORT_STATISTICAL = "CLEAR_FAILURE_DEVICE_TYPE_EXPORT_STATISTICAL";

    /**销障工单处理方案统计*/
    public static final String CLEAR_FAILURE_PROCESSING_SCHEME_EXPORT_STATISTICAL = "CLEAR_FAILURE_PROCESSING_SCHEME_EXPORT_STATISTICAL";

    /**销障工单异常原因统计*/
    public static final String CLEAR_FAILURE_ERROR_REASON_EXPORT_STATISTICAL = "CLEAR_FAILURE_ERROR_REASON_EXPORT_STATISTICAL";

    /**销障工单区域比统计*/
    public static final String CLEAR_FAILURE_AREA_PERCENT_EXPORT_STATISTICAL = "CLEAR_FAILURE_AREA_PERCENT_EXPORT_STATISTICAL";

    /**巡检工单设施类型统计*/
    public static final String INSPECTION_DEVICE_TYPE_EXPORT_STATISTICAL = "INSPECTION_DEVICE_TYPE_EXPORT_STATISTICAL";

    /**巡检工单状态统计*/
    public static final String INSPECTION_STATUS_EXPORT_STATISTICAL = "INSPECTION_STATUS_EXPORT_STATISTICAL";

    /**巡检工单区域比统计*/
    public static final String INSPECTION_AREA_PERCENT_EXPORT_STATISTICAL = "INSPECTION_AREA_PERCENT_EXPORT_STATISTICAL";

    /**销障工单次数统计*/
    public static final String CLEAR_FAILURE_COUNT_EXPORT_STATISTICAL = "CLEAR_FAILURE_COUNT_EXPORT_STATISTICAL";

    /**巡检工单次数统计*/
    public static final String INSPECTION_COUNT_EXPORT_STATISTICAL = "INSPECTION_COUNT_EXPORT_STATISTICAL";

    /**导出超过最大限制*/
    public static final String EXPORT_DATA_TOO_LARGE = "EXPORT_DATA_TOO_LARGE";

    /**创建导出任务失败*/
    public static final String FAILED_TO_CREATE_EXPORT_TASK="FAILED_TO_CREATE_EXPORT_TASK";
    /**创建导出任务成功*/
    public static final String THE_EXPORT_TASK_WAS_CREATED_SUCCESSFULLY="THE_EXPORT_TASK_WAS_CREATED_SUCCESSFULLY";

    /*----------------工单状态----------------*/
    /**待指派*/
    public static final String PROC_STATUS_ASSIGNED = "PROC_STATUS_ASSIGNED";

    /**待处理*/
    public static final String PROC_STATUS_PENDING = "PROC_STATUS_PENDING";

    /**处理中*/
    public static final String PROC_STATUS_PROCESSING = "PROC_STATUS_PROCESSING";

    /**已完成*/
    public static final String PROC_STATUS_COMPLETED = "PROC_STATUS_COMPLETED";

    /** 已退单*/
    public static final String PROC_STATUS_SINGLE_BACK = "PROC_STATUS_SINGLE_BACK";

    /** 已转派*/
    public static final String PROC_STATUS_TURN_PROCESSING = "PROC_STATUS_TURN_PROCESSING";

    /*----------------设施类型----------------*/
    /** 光交箱*/
    public static final String DEVICE_TYPE_001 = "DEVICE_TYPE_001";
    /** 人井*/
    public static final String DEVICE_TYPE_030 = "DEVICE_TYPE_030";
    /** 配线架*/
    public static final String DEVICE_TYPE_060 = "DEVICE_TYPE_060";
    /** 接头盒*/
    public static final String DEVICE_TYPE_090 = "DEVICE_TYPE_090";
    /** 室外柜*/
    public static final String DEVICE_TYPE_210 = "DEVICE_TYPE_210";

    /*----------------故障原因----------------*/
    /** 其他 */
    public static final String ERROR_REASON_0 = "ERROR_REASON_0";
    /** 人为损坏 */
    public static final String ERROR_REASON_1 = "ERROR_REASON_1";
    /** 道路施工 */
    public static final String ERROR_REASON_2 = "ERROR_REASON_2";
    /** 盗穿 */
    public static final String ERROR_REASON_3 = "ERROR_REASON_3";
    /** 销障 */
    public static final String ERROR_REASON_4 = "ERROR_REASON_4";

    /*----------------退单原因----------------*/
    /** 其他 */
    public static final String SINGLE_BACK_REASON_0 = "SINGLE_BACK_REASON_0";
    /** 误报 */
    public static final String SINGLE_BACK_REASON_1 = "SINGLE_BACK_REASON_1";

    /*----------------处理方案----------------*/
    /** 其他 */
    public static final String PROCESSING_SCHEME_0 = "PROCESSING_SCHEME_0";
    /** 报修 */
    public static final String PROCESSING_SCHEME_1 = "PROCESSING_SCHEME_1";
    /** 现场销障 */
    public static final String PROCESSING_SCHEME_2 = "PROCESSING_SCHEME_2";


    /** 用户服务异常*/
    public static final String USER_SERVER_ERROR  = "USER_SERVER_ERROR";

    /**
     * 当前用户超过最大任务数量
     */
    public static final String EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS = "EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS";

    /**
     * 当前设施没有工单信息
     */
    public static final String DEVICE_NOT_HAVE_PROC = "DEVICE_NOT_HAVE_PROC";

    /**
     *  访问图片服务失败
     */
    public static final String DEVICE_PIC_SERVER_ERROR = "DEVICE_PIC_SERVER_ERROR";

    /** 告警服务异常*/
    public static final String ALARM_SERVER_ERROR  = "ALARM_SERVER_ERROR";

    /** 工单下载提醒标题 */
    public static final String PROC_DOWNLOAD_NOTICE_TITLE = "PROC_DOWNLOAD_NOTICE_TITLE";

    /** 告警存在进行中工单 */
    public static final String IS_EXISTS_PROC_FOR_ALARM = "IS_EXISTS_PROC_FOR_ALARM";

}
