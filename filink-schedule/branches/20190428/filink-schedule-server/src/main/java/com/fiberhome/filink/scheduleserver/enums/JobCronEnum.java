package com.fiberhome.filink.scheduleserver.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * cron表达式定义
 *
 * @author yuanyao@wistronits.com
 * create on 2019-01-23 20:00
 */
public enum JobCronEnum {

    /**
     * 五秒种
     */
    FIVE_SECONDS("0/5 * * * * ? "),

    /**
     * 十秒种
     */
    TEN_SECONDS("0/10 * * * * ?  "),

    /**
     * 每秒执行一次
     */
    ONE_SECOND("0/1 * * * * ? "),

    /**
     * 三十秒种
     */
    TIRTY_SECONDS("0/30 * * * * ?  "),

    /**
     * 一分钟
     */
    ONE_MINUTE("0 0/1 * * * ? "),

    /**
     * 五分钟
     */
    FIVE_MINUTE("0 0/5 * * * ? "),

    /**
     * 十分钟
     */
    TEN_MINUTE("0 0/10 * * * ? "),

    /**
     * 0点开始，每两小时执行一次
     */
    TWO_HOUR("0 0 0/2 * * ? "),
    /**
     * 0点开始，每6小时执行一次
     */
    SIX_HOUR("0 0 0/6 * * ? "),
    /**
     * 每天凌晨一点
     */
    EVERY_DAY_AT_ONE_O_CLOCK("0 0 1 * * ?"),

    /**
     * 每天凌晨三点
     */
    EVERY_DAY_AT_THREE_O_CLOCK("0 0 3 * * ?");

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
