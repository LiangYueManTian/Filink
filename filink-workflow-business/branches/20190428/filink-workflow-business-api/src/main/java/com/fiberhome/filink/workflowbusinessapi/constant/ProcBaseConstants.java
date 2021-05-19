package com.fiberhome.filink.workflowbusinessapi.constant;

/**
 * 流程常量
 * @author hedongwei@wistronits.com
 * @date 2019/3/23 11:31
 */

public class ProcBaseConstants {

    /**
     * 工单类型
     */
    public static final String PROC_CLEAR_FAILURE = "clear_failure";
    public static final String PROC_INSPECTION = "inspection";

    /**
     * id列表类型
     */
    public static final String DEVICE_IDS = "device";
    public static final String AREA_IDS = "area";

    /**
     * 待指派
     */
    public static final String PROC_STATUS_ASSIGNED = "assigned";

    /**
     * 待处理
     */
    public static final String PROC_STATUS_PENDING = "pending";

    /**
     * 处理中
     */
    public static final String PROC_STATUS_PROCESSING = "processing";

    /**
     * 已完成
     */
    public static final String PROC_STATUS_COMPLETED = "completed";

    /**
     * 已退单
     */
    public static final String PROC_STATUS_SINGLE_BACK = "singleBack";

    /**
     * 转办状态
     */
    public static final String PROC_STATUS_TURN_PROCESSING = "turnProcessing";

    /**
     * 历史总数
     */
    public static final String PROC_HIS_TOTAL_COUNT = "hisTotalCount";

    /**
     * 分组字段
     */
    public static final String PROC_GROUP_STATUS = "status";
    public static final String PROC_GROUP_ERROR_REASON = "error_reason";
    public static final String PROC_GROUP_PROCESSING_SCHEME = "processing_scheme";
    public static final String PROC_GROUP_DEVICE_TYPE = "device_type";

    /**
     * 工单来源
     */
    public static final String PROC_RESOURCE_TYPE_1 = "1";
    public static final String PROC_RESOURCE_TYPE_2 = "2";
    public static final String PROC_RESOURCE_TYPE_3 = "3";

    /**
     * 流程模板名称
     */
    public static final String PROC_TEMPLATE_NAME = "process";


    /**
     * 未确认退单
     */
    public static final String NOT_CHECK_SINGLE_BACK = "0";

    /**
     * 已确认退单
     */
    public static final String IS_CHECK_SINGLE_BACK = "1";
}
