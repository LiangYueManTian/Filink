package com.fiberhome.filink.dump.constant;

/**
 * 转储服务常量
 * @author hedongwei@wistronits.com
 * @date 2019/8/5 16:00
 */

public class DumpConstant {

    /**
     * 操作日志集合名称
     */
    public static final  String OPERATE_LOG_COLLECTION_NAME = "operateLog";

    /**
     * 安全日志集合名称
     */
    public static final  String SECURITY_LOG_COLLECTION_NAME = "securityLog";


    /**
     * 系统日志集合名称
     */
    public static final  String SYSTEM_LOG_COLLECTION_NAME = "systemLog";

    /**
     * 设施日志集合名称
     */
    public static final  String DEVICE_LOG_COLLECTION_NAME = "deviceLog";

    /**
     * 历史告警集合名称
     */
    public static final  String ALARM_HISTORY_COLLECTION_NAME = "alarm_history";

    /**
     * 转储触发条件定时任务执行
     */
    public static final String DUMP_TRIGGER_JOB = "1";

    /**
     * 转储触发条件立即执行
     */
    public static final String DUMP_TRIGGER_RUN_NOW = "0";

    /**
     * 转储任务执行时间
     */
    public static final int DUMP_TASK_EXEC_HOUR = 1;
}
