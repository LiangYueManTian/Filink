package com.fiberhome.filink.schedule_server.enums;

/**
 * 任务触发器枚举
 *      导出任务       export
 *      转储          dump
 *      清理          clean
 *      日志          log
 *
 * @author yuanyao@wistronits.com
 * create on 2019-01-23 20:07
 */
public enum JobTriggerEnum {

    EXPORT("export"),

    DUMP("dump"),

    CLEAN("clean"),

    LOG("log"),

    OTHER("other");

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
