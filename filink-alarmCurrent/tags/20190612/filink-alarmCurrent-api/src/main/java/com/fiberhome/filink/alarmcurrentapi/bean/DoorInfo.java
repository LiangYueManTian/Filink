package com.fiberhome.filink.alarmcurrentapi.bean;

import lombok.Data;

/**
 * <P>
 * 门信息实体类
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-22
 */

@Data
public class DoorInfo {


    /**
     * 门编号
     */
    private String doorNumber;

    /**
     * 门名称
     */
    private String doorName;

    /**
     * 门状态
     */
    private String doorStatus;
}
