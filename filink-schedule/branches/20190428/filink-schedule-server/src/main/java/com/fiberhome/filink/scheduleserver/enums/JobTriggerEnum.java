package com.fiberhome.filink.scheduleserver.enums;

/**
 * 任务触发器枚举
 *      导出任务       export
 *      转储          dump
 *      清理          clean
 *      巡检          inspectiontask
 *      流程          proc
 *      日志          log
 *      当前告警      alarmCurrent
 * @author yuanyao@wistronits.com
 * create on 2019-01-23 20:07
 */
public enum JobTriggerEnum {
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

    ALARMCURRENT("alarmCurrent"),

    OTHER("other"),

    UNLOCK("unlock"),

    USEREXPIRE("userExpire"),

    FORBIDDEN("forbidden");

    private String groupName;

    JobTriggerEnum(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }}
