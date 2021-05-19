package com.fiberhome.filink.alarmcurrentserver.service;


import com.aliyuncs.exceptions.ClientException;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmCurrent;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmCurrentInfo;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmPictureMsg;
import com.fiberhome.filink.alarmsetapi.bean.AlarmForwardRule;
import com.fiberhome.filink.alarmsetapi.bean.AlarmOrderRule;
import java.util.List;


/**
 * <p>
 * 告警处理服务类
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 14:17 2019/2/27 0027
 */
public interface AlarmDisposeService {

    /**
     * 告警远程通知
     *
     * @param alarmForwardRules 告警远程通知规则
     * @param alarmCurrent     当前告警信息
     * @throws ClientException 异常
     */
    void alarmForward(List<AlarmForwardRule> alarmForwardRules, AlarmCurrent alarmCurrent) throws ClientException;

    /**
     * 告警图片处理逻辑
     *
     * @param alarmPictureMsg 告警图片信息
     */
    void imageDispose(AlarmPictureMsg alarmPictureMsg);

    /**
     * 自动清除告警
     *
     * @param alarmCurrentInfo 告警信息
     */
    void autoCleanAlarm(AlarmCurrentInfo alarmCurrentInfo);

    /**
     * 告警转工单
     *
     * @param alarmOrderRule 告警转工单规则
     * @param alarmCurrent   当前告警信息
     */
    void alarmCastOrder(AlarmOrderRule alarmOrderRule, AlarmCurrent alarmCurrent);
}
