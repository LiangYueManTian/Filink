package com.fiberhome.filink.workflowbusinessserver.constant;

/**
 * 巡检工单常量类
 * @author hedongwei@wistronits.com
 * @date 2019/3/11 17:18
 */

public class ProcInspectionConstants {

    /**
     * 自己新增巡检工单
     */
    public static final String ONESELF_ADD_PROC_INSPECTION = "1";

    /**
     * 巡检任务自动生成巡检工单
     */
    public static final String AUTO_ADD_PROC_INSPECTION = "2";

    /**
     * 告警生成巡检工单
     */
    public static final String AUTO_ADD_PROC_ALARM = "3";


    /**
     * 新增操作
     */
    public static final String OPERATE_ADD = "1";

    /**
     * 修改操作
     */
    public static final String OPERATE_UPDATE = "2";

    /**
     * 巡检工单编号属性名称
     */
    public static final String PROC_INSPECTION_ID_ATTR_NAME = "procId";

    /**
     * 巡检工单名称属性名称
     */
    public static final String PROC_INSPECTION_NAME_ATTR_NAME = "title";


    /**
     * 是告警前台调用
     */
    public static final String IS_ALARM_VIEW_CALL = "1";

    /**
     * 不是是告警前台调用
     */
    public static final String NOT_IS_ALARM_VIEW_CALL = "2";

    /**
     * 巡检结果成功
     */
    public static final String INSPECTION_RESULT_SUCESS = "0";

    /**
     * 巡检结果异常
     */
    public static final String INSPECTION_RESULT_EXCEPTION = "1";

}
