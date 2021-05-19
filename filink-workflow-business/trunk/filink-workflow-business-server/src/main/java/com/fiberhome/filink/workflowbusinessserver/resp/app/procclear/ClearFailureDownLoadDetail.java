package com.fiberhome.filink.workflowbusinessserver.resp.app.procclear;

import lombok.Data;

/**
 * app销障工单下载明细类
 *
 * @author chaofanrong@wistronits.com
 */
@Data
public class ClearFailureDownLoadDetail {
    /**
     * 工单id
     */
    private String procId;

    /**
     * 工单名称
     */
    private String title;

    /**
     * 工单类型
     */
    private String procType;

    /**
     * 执行人
     */
    private String assign;

    /**
     * 设施名称
     */
    private String deviceName;

    /**
     * 设施ID
     */
    private String deviceId;

    /**
     * 设施类型
     */
    private String deviceType;

    /**
     * 基础定位
     */
    private String positionBase;

    /**
     * 设施地址
     */
    private String address;

    /**
     * 告警名称
     */
    private String refAlarmName;

    /**
     * 告警code
     */
    private String refAlarmCode;

    /**
     * 告警时间
     */
    private Long alarmTime;

    /**
     * 门编号
     */
    private String doorNo;

    /**
     * 门名称
     */
    private String doorName;

    /**
     * 工单开始时间（创建时间）
     */
    private Long startTime;

    /**
     * 工单结束时间（实际完工时间）
     */
    private Long endTime;

}
