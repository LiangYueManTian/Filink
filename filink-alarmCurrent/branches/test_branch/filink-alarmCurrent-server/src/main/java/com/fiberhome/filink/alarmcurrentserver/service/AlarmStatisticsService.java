package com.fiberhome.filink.alarmcurrentserver.service;

import com.baomidou.mybatisplus.service.IService;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmHomeParameter;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmSourceHomeParameter;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmStatisticsParameter;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmStatisticsTemp;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmTime;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-05-21
 */
public interface AlarmStatisticsService extends IService<AlarmStatisticsTemp> {


    /**
     * 告警类型统计
     *
     * @param alarmStatisticsParameter 封装条件
     * @return 告警类型统计信息
     */
    Result queryAlarmByLevelAndArea(AlarmStatisticsParameter alarmStatisticsParameter);

    /**
     * 根据告警级别查询告警数量
     *
     * @param queryCondition 封装条件
     * @return 当前告警列表信息
     */
    Result queryAlarmCountByLevel(QueryCondition<AlarmStatisticsParameter> queryCondition);

    /**
     * 告警处理统计
     *
     * @param alarmStatisticsParameter 封装条件
     * @return 告警处理统计信息
     */
    Result alarmHandleStatistics(AlarmStatisticsParameter alarmStatisticsParameter);

    /**
     * 告警名称统计
     *
     * @param queryCondition 封装条件
     * @return 告警名称统计信息
     */
    Result alarmNameStatistics(QueryCondition<AlarmStatisticsParameter> queryCondition);

    /**
     * 告警增量统计
     *
     * @param queryCondition 封装条件
     * @param timeType       日期类型
     * @return 告警名称统计信息
     */
    Result alarmIncrementalStatistics(QueryCondition<AlarmStatisticsParameter> queryCondition, String timeType);

    /**
     * 定时任务告警增量统计（日，周，年）
     *
     * @param alarmSourceHomeParameter  条件信息
     * @return 告警增量信息
     */
    void queryStatisticsData(AlarmSourceHomeParameter alarmSourceHomeParameter);

    /**
     * 删除告警增量统计（日，周，年）
     *
     * @param deleteTime 开始时间
     * @param timeType  时间类型
     * @return 告警增量信息
     */
    void deleteAlarmIncrementalStatistics(Long deleteTime, String timeType);

    /**
     * 批量删除告警统计模板
     *
     * @param ids 模板ids
     * @return 判断结果
     */
    Integer deleteAlarmStatisticsTemp(String[] ids);

    /**
     * 批量修改告警统计模板信息
     *
     * @param alarmStatisticsTemp 告警名称信息
     * @return 判断结果
     */
    Integer updateAlarmStatisticsTemp(AlarmStatisticsTemp alarmStatisticsTemp);

    /**
     * 批量新增告警统计模板信息
     *
     * @param list 告警名称信息
     * @return 判断结果
     */
    Integer addAlarmStatisticsTemp(List<AlarmStatisticsTemp> list);

    /**
     * 根据ID查询告警统计模板
     *
     * @param id 告警id
     * @return 告警模板信息
     */
    AlarmStatisticsTemp queryAlarmStatisticsTempById(String id);

    /**
     * 查询告警tcp
     *
     * @param queryCondition 条件信息
     * @return 告警tcp信息
     */
    Result queryAlarmNameGroup(QueryCondition<AlarmStatisticsParameter> queryCondition);

    /**
     * 首页设施统计告警名称信息
     *
     * @param alarmHomeParameter 条件信息
     * @return 统计告警信息
     */
    Result queryAlarmNameHomePage(AlarmHomeParameter alarmHomeParameter);

    /**
     * 设施统计告警级别信息
     *
     * @param alarmSourceHomeParameter 条件信息
     * @return 统计告警信息
     */
    Result queryAlarmCurrentSourceLevel(AlarmSourceHomeParameter alarmSourceHomeParameter);

    /**
     * 设施统计历史告警级别信息
     *
     * @param alarmSourceHomeParameter 条件信息
     * @return 统计告警信息
     */
    Result queryAlarmHistorySourceLevel(AlarmSourceHomeParameter alarmSourceHomeParameter);

    /**
     * 设施统计当前告警名称信息
     *
     * @param alarmSourceHomeParameter 条件信息
     * @return 统计告警信息
     */
    Result queryAlarmCurrentSourceName(AlarmSourceHomeParameter alarmSourceHomeParameter);

    /**
     * 设施统计历史告警名称信息
     *
     * @param alarmSourceHomeParameter 条件信息
     * @return 统计告警信息
     */
    Result queryAlarmHistorySourceName(AlarmSourceHomeParameter alarmSourceHomeParameter);

    /**
     * 新增告警设施统计信息
     *
     * @param alarmSourceHomeParameter 条件信息
     */
    void querySourceIncremental(AlarmSourceHomeParameter alarmSourceHomeParameter);

    /**
     * 设施增量查询信息
     *
     * @param alarmSourceHomeParameter 条件信息
     * @return 设施增量查询信息
     */
    Result queryAlarmSourceIncremental(AlarmSourceHomeParameter alarmSourceHomeParameter);

    /**
     * 大屏告警级别统计信息
     *
     * @return 告警级别统计信息
     */
    Result queryAlarmCurrentLevelGroup();

    /**
     * 大屏设施根据日周月top
     *
     * @param alarmTime 时间信息
     * @return 告警设施信息
     */
    Result queryScreenDeviceIdGroup(AlarmTime alarmTime);

    /**
     * 大屏设施根据top
     *
     * @return 告警设施信息
     */
    Result queryScreenDeviceIdsGroup();

    /**
     * app告警名称统计
     *
     * @return 告警统计信息
     */
    Result queryAppAlarmNameGroup();
}
