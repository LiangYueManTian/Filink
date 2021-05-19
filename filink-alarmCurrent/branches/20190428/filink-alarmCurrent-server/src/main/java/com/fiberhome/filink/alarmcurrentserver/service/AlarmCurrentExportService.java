package com.fiberhome.filink.alarmcurrentserver.service;

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
}
