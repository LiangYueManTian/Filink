package com.fiberhome.filink.alarmhistoryserver.service;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.exportapi.bean.ExportDto;

/**
 * <p>
 *  导出告警Service
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-22
 */
public interface AlarmHistoryExportService {

    /**
     * 导出列表信息
     *
     * @param exportDto 导出
     * @return 导出信息
     */
    Result exportAlarmList(ExportDto exportDto);
}
