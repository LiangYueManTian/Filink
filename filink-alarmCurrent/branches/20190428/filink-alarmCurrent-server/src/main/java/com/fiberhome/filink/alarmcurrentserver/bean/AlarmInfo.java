package com.fiberhome.filink.alarmcurrentserver.bean;

import com.fiberhome.filink.alarmsetapi.bean.AlarmForwardRule;
import com.fiberhome.filink.alarmsetapi.bean.AlarmOrderRule;
import java.util.List;
import lombok.Data;

/**
 * 告警信息
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-22
 */
@Data
public class AlarmInfo {

    /**
     * 当前告警信息
     */
    private AlarmCurrent alarmCurrent;

    /**
     * 消息推送
     */
    private AlarmMessage alarmMessage;

    /**
     * 远程通知信息
     */
    private List<AlarmForwardRule> alarmForwardRuleList;

    /**
     * 转工单信息
     */
    private AlarmOrderRule alarmOrderRule;
}
