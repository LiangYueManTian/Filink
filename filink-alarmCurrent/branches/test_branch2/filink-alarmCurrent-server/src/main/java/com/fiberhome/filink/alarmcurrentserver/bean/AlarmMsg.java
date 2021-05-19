package com.fiberhome.filink.alarmcurrentserver.bean;

import java.io.Serializable;
import lombok.Data;

/**
 * <p>
 * kafka告警信息接收实体类，
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-22
 */
@Data
public class AlarmMsg implements Serializable {

    /**
     * 告警编码
     */
    private String alarmCode;

    /**
     * 告警设施ID
     */
    private String alarmSource;

    /**
     * 告警发生时间（设施时间）
     */
    private String alarmBeginTime;

    /**
     * 告警状态字段，可以判断是告警还是清除告警，或者正常
     */
    private String alarmStatus;

    /**
     * 告警参数，包括温度，湿度，门锁状态等
     */
    private Object data;

    /**
     * 主控id
     */
    private String equipmentId;

}
