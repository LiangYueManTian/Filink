package com.fiberhome.filink.scheduleapi.bean;

import lombok.Data;

/**
 * 指令重发定时任务实体类
 * @author CongcaiYu
 */
@Data
public class CmdResendTaskBean {

    /**
     * job名称
     */
    private String jobName;
    /**
     * group名称
     */
    private String groupName;
    /**
     * 重发时间
     */
    private Integer retryCycle;
    /**
     * 类型 1:udp 2:oceanConnect 3:oneNet
     */
    private String type;
}
