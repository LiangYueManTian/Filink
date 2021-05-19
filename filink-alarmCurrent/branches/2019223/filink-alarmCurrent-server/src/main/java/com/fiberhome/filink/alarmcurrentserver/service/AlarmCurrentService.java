package com.fiberhome.filink.alarmcurrentserver.service;

import com.fiberhome.filink.alarmcurrentserver.bean.AlarmCurrent;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import java.util.List;

/**
 * <p>
 * 当前告警服务类
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-22
 */
public interface AlarmCurrentService {

    /**
     * 查询当前告警信息列表
     *
     * @param queryCondition 查询条件封装
     * @return 当前告警信息
     */
    Result queryAlarmCurrentList(QueryCondition<AlarmCurrent> queryCondition);

    /**
     * 设施id查询告警信息
     *
     * @param deviceId 设施id
     * @return 判断结果
     */
    Result queryAlarmDeviceId(String deviceId);

    /**
     * 查询单个当前告警信息
     *
     * @param alarmId 当前告警ID
     * @return 当前告警信息
     */
    Result queryAlarmCurrentInfoById(String alarmId);

    /**
     * 批量设置当前告警的告警确认状态
     *
     * @param alarmCurrents 当前告警用户信息
     * @return 判断结果
     */
    Result updateAlarmConfirmStatus(List<AlarmCurrent> alarmCurrents);

    /**
     * 批量设置当前告警的告警清除状态
     *
     * @param alarmCurrents 当前告警用户信息
     * @return 判断结果
     */
    Result updateAlarmCleanStatus(List<AlarmCurrent> alarmCurrents);

    /**
     * 查询各级别告警总数
     *
     * @return 级别告警信息
     */
    Result queryEveryAlarmCount();

    /**
     * 查询设备信息是否存在
     *
     * @param alarmSources 设备id
     * @return 判断结果
     */
    List<String> queryAlarmSource(List<String> alarmSources);

    /**
     * 查询区域信息是否存在
     *
     * @param areaIds 区域ID
     * @return 判断结果
     */
    List<String> queryArea(List<String> areaIds);
}
