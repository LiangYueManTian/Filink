package com.fiberhome.filink.alarm_api.bean;

import lombok.Data;

/**
 * <p>
 *  历史告警实体类
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-22
 */
@Data
public class AlarmHistory {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private String id;

    /**
     * Trap oid
     */
    private String trapOid;

    /**
     * 告警名称
     */
    private String alarmName;

    /**
     * 告警名称id
     */
    private String alarmNameId;

    /**
     * 告警编码
     */
    private String alarmCode;

    /**
     * 告警内容
     */
    private String alarmContent;

    /**
     * 告警类型
     */
    private Integer alarmType;

    /**
     * 告警源(设备id)
     */
    private String alarmSource;

    /**
     * 告警源类型
     */
    private String alarmSourceType;

    /**
     * 告警源类型id
     */
    private String alarmSourceTypeid;

    /**
     * 区域id
     */
    private String areaId;

    /**
     * 区域名称
     */
    private String areaName;

    /**
     * 工单id
     */
    private String orderId;

    /**
     * 地址
     */
    private String address;

    /**
     * 告警级别
     */
    private String alarmFixedLevel;

    /**
     * 告警对象
     */
    private String alarmobject;

    /**
     * 单位id
     */
    private String responsibleDepartmentId;

    /**
     * 负责单位
     */
    private String responsibleDepartment;

    /**
     * 提示音 0是否 1是有
     */
    private String prompt;

    /**
     * 告警发生时间
     */
    private Long alarmBeginTime;

    /**
     * 最近发生时间
     */
    private Long alarmNearTime;

    /**
     * 网管接收时间
     */
    private Long alarmSystemTime;

    /**
     * 网管最近接收时间
     */
    private Long alarmSystemNearTime;

    /**
     * 告警持续时间
     */
    private Integer alarmContinousTime;

    /**
     * 告警发生次数
     */
    private String alarmHappenCount;

    /**
     * 告警清除状态，2是设备清除，1是已清除，3是未清除
     */
    private Integer alarmCleanStatus;

    /**
     * 告警清除时间
     */
    private Long alarmCleanTime;

    /**
     * 告警清除类型
     */
    private Integer alarmCleanType;

    /**
     * 告警清除责任人id
     */
    private String alarmCleanPeopleId;

    /**
     * 告警清除责任人
     */
    private String alarmCleanPeopleNickname;

    /**
     * 告警确认状态,1是已确认，2是未确认
     */
    private Integer alarmConfirmStatus;

    /**
     * 告警确认时间
     */
    private Long alarmConfirmTime;

    /**
     * 告警确认人id
     */
    private String alarmConfirmPeopleId;

    /**
     * 告警确认人
     */
    private String alarmConfirmPeopleNickname;

    /**
     * 附加消息
     */
    private String extraMsg;

    /**
     * 处理信息
     */
    private String alarmProcessing;

    /**
     * 备注
     */
    private String remark;

    /**
     * 门编号
     */
    private String doorNumber;

    /**
     * 门名称
     */
    private String doorName;
}
