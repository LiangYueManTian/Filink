package com.fiberhome.filink.alarmcurrentapi.bean;

import lombok.Data;

import java.util.List;

/**
 * <p>
 * 设施信息实体类
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-22
 */

@Data
public class OrderDeviceInfo {


    /**
     * 设施ID，工单超时告警无ID
     */
    private String alarmSource;

    /**
     * 设施名称 ，工单超时告警无名称
     */
    private String alarmObject;

    /**
     * 门信息，工单超时告警无
     */
    private List<DoorInfo> doorInfoList;

    /**
     * 设施类型id，工单超时告警无
     */
    private String alarmSourceTypeId;

    /**
     * 区域id
     */
    private String areaId;

    /**
     * 区域名称
     */
    private String areaName;

    /**
     * 地址
     */
    private String address;

    /**
     * 单位id，多个单位ID用逗号隔开
     */
    private StringBuffer responsibleDepartmentIds;

    /**
     * 单位名称，多个单位名称用逗号隔开,跟单位ID 按顺序一一对应
     */
    private StringBuffer responsibleDepartmentNames;


    /**
     * 设施参数，设施上报的告警才有
     */
    private String deviceStatus;


}
