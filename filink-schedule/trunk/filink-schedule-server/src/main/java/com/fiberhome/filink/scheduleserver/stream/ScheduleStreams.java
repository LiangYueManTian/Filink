package com.fiberhome.filink.scheduleserver.stream;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/**
 * 输出输入通道定义
 *
 * @author yuanyao@wistronits.com
 * create on 2019/1/8 18:04
 */
public interface ScheduleStreams {
    /**
     * 定时删除导出任务
     */
    String EXPORT_OUTPUT = "export_output";
    /**
     * 定时补偿删除设施关联信息
     */
    String DEVICE_TIMED_TASK_OUTPUT = "device_timed_task_output";

    String WORKFLOW_BUSINESS_OUTPUT = "workflow_business_output";

    String PROC_INSPECTION_OUTPUT = "proc_inspection_output";

    String CMD_RESEND_OUTPUT = "cmd_resend_output";

    String OCEAN_CONNECT_CMD_RESEND_OUTPUT = "ocean_connect_cmd_resend_output";

    String ONE_NET_CMD_RESEND_OUTPUT = "one_net_cmd_resend_output";

    /**
     * 账号禁用
     */
    String USER_FORBIDDEN_OUTPUT = "user_forbidden_output";

    /**
     * 解锁用户
     */
    String UNLOCK_USER = "unlock_user";

    /**
     * 账号的有效期
     */
    String USER_EXPIRE = "user_expire";

    /**
     * 账号的有效期
     */
    String EXPORT_LOG = "export_log";

    /**
     * 刷新首页设施Redis缓存信息
     */
    String HOME_DEVICE_OUTPUT = "home_device_output";

    /**
     * 定时刷新设备升级文件
     */
    String UPGRADE_FILE_OUTPUT = "upgrade_file_output";
    /**
     * 当天工单统计数量
     */
    String NOW_DAY_PROC_COUNT_OUT = "now_day_proc_output";

    /**
     * 周工单统计数量
     */
    String WEEK_PROC_COUNT_OUT = "week_proc_output";

    /**
     * 月工单统计数量
     */
    String MONTH_PROC_COUNT_OUT = "month_proc_output";

    /**
     * 年工单统计数量
     */
    String YEAR_PROC_COUNT_OUT = "year_proc_output";

    /**
     * 开锁次数统计
     */
    String UNLOCKING_STATISTICS_OUTPUT = "unlocking_statistics_output";
    /**
     * 告警增量统计
     */
    String INCREMENTAL_EXPIRE_OUTPUT = "incremental_expire_output";
    /**
     * 告警增量周统计
     */
    String INCREMENTAL_EXPIRE_WEEK_OUTPUT = "incremental_expire_week_output";
    /**
     * 告警增量月统计
     */
    String INCREMENTAL_EXPIRE_MONTH_OUTPUT = "incremental_expire_month_output";
    /**
     * 当前告警转历史
     */
    String DELETE_DEVICE_SENSOR = "delete_device_sensor";
    /**
     * 删除过期传感值
     */
    String ALARM_CLEAN_STATUS = "alarm_clean_status_output";

    /**
     * oneNet命令重发输出
     * @return 返回结果
     */
    @Output(HOME_DEVICE_OUTPUT)
    MessageChannel homeDeviceOutput();
    /**
     * oneNet命令重发输出
     * @return 返回结果
     */
    @Output(ONE_NET_CMD_RESEND_OUTPUT)
    MessageChannel oneNetCmdResendOutput();

    /**
     * oceanConnect命令重发输出
     * @return 返回结果
     */
    @Output(OCEAN_CONNECT_CMD_RESEND_OUTPUT)
    MessageChannel oceanConnectCmdResendOutput();

    /**
     * udp命令重发输出
     * @return 返回结果
     */
    @Output(CMD_RESEND_OUTPUT)
    MessageChannel cmdResendOutput();

    /**
     * 任务中心消息输出通道
     * @return 返回结果
     */
    @Output(EXPORT_OUTPUT)
    MessageChannel output();

    /**
     * 任务中心消息输出通道
     * @return 返回结果
     */
    @Output(WORKFLOW_BUSINESS_OUTPUT)
    MessageChannel workflowBusinessOutput();

    /**
     * 任务中心消息输出通道
     * @return 返回结果
     */
    @Output(USER_FORBIDDEN_OUTPUT)
    MessageChannel userForbiddenOutPut();

    /**
     * 任务中心消息输出通道
     * @return 返回结果
     */
    @Output(UNLOCK_USER)
    MessageChannel unlockUser();

    /**
     * 用户的有效期
     * @return 返回结果
     */
    @Output(USER_EXPIRE)
    MessageChannel userExpire();

    /**
     * 工单日增量统计
     * @return 返回结果
     */
    @Output(NOW_DAY_PROC_COUNT_OUT)
    MessageChannel nowDayProcCountOut();

    /**
     * 工单周增量统计
     * @return 返回结果
     */
    @Output(WEEK_PROC_COUNT_OUT)
    MessageChannel weekProcCountOutput();

    /**
     * 工单月增量统计
     * @return 返回结果
     */
    @Output(MONTH_PROC_COUNT_OUT)
    MessageChannel monthProcCountOutput();

    /**
     * 工单年增量统计
     * @return 返回结果
     */
    @Output(YEAR_PROC_COUNT_OUT)
    MessageChannel yearProcCountOutput();

    /**
     * 定时刷新设备升级文件
     * @return 返回结果
     */
    @Output(UPGRADE_FILE_OUTPUT)
    MessageChannel refreshUpgradeFile();

    /**
     * 定时计算设施开锁次数
     * @return 返回结果
     */
    @Output(UNLOCKING_STATISTICS_OUTPUT)
    MessageChannel unlockingStatistics();

    /**
     * 用户的有效期
     * @return 返回结果
     */
    @Output(EXPORT_LOG)
    MessageChannel exportLog();
    /**
     * 定时计算设施开锁次数
     * @return 返回结果
     */
    @Output(DEVICE_TIMED_TASK_OUTPUT)
    MessageChannel deviceTimedTask();
    /**
     * 告警增量统计
     * @return 返回结果
     */
    @Output(INCREMENTAL_EXPIRE_OUTPUT)
    MessageChannel alarmSourceIncremental();
    /**
     * 告警增量周统计
     * @return 返回结果
     */
    @Output(INCREMENTAL_EXPIRE_WEEK_OUTPUT)
    MessageChannel alarmSourceIncrementalWeek();
    /**
     * 告警增量周统计
     * @return 返回结果
     */
    @Output(INCREMENTAL_EXPIRE_MONTH_OUTPUT)
    MessageChannel alarmSourceIncrementalMonth();

    /**
     * 删除过期传感值通道
     * @return
     */
    @Output(DELETE_DEVICE_SENSOR)
    MessageChannel deleteDeviceSensor();

    /**
     * 当前告警转历史
     * @return 返回结果
     */
    @Output(ALARM_CLEAN_STATUS)
    MessageChannel alarmCleanStatus();
}
