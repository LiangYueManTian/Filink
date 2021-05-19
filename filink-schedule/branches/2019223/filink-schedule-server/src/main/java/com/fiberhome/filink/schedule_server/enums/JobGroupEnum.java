package com.fiberhome.filink.schedule_server.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * 任务分组枚举
 *      导出任务       export
 *      转储          dump
 *      清理          clean
 *      日志          log
 *
 * @author yuanyao@wistronits.com
 * create on 2019-01-23 20:07
 */
public enum JobGroupEnum {

    EXPORT("export"),

    DUMP("dump"),

    CLEAN("clean"),

    LOG("log"),

    OTHER("other");

    private String groupName;

    JobGroupEnum(String groupName) {
        this.groupName = groupName;
    }

    /**
     * 根据参数获取当前枚举
     * @return
     * @param group
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
