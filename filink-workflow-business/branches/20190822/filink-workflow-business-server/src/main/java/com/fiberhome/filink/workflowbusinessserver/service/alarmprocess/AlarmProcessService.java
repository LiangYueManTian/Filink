package com.fiberhome.filink.workflowbusinessserver.service.alarmprocess;

import com.fiberhome.filink.alarmcurrentapi.bean.AlarmCurrentInfo;
import com.fiberhome.filink.bean.Result;

import java.util.List;

/**
 * <p>
 * 告警处理 服务类
 * </p>
 *
 * @author hedongwei@wistronits.com
 * @since 2019-03-11
 */
public interface AlarmProcessService {

    /**
     * 工单超时生成单个告警处理过程
     * @author hedongwei@wistronits.com
     * @date  2019/4/20 21:18
     * @param alarmCurrentInfoList 告警集合
     * @param alarmCurrentInfo 告警信息
     */
    void procTimeOutGenerateAlarmOne(List<AlarmCurrentInfo> alarmCurrentInfoList, AlarmCurrentInfo alarmCurrentInfo);

    /**
     * 工单超时生成告警
     * @author hedongwei@wistronits.com
     * @date  2019/4/4 20:52
     * @return 工单超时生成告警结果
     */
    Result procTimeOutCreateAlarm();

}
