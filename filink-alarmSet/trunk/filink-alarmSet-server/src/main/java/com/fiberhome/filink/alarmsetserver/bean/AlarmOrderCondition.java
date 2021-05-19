package com.fiberhome.filink.alarmsetserver.bean;

import lombok.Data;

/**
 * <p>
 * 告警转工单规则查询条件实体类
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-02-26
 */
@Data
public class AlarmOrderCondition {

    /**
     * id
     */
    private String id;

    /**
     * 区域
     */
    private String areaId;

    /**
     * 设施类型ID
     */
    private String deviceType;

    /**
     * 告警名称
     */
    private String alarmName;


}
