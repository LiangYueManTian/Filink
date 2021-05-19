package com.fiberhome.filink.alarmsetserver.bean;

import lombok.Data;

/**
 * <p>
 * 告警过滤规则查询条件实体类
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-02-26
 */
@Data
public class AlarmFilterCondition {

    private String id;

    /**
     * 告警对象ID
     */
    private String alarmObject;

    /**
     * 告警名称
     */
    private String alarmName;

    /**
     * 告警发生时间（设施时间）
     */
    private Long startTime;

}
