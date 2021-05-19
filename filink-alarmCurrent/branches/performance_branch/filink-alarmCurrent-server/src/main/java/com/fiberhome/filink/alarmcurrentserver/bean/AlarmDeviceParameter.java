package com.fiberhome.filink.alarmcurrentserver.bean;

import lombok.Data;

/**
 * <p>
 * 设施实体类
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-22
 */
@Data
public class AlarmDeviceParameter {

    /**
     * 设施id
     */
    private String alarmSource;

    /**
     * 数量
     */
    private Long count;
}
