package com.fiberhome.filink.alarmhistoryapi.api;

import com.fiberhome.filink.alarmhistoryapi.bean.AlarmHistory;
import com.fiberhome.filink.alarmhistoryapi.fallback.AlarmHistoryFeigmFallback;
import com.fiberhome.filink.bean.Result;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 历史告警模块feign测试 中转站
 *
 * @author wtao103@fiberhome.com
 * create on 2018/12/16 5:19 PM
 */
@FeignClient(name = "filink-alarmhistory-server", fallback = AlarmHistoryFeigmFallback.class)
public interface AlarmHistoryFeign {

    /**
     * 添加历史告警信息
     *
     * @param alarmHistory 历史告警信息
     * @return 判断结果
     */
    @PostMapping("/alarmHistory/insertAlarmHistoryFeign")
    Result insertAlarmHistoryFeign(@RequestBody AlarmHistory alarmHistory);

    /**
     * 定时任务添加历史告警信息
     *
     * @param alarmHistoryList 历史告警信息
     * @return 判断结果
     */
    @PostMapping("/alarmHistory/insertAlarmHistoryListFeign")
    Result insertAlarmHistoryListFeign(@RequestBody List<AlarmHistory> alarmHistoryList);

    /**
     * 查询单个历史告警的信息
     *
     * @param alarmId 历史告警id
     * @return 查询结果
     */
    @PostMapping("/alarmHistory/queryAlarmHistoryByIdFeign/{alarmId}")
    List<AlarmHistory> queryAlarmHistoryByIdFeign(@PathVariable("alarmId") String alarmId);

    /**
     * 删除历史告警信息
     *
     * @param deviceIds 设施id
     * @return 判断结果
     */
    @PostMapping("/alarmHistory/batchDeleteAlarmHistoryFeign")
    Integer deleteAlarmHistoryFeign(@RequestBody List<String> deviceIds);

    /**
     * 根据id查询历史告警信息
     *
     * @param alarmId 告警id
     * @return 历史告警信息
     */
    @PostMapping("/alarmHistory/queryAlarmHistoryByIdsFeign")
    List<AlarmHistory> queryAlarmHistoryByIdsFeign(@RequestBody List<String> alarmId);
}
