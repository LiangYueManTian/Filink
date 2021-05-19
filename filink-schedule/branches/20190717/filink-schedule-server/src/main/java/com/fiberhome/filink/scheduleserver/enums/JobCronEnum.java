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
     * 每周一一点半执行
     */
    MONDAY_TWO_CLOCK("0 30 1 ? * MON"),

    /**
     * 每月一号两点半执行
     */
    EVERY_MONTH_FIRST_HALF_PASS_TWO("0 30 2 1 * ?"),

    /**
     * 每年一月一日三点执行
     */
    EVERY_YEAR_FIRST_THREE_CLOCK("0 0 3 1 1 ? *"),

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
     * 一小时
     */
    HOURS_MINUTE("0 0/60 * * * ? "),

    /**
     * 0点开始，每两小时执行一次
     */
    TWO_HOUR("0 0 0/2 * * ? "),
    /**
     * 0点开始，每6小时执行一次
     */
    SIX_HOUR("0 0 0/6 * * ? "),
    /**
     * 每天凌晨零点
     */
    EVERY_DAY_AT_O_CLOCK("0 0 0 * * ?"),

    /**
     * 每天凌晨一点
     */
    EVERY_DAY_AT_ONE_O_CLOCK("0 0 1 * * ?"),

    /**
     * 每天凌晨两点
     */
    EVERY_DAY_AT_TWO_O_CLOCK("0 0 2 * * ?"),

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
     *
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
    }
}
