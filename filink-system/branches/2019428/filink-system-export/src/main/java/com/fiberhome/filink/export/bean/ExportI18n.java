package com.fiberhome.filink.export.bean;

import lombok.Data;

/**
 * 设施国际化实体类
 *
 * @author WH1707069
 */
@Data
public class ExportI18n {
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
}
