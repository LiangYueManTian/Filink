package com.fiberhome.filink.alarm_api.fallback;


import com.fiberhome.filink.alarm_api.api.AlarmHistoryFeigm;
import com.fiberhome.filink.alarm_api.bean.AlarmHistory;
import com.fiberhome.filink.bean.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 历史告警服务熔断
 *
 * @author wtao103@fiberhome.com
 * create on 2018/12/16 5:39 PM
 */
@Slf4j
@Component
public class AlarmHistoryFeigmFallback implements AlarmHistoryFeigm {

    /**
     * 添加历史告警信息
     *
     * @param alarmHistory 历史告警信息
     * @return 判断结果
     */
    @Override
    public Result insertAlarmHistory(AlarmHistory alarmHistory) {
        log.info("insertAlarmHistory熔断》》》》》》》");
        return null;
    }
}
