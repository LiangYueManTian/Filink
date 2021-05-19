package com.fiberhome.filink.alarmcurrentserver.service;


import com.fiberhome.filink.alarmcurrentserver.bean.AlarmCurrentInfo;

import java.util.List;


/**
 * <p>
 *   告警接收和数据解析服务类
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 14:17 2019/2/27 0027
 */
public interface AlarmReceiveService {

    /**
     * 设施上报告警
     *
     * @param alarmCurrentInfo 告警信息
     */
    void alarmAnalysis(AlarmCurrentInfo alarmCurrentInfo);

    /**
     * 工单超时转告警
     *
     * @param alarmCurrentInfoList 告警信息
     */
    void orderCastAlarm(List<AlarmCurrentInfo> alarmCurrentInfoList);

}
