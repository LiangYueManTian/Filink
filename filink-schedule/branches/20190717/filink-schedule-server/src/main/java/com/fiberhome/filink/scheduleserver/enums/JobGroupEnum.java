package com.fiberhome.filink.scheduleserver.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * 任务分组枚举
 * 导出任务       export
 * 转储          dump
 * 巡检          inspectiontask
 * 流程任务      proc
 * 清理          clean
 * 日志          log
 * 指令重发      cmdResend
 * 当前告警      alarmCurrent
 *
 * @author yuanyao@wistronits.com
 * create on 2019-01-23 20:07
 */
public enum JobGroupEnum {
    /**
     * 首页设施信息缓存刷新
     */
    HOME_DEVICE("homeDevice"),
    /**
     * 刷新升级文件
     */
    UPGRADE_FILE("upgradeFile"),

    EXPORT("export"),

    DUMP("dump"),

    CLEAN("clean"),

    INSPECTION_TASK("inspectiontask"),

    PROC("proc"),

    LOG("log"),

    CMD_RESEND("cmdResend"),

    ALARMCURRENT("alarmCurrent"),

    OTHER("other"),

    UNLOCK("unlock"),

    USEREXPIRE("userExpire"),

    INCREMENTAL("incremental"),

    FORBIDDEN("forbidden"),

    STATISTICS("statistics"),

    RETRY_DEVICE("retryDevice"),

    ALARM_SOURCE_INCREMENTAL("alarmSourceIncremental"),

    ALARM_SOURCE_INCREMENTAL_WEEK("alarmSourceIncrementalWeek"),

    ALARM_SOURCE_INCREMENTAL_MONTH("alarmSourceIncrementalMonth"),

    DEVICE_SENSOR_VALUES(" deleteDeviceSensorValues"),

    ALARM_CLEAN_STATUS("alarmCleanStatus");
    private String groupName;

    JobGroupEnum(String groupName) {
        this.groupName = groupName;
    }

    /**
     * 根据参数获取当前枚举
     *
     * @param group
     * @return
     */
    public static JobGroupEnum getJobGroupEnumByStr(String group) {
        for (JobGroupEnum value : JobGroupEnum.values()) {
            if (StringUtils.equalsIgnoreCase(value.groupName, group)) {
                return value;
            }
        }
        return null;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }}
