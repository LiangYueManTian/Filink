package com.fiberhome.filink.alarmcurrentapi.bean;

import lombok.Data;

import java.util.List;

/**
 * <p>
 * 告警信息实体类，包含设施告警和工单告警,以及通讯中断告警
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-22
 */

@Data
public class AlarmCurrentInfo {


    /**
     * 工单ID
     */
    private String orderId;

    /**
     * 工单名称
     */
    private String orderName;

    /**
     * 告警编码，工单超时告警不用给
     */
    private String alarmCode;


    /**
     * 设施信息,，工单超时告警只填写一个设施信息
     */
    private List<OrderDeviceInfo> orderDeviceInfoList;

    /**
     * 是否存在关联工单，工单超时告警默认为true
     */
    private Boolean isOrder;

    /**
     * 告警发生时间（工单服务当前时间或者设施告警上报的时间）
     */
    private Long alarmBeginTime;

    /**
     * 告警状态字段，可以判断是告警还是清除告警，或者正常，工单超时告警默认为1
     */
    private String alarmStatus;


}
