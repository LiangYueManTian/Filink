package com.fiberhome.filink.workflowbusinessserver.constant;

/**
 * <p>
 * 常量类
 * </p>
 *
 * @author chaofanrong@wistronits.com
 * @since 2019-03-08
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

    /*----------------工单状态----------------*/
    /**待指派*/
    public static final String PROC_STATUS_ASSIGNED = "assigned";

    /**待处理*/
    public static final String PROC_STATUS_PENDING = "pending";

    /**处理中*/
    public static final String PROC_STATUS_PROCESSING = "processing";

    /**已完成*/
    public static final String PROC_STATUS_COMPLETED = "completed";

    /** 已退单*/
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

    /**
     * 逻辑删除状态（正常）
     */
    public static final String IS_DELETED_0 = "0";

    /**
     * 逻辑删除状态（删除）
     */
    public static final String IS_DELETED_1 = "1";

    /**
     * 查询类型
     */
    public static final String PROC_METHOD_DETAIL = "detail";
    public static final String PROC_METHOD_LIST = "list";

    /**
     * 查看图片跳转类型（销障未完工列表）
     */
    public static final String PIC_STATUS_CLEAR_UNFINISHED = "1";
    /**
     * 查看图片跳转类型（销障历史列表）
     */
    public static final String PIC_STATUS_CLEAR_HIS = "2";
    /**
     * 查看图片跳转类型（巡检未完工列表）
     */
    public static final String PIC_STATUS_INSPECTION_UNFINISHED = "3";
    /**
     * 查看图片跳转类型（巡检历史列表）
     */
    public static final String PIC_STATUS_INSPECTION_HIS = "4";

    /**
     * 重新生成工单
     */
    public static final boolean IS_REGENERATE = true;

    /**
     * 不重新生成工单
     */
    public static final boolean IS_NOT_REGENERATE = false;


    /*----------------设施类型----------------*/
    /** 光交箱*/
    public static final String DEVICE_TYPE_001 = "001";
    /** 人井*/
    public static final String DEVICE_TYPE_030 = "030";
    /** 配线架*/
    public static final String DEVICE_TYPE_060 = "060";
    /** 接头盒*/
    public static final String DEVICE_TYPE_090 = "090";
    /** 分纤箱*/
    public static final String DEVICE_TYPE_150 = "150";

    /*----------------故障原因----------------*/
    /** 其他 */
    public static final String ERROR_REASON_0 = "0";
    /** 人为损坏 */
    public static final String ERROR_REASON_1 = "1";
    /** 道路施工 */
    public static final String ERROR_REASON_2 = "2";
    /** 盗穿 */
    public static final String ERROR_REASON_3 = "3";
    /** 销障 */
    public static final String ERROR_REASON_4 = "4";

    /*----------------退单原因----------------*/
    /** 其他 */
    public static final String SINGLE_BACK_REASON_0 = "0";
    /** 误报 */
    public static final String SINGLE_BACK_REASON_1 = "1";

    /*----------------处理方案----------------*/
    /**  */
    public static final String PROCESSING_SCHEME_0 = "0";
    /**  */
    public static final String PROCESSING_SCHEME_1 = "1";
    /**  */
    public static final String PROCESSING_SCHEME_2 = "2";
    /**  */
    public static final String PROCESSING_SCHEME_3 = "3";
    /**  */
    public static final String PROCESSING_SCHEME_4 = "4";

    /*----------------告警存在进行中工单----------------*/
    /** 告警存在进行中工单 */
    public static final String IS_EXISTS_PROC_FOR_ALARM = "1";
    /** 告警不存在进行中工单 */
    public static final String NOT_EXISTS_PROC_FOR_ALARM = "0";

    /*----------------设施信息属性----------------*/
    /** 设施id */
    public static final String DEVICE_INFO_DEVICE_ID = "deviceId";
    /** 设施name */
    public static final String DEVICE_INFO_DEVICE_NAME = "deviceName";
    /** 设施type */
    public static final String DEVICE_INFO_DEVICE_TYPE = "deviceType";
    /** 设施typeName */
    public static final String DEVICE_INFO_DEVICE_TYPE_NAME = "deviceTypeName";
    /** 设施deviceAreaId */
    public static final String DEVICE_INFO_DEVICE_AREA_ID = "deviceAreaId";
    /** 设施deviceAreaName */
    public static final String DEVICE_INFO_DEVICE_AREA_NAME = "deviceAreaName";

    /*----------------操作类型----------------*/
    /** 重新生成 */
    public static final String OPERATOR_TYPE_REGENERATE = "regenerate";
}
