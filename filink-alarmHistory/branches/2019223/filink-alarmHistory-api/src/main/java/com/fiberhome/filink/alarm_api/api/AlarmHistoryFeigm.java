package com.fiberhome.filink.alarm_api.api;

import com.fiberhome.filink.alarm_api.bean.AlarmHistory;
import com.fiberhome.filink.alarm_api.fallback.AlarmHistoryFeigmFallback;
import com.fiberhome.filink.bean.Result;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 历史告警模块feign测试 中转站
 *
 * @author wtao103@fiberhome.com
 * create on 2018/12/16 5:19 PM
 */
@FeignClient(name = "filink-alarmHistory-server", fallback = AlarmHistoryFeigmFallback.class)
public interface AlarmHistoryFeigm {


    /**
     * 添加历史告警信息
     *
     * @param alarmHistory 历史告警信息
     * @return 判断结果
     */
    @PostMapping("/alarmHistory/insertAlarmHistory")
    Result insertAlarmHistory(@RequestBody AlarmHistory alarmHistory);

}
