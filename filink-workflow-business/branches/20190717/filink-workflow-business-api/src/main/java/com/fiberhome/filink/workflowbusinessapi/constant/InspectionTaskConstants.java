package com.fiberhome.filink.workflowbusinessapi.constant;

/**
 * 巡检任务常量类
 * @author hedongwei@wistronits.com
 * @date 2019/2/27 10:40
 */

public class InspectionTaskConstants {

    /**
     * 开启巡检任务
     */
    public static final String IS_OPEN = "1";

    /**
     * 开启巡检任务
     */
    public static final String IS_CLOSE = "0";

    /**
     * 巡检状态未巡检
     */
    public static final String INSPECTION_TASK_WAIT  = "1";

    /**
     * 巡检状态巡检中
     */
    public static final String INSPECTION_TASK_DOING = "2";

    /**
     * 巡检状态已完成
     */
    public static final String INSPECTION_TASK_COMPLETED = "3";


    /**
     * 巡检类型 例行巡检
     */
    public static final String TASK_TYPE_ROUTINE_INSPECTION = "1";

    /**
     * 巡检任务名称属性名称
     */
    public static final String INSPECTION_TASK_NAME_ATTR_NAME = "inspectionTaskName";

    /**
     * 巡检任务编号属性名称
     */
    public static final String INSPECTION_TASK_ID_ATTR_NAME = "inspectionTaskId";

    /**
     * 巡检任务区域编号集合参数列
     */
    public static final String PARAM_COLUMN_AREA_IDS = "areaIds";

    /**
     * 巡检任务单位编号集合参数列
     */
    public static final String PARAM_COLUMN_DEPT_IDS = "departmentIds";
}
