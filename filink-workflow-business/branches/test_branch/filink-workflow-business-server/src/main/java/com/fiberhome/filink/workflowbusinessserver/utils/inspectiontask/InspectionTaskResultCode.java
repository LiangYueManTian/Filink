package com.fiberhome.filink.workflowbusinessserver.utils.inspectiontask;

/**
 * 巡检任务返回参数
 * @author hedongwei@wistronits.com
 * @date 2019/2/26 15:47
 */

public class InspectionTaskResultCode {

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
}
