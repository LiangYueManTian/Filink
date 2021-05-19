package com.fiberhome.filink.alarmcurrentserver.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmStatisticsTemp;
import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-05-21
 */
public interface AlarmStatisticsTempDao extends BaseMapper<AlarmStatisticsTemp> {

    /**
     * 批量新增告警统计模板信息
     *
     * @param list 告警名称信息
     * @return 判断结果
     */
    Integer addAlarmStatisticsTemp(List<AlarmStatisticsTemp> list);

    /**
     * 批量修改告警统计模板信息
     *
     * @param alarmStatisticsTemp 告警名称信息
     * @return 判断结果
     */
    Integer batchUpdateAlarmStatisticsTemp(AlarmStatisticsTemp alarmStatisticsTemp);


    /**
     * 批量删除告警统计模板
     *
     * @param ids 模板ids
     * @return 判断结果
     */
    Integer batchDeleteAlarmStatisticsTemp(String[] ids);

    /**
     * 根据ID查询告警统计模板
     *
     * @param id 告警id
     * @return 告警模板信息
     */
    AlarmStatisticsTemp queryAlarmStatisticsTempById(String id);

    /**
     * 根据ID查询告警统计模板
     *
     * @param id 告警id
     * @return 告警模板信息
     */
    AlarmStatisticsTemp queryAlarmStatisticsTempByIds(String id);

    /**
     * 查询告警统计模板
     *
     * @param alarmStatisticsTemp 告警alarmStatisticsTemp
     * @return 告警模板信息
     */
    List<AlarmStatisticsTemp> queryAlarmStatisticsTemp(AlarmStatisticsTemp alarmStatisticsTemp);

}
