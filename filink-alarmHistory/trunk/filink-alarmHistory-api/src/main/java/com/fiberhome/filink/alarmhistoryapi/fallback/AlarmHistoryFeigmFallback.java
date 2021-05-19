package com.fiberhome.filink.alarmhistoryapi.fallback;


import com.fiberhome.filink.alarmhistoryapi.api.AlarmHistoryFeign;
import com.fiberhome.filink.alarmhistoryapi.bean.AlarmHistory;
import com.fiberhome.filink.bean.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 历史告警熔断
 *
 * @author wtao103@fiberhome.com
 * create on 2018/12/16 5:39 PM
 */
@Slf4j
@Component
public class AlarmHistoryFeigmFallback implements AlarmHistoryFeign {

    /**
     * 添加历史告警信息
     *
     * @param alarmHistory 历史告警信息
     * @return 判断结果
     */
    @Override
    public Result insertAlarmHistoryFeign(AlarmHistory alarmHistory) {
        log.warn("insertAlarmHistoryFeign failed");
        return null;
    }

    /**
     * 定时任务添加历史告警信息
     *
     * @param alarmHistoryList 历史告警信息
     * @return 判断结果
     */
    @Override
    public Result insertAlarmHistoryListFeign(List<AlarmHistory> alarmHistoryList) {
        log.warn("insertAlarmHistoryListFeign failed");
        return null;
    }

    /**
     * 查询单个历史告警的信息
     *
     * @param alarmId 历史告警id
     * @return 查询结果
     */
    @Override
    public List<AlarmHistory> queryAlarmHistoryByIdFeign(String alarmId) {
        log.warn("queryAlarmHistoryByIdFeign failed");
        return null;
    }

    /**
     * 删除历史告警信息
     *
     * @param deviceIds 设施id
     * @return 判断结果
     */
    @Override
    public Integer deleteAlarmHistoryFeign(List<String> deviceIds) {
        log.warn("deleteAlarmHistory failed");
        return null;
    }

    /**
     * 根据id查询历史告警信息
     *
     * @param alarmId 告警id
     * @return 历史告警信息
     */
    @Override
    public List<AlarmHistory> queryAlarmHistoryByIdsFeign(List<String> alarmId) {
        log.warn("queryAlarmHistoryByIdsFeign failed");
        return null;
    }
}
