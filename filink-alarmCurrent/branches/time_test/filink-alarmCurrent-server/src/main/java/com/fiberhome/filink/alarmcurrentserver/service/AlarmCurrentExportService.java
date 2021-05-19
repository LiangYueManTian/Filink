package com.fiberhome.filink.alarmcurrentserver.service;

import com.fiberhome.filink.alarmcurrentserver.bean.AlarmSourceLevelParameter;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.exportapi.bean.ExportDto;

/**
 * <p>
 * 当前告警服务类
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-22
 */
public interface AlarmCurrentExportService {

    /**
     * 导出列表信息
     *
     * @param exportDto 导出
     * @return 导出信息
     */
    Result exportAlarmList(ExportDto exportDto);
    /**
     * 告警设施总数信息
     *
     * @param alarmObject 设施类型
     * @return 总数信息
     */
    Result queryAlarmObjectCount(String alarmObject);

    /**
     * 首页告警设施id更多信息
     *
     * @param alarmSourceLevelParameter 条件信息
     * @return 首页告警设施id更多信息
     */
    Result queryAlarmObjectCountHonePage(AlarmSourceLevelParameter alarmSourceLevelParameter);

    /**
     * 首页告警级别更多信息
     *
     * @param alarmSourceLevelParameter 条件信息
     * @return 首页告警级别更多信息
     */
    Result queryAlarmDeviceIdHonePage(AlarmSourceLevelParameter alarmSourceLevelParameter);

    /**
     * 首页告警告警id查询设施类型
     *
     * @param alarmSourceLevelParameter 条件信息
     * @return 首页告警设施id更多信息
     */
    Result queryAlarmIdCountHonePage(AlarmSourceLevelParameter alarmSourceLevelParameter);

    /**
     * 首页告警告警id查询告警级别
     *
     * @param alarmSourceLevelParameter 条件信息
     * @return 首页告警级别更多信息
     */
    Result queryAlarmIdHonePage(AlarmSourceLevelParameter alarmSourceLevelParameter);

    /**
     * 告警top导出
     *
     * @param exportDto 条件信息
     * @return 返回结果
     */
    Result exportDeviceTop(ExportDto exportDto);

    /**
     * 当前转历史
     */
    void updateAlarmCleanStatusCompensation();
}
