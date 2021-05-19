package com.fiberhome.filink.workflowbusinessserver.bean.inspectiontask;

import lombok.Data;

/**
 * 工单国际化实体类
 *
 * @author hedongwei@wistronits.com
 */
@Data
public class InspectionTaskI18n {

    /**
     * 参数错误
     */
    public static final String PARAM_ERROR = "PARAM_ERROR";

    /**
     * 存在关闭的数据
     */
    public static final String EXISTS_CLOSE_DATA = "EXISTS_CLOSE_DATA";

    /**
     * 存在开启的数据
     */
    public static final String EXISTS_OPEN_DATA = "EXISTS_OPEN_DATA";

    /**
     * 巡检任务名称重复
     */
    public static final String INSPECTION_NAME_IS_REPEAT = "INSPECTION_NAME_IS_REPEAT";

    /**
     * 巡检任务名称可用
     */
    public static final String INSPECTION_NAME_IS_AVAILABLE = "INSPECTION_NAME_IS_AVAILABLE";

    /**
     * 新增巡检任务成功
     */
    public static final String SUCCESS_INSERT_INSPECTION_TASK = "SUCCESS_INSERT_INSPECTION_TASK";

    /**
     * 新增巡检任务失败
     */
    public static final String FAIL_INSERT_INSPECTION_TASK = "FAIL_INSERT_INSPECTION_TASK";

    /**
     * 新增巡检任务记录成功
     */
    public static final String SUCCESS_INSERT_TASK_RECORD = "SUCCESS_INSERT_TASK_RECORD";

    /**
     * 巡检任务不存在
     */
    public static final String INSPECTION_TASK_NOT_EXIST = "INSPECTION_TASK_NOT_EXIST";

    /**
     * 存在已经删除的任务
     */
    public static final String EXIST_IS_DELETED_TASK_DATA = "EXIST_IS_DELETED_TASK_DATA";

    /**
     * 开启巡检任务成功
     */
    public static final String INSPECTION_TASK_OPEN_SUCCESS= "INSPECTION_TASK_OPEN_SUCCESS";

    /**
     * 关闭巡检任务成功
     */
    public static final String INSPECTION_TASK_CLOSE_SUCCESS = "INSPECTION_TASK_CLOSE_SUCCESS";

    /**
     * 巡检任务列表
     */
    public static final String INSPECTION_TASK_LIST = "INSPECTION_TASK_LIST" ;

    /**-------------------------------巡检状态----------------------------------- */
    /**
     * 巡检状态未巡检
     */
    public static final String INSPECTION_STATUS_WAIT = "INSPECTION_STATUS_WAIT";

    /**
     * 巡检状态巡检中
     */
    public static final String INSPECTION_STATUS_DOING = "INSPECTION_STATUS_DOING";

    /**
     * 巡检状态已完成
     */
    public static final String INSPECTION_STATUS_COMPLETED = "INSPECTION_STATUS_COMPLETED";

    /**-------------------------------巡检任务类型----------------------------------- */
    /**
     * 巡检类型例行巡检
     */
    public static final String TASK_TYPE_ROUTINE_INSPECTION = "TASK_TYPE_ROUTINE_INSPECTION";

    /**-------------------------------巡检任务是否开启----------------------------------- */

    /**
     * 启用
     */
    public static final String IS_OPEN = "IS_OPEN";

    /**
     * 禁用
     */
    public static final String IS_CLOSE = "IS_CLOSE";
}
