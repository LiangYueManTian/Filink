package com.fiberhome.filink.alarmhistoryserver.service;

import com.baomidou.mybatisplus.service.IService;
import com.fiberhome.filink.alarmhistoryserver.bean.AlarmHistory;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import java.util.List;

/**
 * <p>
 * 历史告警服务类
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-22
 */
public interface AlarmHistoryService extends IService<AlarmHistory> {

    /**
     * 查询历史告警列表信息
     *
     * @param queryCondition 查询条件封装
     * @return 历史告警列表信息
     */
    Result queryAlarmHistoryList(QueryCondition<AlarmHistory> queryCondition);

    /**
     * 查询单个历史告警的信息
     *
     * @param alarmId 告警id
     * @return 查询结果
     */
    Result queryAlarmHistoryInfoById(String alarmId);

    /**
     * 查询单个历史告警的信息
     *
     * @param alarmId 告警id
     * @return 查询结果
     */
    List<AlarmHistory> queryAlarmHistoryById(String alarmId);

    /**
     * 根据设施id查询告警信息
     *
     * @param deviceId 设施id
     * @return 告警信息
     */
    Result queryAlarmHistoryDeviceId(String deviceId);

    /**
     * 批量修改历史告警备注信息
     *
     * @param alarmHistories 当前告警信息
     * @return 判断结果
     */
    Result batchUpdateAlarmRemark(List<AlarmHistory> alarmHistories);

    /**
     * 添加历史告警信息
     *
     * @param alarmHistory 历史告警信息
     * @return 判断信息
     */
    Result insertAlarmHistoryFeign(AlarmHistory alarmHistory);

    /**
     * 定时任务添加历史告警信息
     *
     * @param alarmHistoryList 历史告警信息
     * @return 判断结果
     */
    void insertAlarmHistoryList(List<AlarmHistory> alarmHistoryList);

    /**
     * 删除历史告警信息
     *
     * @param deviceIds 设施id
     * @return 判断结果
     */
    Integer deleteAlarmHistory(List<String> deviceIds);

    /**
     * 根据id查询历史告警信息
     *
     * @param alarmIds 告警id
     * @return 历史告警信息
     */
    List<AlarmHistory> queryAlarmHistoryByIds(List<String> alarmIds);

    /**
     * 查询单位信息
     *
     * @param alarmIds 告警id
     * @return 单位信息
     */
    Result queryDepartmentHistory(List<String> alarmIds);

    /**
     * 插入历史告警数据
     */
    void addAlarmHistoryData();
}
