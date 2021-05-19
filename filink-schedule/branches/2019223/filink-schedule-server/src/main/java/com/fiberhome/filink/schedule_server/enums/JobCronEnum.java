package com.fiberhome.filink.schedule_server.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * cron表达式定义
 *
 * @author yuanyao@wistronits.com
 * create on 2019-01-23 20:00
 */
public enum JobCronEnum {

    /**
     * 一分钟
     */
    ONE_MINUTE("0 0/1 * * * ? "),

    /**
     * 五秒种
     */
    FIVE_SECONDS("0/5 * * * * ? "),

    /**
     * 十秒种
     */
    TEN_SECONDS("0/10 * * * * ?  "),

    /**
     * 五分钟
     */
    FIVE_MINUTE("0 0/5 * * * ? ");

    private String cron;

    JobCronEnum(String cron) {
        this.cron = cron;
    }

    /**
     * 根据参数获取枚举
     * @param cronExpression
     * @return
     */
    public static JobCronEnum getEnumByStr(String cronExpression) {
        for (JobCronEnum jobCronEnum : JobCronEnum.values()) {
            if (StringUtils.equalsIgnoreCase(jobCronEnum.getCron(), cronExpression)) {
                return jobCronEnum;
            }
        }
        return null;
    }


    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }}
