package com.fiberhome.filink.dump.constant;

import lombok.Data;

/**
 * 设施国际化实体类
 *
 * @author WH1707069
 */
@Data
public class ExportI18nConstant {
    /**
     * 有必填字段为null
     */
    public static final String PARAM_NULL = "PARAM_NULL";
    /**
     * 数据异常
     */
    public static final String DIRTY_DATA = "DIRTY_DATA";
    /**
     * 数据库异常
     */
    public static final String DATA_BASE_ERROR = "DATABASE_ERROR";
    /**
     * 停止任务成功
     */
    public static final String STOP_MISSION_SUCCESSFUL = "STOP_MISSION_SUCCESSFUL";
    /**
     * 删除任务成功
     */
    public static final String DELETE_MISSION_SUCCESSFUL = "DELETE_MISSION_SUCCESSFUL";
    /**
     * 任务不存在
     */
    public static final String TASK_DOES_NOT_EXIST = "TASK_DOES_NOT_EXIST";
    /**
     * 新增任务成功
     */
    public static final String INCREASE_TASK_SUCCESS = "INCREASE_TASK_SUCCESS";
    /**
     * 有新导出任务完成
     */
    public static final String ONE_EXPORT_TASK_COMPLETED = "ONE_EXPORT_TASK_COMPLETED";

    /**
     * 数据转储成功
     */
    public static final String DUMP_DATA_SUCCESS = "DUMP_DATA_SUCCESS";

    /**
     * 未开启数据转储
     */
    public static final String DISABLE_DUMP_DATA = "DISABLE_DUMP_DATA";

    /**
     * 没有足够的转储数据
     */
    public static final String NOT_ENOUGH_DUMP_DATA = "NOT_ENOUGH_DUMP_DATA";

    /**
     * 指定的时间内已经转储过数据
     */
    public static final String SPECIFIED_TIME_HAS_DUMP_DATA = "SPECIFIED_TIME_HAS_DUMP_DATA";

    /**
     * 未到达转储时间
     */
    public static final String DUMP_TIME_NOT_REACHED = "DUMP_TIME_NOT_REACHED";

    /**
     * 操作日志
     */
    public static final String OPERATE_LOG = "OPERATE_LOG";

    /**
     * 安全日志
     */
    public static final String SECURITY_LOG = "SECURITY_LOG";


    /**
     * 系统日志
     */
    public static final String SYSTEM_LOG = "SYSTEM_LOG";

    /**
     * 告警
     */
    public static final String ALARM = "ALARM";

    /**
     * 设施日志
     */
    public static final String DEVICE_LOG = "DEVICE_LOG";

}
