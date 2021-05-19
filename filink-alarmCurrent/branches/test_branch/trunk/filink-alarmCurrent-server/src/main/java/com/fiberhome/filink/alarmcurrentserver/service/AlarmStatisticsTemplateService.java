package com.fiberhome.filink.alarmcurrentserver.service;

import com.fiberhome.filink.alarmcurrentserver.bean.AlarmStatisticsTemp;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.exportapi.bean.ExportDto;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-05-21
 */
public interface AlarmStatisticsTemplateService {

    /**
     * 根据ID查询告警统计模板列表信息
     *
     * @param tempId 封装条件
     * @return 当前告警列表信息
     */
    AlarmStatisticsTemp queryAlarmStatisticsTempId(String tempId);

    /**
     * 查询告警统计模板列表信息
     *
     * @param pageType 封装条件
     * @return 当前告警列表信息
     */
    Result queryAlarmStatisticsTempList(String pageType);

    /**
     * 新建告警统计模板
     *
     * @param alarmStatisticsTemp 封装条件
     * @return 当前告警列表信息
     */
    Result addAlarmStatisticsTemp(AlarmStatisticsTemp alarmStatisticsTemp);

    /**
     * 修改告警统计模板信息
     *
     * @param alarmStatisticsTemp 封装条件
     * @return 当前告警列表信息
     */
    Result updateAlarmStatisticsTemp(AlarmStatisticsTemp alarmStatisticsTemp);


    /**
     * 删除告警统计模板信息
     *
     * @param ids 封装条件
     * @return 当前告警列表信息
     */
    Result deleteManyAlarmStatisticsTemp(String[] ids);

    /**
     * 告警统计导出
     *
     * @param exportDto 封装条件
     * @return 当前告警列表信息
     */
    Result exportAlarmStatisticList(ExportDto exportDto);
}
