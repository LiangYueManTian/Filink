package com.fiberhome.filink.alarmhistoryserver.service;

import com.baomidou.mybatisplus.service.IService;
import com.fiberhome.filink.alarmhistoryserver.bean.AlarmHistory;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;

/**
 * <p>
 *  历史告警服务类
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
     * 添加历史告警信息
     *
     * @param alarmHistory 历史告警信息
     * @return 判断信息
     */
    Result insertAlarmHistory(AlarmHistory alarmHistory);
}
