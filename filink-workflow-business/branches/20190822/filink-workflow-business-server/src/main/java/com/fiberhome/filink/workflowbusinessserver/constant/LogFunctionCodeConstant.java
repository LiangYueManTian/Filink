package com.fiberhome.filink.workflowbusinessserver.constant;

/**
 * 日志功能项编码
 * @author hedongwei@wistronits.com
 * @date 2019/5/8 12:39
 */

public class LogFunctionCodeConstant {

    /*--------------------日志功能项---------------------**/


    /*--------------------巡检工单---------------------**/
    /**
     * 新增巡检工单功能code
     */
    public static final String ADD_PROC_INSPECTION_FUNCTION_CODE = "1603101";

    /**
     * 修改巡检工单功能code
     */
    public static final String UPDATE_PROC_INSPECTION_FUNCTION_CODE = "1603102";

    /**
     * 删除巡检工单功能code
     */
    public static final String DELETE_PROC_INSPECTION_FUNCTION_CODE = "1603103";

    /**
     * 重新生成巡检工单
     */
    public static final String REGENERATE_PROC_INSPECTION_FUNCTION_CODE = "1603108";



    /*--------------------巡检任务---------------------**/

    /**
     * 新增巡检任务功能编码
     */
    public static final String INSERT_TASK_FUNCTION_CODE = "1604101";

    /**
     * 修改巡检任务功能编码
     */
    public static final String UPDATE_TASK_FUNCTION_CODE = "1604102";

    /**
     * 删除巡检任务功能编码
     */
    public static final String DELETE_TASK_FUNCTION_CODE = "1604103";

    /**
     * 开启巡检任务功能编码
     */
    public static final String OPEN_TASK_FUNCTION_CODE = "1604104";

    /**
     * 关闭巡检任务功能编码
     */
    public static final String CLOSE_TASK_FUNCTION_CODE = "1604105";


    /*--------------------工单通用功能---------------------**/
    /**
     * 指派销障工单功能编码
     */
    public static final String ASSIGN_CLEAR_FAILURE_PROC_FUNCTION_CODE = "1601101";

    /**
     * 撤回销障工单功能编码
     */
    public static final String REVOKE_CLEAR_FAILURE_PROC_FUNCTION_CODE = "1601102";

    /**
     * 确认销障工单退单功能编码
     */
    public static final String CHECK_CLEAR_FAILURE_SINGLE_BACK_FUNCTION_CODE = "1601103";

    /**
     * 销障工单退单功能编码
     */
    public static final String SINGLE_BACK_CLEAR_FAILURE_FUNCTION_CODE = "1601104";

    /**
     * 销障工单下载功能编码
     */
    public static final String DOWNLOAD_CLEAR_FAILURE_FUNCTION_CODE = "1601105";

    /**
     * 销障工单转派功能编码
     */
    public static final String TURN_CLEAR_FAILURE_FUNCTION_CODE = "1601106";

    /**
     * 销障工单工单回单编码
     */
    public static final String RETURN_CLEAR_FAILURE_FUNCTION_CODE = "1601107";

    /**
     * 巡检工单指派巡检工单
     */
    public static final String ASSIGN_PROC_INSPECTION_FUNCTION_CODE = "1601108";

    /**
     * 巡检工单撤回工单功能编码
     */
    public static final String REVOKE_INSPECTION_PROC_FUNCTION_CODE = "1601109";

    /**
     * 确认巡检工单退单功能编码
     */
    public static final String CHECK_INSPECTION_SINGLE_BACK_FUNCTION_CODE = "1601110";

    /**
     * 巡检工单退单功能编码
     */
    public static final String SINGLE_BACK_INSPECTION_FUNCTION_CODE = "1601111";

    /**
     * 巡检工单下载功能编码
     */
    public static final String DOWNLOAD_INSPECTION_FUNCTION_CODE = "1601112";

    /**
     * 巡检工单转派功能编码
     */
    public static final String TURN_INSPECTION_FUNCTION_CODE = "1601113";

    /**
     * 巡检工单工单回单编码
     */
    public static final String RETURN_INSPECTION_FUNCTION_CODE = "1601114";


    /*--------------------销障工单---------------------**/

    /**
     * 新增销障工单功能code
     */
    public static final String INSERT_PROC_CLEAR = "1602101";

    /**
     * 修改销障工单功能code
     */
    public static final String UPDATE_PROC_CLEAR = "1602102";

    /**
     * 删除销障工单功能code
     */
    public static final String DELETE_PROC_CLEAR_FAILURE = "1602103";

    /**
     * 重新生成销障工单功能code
     */
    public static final String REGENERATE_PROC_CLEAR_FAILURE_FUNCTION_CODE = "1602108";

    /*--------------------导出---------------------**/
    /**
     * 未完工巡检工单导出
     */
    public static final String PROC_INSPECTION_PROCESS_EXPORT_CODE = "1603104";

    /**
     * 已完工巡检记录导出
     */
    public static final String PROC_INSPECTION_COMPLETE_EXPORT_CODE = "1603105";

    /**
     * 巡检任务导出
     */
    public static final String INSPECTION_TASK_EXPORT_CODE = "1604106";

    /**
     * 未完工销障工单导出
     */
    public static final String PROC_CLEAR_FAILURE_UNFINISHED_EXPORT_CODE = "1603106";

    /**
     * 历史销障工单导出
     */
    public static final String PROC_CLEAR_FAILURE_HIS_EXPORT_CODE = "1603107";

    /**
     * 销障工单状态导出
     */
    public static final String CLEAR_FAILURE_STATUS_EXPORT_CODE = "1603109";

    /**
     * 销障工单设施类型导出
     */
    public static final String CLEAR_FAILURE_DEVICE_TYPE_EXPORT_CODE = "1603110";

    /**
     * 销障工单处理方案导出
     */
    public static final String CLEAR_FAILURE_PROCESSING_SCHEME_EXPORT_CODE = "1603111";

    /**
     * 销障工单故障原因导出
     */
    public static final String CLEAR_FAILURE_ERROR_REASON_EXPORT_CODE = "1603112";

    /**
     * 销障工单区域比导出
     */
    public static final String CLEAR_FAILURE_AREA_PERCENT_EXPORT_CODE = "1603113";

    /**
     * 巡检工单设施类型导出
     */
    public static final String INSPECTION_DEVICE_TYPE_EXPORT_CODE = "1603114";

    /**
     * 巡检工单状态导出
     */
    public static final String INSPECTION_STATUS_EXPORT_CODE = "1603115";

    /**
     * 区域巡检工单比导出
     */
    public static final String INSPECTION_AREA_PERCENT_EXPORT_CODE = "1603116";

    /**
     * 销障工单数量统计(top)
     */
    public static final String CLEAR_FAILURE_COUNT_EXPORT_CODE = "1603117";

    /**
     * 巡检工单数量统计(top)
     */
    public static final String INSPECTION_COUNT_EXPORT_CODE = "1603118";

}
